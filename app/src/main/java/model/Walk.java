package model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by root on 6/14/15.
 */
public class Walk implements Parcelable, Comparable<Walk>{

    public static final Parcelable.Creator CREATOR =
            new Parcelable.Creator() {
                public Walk createFromParcel(Parcel in) {
                    return new Walk(in);
                }

                public Walk[] newArray(int size) {
                    return new Walk[size];
                }
            };

    private String name;

    public Walk(String name){
        this.name = name;
    }

    public Walk(Parcel in) {
        readFromParcel(in);
    }

    @Override
    public int compareTo(Walk another) {
        return name.compareTo(another.name);
    }

    @Override
    public int describeContents() {
        return 1;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.name);
    }
    private void readFromParcel(Parcel in) {
        name = in.readString();
    }
}
