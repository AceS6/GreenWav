package view.custom.adapter;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.greenwav.greenwav.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import model.Network;
import model.db.external.didier.GetNetworkVersion;
import view.activity.NetworkConfigurationActivity;

/**
 * Custom adapter for Line
 * @see model.Line
 * @see android.widget.ArrayAdapter
 * @author Antoine Sauray
 * @version 1.0
 */
public class NetworkAdapter extends RecyclerView.Adapter<NetworkAdapter.ViewHolder> {

    private List<Network> mDataset;
    private Map<Integer, Integer> positionMap;
    /**
     * First parameter is the position in the dataset
     * The value returned is the number of schedules to display
     */
    private GridLayoutManager layoutManager;

    private Activity activity;

    private static final String TAG="LINE_ADATER";

    // Provide a suitable constructor (depends on the kind of dataset)
    public NetworkAdapter(Activity activity, GridLayoutManager layoutManager) {
        this.activity = activity;
        mDataset = new ArrayList<Network>();
        positionMap = new HashMap<Integer, Integer>();
        this.layoutManager = layoutManager;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public NetworkAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_network, parent, false);
        // set the view's size, margins, paddings and layout parameters
        ViewHolder vh = new ViewHolder(activity, (LinearLayout) v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        Log.d(""+position, "BINDVIEW");
        holder.setItem(mDataset.get(position));
        Network n = mDataset.get(position);

        holder.specification.removeAllViews();

        holder.name.setText(n.toString());
        if(n.getUpdateAvailable()){
            holder.updateAvailable.setText("Mise à jour");
        }

        if(!n.getLocal()){
            ImageView local = new ImageView(activity);
            local.setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_schedule_cloud));
            local.setAlpha(0.5f);
            holder.specification.addView(local);
        }
        else{
            ImageView local = new ImageView(activity);
            local.setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_schedule_downloaded));
            local.setAlpha(0.5f);
            holder.specification.addView(local);
        }

        if(n.getBus() != 0){
            ImageView bus = new ImageView(activity);
            bus.setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_menu_line_bus));
            bus.setAlpha(0.5f);
            holder.specification.addView(bus);
        }

        if(n.getVelo() != 0){
            ImageView bike = new ImageView(activity);
            bike.setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_menu_line_bike));
            bike.setAlpha(0.5f);
            holder.specification.addView(bike);
        }

        if(n.getVoiture() != 0){
            ImageView voiture = new ImageView(activity);
            voiture.setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_menu_line_car));
            voiture.setAlpha(0.5f);
            holder.specification.addView(voiture);
        }
    }

    public void add(Network item) {
        int position = mDataset.size();
        mDataset.add(position, item);
        positionMap.put(item.getIdBdd(), position);
        notifyItemInserted(position);
    }

    public void removeAll(){
        int size = mDataset.size();
        mDataset.clear();
        positionMap.clear();
        notifyItemRangeRemoved(0, size);
    }

    private void replace(Network network){
        int localPos = positionMap.get(network.getIdBdd());
        mDataset.set(localPos, network);
        notifyItemChanged(localPos);
    }

    public void checkUpdate(Network network){
        Integer pos = positionMap.get(network.getIdBdd());
        if(pos != null) {
            Network localNetwork = mDataset.get(pos);
            int busVersion = localNetwork.getBus();
            int veloVersion = localNetwork.getVelo();
            int voitureVersion = localNetwork.getVoiture();
            if ( (busVersion != 0 && busVersion < network.getBus())) {
                Log.d("update", "bus update");
                network.setUpdateAvailable(true);
                replace(network);
            }
            if (veloVersion != 0 && veloVersion < network.getVelo()) {
                Log.d("update", "velo update");
                network.setUpdateAvailable(true);
                replace(network);
            }
            if (voitureVersion != 0 && voitureVersion < network.getVoiture()) {
                Log.d("update", "car update");
                network.setUpdateAvailable(true);
                replace(network);
            }

            if((network.getBus() == 0) || network.getVelo() == 0 || network.getVoiture() == 0){
                replace(network);
            }
        }
    }

    public Integer getItemIndex(int key){
        return positionMap.get(key);
    }

    public int getItemSize(int position){
        return 1;
    }

    public List<Network> getDataSet(){
        return mDataset;
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        // each data item is just a string in this case
        TextView name, updateAvailable;
        LinearLayout specification;
        Network networkItem;
        Activity activity;

        public ViewHolder(Activity activity, LinearLayout lyt_main) {
            super(lyt_main);
            lyt_main.setOnClickListener(this);
            this.activity = activity;
            name = (TextView) lyt_main.findViewById(R.id.name);
            updateAvailable = (TextView) lyt_main.findViewById(R.id.updateAvailable);
            specification = (LinearLayout) lyt_main.findViewById(R.id.specifications);
        }

        public void setItem(Network item) {
            networkItem = item;
        }

        @Override
        public void onClick(View v) {
            if (networkItem.getLocal()) {
                new GetNetworkVersion(activity, networkItem).execute();
            } else {
                Intent mStartActivity = new Intent(activity, NetworkConfigurationActivity.class);
                mStartActivity.putExtra("NETWORK", networkItem);
                activity.startActivity(mStartActivity);
            }
        }
    }
}