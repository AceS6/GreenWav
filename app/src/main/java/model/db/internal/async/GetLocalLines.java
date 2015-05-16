package model.db.internal.async;

import android.content.Context;
import android.os.AsyncTask;

import java.util.Iterator;

import model.Line;
import model.Network;
import model.db.internal.JamboDAO;
import view.custom.adapter.BusComponentAdapter;

/**
 * Created by asauray on 2/20/15.
 */
public class GetLocalLines extends AsyncTask<Void, Line, Void> {

    private Context context;
    private Network network;
    private BusComponentAdapter bcAdapter;

    public GetLocalLines(Context context, Network network, BusComponentAdapter bcAdapter){
        this.network = network;
        this.context = context;
        this.bcAdapter = bcAdapter;
    }

    @Override
    protected Void doInBackground(Void... params) {
        JamboDAO dao = new JamboDAO(context);
        dao.open();
        Iterator<Line> it = dao.findLignes(network.getIdBdd()).values().iterator();
        while(it.hasNext()){
            publishProgress(it.next());
        }
        dao.close();
        return null;
    }

    @Override
    protected void onProgressUpdate(model.Line... result){
        bcAdapter.add(result[0]);
    }
}