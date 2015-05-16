package model.db.internal.async;

import android.content.Context;
import android.os.AsyncTask;

import java.util.Iterator;

import model.Route;
import model.Stop;
import model.db.internal.JamboDAO;
import view.custom.adapter.StopAdapter;

/**
 * Created by asauray on 2/20/15.
 */
public class GetLocalStops extends AsyncTask<Void, Stop, Void> {

    private Context context;
    private Route route;
    private StopAdapter stopAdapter;


    public GetLocalStops(Context context, Route route, StopAdapter stopAdapter){
        this.context = context;
        this.stopAdapter = stopAdapter;
        this.route = route;
    }

    @Override
    protected Void doInBackground(Void... params) {
        JamboDAO dao = new JamboDAO(context);
        dao.open();
        Iterator<Stop> it = dao.findAssociateArrets(route).values().iterator();
        while(it.hasNext()){
            publishProgress(it.next());
        }
        dao.close();
        return null;
    }

    @Override
    protected void onProgressUpdate(Stop... result){
        stopAdapter.add(result[0]);
    }

}