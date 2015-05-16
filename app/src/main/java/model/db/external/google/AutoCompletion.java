package model.db.external.google;

import android.content.Context;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.os.AsyncTask;
import android.support.v4.widget.CursorAdapter;
import android.util.Log;

import com.greenwav.greenwav.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

import model.PlaceInformation;

public class AutoCompletion extends AsyncTask<String, Cursor, Cursor> {


    private static final String LOG_TAG = "ExampleApp";

    private static final String PLACES_API_BASE = "https://maps.googleapis.com/maps/api/place";
    private static final String TYPE_AUTOCOMPLETE = "/autocomplete";
    private static final String OUT_JSON = "/json";

    private ArrayList<PlaceInformation> placeInformationArrayList;
    private Context context;
    private CursorAdapter cursorAdapter;

    public AutoCompletion(Context context, ArrayList<PlaceInformation> placeInformationArrayList, CursorAdapter cursorAdapter) {
        this.context = context;
        this.placeInformationArrayList = placeInformationArrayList;
        this.cursorAdapter = cursorAdapter;
    }

    @Override
    protected Cursor doInBackground(String... params) {
        // TODO Auto-generated method stub
        return autocomplete(params[0]);
    }

    private Cursor autocomplete(String input) {
        ArrayList<String> resultList = null;
        MatrixCursor c = new MatrixCursor(new String[]{"_id", "description", "placeID"});

        HttpURLConnection conn = null;
        StringBuilder jsonResults = new StringBuilder();
        try {
            StringBuilder sb = new StringBuilder(PLACES_API_BASE + TYPE_AUTOCOMPLETE + OUT_JSON);
            sb.append("?key=" + context.getResources().getString(R.string.google_api_key));
            sb.append("&components=country:fr");
            sb.append("&language=fr");
            sb.append("&types=address");
            sb.append("&input=" + URLEncoder.encode(input, "utf8"));

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
            Log.e(LOG_TAG, "Error processing Places API URL", e);
            return c;
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error connecting to Places API", e);
            return c;
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }

        try {
            // Create a JSON object hierarchy from the results
            JSONObject jsonObj = new JSONObject(jsonResults.toString());
            JSONArray predsJsonArray = jsonObj.getJSONArray("predictions");


            // Extract the Place descriptions from the results
            for (int i = 0; i < predsJsonArray.length(); i++) {
                JSONObject placeObject = predsJsonArray.getJSONObject(i);
                String description = placeObject.getString("description");
                String placeID = placeObject.getString("place_id");
                c.addRow(new String[]{"1", description, placeID});
                placeInformationArrayList.add(new PlaceInformation(description, placeID, null));
                publishProgress(c);
                Log.d("placeID", "placeID=" + placeID);
            }
        } catch (JSONException e) {
            Log.e(LOG_TAG, "Cannot process JSON results", e);
        }

        return c;
    }

    public void onProgressUpdate(Cursor... progress) {
        cursorAdapter.changeCursor(progress[0]);

    }
}

