package model.db.external.didier;

import android.os.AsyncTask;
import android.support.v4.widget.SwipeRefreshLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

import model.Line;
import model.Network;
import model.Route;
import view.custom.adapter.LineConfigurationAdapter;

/**
 * Created by sauray on 14/03/15.
 */
public class GetLines extends AsyncTask<Void, Line, Void>{

    private Network network;
    private LineConfigurationAdapter adapter;
    private SwipeRefreshLayout swipeRefreshLayout;

    public GetLines(Network network, LineConfigurationAdapter adapter, SwipeRefreshLayout swipeRefreshLayout){
        this.network = network;
        this.adapter = adapter;
        this.swipeRefreshLayout = swipeRefreshLayout;
    }

    @Override
    protected Void doInBackground(Void... params) {
        try {
            getLignes();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    private void getLignes() throws IOException {
        StringBuilder jsonResult = new StringBuilder();
        final String BASE_URL = "http://sauray.me/greenwav/gorilla_line.php?";
        HashMap<String, Line> ret = new HashMap<String, Line>();
        StringBuilder sb = new StringBuilder(BASE_URL);
        if (network.getIdBdd() != -1) {
            sb.append("reseau=" + network.getIdBdd());
        }
        URL url = new URL(sb.toString());
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


            JSONArray jsonMainNode = jsonObj.optJSONArray("ligne");
            int length = jsonMainNode.length();
            for (int i = 0; i < length; i++) {
                JSONObject jsonChildNode = jsonMainNode.getJSONObject(i);
                int id = jsonChildNode.optInt("id");
                String nom = jsonChildNode.optString("nom");
                String direction1 = jsonChildNode.optString("direction1");
                String direction2 = jsonChildNode.optString("direction2");
                String couleur = jsonChildNode.optString("couleur");
                int idReseau = jsonChildNode.optInt("reseau");
                int etat = jsonChildNode.optInt("etat");
                Line l = new Line(id, nom, direction1, direction2, couleur, idReseau, 0, etat);
                l.setRoutes(getRoutes(l));
                this.publishProgress(l);
            }

        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    private ArrayList<Route> getRoutes(Line l){
        ArrayList<Route> ret = new ArrayList<Route>();
        StringBuilder jsonResult = new StringBuilder();
        final String BASE_URL = "http://sauray.me/greenwav/gorilla_route.php?";

        StringBuilder sb = new StringBuilder(BASE_URL);
        if (l.getIdBdd() != -1) {
            sb.append("ligne=" + l.getIdBdd());
        }
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
            jsonObj = new JSONObject(jsonResult.toString());
            JSONArray jsonMainNode = jsonObj.optJSONArray("route");
            for (int i = 0; i < jsonMainNode.length(); i++) {
                JSONObject jsonChildNode = jsonMainNode.getJSONObject(i);
                int id = jsonChildNode.optInt("id");
                String nom = jsonChildNode.optString("nom");
                int ligne = jsonChildNode.optInt("ligne");
                Route r  = new Route(id, ligne, nom);
                ret.add(r);
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return ret;
    }

    @Override
    protected void onProgressUpdate(Line...progress){
        adapter.add(progress[0]);
    }

    @Override
    protected void onPostExecute(Void result){
        swipeRefreshLayout.setRefreshing(false);
    }

}
