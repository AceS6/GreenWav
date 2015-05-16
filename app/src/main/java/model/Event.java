package model;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.android.gms.maps.model.LatLng;
import com.greenwav.greenwav.R;

/**
 * Created by sauray on 16/03/15.
 */
public class Event implements Parcelable {

    private int id, type;
    private String nom, url;
    private Bitmap icon;
    private LatLng latLng;

    public static final int EVENT=1, CONCERT=2;

    public Event(int id, String nom, String url, int type, Bitmap icon, LatLng latLng){
        this.id = id;
        this.nom = nom;
        this.url = url;
        this.latLng = latLng;
        this.icon = icon;
        this.type = type;
    }

    public Event(Parcel in) {
        readFromParcel(in);
    }

    public int getId(){
        return id;
    }

    public String getUrl(){
        return url;
    }

    public String getNom(){
        return nom;
    }

    public LatLng getLatLng(){
        return latLng;
    }

    public Bitmap getIcon(Context c){
            return icon;
    }

    @Override
    public int describeContents() {
        return 6;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(this.nom);
        dest.writeString(this.url);
        dest.writeParcelable(latLng, flags);
        dest.writeParcelable(icon, flags);
        dest.writeInt(type);
    }

    private void readFromParcel(Parcel in) {
        id = in.readInt();
        nom = in.readString();
        url = in.readString();
        latLng = in.readParcelable(LatLng.class.getClassLoader());
        icon = in.readParcelable(LatLng.class.getClassLoader());
        type = in.readInt();
    }

    public static final Parcelable.Creator CREATOR =
            new Parcelable.Creator() {
                public Event createFromParcel(Parcel in) {
                    return new Event(in);
                }

                public Event[] newArray(int size) {
                    return new Event[size];
                }
            };

}
