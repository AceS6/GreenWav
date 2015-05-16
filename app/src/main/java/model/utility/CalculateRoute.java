package model.utility;

import android.content.Context;
import android.location.Location;
import android.os.AsyncTask;
import android.view.View;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Iterator;

import model.Line;
import model.Network;
import model.Stop;
import view.custom.adapter.BusComponentAdapter;

/**
 * Copyright 2014 Antoine Sauray
 * Calculate routes to a certain destination
 *
 * @author Antoine Sauray
 * @version 0.5
 */
public class CalculateRoute extends AsyncTask<Integer, Line, ArrayList<Line>> {


    // ----------------------------------- UI
    /**
     * The current context
     */
    private Context context;
    /**
     * The main layout
     */
    private RelativeLayout layout;
    /**
     * The list of suggestions which will be filled with the results found
     */
    private ListView list;
    /**
     * The adapter of the list of suggestions
     */
    private BusComponentAdapter listAdapter;

    // ----------------------------------- Model
    /**
     * The current location
     */
    private Location currentLocation;
    /**
     * The destination
     */
    private Location destination;
    /**
     * The lines which will be returned as a result
     */
    private ArrayList<Line> items;
    /**
     * The current network
     */
    private Network currentNetwork;

    public CalculateRoute(Context context, RelativeLayout layout, ListView list, Network currentNetwork, Location currentLocation, Location destination) {
        this.currentLocation = currentLocation;
        this.destination = destination;
        this.context = context;
        this.layout = layout;
        this.list = list;
        items = new ArrayList<Line>();
        //listAdapter = new LineAdapter(context, items);
        this.currentNetwork = currentNetwork;
        layout.setVisibility(View.VISIBLE);
    }

    @Override
    protected ArrayList<Line> doInBackground(Integer... params) {
        ArrayList<Line> ret = new ArrayList<Line>();
        if (currentLocation != null) {
            ArrayList<Stop> arretsProcheUtilisateur = null;
            ArrayList<Stop> arretsProcheDestination = null;

            arretsProcheUtilisateur = getStopsCloseToUser(params[0]);
            arretsProcheDestination = getStopsCloseToDestination(params[1]);
            ret = lookForLines(arretsProcheUtilisateur, arretsProcheDestination);

        }

        return ret;
    }

    @Override
    protected void onPostExecute(ArrayList<Line> result) {
        super.onPostExecute(result);
        //LineAdapter listAdapter = new LineAdapter(context, result);
        //list.setAdapter(listAdapter);
        list.invalidate();
        layout.setVisibility(View.INVISIBLE);
        if (result.size() == 0) {
            Toast.makeText(context, "Aucun trajet disponible", Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Returns the stops close to the user
     *
     * @return a list of Stop
     * @see model.Stop
     */
    private ArrayList<Stop> getStopsCloseToUser(int distance) {

        ArrayList<Stop> ret = new ArrayList<Stop>();

        Iterator<Stop> it = currentNetwork.getArrets().values().iterator();
        while (it.hasNext()) {
            Stop a = it.next();
            float distanceTo = currentLocation.distanceTo(a.getLocation());
            if (distanceTo < distance) {
                ret.add(a);
            }
        }
        return ret;
    }

    /**
     * Returns the stops close to the destination
     *
     * @return a list of Stop
     * @see model.Stop
     */
    private ArrayList<Stop> getStopsCloseToDestination(int distance) {

        ArrayList<Stop> ret = new ArrayList<Stop>();

        Iterator<Stop> it = currentNetwork.getArrets().values().iterator();
        while (it.hasNext()) {
            Stop a = it.next();
            float distanceTo = destination.distanceTo(a.getLocation());
            if (distanceTo < distance) {
                ret.add(a);
            }
        }
        return ret;
    }

    /**
     * Returns lines which are a valid for the chosen route
     *
     * @return a list of Line
     * @see model.Line
     */
    private ArrayList<Line> lookForLines(ArrayList<Stop> a1, ArrayList<Stop> a2) {
        ArrayList<Line> ret = new ArrayList<Line>();
        Iterator<Line> itLignes = currentNetwork.getLignes().values().iterator();

        while (itLignes.hasNext()) {
            Line l = itLignes.next();
            Iterator<Stop> a1It = a1.iterator();
            Iterator<Stop> a2It = a2.iterator();
            boolean continuer = true;
            while (a1It.hasNext() && a2It.hasNext() && continuer) {
                Stop a1Next = a1It.next();
                Stop a2Next = a2It.next();
                /*
                if (l.getStop().containsKey(a1Next.toString()) && l.getStop().containsKey(a2Next.toString()) && !ret.contains(l)) {
                    this.publishProgress(l);
                    ret.add(l);
                    Log.d(l.toString(), "Nouvelle ligne trouvee");
                    continuer = false;
                }
                */
            }

        }

        return ret;
    }

    @Override
    protected void onProgressUpdate(Line... progress) {
        items.add(progress[0]);
        listAdapter.notifyDataSetChanged();
        //list.setAdapter(listAdapter);
        list.invalidate();
        layout.setVisibility(View.INVISIBLE);
    }
}
