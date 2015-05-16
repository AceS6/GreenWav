package model.db.external.google;

import android.os.AsyncTask;

import com.androidmapsextensions.GoogleMap;
import com.androidmapsextensions.Marker;
import com.androidmapsextensions.MarkerOptions;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.greenwav.greenwav.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;

import model.Information;
import model.PlaceInformation;
import view.activity.HomeActivity;

/**
 * @author Antoine Sauray
 * @version 0.1
 */
public class GetDetails extends AsyncTask<PlaceInformation, PlaceInformation, PlaceInformation> {

    private HomeActivity homeActivity;
    private GoogleMap gMap;
    private HashMap<Integer, Information> informationHashMap;

    public GetDetails(HomeActivity homeActivity, GoogleMap gMap, HashMap<Integer, Information> informationHashMap) {
        this.homeActivity = homeActivity;
        this.gMap = gMap;
        this.informationHashMap = informationHashMap;
    }

    @Override
    protected PlaceInformation doInBackground(PlaceInformation... places) {
        // TODO Auto-generated method stub
        final String PLACES_API_BASE = "https://maps.googleapis.com/maps/api/place/details/json?";

        PlaceInformation place = places[0];

        HttpURLConnection conn = null;
        StringBuilder jsonResults = new StringBuilder();
        try {
            StringBuilder sb = new StringBuilder(PLACES_API_BASE);
            sb.append("&key=" + homeActivity.getResources().getString(R.string.google_api_key));
            sb.append("&placeid=" + URLEncoder.encode(place.getID(), "utf8"));

            URL url = new URL(sb.toString());
            conn = (HttpURLConnection) url.openConnection();
            InputStreamReader in = new InputStreamReader(conn.getInputStream());

            // Load the results into a StringBuilder
            int read;
            char[] buff = new char[1024];
            while ((read = in.read(buff)) != -1) {
                jsonResults.append(buff, 0, read);
            }
        } catch (MalformedURLException e) {
            return null;
        } catch (IOException e) {
            return null;
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }

        try {
            // Create a JSON object hierarchy from the results
            JSONObject jsonObj = new JSONObject(jsonResults.toString()).getJSONObject("result");
            //jsonObj.getString("name");
            JSONObject loc = jsonObj.getJSONObject("geometry").getJSONObject("location");
            LatLng placeLL = new LatLng(
                    Double.valueOf(loc.getString("lat")),
                    Double.valueOf(loc.getString("lng")));
            place.setLatLng(placeLL);
        } catch (JSONException e) {
            place = null;
        }
        return place;
    }

    @Override
    protected void onPostExecute(PlaceInformation result) {
        super.onPostExecute(result);
        if (result != null) {
            Marker m = gMap.addMarker(new MarkerOptions()
                    .position(result.getLatLng())
                    .title("Destination")
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_maps_destination)));
            result.setMarker(m);
            //informationHashMap.put(m.getTitle(), result);
            gMap.animateCamera(CameraUpdateFactory.newLatLngZoom(result.getLatLng(), 14.0f));
            m.showInfoWindow();
            homeActivity.onMarkerClick(m);
        }
    }

}
