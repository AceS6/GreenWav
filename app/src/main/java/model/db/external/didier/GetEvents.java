package model.db.external.didier;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.SystemClock;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.animation.BounceInterpolator;
import android.view.animation.Interpolator;

import com.androidmapsextensions.GoogleMap;
import com.androidmapsextensions.Marker;
import com.androidmapsextensions.MarkerOptions;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.greenwav.greenwav.R;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.ByteArrayBuffer;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Iterator;

import model.Borne;
import model.Event;
import model.db.internal.JamboDAO;
import view.custom.adapter.BorneAdapter;

public class GetEvents extends AsyncTask<Void, Event, Void>{

    private Activity a;
    private GoogleMap gMap;
    private int network;
    private HashMap<Integer, Marker> markers;

    private final String imagesLocation = "http://sauray.me/greenwav/images/";

    public GetEvents(Activity a, int network, GoogleMap gMap, HashMap<Integer, Marker> markers) {
        this.a = a;
        this.network = network;
        this.gMap = gMap;
        this.markers = markers;
    }

    @Override
    protected Void doInBackground(Void... params) {
        StringBuilder jsonResult = new StringBuilder();
        final String BASE_URL = "http://sauray.me/greenwav/gorilla_event.php?";
        HashMap<Integer, Event> ret = new HashMap<Integer, Event>();
        jsonResult = new StringBuilder();
        StringBuilder sb = new StringBuilder(BASE_URL);
        sb.append("reseau=" + network);
        URL url = null;
        try {
            url = new URL(sb.toString());

        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        InputStreamReader in = new InputStreamReader(conn.getInputStream());

        BufferedReader jsonReader = new BufferedReader(in);
        String lineIn;
        while ((lineIn = jsonReader.readLine()) != null) {
            jsonResult.append(lineIn);
        }

        JSONObject jsonObj;
        try {
            jsonObj = new JSONObject(jsonResult.toString());
            JSONArray jsonMainNode = jsonObj.optJSONArray("event");
            for (int i = 0; i < jsonMainNode.length(); i++) {
                JSONObject jsonChildNode = jsonMainNode.getJSONObject(i);
                int id = jsonChildNode.optInt("id");
                int type = jsonChildNode.optInt("type");
                String nom = jsonChildNode.optString("nom");
                String urlEvent = jsonChildNode.optString("url");
                double lat = jsonChildNode.optDouble("lat");
                double lng = jsonChildNode.optDouble("lng");
                DisplayMetrics metrics = new DisplayMetrics();
                a.getWindowManager().getDefaultDisplay().getMetrics(metrics);
                url = new URL(imagesLocation + id + "_" + metrics.densityDpi);
                Log.d(url.toString(), "URL");
                Bitmap b = null;
                try{
                    b = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                    Log.d("success", "Bitmap online");
                }
                catch(FileNotFoundException e){
                    b = BitmapFactory.decodeResource(a.getResources(), R.drawable.ic_maps_event);
                    Log.d("failed", "Bitmap offline");
                }
                Log.d(b.toString(), "bitmap");
                this.publishProgress(new Event(-id, nom, urlEvent, type, b, new LatLng(lat, lng)));

            }
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    protected void onProgressUpdate(Event... result){
        if(gMap != null){
            Log.d(result[0].getNom(), "EVENT");
            Log.d(result[0].getIcon(a).toString(), "bitmap");
            Marker m = gMap.addMarker(new MarkerOptions()
                    .position(result[0].getLatLng())
                    .alpha(0.7f)
                    .icon(BitmapDescriptorFactory.fromBitmap(result[0].getIcon(a)))
                    .title(result[0].getNom()));
            dropPinEffect(m);
            m.setData(result[0]);
            markers.put(result[0].getId(), m);
        }
    }

    private void dropPinEffect(final Marker marker) {
        final Handler handler = new Handler();
        final long start = SystemClock.uptimeMillis();
        final long duration = 1500;

        final Interpolator interpolator = new BounceInterpolator();

        handler.post(new Runnable() {
            @Override
            public void run() {
                long elapsed = SystemClock.uptimeMillis() - start;
                float t = Math.max(
                        1 - interpolator.getInterpolation((float) elapsed
                                / duration), 0);
                marker.setAnchor(0.5f, 1.0f + 14 * t);

                if (t > 0.0) {
                    // Post again 15ms later.
                    handler.postDelayed(this, 15);
                } else {

                }
            }
        });
    }
}


