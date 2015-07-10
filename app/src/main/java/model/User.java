package model;

import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.android.gms.maps.model.LatLng;
import com.greenwav.greenwav.R;

import model.custom.GreenSearch;

/**
 * Created by root on 6/13/15.
 */
public class User implements GreenSearch, Parcelable {

    private String name, mail;
    private int image;

    public static final Parcelable.Creator CREATOR =
            new Parcelable.Creator() {
                public User createFromParcel(Parcel in) {
                    return new User(in);
                }

                public User[] newArray(int size) {
                    return new User[size];
                }
            };

    public User(String name){
        this.name = name;
        image = R.drawable.ic_person_black;
    }

    public User(String name, String mail, Drawable icon){
        this.name = name;
        this.mail = mail;
    }

    public User(Parcel in){
        readFromParcel(in);
    }

    public String getName(){
        return name;
    }

    @Override
    public String getInformation() {
        return "user";
    }

    @Override
    public int getDrawable() {
        return image;
    }

    @Override
    public int compareTo(GreenSearch another) {
        return 0;
    }

    @Override
    public int describeContents() {
        return 3;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(mail);
        dest.writeInt(image);
    }
    private void readFromParcel(Parcel in) {
        name = in.readString();
        mail = in.readString();
        image = in.readInt();
    }
}
