package model;

import com.androidmapsextensions.Marker;

/**
 * Created by Antoine on 13/02/2015.
 * Gives information on a map entity
 */
public class Information {

    /**
     * Contants used for the category
     */
    public static final int BUS_STOP = 1, DESTINATION = 2, PLACE = 3, CONCERT = 4, BAR = 5;
    /**
     * The category of the entity
     */
    protected int category;
    /**
     * The marker
     */
    private Marker marker;

    /**
     * Constructor
     */
    protected Information(int category) {
        this.category = category;
        this.marker = null;
    }

    /**
     * Constructor
     */
    public Information(int category, Marker marker) {
        this.category = category;
        this.marker = marker;
    }

    public int getCategory() {
        return category;
    }

    public Marker getMarker() {
        return marker;
    }

    public void setMarker(Marker marker) {
        this.marker = marker;
    }

}
