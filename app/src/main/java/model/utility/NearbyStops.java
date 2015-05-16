package model.utility;

import android.app.ProgressDialog;
import android.content.Intent;
import android.location.Location;
import android.os.AsyncTask;

import com.greenwav.greenwav.R;

import model.Line;
import model.Stop;
import view.activity.HomeActivity;

/**
 * Copyright 2014 Antoine Sauray
 * Returns the nearest stop from the user
 *
 * @author Antoine Sauray
 * @version 0.1
 */
public class NearbyStops extends AsyncTask<Void, String, Stop> {


    // ----------------------------------- UI
    /**
     * A progress dialog
     */
    private ProgressDialog pd;
    /**
     * The current activity
     * Serves as context
     */
    private HomeActivity homeActivity;

    // ----------------------------------- Model
    /**
     * The current line
     */
    private Line currentLine;
    /**
     * The current location
     */
    private Location currentLocation;

    public NearbyStops(HomeActivity homeActivity, Line currentLine, Location currentLocation) {
        this.homeActivity = homeActivity;
        this.currentLine = currentLine;
        this.currentLocation = currentLocation;
        pd = new ProgressDialog(homeActivity);
        pd.setTitle(homeActivity.getResources().getString(R.string.loading));
        pd.setMessage(homeActivity.getResources().getString(R.string.calculating_nearest_stop));
        pd.show();
    }

    @Override
    protected Stop doInBackground(Void... params) {
        /*
        Iterator<Stop> it = currentRoute.getStop().values().iterator();
        float distance = 10000000;
        Stop nearest = null;
        while (it.hasNext()) {
            Stop nouvelStop = it.next();
            if (currentLocation.distanceTo(nouvelStop.getLocation()) < distance) {
                distance = currentLocation.distanceTo(nouvelStop.getLocation());
                nearest = nouvelStop;
            }
        }
        */
        return null;
    }

    @Override
    protected void onProgressUpdate(String... progress) {

    }

    @Override
    protected void onPostExecute(Stop result) {
        super.onPostExecute(result);
        pd.dismiss();
        Intent returnIntent = new Intent();
        returnIntent.putExtra("BUS_STOP", result);
        homeActivity.setResult(homeActivity.RESULT_OK, returnIntent);
        homeActivity.finish();
    }
}
