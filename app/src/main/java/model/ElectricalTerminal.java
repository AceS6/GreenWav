package model;

import android.location.Location;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.android.gms.maps.model.LatLng;

import model.utility.MapEntity;

/**
 * Copyright 2014 Antoine Sauray
 * ElectricalTerminal is a electric car borne class
 *
 * @author Antoine Sauray & Alexis Robin
 * @version 0.2
 */
public class ElectricalTerminal extends MapEntity implements Comparable<ElectricalTerminal>, Parcelable {


    // ----------- ATTRIBUTES

    public static final Parcelable.Creator CREATOR =
            new Parcelable.Creator() {
                public ElectricalTerminal createFromParcel(Parcel in) {
                    return new ElectricalTerminal(in);
                }

                public ElectricalTerminal[] newArray(int size) {
                    return new ElectricalTerminal[size];
                }
            };

    private int idBdd, reseau, nbrePdc;
    private String nom;
    private String adresse;
    private String nomPorteur;
    private String typeChargeur;
    private String typeConnecteur;
    private String observations;
    private LatLng latLng;
    private Location location;


    // ----------- CONSTRUTORS

    public ElectricalTerminal(int id, String nom, String adresse, LatLng latLng, String nomPorteur, String typeChargeur, int nbrePdc, String typeConnecteur, String observations, int reseau) {
        this.idBdd = id;
        this.latLng = latLng;
        this.nom = nom;
        this.adresse = adresse;
        this.nomPorteur = nomPorteur;
        this.typeChargeur = typeChargeur;
        this.nbrePdc = nbrePdc;
        this.typeConnecteur = typeConnecteur;
        this.observations = observations;
        this.reseau = reseau;
        this.location = new Location(nom);
        this.location.setLatitude(latLng.latitude);
        this.location.setLongitude(latLng.longitude);
    }

    // ----------- METHODS

    public ElectricalTerminal(Parcel in) {
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

    public int getNbrePdc() {
        return nbrePdc;
    }

    public String getNomPorteur() {
        return nomPorteur;
    }

    public String getTypeChargeur() {
        return typeChargeur;
    }

    public String getTypeConnecteur() {
        return typeConnecteur;
    }

    public String getObservations() {
        return observations;
    }

    public String toString() {
        return nom;
    }

    @Override
    public int compareTo(ElectricalTerminal another) {
        return nom.compareTo(another.nom);
    }

    @Override
    public int describeContents() {
        return 7;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(idBdd);
        dest.writeString(this.nom);
        dest.writeString(this.adresse);
        dest.writeParcelable(latLng, flags);
        dest.writeString(this.nomPorteur);
        dest.writeString(this.typeChargeur);
        dest.writeInt(this.nbrePdc);
        dest.writeString(this.typeConnecteur);
        dest.writeString(this.observations);
        dest.writeInt(reseau);
    }

    private void readFromParcel(Parcel in) {
        idBdd = in.readInt();
        nom = in.readString();
        adresse = in.readString();
        latLng = in.readParcelable(LatLng.class.getClassLoader());
        nomPorteur = in.readString();
        typeChargeur = in.readString();
        nbrePdc = in.readInt();
        typeConnecteur = in.readString();
        observations = in.readString();
        reseau = in.readInt();
    }

    @Override
    public int getId() {
        return idBdd;
    }
}
