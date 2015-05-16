package model.db.internal.async;

import android.content.Context;
import android.os.AsyncTask;

import java.util.Iterator;

import model.Line;
import model.Route;
import model.db.internal.JamboDAO;
import view.custom.adapter.RouteAdapter;

/**
 * Created by asauray on 2/20/15.
 */
public class GetLocalRoutes extends AsyncTask<Void, model.Route, Void> {

    private Context context;
    private Line line;
    private RouteAdapter routeAdapter;


    public GetLocalRoutes(Context context, Line l, RouteAdapter routeAdapter){
        this.context = context;
        this.routeAdapter = routeAdapter;
        this.line = line;
    }

    @Override
    protected Void doInBackground(Void... params) {
        JamboDAO dao = new JamboDAO(context);
        dao.open();
        Iterator<Route> it = dao.findRoutes(line.getIdBdd()).values().iterator();
        while(it.hasNext()){
            publishProgress(it.next());
        }
        dao.close();
        return null;
    }

    @Override
    protected void onProgressUpdate(model.Route... result){
        routeAdapter.add(result[0]);
    }
}