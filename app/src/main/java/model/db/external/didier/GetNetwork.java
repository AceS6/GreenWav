package model.db.external.didier;

import android.app.Activity;
import android.os.AsyncTask;
import android.support.v4.widget.SwipeRefreshLayout;
import android.widget.TextView;

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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import model.Network;
import model.db.internal.JamboDAO;
import view.custom.adapter.NetworkAdapter;


public class GetNetwork extends AsyncTask<Void, Network, Void>{

    private int idReseau;
    private Network network;
    private TextView operation;
    private NetworkAdapter networkAdapter;
    private Activity a;
    private ArrayList<Network> reseaux = new ArrayList<Network>();
    private boolean foundReseauLocally;
    private HashMap<Integer, Network> localNetworks;
    private SwipeRefreshLayout swipeRefreshLayout;

    public GetNetwork(Activity a, NetworkAdapter networkAdapter) {
        this.networkAdapter = networkAdapter;
        idReseau = -1;
        this.a = a;
        foundReseauLocally = false;
        operation = (TextView) a.findViewById(R.id.operation);
        this.reseaux = new ArrayList<Network>();
        localNetworks = new HashMap<Integer, Network>();
        swipeRefreshLayout = null;
    }

    public GetNetwork(Activity a, NetworkAdapter networkAdapter, SwipeRefreshLayout swipeRefreshLayout) {
        this.networkAdapter = networkAdapter;
        idReseau = -1;
        this.a = a;
        foundReseauLocally = false;
        operation = (TextView) a.findViewById(R.id.operation);
        this.reseaux = new ArrayList<Network>();
        localNetworks = new HashMap<Integer, Network>();
        this.swipeRefreshLayout = swipeRefreshLayout;
    }



    @Override
    protected Void doInBackground(Void... params) {
        // TODO Auto-generated method stub
        getLocalNetworks();
        StringBuilder jsonResult = new StringBuilder();
        final String BASE_URL = "http://sauray.me/greenwav/gorilla_network.php?";

        HttpURLConnection conn = null;
        try {
            StringBuilder sb = new StringBuilder(BASE_URL);
            if (idReseau != -1) {
                sb.append("reseau=" + idReseau);
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

            JSONArray jsonMainNode = jsonObj.optJSONArray("reseau");

            for (int i = 0; i < jsonMainNode.length(); i++) {
                JSONObject jsonChildNode = jsonMainNode.getJSONObject(i);
                int visible = jsonChildNode.optInt("visible");

                if(visible != 0) {
                    int id = jsonChildNode.optInt("id");
                    String nom = jsonChildNode.optString("nom");
                    double latitude = jsonChildNode.optDouble("latitude");
                    double longitude = jsonChildNode.optDouble("longitude");
                    String image = jsonChildNode.optString("image");
                    int bus = jsonChildNode.optInt("bus");
                    int velo = jsonChildNode.optInt("velo");
                    int voiture = jsonChildNode.optInt("voiture");
                    int dayByDay = jsonChildNode.optInt("bus_daybyday");
                    Network network = new Network(id, latitude, longitude, nom, image, bus, velo, voiture, dayByDay);
                    this.publishProgress(network);
                }
            }

        } catch (MalformedURLException e) {

        } catch (JSONException e) {

        } catch (IOException e) {
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
        return null;
    }

    @Override
    protected void onProgressUpdate(Network... progress) {
        Integer index = networkAdapter.getItemIndex(progress[0].getIdBdd());
        if(index == null){
            networkAdapter.add(progress[0]);
        }
        else{
            networkAdapter.checkUpdate(progress[0]);
        }

    }

    private void getLocalNetworks() {
        JamboDAO dao = new JamboDAO(a);
        dao.open();
        ArrayList<Network> ret = dao.findReseaux();
        Iterator<Network> it = ret.iterator();
        while (it.hasNext()) {
            Network n = it.next();
            n.setLocal(true);
            localNetworks.put(n.getIdBdd(), n);
            this.publishProgress(n);
        }
        dao.close();
    }

    @Override
    protected void onPostExecute(Void result){
        if(swipeRefreshLayout != null){
            swipeRefreshLayout.setRefreshing(false);
        }
    }

}


