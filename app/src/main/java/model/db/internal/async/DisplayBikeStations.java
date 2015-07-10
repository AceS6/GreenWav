package model.db.internal.async;

import android.animation.Animator;
import android.content.Context;
import android.os.AsyncTask;

import com.androidmapsextensions.GoogleMap;
import com.androidmapsextensions.Marker;
import com.androidmapsextensions.MarkerOptions;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import model.Line;
import model.Station;
import model.Stop;
import model.db.internal.JamboDAO;
import view.custom.google.LatLngInterpolator;
import view.custom.google.MarkerAnimation;

/**
 * Created by root on 6/20/15.
 */
public class DisplayBikeStations extends AsyncTask<Boolean, Station, Void> {

    private int markerId;
    private ArrayList<Station> stops;
    private Marker bus;
    private LatLngInterpolator.LinearFixed interpolator;
    private LatLng initialPosition;
    private Context context;
    private GoogleMap googleMap;
    private HashMap<Integer, Marker> markers;
    private int network;

    public DisplayBikeStations(Context context, GoogleMap googleMap, HashMap<Integer, Marker> markers, int network){
        this.context = context;
        this.googleMap = googleMap;
        this.markers = markers;
        this.network = network;
    }

    @Override
    protected void onPreExecute(){
        googleMap.clear();
        stops = null;

        interpolator = new LatLngInterpolator.LinearFixed();
    }

    @Override
    protected Void doInBackground(Boolean... params) {


        markers.clear();
        JamboDAO dao = new JamboDAO(context);
        dao.open();
        ArrayList<Station> bikes = dao.findStation(network);

        for(Station s : bikes){
            publishProgress(s);
        }
        dao.close();
        return null;
    }

    @Override
    protected void onProgressUpdate(Station...result){
        Marker m = googleMap.addMarker(new MarkerOptions()
                .position(result[0].getLatLng())
                .alpha(0.8f)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE))
                .title(result[0].toString()));
        m.setData(result[0]);
        markers.put(result[0].getIdBdd(), m);
    }
}