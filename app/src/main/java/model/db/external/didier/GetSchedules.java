package model.db.external.didier;

import android.content.Context;
import android.media.Image;
import android.os.AsyncTask;
import android.support.design.widget.TabLayout;
import android.text.format.Time;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
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
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.Iterator;

import model.Network;
import model.Schedule;
import model.db.internal.JamboDAO;
import model.utility.NetworkUtil;
import view.custom.adapter.ScheduleAdapter;

public class GetSchedules extends AsyncTask<Void, Schedule, Void> {

    private Context c;
    private int idAppartient, calendar;
    private TableLayout tableLayout;
    private int currentHour;
    private View previousLayout, nextLayout, afterLayout;
    private TextView textPrevious, textNext, textAfter, noConnexion;
    private ImageView imagePrevious, imageNext, imageAfter;
    private ProgressBar progressBar;
    private Schedule next, previous, after;

    public GetSchedules(Context c, Network network, int idAppartient, int calendarGiven, TableLayout tabLayout, View quickViewSchedule) {
        this.c = c;
        this.idAppartient = idAppartient;
        this.tableLayout = tabLayout;
        if(network.getDaybyday() == 0 && calendarGiven != 1 && calendarGiven != 7){
            calendarGiven = 10;
        }
        this.calendar = calendarGiven;

        progressBar = (ProgressBar) quickViewSchedule.findViewById(R.id.progress);
        progressBar.setVisibility(View.VISIBLE);

        noConnexion = (TextView) quickViewSchedule.findViewById(R.id.noConnexion);
        noConnexion.setVisibility(View.INVISIBLE);

        this.previousLayout = (RelativeLayout) quickViewSchedule.findViewById(R.id.previousSchedule);
        this.nextLayout = (RelativeLayout) quickViewSchedule.findViewById(R.id.nextSchedule);
        this.afterLayout = (RelativeLayout) quickViewSchedule.findViewById(R.id.afterSchedule);

        textPrevious = (TextView) previousLayout.findViewById(R.id.previousScheduleText);
        textNext = (TextView) nextLayout.findViewById(R.id.nextScheduleText);
        textAfter = (TextView) afterLayout.findViewById(R.id.afterScheduleText);

        textPrevious.setVisibility(View.INVISIBLE);
        textNext.setVisibility(View.INVISIBLE);
        textAfter.setVisibility(View.INVISIBLE);

        imagePrevious = (ImageView) previousLayout.findViewById(R.id.previousScheduleImage);
        imageNext = (ImageView) nextLayout.findViewById(R.id.nextScheduleImage);
        imageAfter = (ImageView) afterLayout.findViewById(R.id.afterScheduleImage);

        imagePrevious.setVisibility(View.INVISIBLE);
        imageNext.setVisibility(View.INVISIBLE);
        imageAfter.setVisibility(View.INVISIBLE);

        next = null;
        previous = null;
        after = null;

        tabLayout.removeAllViews();
    }

    @Override
    protected Void doInBackground(Void... params) {
        // TODO Auto-generated method stub

        ArrayList<Schedule> schedules = new ArrayList<Schedule>();
        Time t = new Time();
        t.setToNow();

        if (NetworkUtil.isConnected(c)) {
            Integer ret = null;
            ArrayList<Schedule> horaires = new ArrayList<Schedule>();
            StringBuilder jsonResult = new StringBuilder();
            final String BASE_URL = "http://sauray.me/greenwav/gorilla_schedule.php?";

            HttpURLConnection conn = null;
            try {
                StringBuilder sb = new StringBuilder(BASE_URL);
                sb.append("appartient=" + idAppartient);
                Log.d(calendar+"", "calendar");
                Log.d(idAppartient + "", "appartient");
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
                JSONObject jsonChildNodePre = jsonMainNode.getJSONObject(0);
                String horairePre = jsonChildNodePre.optString("horaire");
                int idPre = jsonChildNodePre.optInt("id");
                Schedule sPre = new Schedule(idPre, horairePre, calendar, idAppartient);
                currentHour = sPre.hour;

                Log.d(t.toString(), "now");

                for (int i = 0; i < jsonMainNode.length(); i++) {
                    JSONObject jsonChildNode = jsonMainNode.getJSONObject(i);
                    String horaire = jsonChildNode.optString("horaire");
                    int id = jsonChildNode.optInt("id");
                    Schedule s = new Schedule(id, horaire, calendar, idAppartient);
                    Log.d(s.toString(), "horaire");
                    if(s.hour == currentHour){
                        schedules.add(s);
                    }
                    else{
                        publishProgress(schedules.toArray(new Schedule[0]));
                        schedules.clear();
                        currentHour = s.hour;
                    }
                    horaires.add(s);
                    if(next == null){
                        previous = s;
                    }
                    if(next == null && s.isSuperior(t.hour, t.minute)){
                        next = s;
                    }
                    if(after == null && next != null && s.isSuperior(next.hour, next.minute)){
                        after = s;
                    }
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

            if(it.hasNext()){
                Schedule sPre = it.next();
                currentHour = sPre.hour;
                schedules.add(sPre);
            }
            while(it.hasNext()){
                Schedule s = it.next();
                if(s.hour == currentHour){
                    schedules.add(s);
                }
                else{
                    publishProgress(schedules.toArray(new Schedule[0]));
                    schedules.clear();
                    currentHour = s.hour;
                }
                if(next == null){
                    previous = s;
                }
                if(next == null && s.isSuperior(t.hour, t.minute)){
                    next = s;
                }
                if(after == null && next != null && s.isSuperior(next.hour, next.minute)){
                    after = s;
                }
            }
        }
        return null;
    }

    @Override
    protected void onProgressUpdate(Schedule... progress) {
        TableRow tableRow = new TableRow(c);
        for(int i=0; i < progress.length;i++){
            final String col = progress[i].toString();
            final TextView columsView = new TextView(c);
            columsView.setText(col);
            tableRow.addView(columsView, i);
            Log.d(progress[i].toString(), "schedule");
        }
        tableLayout.addView(tableRow, TabLayout.LayoutParams.MATCH_PARENT, TabLayout.LayoutParams.WRAP_CONTENT);
        tableLayout.requestLayout();
        tableLayout.refreshDrawableState();
    }

    protected void onPostExecute(Void result){
        progressBar.setVisibility(View.INVISIBLE);
        if(previous == null && next == null && after == null){
            noConnexion.setVisibility(View.VISIBLE);
        }
        else{
            if(previous != null){
                textPrevious.setText(previous.toString());
                textPrevious.setVisibility(View.VISIBLE);
            }
            else{
                imagePrevious.setVisibility(View.VISIBLE);
            }

            if(next != null){
                textNext.setText(next.toString());
                textNext.setVisibility(View.VISIBLE);
                if(after != null){
                    textAfter.setText(after.toString());
                    textAfter.setVisibility(View.VISIBLE);
                }
                else{
                    imageAfter.setVisibility(View.VISIBLE);
                }
            }
            else{
                imageNext.setVisibility(View.VISIBLE);
                imageAfter.setVisibility(View.VISIBLE);
            }
        }



    }
}
    