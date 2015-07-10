package view.custom.adapter;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.greenwav.greenwav.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import model.Stop;
import model.db.internal.BusActivityCallBack;
import view.custom.callback.StopCallBack;

/**
 * Custom adapter for Line
 * @see model.Line
 * @see android.widget.ArrayAdapter
 * @author Antoine Sauray
 * @version 1.0
 */
public class StopAdapter extends RecyclerView.Adapter<StopAdapter.ViewHolder> {

    private List<Stop> mDataset;
    private Activity activity;
    private BusActivityCallBack callback;

    private static final String TAG="LINE_ADATER";

    // Provide a suitable constructor (depends on the kind of dataset)
    public StopAdapter(Activity activity, BusActivityCallBack callback) {
        this.activity = activity;
        mDataset = new ArrayList<Stop>();
        this.callback = callback;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public StopAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_stop, parent, false);
        // set the view's size, margins, paddings and layout parameters
        ViewHolder vh = new ViewHolder(activity, (LinearLayout) v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        holder.setItem(mDataset.get(position));
        Stop s = mDataset.get(position);
        holder.name.setText(s.toString());
    }

    public void add(Stop item) {
        int position = mDataset.size();
        mDataset.add(position, item);
        Collections.sort(mDataset);
        mDataset.indexOf(item);
        notifyItemInserted(position);
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        // each data item is just a string in this case
        TextView name;
        Stop stopItem;
        Activity activity;

        public ViewHolder(Activity activity, LinearLayout lyt_main) {
            super(lyt_main);
            lyt_main.setOnClickListener(this);
            this.activity = activity;
            name = (TextView) lyt_main.findViewById(R.id.nom);
        }

        @Override
        public void onClick(View v) {
            callback.stopSelected(stopItem);
        }

        public void setItem(Stop item) {
            stopItem = item;
        }
    }
}
