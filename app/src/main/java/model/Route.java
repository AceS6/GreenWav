package model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Alexis on 17/02/2015.
 */
public class Route extends BusComponent implements Parcelable{

    // ---------- ATTRIBUTES

    private int idBdd, ligne;
    private String nom;
    private ArrayList<Stop> stop;


    // ---------- CONSTRUCTOR

    public Route(int id, int ligne, String nom){
        this.idBdd = id;
        this.ligne = ligne;
        this.nom = nom;
    }

    public Route(Parcel in){
        readFromParcel(in);
    }


    // ---------- METHODS

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public int getIdBdd() {
        return idBdd;
    }

    public void setIdBdd(int idBdd) {
        this.idBdd = idBdd;
    }

    public int getLigne() {
        return ligne;
    }

    public void setLigne(int ligne) {
        this.ligne = ligne;
    }

    public ArrayList<Stop> getStop() {
        return stop;
    }

    public void setStop(ArrayList<Stop> stop) {
        this.stop = stop;
    }

    @Override
    public String toString(){
        return nom;
    }

    @Override
    public int describeContents() {
        return 4;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(idBdd);
        dest.writeInt(ligne);
        dest.writeString(nom);

        // Map not parcelled
        // Do a dao request if you need to
    }

    private void readFromParcel(Parcel in) {
        idBdd = in.readInt();
        ligne = in.readInt();
        nom = in.readString();
    }

    public static final Parcelable.Creator CREATOR =
            new Parcelable.Creator() {
                public Route createFromParcel(Parcel in) {
                    return new Route(in);
                }

                public Route[] newArray(int size) {
                    return new Route[size];
                }
            };

}
