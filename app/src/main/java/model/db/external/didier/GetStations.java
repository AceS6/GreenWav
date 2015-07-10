package model.db.external.didier;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;

import com.androidmapsextensions.GoogleMap;
import com.androidmapsextensions.Marker;
import com.androidmapsextensions.MarkerOptions;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.greenwav.greenwav.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import model.Station;
import model.db.internal.JamboDAO;
import view.custom.adapter.BikeAdapter;

public class GetStations extends AsyncTask<Void, Station, Void>{

    private GoogleMap gMap;
    private int network;
    private BikeAdapter stationAdapter;
    private Activity a;
    private HashMap<Integer, Marker> markers;

    public GetStations(Activity a, int network, BikeAdapter stationAdapter) {
        this.stationAdapter = stationAdapter;
        this.network = network;
        this.a = a;
    }

    public GetStations(Activity a, int network, GoogleMap gMap, HashMap<Integer, Marker> markers) {
        this.network = network;
        this.a = a;
        this.gMap = gMap;
        this.markers = markers;
    }

    @Override
    protected Void doInBackground(Void... params) {
        JamboDAO dao = new JamboDAO(a);
        dao.open();
        ArrayList<Station> stations = dao.findStation(network);
        for(Station s : stations){
            publishProgress(s);
        }
        return null;
    }

    protected void onProgressUpdate(Station... result){
        stationAdapter.add(result[0]);
    }
}


