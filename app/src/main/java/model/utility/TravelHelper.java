package model.utility;

/**
 * Copyright 2015 Antoine Sauray
 * Provides usefuls tools for travel calculation
 *
 * @author Antoine Sauray
 * @version 0.1
 */
public class TravelHelper {


    public static int[] getTravelTime(int[] distancesTrajet) {
        int[] ret = new int[2];
        double coef = 1.4;

        int distanceBus = distancesTrajet[0];
        int distanceMarche = distancesTrajet[1];

        double t1 = (distanceBus * coef) / 11.1;
        double t2 = (distanceMarche * coef) / 1.38;

        ret[0] = (int) (t1);
        ret[1] = (int) (t2);

        return ret;
    }

    public static int getWalkingTime(int distance) {
        double coef = 1.4;
        return (int) ((distance * coef) / 1.38);
    }

    public static int getBusTime(int distance) {
        double coef = 1.4;
        return (int) ((distance * coef) / 11.1);
    }

}
