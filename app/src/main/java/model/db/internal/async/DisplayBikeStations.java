package model.db.internal.async;

import android.content.Context;
import android.os.AsyncTask;

import com.androidmapsextensions.GoogleMap;
import com.androidmapsextensions.Marker;
import com.androidmapsextensions.MarkerOptions;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.HashMap;

import model.BikeStation;
import model.db.internal.JamboDAO;
import view.custom.google.LatLngInterpolator;

/**
 * Created by root on 6/20/15.
 */
public class DisplayBikeStations extends AsyncTask<Boolean, BikeStation, Void> {

    private int markerId;
    private ArrayList<BikeStation> stops;
    private LatLngInterpolator.LinearFixed interpolator;
    private LatLng initialPosition;
    private Context context;
    private GoogleMap googleMap;
    private HashMap<Integer, Marker> markers;
    private int network;

    public DisplayBikeStations(Context context, GoogleMap googleMap, HashMap<Integer, Marker> markers, int markerId, int network){
        this.context = context;
        this.googleMap = googleMap;
        this.markers = markers;
        this.network = network;
        this.markerId = markerId;
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
        ArrayList<BikeStation> bikes = dao.findStation(network);

        for(BikeStation s : bikes){
            publishProgress(s);
        }
        dao.close();
        return null;
    }

    @Override
    protected void onProgressUpdate(BikeStation...result){
        Marker m = googleMap.addMarker(new MarkerOptions()
                .position(result[0].getLatLng())
                .alpha(0.8f)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE))
                .title(result[0].toString()));
        m.setData(result[0]);
        markers.put(result[0].getIdBdd(), m);
    }

    @Override
    protected void onPostExecute(Void result){
        if(markerId >= 0 && markers.containsKey(markerId)){
            Marker marker = markers.get(markerId);
            marker.showInfoWindow();
            googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition.Builder().target(marker.getPosition()).zoom(googleMap.getCameraPosition().zoom).build()));
        }
    }
}