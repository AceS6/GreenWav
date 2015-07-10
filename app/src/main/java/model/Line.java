package model;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;


import com.google.android.gms.maps.model.BitmapDescriptor;

import java.util.ArrayList;
import java.util.HashMap;


/**
 * Ligne is a bus line class
 * It has stops and belongs to a certain company
 * Copyright 2015 Antoine Sauray
 *
 * @author Antoine Sauray & Alexis Robin
 * @version 0.2
 */
public class Line extends BusComponent implements Comparable<BusComponent>, Parcelable {

    public final static int INVISIBLE = 0, SOCIAL = 1, MODERE = 2, NIGHT = 3, BOAT = 4, TRAM=5;

    // ----------------------------------- Model
    private int idBdd, reseau, favoris, state;
    private boolean favorite, download;
    private String numero, description, color;
    private BitmapDescriptor markerColor;

    // -- new stuff for Jambo implementation, like a banana
    private ArrayList<Route> routes;

    // ----------- CONSTRUCTORS
    public Line(int idBdd, String numero, String direction1, String direction2, String color, int reseau, int favoris, int state) {
        this.idBdd = idBdd;
        this.numero = numero;
        this.description = direction1+" - "+direction2;
        this.color = color;
        this.reseau = reseau;
        this.state = state;
        Log.d("Nouvelle Ligne", this.toString());
        this.setFavorite(favoris);
    }

    public Line(int idBdd, String numero, String direction1, String color, int reseau, int favoris, int state) {
        this.idBdd = idBdd;
        this.numero = numero;
        description = direction1;
        this.color = color;
        this.reseau = reseau;
        this.state = state;
        this.setFavorite(favoris);
    }

    // ========> NEW CONSTRUCTOR FOR JAMBO
    public Line(int idBdd, String numero, String description, String color, int reseau, int favoris, int state, int download) {
        this.idBdd = idBdd;
        this.numero = numero;
        this.description = description;
        this.color = color;
        this.reseau = reseau;
        this.state = state;
        Log.d("Nouvelle Ligne", this.toString());
        routes = new ArrayList<Route>();
        this.setFavorite(favoris);
        this.setDownload(download);
    }

    // ---------- METHODS
    // ----- ACCESSORS

    public Line(Parcel in) {
        readFromParcel(in);
    }

    public int getIdBdd() {
        return idBdd;
    }

    public void setIdBdd(int idBdd) {
        this.idBdd = idBdd;
    }

    public String getDescription() {
        return description;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public int getReseau() {
        return reseau;
    }

    public boolean getFavorite() {
        return this.favorite;
    }

    public void setFavorite(int favorisInt) {
        if (favorisInt == 0) {
            this.favorite = false;
        } else {
            this.favorite = true;
        }
    }

    public boolean getDownload() {
        return this.download;
    }

    public void setDownload(int downloadInt) {
        if (downloadInt == 0) {
            this.download = false;
        } else {
            this.download = true;
        }
    }

    public int getState() {
        return state;
    }

    // ----- OTHER METHODS

    public BitmapDescriptor getMarkerColor() {
        return markerColor;
    }

    public String getEtatString() {

        String ret = null;

        switch (state) {
            case INVISIBLE:
                ret = "Invisible";
                break;
            case SOCIAL:
                ret = "Social";
                break;
            case MODERE:
                ret = "Moderee";
                break;
        }
        return ret;
    }

    public String toString() {
        return ("Ligne " + this.numero);
    }

    @Override
    public int compareTo(BusComponent anotherBC) {
        // TODO Auto-generated method stub
        int ret = -1;

        Line another = (Line) anotherBC;

        if (this.favorite && !another.favorite) {
            ret = -1;
        } else if (another.favorite && !this.favorite) {
            ret = 1;
        } else {
            String thisNum = new String(numero);
            String anotherNum = new String(another.numero);

            try {
                Integer.parseInt(thisNum);
            } catch (NumberFormatException e) {
                thisNum = getOnlyNumerics(thisNum);
            }

            try {
                Integer.parseInt(anotherNum);
            } catch (NumberFormatException e) {
                anotherNum = getOnlyNumerics(anotherNum);
            }


            if (!thisNum.equals(numero) && !anotherNum.equals(another.numero)) {
                // On va devoir comparer deux lignes contenant une lettre
                try {
                    if (Integer.parseInt(thisNum) > Integer.parseInt(anotherNum)) {
                        ret = 1;
                    } else if (Integer.parseInt(thisNum) == Integer.parseInt(anotherNum)) {
                        if (!anotherNum.equals(another.numero)) {
                            ret = -1;
                        } else {
                            ret = 0;
                        }

                    } else if (Integer.parseInt(thisNum) < Integer.parseInt(anotherNum)) {
                        ret = -1;
                    }
                } catch (NumberFormatException e) {

                }
            } else if (!anotherNum.equals(another.numero)) {
                // arret superieur a l'autre car il ne possede pas de lettre
                ret = -1;
            } else if (!thisNum.equals(numero)) {
                ret = 1;
            } else {
                // Deux lignes sans lettres
                try {
                    if (Integer.parseInt(thisNum) > Integer.parseInt(anotherNum)) {
                        ret = 1;
                    } else if (Integer.parseInt(thisNum) == Integer.parseInt(anotherNum)) {
                        if (!anotherNum.equals(another.numero)) {
                            ret = -1;
                        } else {
                            ret = 0;
                        }

                    } else if (Integer.parseInt(thisNum) < Integer.parseInt(anotherNum)) {
                        ret = -1;
                    }
                } catch (NumberFormatException e) {

                }
            }

        }

        return ret;
    }

    private String getOnlyNumerics(String str) {

        StringBuffer strBuff = new StringBuffer();
        char c;

        for (int i = 0; i < str.length(); i++) {
            c = str.charAt(i);

            if (Character.isDigit(c)) {
                strBuff.append(c);
            }
        }
        return strBuff.toString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(idBdd);
        dest.writeString(numero);
        dest.writeString(color);
        dest.writeInt(reseau);
        dest.writeInt(state);

        Bundle extras = new Bundle();
        extras.putSerializable("ROUTE", routes);
        dest.writeBundle(extras);
    }

    private void readFromParcel(Parcel in) {
        idBdd = in.readInt();
        numero = in.readString();
        color = in.readString();
        reseau = in.readInt();
        state = in.readInt();
        Bundle extras = in.readBundle(Route.class.getClassLoader());
        routes = (ArrayList<Route>) extras.getSerializable("ROUTE");
    }

    public ArrayList<Route> getRoutes() {
        return routes;
    }

    public void setRoutes(ArrayList<Route> routes) {
        this.routes = routes;
    }

    public static final Parcelable.Creator CREATOR =
            new Parcelable.Creator() {
                public Line createFromParcel(Parcel in) {
                    return new Line(in);
                }

                public Line[] newArray(int size) {
                    return new Line[size];
                }
            };

}
