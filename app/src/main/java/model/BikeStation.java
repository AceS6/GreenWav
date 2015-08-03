package model;

import android.location.Location;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.android.gms.maps.model.LatLng;

import java.util.Map;

import model.utility.MapEntity;

/**
 * Copyright 2014 Antoine Sauray
 * BikeStation is a bike station class
 *
 * @author Antoine Sauray & Alexis Robin
 * @version 0.2
 */
public class BikeStation extends MapEntity implements Comparable<BikeStation>, Parcelable {

    // ----------- ATTRIBUTES

    public static final Parcelable.Creator CREATOR =
            new Parcelable.Creator() {
                public BikeStation createFromParcel(Parcel in) {
                    return new BikeStation(in);
                }

                public BikeStation[] newArray(int size) {
                    return new BikeStation[size];
                }
            };

    private int idBdd, reseau, idExt;
    private String nom, adresse;
    private LatLng latLng;
    private Location location;


    // ----------- CONSTRUTORS

    public BikeStation(int id, String nom, String adresse, LatLng latLng, int reseau, int idExt) {
        this.idBdd = id;
        this.latLng = latLng;
        this.nom = nom;
        this.adresse = adresse;
        this.reseau = reseau;
        this.idExt = idExt;
        this.location = new Location(nom);
        this.location.setLatitude(latLng.latitude);
        this.location.setLongitude(latLng.longitude);
    }

    // ----------- METHODS

    public BikeStation(Parcel in) {
        readFromParcel(in);
    }

    public int getIdBdd() {
        return idBdd;
    }

    public LatLng getLatLng() {
        return latLng;
    }

    @Override
    public String getTitle() {
        return nom;
    }

    public Location getLocation() {
        return location;
    }

    public int getReseau() {
        return reseau;
    }

    public String getNom() {
        return nom;
    }

    public String getAdresse() {
        return adresse;
    }

    public int getIdExt() {
        return idExt;
    }

    public String toString() {
        return nom;
    }

    @Override
    public int compareTo(BikeStation another) {
        return nom.compareTo(another.nom);
    }

    @Override
    public int describeContents() {
        return 7;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.nom);
        dest.writeString(this.adresse);
        dest.writeInt(idBdd);
        dest.writeInt(reseau);
        dest.writeInt(idExt);
        dest.writeParcelable(latLng, flags);
    }

    private void readFromParcel(Parcel in) {
        nom = in.readString();
        adresse = in.readString();
        idBdd = in.readInt();
        reseau = in.readInt();
        idExt = in.readInt();
        latLng = in.readParcelable(LatLng.class.getClassLoader());
    }

    @Override
    public int getId() {
        return idBdd;
    }
}
