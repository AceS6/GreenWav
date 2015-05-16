package view.custom.adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.greenwav.greenwav.R;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import model.BusComponent;
import model.Line;
import model.Network;
import model.Route;
import model.db.external.didier.GetLineSchedules;
import model.db.internal.JamboDAO;
import model.utility.NetworkUtil;

/**
 * Custom adapter for Line
 * @see model.Line
 * @see android.widget.ArrayAdapter
 * @author Antoine Sauray
 * @version 1.0
 */
 public class BusComponentAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private List<BusComponent> mDataset;
    private Activity activity;
    private Network network;

    private static final String TAG="LINE_ADATER";
    private static final int LINE=1, ROUTE=2;

    private LineHolder currentLineSelected;
    private int currentLineSelectedIndex;
    private boolean routesDisplayed;
    private int firstRouteDisplayedIndex, nbRoutesDisplayed;

    // Provide a suitable constructor (depends on the kind of dataset)
    public BusComponentAdapter(Activity activity, List<BusComponent> myDataset, Network network) {
        this.activity = activity;
        mDataset = myDataset;
        this.network = network;
        routesDisplayed = false;
        currentLineSelected = null;
        currentLineSelectedIndex = 0;
        firstRouteDisplayedIndex = 0;
        nbRoutesDisplayed = 0;
    }

    @Override
    public int getItemViewType(int position) {
        // Just as an example, return 0 or 2 depending on position
        // Note that unlike in ListView adapters, types don't have to be contiguous
        if(mDataset.get(position) instanceof  Line){
            return BusComponentAdapter.LINE;
        }
        else if(mDataset.get(position) instanceof  Route){
            return BusComponentAdapter.ROUTE;
        }
        else{
            return 0;
        }
    }

    public int getSpanSize(int position){
        switch (getItemViewType(position)){
            case BusComponentAdapter.LINE:
                if(!routesDisplayed){
                    return 1;
                }
                else{
                    Log.d(nbRoutesDisplayed + "", "size returned");
                    return nbRoutesDisplayed;
                }
            case BusComponentAdapter.ROUTE:
                return 1;
            default:
                return 1;
        }
    }

    // Create new views (invoked by the layout manager)
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View v = null;
        RecyclerView.ViewHolder vh = null;
        switch (viewType) {
            case BusComponentAdapter.LINE:
                v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_line, parent, false);
                // set the view's size, margins, paddings and layout parameters
                vh = new LineHolder(activity, (RelativeLayout)v);
                break;
            case BusComponentAdapter.ROUTE:
                v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_route, parent, false);
                // set the view's size, margins, paddings and layout parameters
                vh = new RouteHolder(activity, (LinearLayout)v);
                break;
        }

        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        switch(getItemViewType(position)){
            case BusComponentAdapter.LINE:
                Line l = (Line) mDataset.get(position);
                LineHolder lineHolder = (LineHolder) holder;
                lineHolder.setItem(l);
                lineHolder.number.setText(l.getNumero());
                lineHolder.number.setTextColor(Color.WHITE);
                lineHolder.number.setBackgroundColor((Color.parseColor(l.getColor())));
                if(lineHolder.lineItem.getFavorite()){
                    lineHolder.favorite.setVisibility(View.VISIBLE);
                    if(lineHolder.lineItem.getFavorite()){
                        lineHolder.favorite.setImageResource(R.drawable.ic_action_important);
                    }
                }
                else{
                    lineHolder.favorite.setVisibility(View.GONE);
                }
                if(lineHolder.lineItem.getDownload()){
                    lineHolder.downloaded.setVisibility(View.VISIBLE);
                }
                switch(l.getState()){
                    case Line.MODERE:
                        ((LineHolder) holder).status.setImageResource(R.drawable.ic_background_bus_line);
                        break;
                    case Line.NIGHT:
                        ((LineHolder) holder).status.setImageResource(R.drawable.ic_background_night_line);
                        break;
                    case Line.BOAT:
                        ((LineHolder) holder).status.setImageResource(R.drawable.ic_background_boat_line);
                        break;
                    case Line.TRAM:
                        ((LineHolder) holder).status.setImageResource(R.drawable.ic_background_tram_line);
                        break;
                    default:
                        ((LineHolder) holder).status.setImageResource(R.drawable.ic_background_bus_line);
                        break;
                }
                break;
            case BusComponentAdapter.ROUTE:
                Route r = (Route) mDataset.get(position);
                RouteHolder routeHolder = (RouteHolder) holder;
                routeHolder.setItem(r);
                routeHolder.name.setText(r.toString());
                break;
        }
    }

    public void add(Line item) {
            int size = mDataset.size();
            int i=0;
            Iterator<BusComponent> it = mDataset.iterator();
            BusComponent next=null;
            while(it.hasNext()){
                next = it.next();
                if(item.compareTo(next)==-1){
                    break;
                }
                i++;
            }
            mDataset.add(i, item);
            notifyItemInserted(i);
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public class LineHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        // each data item is just a string in this case
        TextView number;
        ImageButton favorite, downloaded;
        ImageView status;
        RelativeLayout lyt_main;
        Line lineItem;
        Activity activity;
        int index;
        DisplayRoutes displayRoutes;
        GetLineSchedules getLinesSchedule;

        public LineHolder(Activity activity, RelativeLayout lyt_main) {
            super(lyt_main);
            this.lyt_main = lyt_main;
            lyt_main.setOnClickListener(this);
            this.activity = activity;
            number = (TextView) lyt_main.findViewById(R.id.numero);

            displayRoutes = new DisplayRoutes();
            getLinesSchedule = new GetLineSchedules(activity, network, lineItem, downloaded);

            favorite = (ImageButton) lyt_main.findViewById(R.id.favorite);
            downloaded = (ImageButton) lyt_main.findViewById(R.id.downloaded);
            status = (ImageView) lyt_main.findViewById(R.id.status);

            favorite.setOnClickListener(this);
            downloaded.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {

            if(v.getId() == R.id.lyt_main) {

                if(currentLineSelected != null) {
                    if(!currentLineSelected.lineItem.getFavorite()) {
                        currentLineSelected.favorite.setVisibility(View.GONE);
                    }
                    currentLineSelected.downloaded.setVisibility(View.GONE);
                }
                favorite.setVisibility(View.VISIBLE);

                if(lineItem.getFavorite()){
                    favorite.setImageResource(R.drawable.ic_action_important);
                }
                if(!lineItem.getDownload()){
                    downloaded.setImageResource(R.drawable.ic_schedule_cloud);
                }
                downloaded.setVisibility(View.VISIBLE);

                if (routesDisplayed && displayRoutes.getStatus() != AsyncTask.Status.RUNNING) {
                    routesDisplayed = false;
                    int indexMax = firstRouteDisplayedIndex + nbRoutesDisplayed;

                    for (int i = firstRouteDisplayedIndex; i < indexMax; i++) {
                        Route r = (Route) mDataset.get(firstRouteDisplayedIndex);
                        mDataset.remove(r);
                        nbRoutesDisplayed--;
                    }

                    notifyItemRangeRemoved(firstRouteDisplayedIndex, indexMax - firstRouteDisplayedIndex);
                    firstRouteDisplayedIndex = 0;

                    if (this != currentLineSelected && displayRoutes.getStatus() != AsyncTask.Status.RUNNING) {
                            displayRoutes = new DisplayRoutes();
                            displayRoutes.execute();
                    } else {
                        notifyDataSetChanged();
                    }
                }
                else if(!routesDisplayed){
                    routesDisplayed = true;
                    displayRoutes = new DisplayRoutes();
                    displayRoutes.execute();
                }
            }
            else if (v.getId() == R.id.favorite){
                if(lineItem.getFavorite()){
                    favorite.setImageResource(R.drawable.ic_action_not_important);
                    lineItem.setFavorite(0);
                    JamboDAO dao = new JamboDAO(activity);
                    dao.open();
                    dao.setLigneNotFavoris(lineItem.getIdBdd());
                    dao.close();
                }
                else{
                    favorite.setImageResource(R.drawable.ic_action_important);
                    lineItem.setFavorite(1);
                    JamboDAO dao = new JamboDAO(activity);
                    dao.open();
                    dao.setLigneFavoris(lineItem.getIdBdd());
                    dao.close();
                }
            }
            else if (v.getId() == R.id.downloaded){

                if(getLinesSchedule.getStatus() != AsyncTask.Status.RUNNING){
                    if(NetworkUtil.isConnected(activity)) {
                        getLinesSchedule = new GetLineSchedules(activity, network, lineItem, downloaded);
                        getLinesSchedule.execute();
                    }
                    else{
                        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                        builder.setTitle(R.string.error);
                        builder.setMessage(R.string.download_failed);
                        builder.setNeutralButton(R.string.ok, null);
                        builder.show();
                    }
                }
            }

        }

        public Line getLineItem(){
            return lineItem;
        }

        private class DisplayRoutes extends AsyncTask<Void, Integer, Void>{

            @Override
            protected Void doInBackground(Void... params) {
                JamboDAO dao = new JamboDAO(activity);
                dao.open();
                Iterator<Route> it = dao.findRoutes(lineItem.getIdBdd()).values().iterator();
                dao.close();

                index = mDataset.indexOf(lineItem) +1;
                firstRouteDisplayedIndex = index;
                routesDisplayed = true;
                currentLineSelected = LineHolder.this;

                while (it.hasNext()) {
                    Route r = it.next();
                    mDataset.add(index, r);
                    nbRoutesDisplayed++;
                    publishProgress(index);
                    index++;
                }
                return null;
            }

            protected void onProgressUpdate(Integer... progress){
                notifyItemInserted(progress[0]);
            }
        }

        public void setItem(Line item) {
            lineItem = item;
        }
    }

    public class RouteHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        // each data item is just a string in this case
        TextView name;
        LinearLayout lyt_main;
        Route routeItem;
        Activity activity;

        public RouteHolder(Activity activity, LinearLayout lyt_main) {
            super(lyt_main);
            this.lyt_main = lyt_main;
            lyt_main.setOnClickListener(this);
            this.activity = activity;
            name = (TextView) lyt_main.findViewById(R.id.name);
        }

        @Override
        public void onClick(View v) {
                Intent returnIntent = new Intent();
            if(routeItem==null){
                Toast.makeText(activity, "Route null", Toast.LENGTH_SHORT).show();
            }
                returnIntent.putExtra("BUS_ROUTE", routeItem);
                returnIntent.putExtra("BUS_LINE", currentLineSelected.getLineItem());
                activity.setResult(activity.RESULT_OK, returnIntent);
                activity.finish();
        }

        public void setItem(Route item) {
            routeItem = item;
        }
    }
 }
