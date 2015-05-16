package model.db.external.didier;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.greenwav.greenwav.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;

import model.Borne;
import model.Line;
import model.Network;
import model.Route;
import model.Station;
import model.Stop;
import model.db.internal.DAOCallback;
import model.db.internal.JamboDAO;
import view.activity.HomeActivity;

/**
 * @author Antoine Sauray
 *         Downloads a full Network and adds it to the internal database
 *         Keeps a track of the version downloaded
 */
public class DownloadNetwork extends AsyncTask<Void, String, HashMap<String, Stop>> implements DAOCallback {

    private Network network;
    private ProgressDialog pd;
    private Context c;

    private HashMap<Integer, Line> lines;
    private boolean bike, car;

    public DownloadNetwork(Context c, Network network, HashMap<Integer, Line> lines, boolean bike, boolean car) {
        this.network = network;
        this.c = c;
        pd = new ProgressDialog(c);
        this.lines = lines;
        this.bike = bike;
        this.car = car;
        Log.d(lines.size()+"", "nb lines");
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        pd.setTitle(c.getResources().getString(R.string.downloading_network) + network.toString());
        pd.setCanceledOnTouchOutside(false);
        pd.setCancelable(false);
        pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        pd.setProgress(0);
        pd.setMax(100);
        pd.show();
    }

    @Override
    protected HashMap<String, Stop> doInBackground(Void... params) {
        // TODO Auto-generated method stub

        HashMap<String, Stop> ret = new HashMap<String, Stop>();
        Resources res = c.getResources();

        try {
            JamboDAO dao = new JamboDAO(c);
                dao.open();

            if(bike) {
                network.setStations(getStations());
                SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(c);
                sharedPref.edit().putString("pref_service", "1").apply();
            }
            else{
                dao.removeStation(network.getIdBdd());
                network.setVelo(0);
            }
            if(car) {
                network.setBornes(getBornes());
                SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(c);
                sharedPref.edit().putString("pref_service", "2").apply();
            }
            else{
                dao.removeBorne(network.getIdBdd());
                network.setVoiture(0);
            }

            if(lines.size() == 0){
                network.setBus(0);
            }
            else{
                dao.removeLigne(network.getIdBdd());
                network.setLignes(lines);    // On recupere toutes les lignes du reseau
                Iterator<Line> it = network.getLignes().values().iterator();    // On assigne a chaque ligne ses arrets
                while (it.hasNext()) {
                    Line l = it.next();
                    l.setRoutes(getRoutes(l));
                    this.publishProgress(res.getString(R.string.downloading_line) + l.getNumero());
                }
                SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(c);
                sharedPref.edit().putString("pref_service", "0").apply();
            }

            if (dao.findReseaux().size() == 0) {
                SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(c).edit();
                editor.putString("pref_reseau", network.toString());
                editor.apply();
            }
            dao.insertReseau(network, this);
            dao.close();

        } catch (IOException e1) {
            // TODO Auto-generated catch block
            Log.d("exception", "IOException");
            JamboDAO dao = new JamboDAO(c);
            dao.open();
            dao.removeReseau(network.getIdBdd());
            dao.close();
            e1.printStackTrace();
            ret = null;
        } // On recupere tous les arrets du reseau

        return ret;

    }

    private HashMap<Integer, Stop> getStops(Route r) throws IOException {
        StringBuilder jsonResult = new StringBuilder();
        final String BASE_URL = "http://sauray.me/greenwav/gorilla_stop.php?";
        HashMap<Integer, Stop> ret = new HashMap<Integer, Stop>();
        StringBuilder sb = new StringBuilder(BASE_URL);
        sb.append("route=" + r.getIdBdd());

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
            JSONArray jsonMainNode = jsonObj.optJSONArray("arret");
            int length = jsonMainNode.length();
            for (int i = 0; i < length; i++) {
                JSONObject jsonChildNode = jsonMainNode.getJSONObject(i);
                int id = jsonChildNode.optInt("id");
                String nom = jsonChildNode.optString("nom");
                double lat = jsonChildNode.optDouble("latitude");
                double lng = jsonChildNode.optDouble("longitude");
                int reseau = jsonChildNode.optInt("reseau");
                int idAppartient = jsonChildNode.optInt("idappartient");
                int position = jsonChildNode.optInt("position");
                Stop a = new Stop(id, nom, new LatLng(lat, lng), reseau, 0, idAppartient, position, 0);
                ret.put(id, a);
                network.getArrets().put(id, a);
                publishProgress(a.toString(), i+"", length+"");
                Log.d(a.toString(), "Route " + r.toString() + " : Nouvel arret trouve");
            }
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return ret;
    }

    private HashMap<String, Route> getRoutes(Line l){
        HashMap<String, Route> ret = new HashMap<String, Route>();
        Resources res = c.getResources();
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
                    r.setStop(getStops(r));
                    ret.put(nom, r);
                    this.publishProgress(res.getString(R.string.downloading_route) + r.toString());
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

    private HashMap<Integer, Line> getLignes() throws IOException {
        Resources res = c.getResources();
        StringBuilder jsonResult = new StringBuilder();
        final String BASE_URL = "http://sauray.me/greenwav/gorilla_line.php?";
        HashMap<Integer, Line> ret = new HashMap<Integer, Line>();
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
            pd.setMax(length);
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
                ret.put(id, l);
                this.publishProgress(res.getString(R.string.downloading_line) + l.getNumero(), i+"");
            }

        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        this.publishProgress(res.getString(R.string.configuration));

        return ret;

    }

    private HashMap<Integer, Station> getStations(){
        // TODO Auto-generated method stub
        StringBuilder jsonResult = new StringBuilder();
        final String BASE_URL = "http://sauray.me/greenwav/gorilla_station.php?";
        HashMap<Integer, Station> ret = new  HashMap<Integer, Station>();

        HttpURLConnection conn = null;
        try {
            StringBuilder sb = new StringBuilder(BASE_URL);
            if (network.getIdBdd() != -1) {
                sb.append("reseau=" + network.getIdBdd());
            }
            URL url = new URL(sb.toString());
            conn = (HttpURLConnection) url.openConnection();
            InputStreamReader in = new InputStreamReader(conn.getInputStream());

            BufferedReader jsonReader = new BufferedReader(in);
            String lineIn;
            while ((lineIn = jsonReader.readLine()) != null) {
                jsonResult.append(lineIn);
            }

            JSONObject jsonObj = new JSONObject(jsonResult.toString());

            JSONArray jsonMainNode = jsonObj.optJSONArray("station");
            int length = jsonMainNode.length();
            for (int i = 0; i < length; i++) {
                JSONObject jsonChildNode = jsonMainNode.getJSONObject(i);
                int id = jsonChildNode.optInt("id");
                String nom = jsonChildNode.optString("nom");
                double latitude = jsonChildNode.optDouble("latitude");
                double longitude = jsonChildNode.optDouble("longitude");
                String adresse = jsonChildNode.optString("adresse");
                int externalId = jsonChildNode.optInt("id_ext");
                Log.d(nom + ", etat = " + ret + ", id = " + externalId, " idext");
                Station s = new Station(id, nom, adresse, new LatLng(latitude, longitude), network.getIdBdd(), externalId);
                ret.put(s.getIdBdd(), s);
                publishProgress("Téléchargement de la station "+s.getNom());
            }

        } catch (MalformedURLException e) {

        } catch (JSONException e) {

        } catch (IOException e) {
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
        return ret;
    }

    private HashMap<Integer, Borne> getBornes() throws IOException {

        StringBuilder jsonResult = new StringBuilder();
        final String BASE_URL = "http://sauray.me/greenwav/gorilla_borne.php?";
        HashMap<Integer, Borne> ret = new HashMap<Integer, Borne>();
        jsonResult = new StringBuilder();
        StringBuilder sb = new StringBuilder(BASE_URL);
        sb.append("reseau=" + network.getIdBdd());
        URL url = new URL(sb.toString());
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        InputStreamReader in = new InputStreamReader(conn.getInputStream());

        Resources res = c.getResources();

        BufferedReader jsonReader = new BufferedReader(in);
        String lineIn;
        while ((lineIn = jsonReader.readLine()) != null) {
            jsonResult.append(lineIn);
        }

        JSONObject jsonObj;
        try {
            jsonObj = new JSONObject(jsonResult.toString());
            JSONArray jsonMainNode = jsonObj.optJSONArray("borne");

            for (int i = 0; i < jsonMainNode.length(); i++) {
                JSONObject jsonChildNode = jsonMainNode.getJSONObject(i);
                int id = jsonChildNode.optInt("id");
                String nom = jsonChildNode.optString("nom");
                String adresse = jsonChildNode.optString("nom");
                double lat = jsonChildNode.optDouble("latitude");
                double lng = jsonChildNode.optDouble("longitude");
                String nomPorteur = jsonChildNode.optString("nom_porteur");
                String typeChargeur = jsonChildNode.optString("type_charge");
                int nbrePdc = jsonChildNode.optInt("nbre_pdc");
                String typeConnecteur = jsonChildNode.optString("type_connecteur");
                String observations = jsonChildNode.optString("observations");
                int reseau = jsonChildNode.optInt("reseau");
                Borne b = new Borne(id, nom, adresse, new LatLng(lat, lng), nomPorteur, typeChargeur, nbrePdc, typeConnecteur, observations, reseau);
                ret.put(id, b);
                this.publishProgress(res.getString(R.string.downloading_borne) + this.network.toString());
            }
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return ret;
    }

    @Override
    protected void onProgressUpdate(String... progress) {
        pd.setMessage(progress[0]);
        if(progress.length > 1){
            pd.setProgress(Integer.parseInt(progress[1]));
        }
    }

    @Override
    protected void onPostExecute(HashMap<String, Stop> result) {
        super.onPostExecute(result);
        pd.dismiss();
        if (result != null) {
            Intent mStartActivity = new Intent(c, HomeActivity.class);
            mStartActivity.putExtra("NETWORK", network);
            this.c.startActivity(mStartActivity);
        } else {
            Resources res = c.getResources();
            AlertDialog.Builder builder = new AlertDialog.Builder(c);
            builder.setTitle(res.getString(R.string.error));
            builder.setMessage(res.getString(R.string.download_failed));
            //alertDialog.setIcon(R.drawable.tick);
            // Setting OK Button
            builder.setNeutralButton(res.getString(R.string.ok), new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    // Write your code here to execute after dialog closed
                }
            });
            builder.show();
        }
    }

    @Override
    public void insertionArretPerformed(int progress, int size) {
        pd.setMax(size);
        pd.setProgress(progress);
    }

    @Override
    public void insertionLignePerformed(int progress, int size) {
        pd.setMax(size);
        pd.setProgress(progress);
    }

    @Override
    public void insertionStationPerformed(int progress, int size) {
        pd.setMax(size);
        pd.setProgress(progress);
    }

    @Override
    public void insertionBornePerformed(int progress, int size) {

    }

    @Override
    public void associationPerformed(int progress, int size) {
        pd.setMax(size);
        pd.setProgress(progress);
    }
}


