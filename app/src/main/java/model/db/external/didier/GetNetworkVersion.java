package model.db.external.didier;

import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;

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
import java.util.Iterator;

import model.Network;
import model.db.internal.JamboDAO;
import model.utility.NetworkUtil;
import view.activity.HomeActivity;
import view.activity.NetworkConfigurationActivity;

public class GetNetworkVersion extends AsyncTask<Void, String, Integer[]> implements OnDismissListener {

    private Context context;
    private Network network;

    private int versionBus, versionVelo, versionVoiture;

    public GetNetworkVersion(Context context) {
        this.context = context;
        this.network = null;
    }

    public GetNetworkVersion(Context context, Network network) {
        this.context = context;
        this.network = network;
        versionBus = network.getBus();
        versionVelo = network.getVelo();
        versionVoiture = network.getVoiture();
    }

    @Override
    protected Integer[] doInBackground(Void... params) {
        // TODO Auto-generated method stub

        if (network != null) {
            return getVersion(network);
        } else {
            JamboDAO dao = new JamboDAO(context);
            dao.open();
            ArrayList<Network> reseaux = dao.findReseaux();
            this.network = reseaux.get(0);
            dao.close();
            Integer[] ret = new Integer[3];
            if (reseaux.size() != 0) {
                String reseauStr = PreferenceManager.getDefaultSharedPreferences(context).getString("pref_reseau", "Aucun réseau");
                Iterator<Network> it = reseaux.iterator();
                while (it.hasNext()) {
                    Network rNew = it.next();
                    if (rNew.toString().equals(reseauStr)) {
                        network = rNew;
                    }
                }
                //Success! Do what you want
                versionBus = network.getBus();
                versionVelo = network.getVelo();
                versionVoiture = network.getVoiture();

                if (NetworkUtil.isConnected(context)) {
                    ret = this.getVersion(network);
                } else {
                    ret = new Integer[]{network.getBus(), network.getVelo(), network.getVoiture()};
                }
            }
            return ret;
        }


    }

    public Integer[] getVersion(Network r) {
        StringBuilder jsonResult = new StringBuilder();
        final String BASE_URL = "http://sauray.me/greenwav/gorilla_network.php?";

        int newVersionBus = versionBus;
        int newVersionVelo = versionVelo;
        int newVersionVoiture = versionVoiture;

        HttpURLConnection conn = null;
        try {
            StringBuilder sb = new StringBuilder(BASE_URL);
            sb.append("reseau=" + r.getIdBdd());

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
            JSONObject jsonChildNode = jsonMainNode.getJSONObject(0);

            newVersionBus = jsonChildNode.optInt("bus");
            newVersionVelo = jsonChildNode.optInt("velo");
            newVersionVoiture = jsonChildNode.optInt("voiture");

        } catch (MalformedURLException e) {

        } catch (JSONException e) {

        } catch (IOException e) {
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
        return new Integer[]{newVersionBus, newVersionVelo, newVersionVoiture};
    }

    @Override
    protected void onPostExecute(Integer[] result) {
        super.onPostExecute(result);
        boolean busOk=false, veloOk=false, voitureOk=false;

        Log.d(versionBus+"", "version bus");
        Log.d(versionVelo+"", "version velo");
        Log.d(versionVoiture + "", "version voiture");

        if(versionBus == 0 || result[0] == versionBus){
            busOk = true;
        }

        if(versionVelo == 0 || result[1] == versionVelo){
            veloOk = true;
        }

        if(versionVoiture == 0 || result[2] == versionVoiture){
            voitureOk = true;
        }

        if(busOk && veloOk && voitureOk){
            Intent home = new Intent(context, HomeActivity.class);
            home.putExtra("NETWORK", network);
            context.startActivity(home);
        }
        else{
            JamboDAO dao = new JamboDAO(context);
            //new DownloadNetwork(context, network, dao.findLignes(network.getIdBdd()), (versionVelo!=0), (versionBus!=0)).execute();
            dao.close();
        }

    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        // TODO Auto-generated method stub
        this.cancel(true);
    }


}


