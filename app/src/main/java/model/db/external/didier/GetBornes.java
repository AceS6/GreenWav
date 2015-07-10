package model.db.external.didier;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;

import com.androidmapsextensions.GoogleMap;
import com.androidmapsextensions.Marker;
import com.androidmapsextensions.MarkerOptions;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.greenwav.greenwav.R;

import java.util.HashMap;
import java.util.Iterator;

import model.Borne;
import model.db.internal.JamboDAO;
import view.custom.adapter.ElectricalAdapter;

public class GetBornes extends AsyncTask<Void, Borne, Void>{

    private GoogleMap gMap;
    private int network;
    private ElectricalAdapter borneAdapter;
    private Activity a;
    private HashMap<Integer, Marker> markers;

    public GetBornes(Activity a, int network, ElectricalAdapter borneAdapter) {
        this.borneAdapter = borneAdapter;
        this.network = network;
        this.a = a;
    }

    public GetBornes(Activity a, int network, GoogleMap gMap, HashMap<Integer, Marker> markers) {
        this.network = network;
        this.a = a;
        this.gMap = gMap;
        this.markers = markers;
    }

    @Override
    protected Void doInBackground(Void... params) {
        JamboDAO dao = new JamboDAO(a);
        dao.open();
        HashMap<Integer, Borne> bornes = dao.findBorne(network);
        Iterator<Borne> it = bornes.values().iterator();
        while(it.hasNext()){
            publishProgress(it.next());
        }
        return null;
    }

    protected void onProgressUpdate(Borne... result){
        if(gMap != null){
            Log.d(result[0].getNom(), "BORNE");
            Marker m = gMap.addMarker(new MarkerOptions()
                    .position(result[0].getLatLng())
                    .alpha(0.7f)
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_maps_car))
                    .title(result[0].getNom()));
            m.setData(result[0]);
            markers.put(result[0].getIdBdd(), m);
        }

        if(borneAdapter != null){
            borneAdapter.add(result[0]);
        }
    }
}


