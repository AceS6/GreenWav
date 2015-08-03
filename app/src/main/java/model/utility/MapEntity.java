package model.utility;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by Antoine on 03/08/2015.
 */
public abstract class MapEntity implements Parcelable{


    public abstract int getId();

    public abstract LatLng getLatLng();

    public abstract String getTitle();

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

    }
}
