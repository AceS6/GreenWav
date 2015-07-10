package model.db.external.didier;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

import com.greenwav.greenwav.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import model.Line;
import model.Network;
import model.Route;
import model.Schedule;
import model.Stop;
import model.db.internal.JamboDAO;

public class GetLineSchedules extends AsyncTask<Void, String, Void> implements DialogInterface.OnDismissListener {

    private Context c;
    private Line line;
    private ProgressDialog pd;
    private ImageView img;
    private Network network;

    public GetLineSchedules(Context c, Network network, Line line, ImageView img) {
        this.c = c;
        this.line = line;
        this.img = img;
        this.network = network;
    }

    protected void onPreExecute(){
        pd = new ProgressDialog(c);
        pd.setOnDismissListener(this);
        pd.setTitle(R.string.downloading_schedules);
        pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        pd.setProgress(0);
        pd.show();
    }

    @Override
    protected Void doInBackground(Void... params) {
        // TODO Auto-generated method stub
        ArrayList<Schedule> horaires = new ArrayList<Schedule>();
            Integer ret = null;

            JamboDAO dao = new JamboDAO(c);
            dao.open();
            ArrayList<Route> routes = dao.findRoutes(line.getIdBdd());
            Iterator<Route> it = routes.iterator();

            while (it.hasNext()){
                Route r = it.next();
                ArrayList<Stop> stops = new ArrayList<Stop>(dao.findAssociateArrets(r, "ASC"));
                int size = stops.size();
                pd.setMax(stops.size()*routes.size());
                for (int i=0; i < size; i++) {
                    Stop s = stops.get(i);
                    publishProgress(r.toString(), s.toString());
                    try {
                        if(network.getDaybyday() == 0){
                            dao.insertHoraires(getSchedule(s.getIdAppartient(), 10));
                        }
                        else {
                            for(int j=2;i<7;j++){
                                dao.insertHoraires(getSchedule(s.getIdAppartient(), j));
                            }

                        }
                        dao.insertHoraires(getSchedule(s.getIdAppartient(), 7));
                        dao.insertHoraires(getSchedule(s.getIdAppartient(), 1));
                    } catch (IOException e) {
                        e.printStackTrace();
                        this.cancel(true);
                        error();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
            dao.setLigneDownload(line.getIdBdd());
            dao.close();
        return null;
    }
    @Override
    protected void onProgressUpdate(String ... result){
        pd.setProgress(pd.getProgress()+1);
        pd.setTitle(result[0] + " : " + result[1]);
    }

    private ArrayList<Schedule> getSchedule(int idAppartient, int calendar) throws IOException, JSONException {
        StringBuilder jsonResult = new StringBuilder();
        final String BASE_URL = "http://sauray.me/greenwav/gorilla_schedule.php?";
        ArrayList<Schedule> ret = new ArrayList<>();
        HttpURLConnection conn = null;

            StringBuilder sb = new StringBuilder(BASE_URL);
            sb.append("appartient=" + idAppartient);
            sb.append("&calendrier=" + calendar);

        Log.d(sb.toString(), "sb=");

            URL url = new URL(sb.toString());
            conn = (HttpURLConnection) url.openConnection();
            InputStreamReader in = new InputStreamReader(conn.getInputStream());

            BufferedReader jsonReader = new BufferedReader(in);
            String lineIn;
            while ((lineIn = jsonReader.readLine()) != null) {
                jsonResult.append(lineIn);
            }

            JSONObject jsonObj = new JSONObject(jsonResult.toString());

            JSONArray jsonMainNode = jsonObj.optJSONArray("horaire");

            for (int i = 0; i < jsonMainNode.length(); i++) {
                JSONObject jsonChildNode = jsonMainNode.getJSONObject(i);
                String horaire = jsonChildNode.optString("horaire");
                int id = jsonChildNode.optInt("id");
                Schedule s = new Schedule(id, horaire, calendar, idAppartient);
                Log.d(s.toString(), "horaire");
                ret.add(s);
            }

        return ret;
    }

    @Override
    protected void onPostExecute(Void result){
        pd.hide();
        line.setDownload(1);
        img.setImageResource(R.drawable.ic_schedule_downloaded);
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        this.cancel(true);
    }

    private void error(){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(c);

        // set title

        alertDialogBuilder.setTitle("Erreur de connexion");

        // set dialog message
        alertDialogBuilder
                .setMessage("La connexion a échoué")
                .setCancelable(false)
                .setNeutralButton("Ok",  null);
        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();
        // show it
        alertDialog.show();

    }
}
