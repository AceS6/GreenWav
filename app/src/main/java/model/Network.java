package model;

import android.location.Location;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.android.gms.maps.model.LatLng;

import java.util.HashMap;

/**
 * Copyright 2014 Antoine Sauray
 * Entreprise is a bus company class
 *
 * @author Antoine Sauray, Alexis Robin
 * @version 0.2.1
 */
public class Network implements Comparable<Network>, Parcelable {

    public static final Parcelable.Creator CREATOR =
            new Parcelable.Creator() {
                public Network createFromParcel(Parcel in) {
                    return new Network(in);
                }

                public Network[] newArray(int size) {
                    return new Network[size];
                }
            };
    protected String nom;
    protected HashMap<Integer, Line> lignes;
    protected HashMap<Integer, Stop> arrets;
    protected HashMap<Integer, BikeStation> stations;
    protected HashMap<Integer, ElectricalTerminal> bornes;
    private int idBdd;
    private int bus;
    private int velo;
    private int voiture;
    private int daybyday;
    private String image;
    private LatLng position;
    private Location location;

    private boolean isLocal, updateAvailable;

    public Network(int id, String nom, String img, int bus, int velo, int voiture) {
        this.nom = nom;
        this.idBdd = id;
        this.image = img;
        this.bus = bus;
        this.velo = velo;
        this.voiture = voiture;
        daybyday=0;
        lignes = new HashMap<Integer, Line>();
        arrets = new HashMap<Integer, Stop>();
        position = new LatLng(47.7481, -3.36455);
        isLocal = false;
        updateAvailable = false;
        location = new Location("network");
        location.setLatitude(position.latitude);
        location.setLongitude(position.longitude);
    }

    public Network(int id, double latitude, double longitude, String nom, String img, int bus, int velo, int voiture, int daybyday) {
        this.nom = nom;
        this.idBdd = id;
        this.image = img;
        this.bus = bus;
        this.velo = velo;
        this.voiture = voiture;
        this.daybyday = daybyday;
        lignes = new HashMap<Integer, Line>();
        arrets = new HashMap<Integer, Stop>();
        position = new LatLng(latitude, longitude);
        isLocal = false;
        updateAvailable = false;
        location = new Location("network");
        location.setLatitude(position.latitude);
        location.setLongitude(position.longitude);
    }

    public Network(String nom) {
        this.nom = nom;
        lignes = new HashMap<Integer, Line>();
        arrets = new HashMap<Integer, Stop>();
        isLocal = false;
        updateAvailable = false;
    }

    public Network(Parcel in) {
        readFromParcel(in);
        lignes = new HashMap<Integer, Line>();
        arrets = new HashMap<Integer, Stop>();
        location = new Location("network");
        location.setLatitude(position.latitude);
        location.setLongitude(position.longitude);
    }

    public HashMap<Integer, Line> getLignes() {
        return lignes;
    }

    public void setLignes(HashMap<Integer, Line> lignes) {
        this.lignes = lignes;
    }

    public HashMap<Integer, Stop> getArrets() {
        return arrets;
    }

    public void setArrets(HashMap<Integer, Stop> arrets) {
        this.arrets = arrets;
    }

    public HashMap<Integer, BikeStation> getStations() {
        return stations;
    }

    public void setStations(HashMap<Integer, BikeStation> stations) {
        this.stations = stations;
    }

    public HashMap<Integer, ElectricalTerminal> getBornes() {
        return bornes;
    }

    public void setBornes(HashMap<Integer, ElectricalTerminal> bornes) {
        this.bornes = bornes;
    }

    public String getImage() {
        return this.image;    // a modifier : Image est l'url qui donne vers l'image
    }

    public int getBus() {
        return bus;
    }

    public void setBus(int bus) {
        this.bus = bus;
    }

    public int getVelo() {
        return velo;
    }

    public void setVelo(int velo) {
        this.velo = velo;
    }

    public int getVoiture() {
        return voiture;
    }

    public void setVoiture(int voiture) {
        this.voiture = voiture;
    }

    public int getDaybyday() {
        return daybyday;
    }

    public void setDaybyday(int daybyday) {
        this.daybyday = daybyday;
    }


    public String toString() {
        return nom;
    }

    public int getIdBdd() {
        return idBdd;
    }

    public String getNom() {
        return this.nom;
    }

    public void setPosition(double latitude, double longitude) {
        this.position = new LatLng(latitude, longitude);
    }

    public LatLng getPosition() {
        return this.position;
    }

    public void setPosition(LatLng position) {
        this.position = position;
    }

    public Location getLocation() {
        return this.location;
    }

    public boolean getLocal(){
        return isLocal;
    }

    public void setLocal(boolean local){
        this.isLocal = local;
    }

    public boolean getUpdateAvailable(){
        return updateAvailable;
    }

    public void setUpdateAvailable(boolean updateAvailable){
        this.updateAvailable = updateAvailable;
    }

    @Override
    public int compareTo(Network another) {
        // TODO Auto-generated method stub
        int ret = -1;
        if (this.nom.charAt(0) > another.nom.charAt(0)) {
            ret = 1;
        } else if (this.nom.charAt(0) == another.nom.charAt(0)) {
            ret = 0;
        }
        return ret;
    }

    @Override
    public int describeContents() {
        return 8;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.nom);
        dest.writeInt(idBdd);
        dest.writeString(image);
        dest.writeInt(bus);
        dest.writeInt(velo);
        dest.writeInt(voiture);
        dest.writeInt(daybyday);
        dest.writeParcelable(position, flags);

        // Maps are not parcelled
        // Do a dao request if you need
    }

    private void readFromParcel(Parcel in) {
        nom = in.readString();
        idBdd = in.readInt();
        image = in.readString();
        bus = in.readInt();
        velo = in.readInt();
        voiture = in.readInt();
        daybyday = in.readInt();
        position = in.readParcelable(LatLng.class.getClassLoader());

    }
}
