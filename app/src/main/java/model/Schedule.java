package model;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.format.Time;

import java.util.Calendar;

public class Schedule extends Time implements Comparable<Time>, Parcelable {

    public static final Parcelable.Creator CREATOR =
            new Parcelable.Creator() {
                public Schedule createFromParcel(Parcel in) {
                    return new Schedule(in);
                }

                public Schedule[] newArray(int size) {
                    return new Schedule[size];
                }
            };
    private int id;
    private int calendar;
    private int idAppartient;


    // new constructor for gorilla
    public Schedule(int id, String schedule, int calendar, int idAppartient) {
        this.format("hh:mm");
        this.hour = Integer.parseInt(schedule.substring(0, 2));
        this.minute = Integer.parseInt(schedule.substring(3, 5));
        this.idAppartient = idAppartient;
        this.calendar = calendar;
        this.id = id;
    }

    public Schedule(Parcel in) {
        readFromParcel(in);
    }

    public static int getDayOfWeekStr(int index) {
        Calendar calendar = Calendar.getInstance();
        int day = 0;
        switch(index){
            case 0:
                day = Calendar.MONDAY;
                break;
            case 1:
                day = Calendar.TUESDAY;
                break;
            case 2:
                day = Calendar.WEDNESDAY;
                break;
            case 3:
                day = Calendar.THURSDAY;
                break;
            case 4:
                day = Calendar.FRIDAY;
                break;
            case 5:
                day = Calendar.SATURDAY;
                break;
            case 6:
                day = Calendar.SUNDAY;
                break;
        }

        return day;
    }

    public static int getDayOfWeek(){
        Calendar calendar = Calendar.getInstance();
        return calendar.get(Calendar.DAY_OF_WEEK);
    }

    public int getCalendar() {
        return calendar;
    }

    public String toString() {
        String ret = "";
        if (hour < 10) {
            ret += "0";
        }
        ret += hour + ":";
        if (minute < 10) {
            ret += "0";
        }
        ret += minute;
        return ret;
    }

    public int getId() {
        return id;
    }

    @Override
    public int compareTo(Time another) {
        // TODO Auto-generated method stub
        return toString().compareTo(another.toString());
    }

    @Override
    public int describeContents() {
        return 8;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeInt(this.hour);
        dest.writeInt(this.minute);
        dest.writeInt(this.calendar);
    }

    private void readFromParcel(Parcel in) {
        id = in.readInt();
        hour = in.readInt();
        minute = in.readInt();
        calendar = in.readInt();
    }

    public int getIdAppartient() {
        return idAppartient;
    }

    public void setIdAppartient(int idAppartient) {
        this.idAppartient = idAppartient;
    }
}
