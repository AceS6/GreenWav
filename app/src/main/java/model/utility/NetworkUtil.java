package model.utility;

import android.content.Context;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

/**
 * Copyright 2015 Antoine Sauray
 * Provides useful tools for the network operations
 *
 * @author Antoine Sauray
 * @version 0.1
 */
public class NetworkUtil {

    public static int TYPE_WIFI = 1;
    public static int TYPE_MOBILE = 2;
    public static int TYPE_NOT_CONNECTED = 0;


    /**
     * Returns the status of the connexion
     * More detailed result than isConnected method
     *
     * @return int
     */
    public static int getConnectivityStatus(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (null != activeNetwork) {
            if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI)
                return TYPE_WIFI;

            if (activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE)
                return TYPE_MOBILE;
        }
        return TYPE_NOT_CONNECTED;
    }

    /**
     * Returns the status of the connexion
     *
     * @return boolean
     */
    public static boolean isConnected(Context c) {
        return getConnectivityStatus(c) != TYPE_NOT_CONNECTED;
    }

    /**
     * Returns the status of the connexion
     * More detailed result than getConnectivityStatus method
     *
     * @return String
     */
    public static String getConnectivityStatusString(Context context) {
        int conn = NetworkUtil.getConnectivityStatus(context);
        String status = null;
        if (conn == NetworkUtil.TYPE_WIFI) {
            status = "Wifi enabled";
        } else if (conn == NetworkUtil.TYPE_MOBILE) {
            status = "Mobile data enabled";
        } else if (conn == NetworkUtil.TYPE_NOT_CONNECTED) {
            status = "Not connected to Internet";
        }
        return status;
    }

    /**
     * Returns the best provider for the location manager
     *
     * @return String
     * @see android.location.LocationManager
     */
    public static String getBestProvider(LocationManager locationManager) {

        String ret = null;

        boolean isGPSEnabled = locationManager
                .isProviderEnabled(LocationManager.GPS_PROVIDER);

        // getting network status
        boolean isNetworkEnabled = locationManager
                .isProviderEnabled(LocationManager.NETWORK_PROVIDER);

        if (!isGPSEnabled && !isNetworkEnabled) {
            Log.d("No Provider", "Geolocalisaton unavailable");
        } else {
            if (isGPSEnabled && locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER) != null) {
                ret = LocationManager.GPS_PROVIDER;
            } else if (isNetworkEnabled && locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER) != null) {
                ret = LocationManager.NETWORK_PROVIDER;
            }
        }
        return ret;
    }
}

