package model.db.external.didier;

import android.content.Context;
import android.os.AsyncTask;
import android.text.format.Time;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.greenwav.greenwav.R;

import java.util.ArrayList;
import java.util.Iterator;

import model.Schedule;
import model.db.internal.JamboDAO;

public class GetNextSchedule extends AsyncTask<Void, Void, Long> {

    private Context c;
    private int idAppartient, calendar;
    private View card;
    private TextView tv;

    public GetNextSchedule(Context c, int idAppartient, int calendarGiven, View card) {
        this.c = c;
        this.idAppartient = idAppartient;

        this.card = card;
        this.tv = (TextView) card.findViewById(R.id.nextSchedule);
        if(calendarGiven != 1 && calendarGiven != 7){
            calendarGiven = 10;
        }
        this.calendar = calendarGiven;
    }


    @Override
    protected Long doInBackground(Void... params) {
        // TODO Auto-generated method stub
        JamboDAO dao = new JamboDAO(c);
        dao.open();
        ArrayList<Schedule> list = dao.findHoraire(idAppartient, calendar);
        dao.close();
        Iterator<Schedule> it = list.iterator();

        Time now = new Time();
        now.setToNow();
        now.format("hh:mm");

        String nowStr = now.hour+":";
        if(now.minute < 10){
            nowStr+="0";
        }
        nowStr+=now.minute;
        Log.d(nowStr, "NOW");
        boolean found = false;
        Schedule s = null;
        while(it.hasNext() && !found){
            s = it.next();
            Log.d(s.toString(), "schedule");
           if(s.toString().compareTo(nowStr) > 0) {
               found=true;
           }
        }
        Long ret = Long.valueOf(-1);
        if(s != null){
            int scheduleMillis = s.hour * 60 + s.minute;
            int nowMillis = now.hour * 60 + now.minute;
            ret = Long.valueOf(scheduleMillis - nowMillis);
        }
        return ret;
    }

    @Override
    protected void onPostExecute(Long result) {

        if(result>=0){
            tv.setText(result.toString()+ " "+c.getResources().getString(R.string.minutes));
        }
        else if (result==-1){
            tv.setText(R.string.activity_schedule);
        }
        else{
            tv.setText(R.string.no_schedules);
        }
    }
}
