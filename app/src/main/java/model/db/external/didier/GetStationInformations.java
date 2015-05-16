package model.db.external.didier;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.greenwav.greenwav.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import model.Network;
import model.Station;

public class GetStationInformations extends AsyncTask<Void, Station, Integer[]>{

    private Context c;
    private Station s;
    private Network n;
    private View v;

    public GetStationInformations(Context c, Station s, Network n, View v) {
        this.c = c;
        this.s = s;
        this.n = n;
        this.v = v;
    }

    @Override
    protected Integer[] doInBackground(Void... params) {
        // TODO Auto-generated method stub
        StringBuilder jsonResult = new StringBuilder();
        final String BASE_URL = "http://sauray.me/greenwav/gorilla_bike/";

        int bikes = -1;
        int slots = -1;

        HttpURLConnection conn = null;
        try {
            StringBuilder sb = new StringBuilder(BASE_URL);
            sb.append(n.getNom().toLowerCase());
            sb.append(".php?station=");
            sb.append(s.getIdExt());

            Log.d(n.getNom().toLowerCase(), "NOM");
            Log.d(s.getIdExt()+"", "IDEXT");

            Log.d(sb.toString(), "url");

            URL url = new URL(sb.toString());
            conn = (HttpURLConnection) url.openConnection();
            InputStreamReader in = new InputStreamReader(conn.getInputStream());

            BufferedReader jsonReader = new BufferedReader(in);
            String lineIn;
            while ((lineIn = jsonReader.readLine()) != null) {
                jsonResult.append(lineIn);
            }

            JSONObject jsonObj = new JSONObject(jsonResult.toString());


            for (int i = 0; i < jsonObj.length(); i++) {
                JSONObject jsonChildNode = jsonObj.getJSONObject("station");
                bikes = jsonChildNode.optInt("bikes");
                slots = jsonChildNode.optInt("slots");
            }


        } catch (MalformedURLException e) {

        } catch (JSONException e) {

        } catch (IOException e) {
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
        Integer[] ret = {bikes, slots};
        return ret;
    }

    protected void onPostExecute(Integer[] result){
        v.findViewById(R.id.progress).setVisibility(View.INVISIBLE);
        if(result[0] == -1 || result[1] == -1){
            v.findViewById(R.id.offline).setVisibility(View.VISIBLE);
            v.findViewById(R.id.online).setVisibility(View.INVISIBLE);
        }
        else{
            v.findViewById(R.id.offline).setVisibility(View.INVISIBLE);
            v.findViewById(R.id.online).setVisibility(View.VISIBLE);
            ((TextView)v.findViewById(R.id.bikeAvailable)).setText(result[0]+" vélos disponibles");
            ((TextView)v.findViewById(R.id.slotsAvailable)).setText(result[1]+" espaces disponibles");
        }

    }

}


