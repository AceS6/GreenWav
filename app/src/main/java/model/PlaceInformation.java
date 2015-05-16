package model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.android.gms.maps.model.LatLng;

public class PlaceInformation extends Information implements Parcelable {
    public static final Parcelable.Creator CREATOR =
            new Parcelable.Creator() {
                public PlaceInformation createFromParcel(Parcel in) {
                    return new PlaceInformation(in);
                }

                public PlaceInformation[] newArray(int size) {
                    return new PlaceInformation[size];
                }
            };
    private String name;
    private LatLng latLng;
    private String id;

    public PlaceInformation(String name, LatLng latLng) {
        super(PlaceInformation.PLACE);
        this.name = name;
        this.latLng = latLng;
    }

    public PlaceInformation(String name, String placeID, LatLng latLng) {
        super(PlaceInformation.PLACE);
        this.name = name;
        this.latLng = latLng;
        this.id = placeID;
    }

    public PlaceInformation(Parcel in) {
        super(PlaceInformation.PLACE);
        readFromParcel(in);
    }

    public LatLng getLatLng() {
        return latLng;
    }

    public void setLatLng(LatLng newLatLng) {
        this.latLng = newLatLng;
    }

    public String toString() {
        return name;
    }

    public String getID() {
        return id;
    }

    @Override
    public int describeContents() {
        return 3;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(category);
        dest.writeString(this.name);
        dest.writeString(id);
        dest.writeParcelable(latLng, flags);
    }

    protected void readFromParcel(Parcel in) {
        category = in.readInt();
        name = in.readString();
        id = in.readString();
        latLng = in.readParcelable(LatLng.class.getClassLoader());
    }
}
