package model.db.external.didier;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v4.widget.SwipeRefreshLayout;
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
import model.Schedule;
import model.db.internal.JamboDAO;
import model.utility.NetworkUtil;
import view.custom.adapter.ScheduleAdapter;

public class GetSchedules extends AsyncTask<Void, Schedule, Void> {

    private Context c;
    private int idAppartient, calendar;
    private ScheduleAdapter adapter;
    private SwipeRefreshLayout swipeRefreshLayout;

    public GetSchedules(Context c, Network network, int idAppartient, int calendarGiven, ScheduleAdapter adapter, SwipeRefreshLayout swipeRefreshLayout) {
        this.c = c;
        this.idAppartient = idAppartient;

        this.adapter = adapter;
        if(network.getDaybyday() == 0 && calendarGiven != 1 && calendarGiven != 7){
            calendarGiven = 10;
        }
        this.calendar = calendarGiven;
        this.swipeRefreshLayout = swipeRefreshLayout;
    }


    @Override
    protected Void doInBackground(Void... params) {
        // TODO Auto-generated method stub
        ArrayList<Schedule> horaires = new ArrayList<Schedule>();
        if (NetworkUtil.isConnected(c)) {
            Integer ret = null;

            StringBuilder jsonResult = new StringBuilder();
            final String BASE_URL = "http://sauray.me/greenwav/gorilla_schedule.php?";

            HttpURLConnection conn = null;
            try {
                StringBuilder sb = new StringBuilder(BASE_URL);
                sb.append("appartient=" + idAppartient);
                Log.d(calendar+"", "calendar");
                Log.d(idAppartient+"", "appartient");
                sb.append("&calendrier=" + calendar);

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
                    publishProgress(s);
                    horaires.add(s);
                }
                JamboDAO dao = new JamboDAO(c);
                dao.open();
                dao.insertHoraires(horaires);
                dao.close();

            } catch (MalformedURLException e) {

            } catch (JSONException e) {

            } catch (IOException e) {
            } finally {
                if (conn != null) {
                    conn.disconnect();
                }
            }
        }
        else {
            JamboDAO dao = new JamboDAO(c);
            dao.open();
            Iterator<Schedule> it = dao.findHoraire(idAppartient, calendar).iterator();
            dao.close();
            while(it.hasNext()){
                publishProgress(it.next());
            }
        }
        return null;
    }

    @Override
    protected void onProgressUpdate(Schedule... progress) {
       adapter.add(progress[0]);
    }

    protected void onPostExecute(Void result){
        swipeRefreshLayout.setRefreshing(false);
    }
}
    