package view.custom.adapter;

import android.app.Activity;
import android.content.Intent;
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

import model.Walk;

/**
 * Custom adapter for Line
 * @see model.Line
 * @see android.widget.ArrayAdapter
 * @author Antoine Sauray
 * @version 1.0
 */
public class WalkAdapter extends RecyclerView.Adapter<WalkAdapter.ViewHolder> {

    private List<Walk> mDataset;
    private Activity activity;

    private static final String TAG="STATION_ADATER";

    // Provide a suitable constructor (depends on the kind of dataset)
    public WalkAdapter(Activity activity) {
        this.activity = activity;
        mDataset = new ArrayList<Walk>();
    }

    // Create new views (invoked by the layout manager)
    @Override
    public WalkAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_bike, parent, false);
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
        Walk w = mDataset.get(position);
        holder.name.setText(w.toString());
    }

    public void add(Walk item) {
        int position = mDataset.size();
        mDataset.add(position, item);
        notifyItemInserted(position);
        Collections.sort(mDataset);
        notifyItemRangeChanged(0, mDataset.size());
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        // each data item is just a string in this case
        TextView name;
        Walk item;
        Activity activity;

        public ViewHolder(Activity activity, LinearLayout lyt_main) {
            super(lyt_main);
            lyt_main.setOnClickListener(this);
            this.activity = activity;
            name = (TextView) lyt_main.findViewById(R.id.nom);
        }

        @Override
        public void onClick(View v) {
            Intent returnIntent = new Intent();
            returnIntent.putExtra("WALK", item);
            activity.setResult(activity.RESULT_OK, returnIntent);
            activity.finish();
        }

        public void setItem(Walk item) {
            this.item = item;
        }
    }
}
