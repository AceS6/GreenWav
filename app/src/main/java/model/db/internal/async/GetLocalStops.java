package model.db.internal.async;

import android.content.Context;
import android.os.AsyncTask;

import java.util.ArrayList;
import java.util.Iterator;

import model.Line;
import model.Route;
import model.Stop;
import model.db.internal.AnimationCallBack;
import model.db.internal.DAOCallback;
import model.db.internal.JamboDAO;
import view.custom.adapter.StopAdapter;

/**
 * Created by asauray on 2/20/15.
 */
public class GetLocalStops extends AsyncTask<Void, Stop, Void> {

    private Context context;
    private Line line;
    private StopAdapter stopAdapter;


    public GetLocalStops(Context context, Line line, StopAdapter stopAdapter){
        this.context = context;
        this.stopAdapter = stopAdapter;
        this.line = line;
    }

    @Override
    protected Void doInBackground(Void... params) {
        JamboDAO dao = new JamboDAO(context);
        dao.open();
        ArrayList<Route> routes = dao.findRoutes(line.getIdBdd());
        line.setRoutes(routes);
        for(Route r : routes){
            ArrayList<Stop> stops = dao.findAssociateArrets(r, "ASC");
            for(Stop s : stops){
                publishProgress(s);
            }
        }
        dao.close();
        return null;
    }

    @Override
    protected void onProgressUpdate(Stop... result){
        stopAdapter.add(result[0]);
    }

}