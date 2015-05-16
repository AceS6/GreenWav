package model;

import android.location.Location;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.android.gms.maps.model.LatLng;

/**
 * Copyright 2014 Antoine Sauray
 * Arret is a bus stop class
 *
 * @author Antoine Sauray & Alexis Robin
 * @version 0.2
 */
public class Stop implements Comparable<Stop>, Parcelable {

    // ----------- ATTRIBUTES

    public static final Parcelable.Creator CREATOR =
            new Parcelable.Creator() {
                public Stop createFromParcel(Parcel in) {
                    return new Stop(in);
                }

                public Stop[] newArray(int size) {
                    return new Stop[size];
                }
            };
    private int idBdd, reseau;
    private String nom;
    private LatLng latLng;
    private Location location;
    private int distance;
    private boolean favorite, download;

    // NEW COZ OF GORILLA
    private int idAppartient, position;


    // ----------- CONSTRUTORS

    public Stop(int id, String nom, LatLng latLng, int reseau, int favoris) {
        this.idBdd = id;
        this.latLng = latLng;
        this.nom = nom;
        this.distance = -1;
        this.reseau = reseau;
        this.location = new Location(nom);
        this.location.setLatitude(latLng.latitude);
        this.location.setLongitude(latLng.longitude);
        this.setFavorite(favoris);
    }

    // New constructor for gorilla implementation
    public Stop(int id, String nom, LatLng latLng, int reseau, int favoris, int idAppartient, int position, int download) {
        this.idBdd = id;
        this.latLng = latLng;
        this.nom = nom;
        this.distance = -1;
        this.reseau = reseau;
        this.location = new Location(nom);
        this.location.setLatitude(latLng.latitude);
        this.location.setLongitude(latLng.longitude);
        this.setFavorite(favoris);
        this.setDownload(download);
        this.idAppartient = idAppartient;
        this.position = position;
    }

    // ----------- METHODS

    public Stop(Parcel in) {
        readFromParcel(in);
    }

    public int getIdBdd() {
        return idBdd;
    }

    public LatLng getLatLng() {
        return latLng;
    }

    //public ArrayList<String> getLignesDesservant(){return lignesDesservant;}

    public Location getLocation() {
        return location;
    }

    public int getReseau() {
        return reseau;
    }

    public String getNom() {
        return nom;
    }

    public int getFavorite() {
        int ret;

        if (favorite == false) {
            ret = 0;
        } else {
            ret = 1;
        }

        return ret;
    }

    public void setFavorite(int favorisInt) {
        if (favorisInt == 0) {
            this.favorite = false;
        } else {
            this.favorite = true;
        }
    }

    public int getDownload() {
        int ret;

        if (download == false) {
            ret = 0;
        } else {
            ret = 1;
        }

        return ret;
    }

    public void setDownload(int downloadInt) {
        if (downloadInt == 0) {
            this.download = false;
        } else {
            this.download = true;
        }
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public int getIdAppartient() {
        return idAppartient;
    }

    public void setIdAppartient(int idAppartient) {
        this.idAppartient = idAppartient;
    }

    public int getDistance() {
        return this.distance;
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }

    public String toString() {
        return nom;
    }

    @Override
    public int compareTo(Stop another) {
        // TODO Auto-generated method stub
        if(position < another.position){
            return -1;
        }
        else if (position == another.position){
            return 0;
        }
        return 1;
    }

    @Override
    public int describeContents() {
        return 7;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.nom);
        dest.writeInt(idBdd);
        dest.writeInt(distance);
        dest.writeInt(reseau);
        dest.writeInt(idAppartient);
        dest.writeByte((byte) (favorite ? 1 : 0));
        dest.writeParcelable(latLng, flags);
        dest.writeParcelable(location, flags);
    }

    private void readFromParcel(Parcel in) {
        nom = in.readString();
        idBdd = in.readInt();
        distance = in.readInt();
        reseau = in.readInt();
        idAppartient = in.readInt();
        favorite = in.readByte() != 0;
        latLng = in.readParcelable(LatLng.class.getClassLoader());
        location = in.readParcelable(Location.class.getClassLoader());
    }
}
