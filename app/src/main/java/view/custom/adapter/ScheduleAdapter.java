package view.custom.adapter;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.greenwav.greenwav.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import model.Schedule;

/**
 * Custom adapter for Line
 * @see model.Line
 * @see android.widget.ArrayAdapter
 * @author Antoine Sauray
 * @version 1.0
 */
public class ScheduleAdapter extends RecyclerView.Adapter<ScheduleAdapter.ViewHolder> {

    private List<Schedule> mDataset;
    /**
     * First parameter is the position in the dataset
     * The value returned is the number of schedules to display
     */
    private int hoursAdded;
    private int color;

    private static final String TAG="LINE_ADATER";

    // Provide a suitable constructor (depends on the kind of dataset)
    public ScheduleAdapter(String color) {
        mDataset = new ArrayList<Schedule>();
        hoursAdded = 0;
        this.color = Color.parseColor(color);

        // Solution pour les lignes avec aucune horaire sur une heure
        // -> tenir un map des entiers d'heure en assignant gauche ou droite apres les avoir rencontré une fois
        // Très lourd pour très peu de fréquence ... Ratio ?
    }

    // Create new views (invoked by the layout manager)
    @Override
    public ScheduleAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_schedule, parent, false);
        // set the view's size, margins, paddings and layout parameters
        ViewHolder vh = new ViewHolder((RelativeLayout) v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        Log.d(""+position, "BINDVIEW");
        holder.setItem(mDataset.get(position));
        Schedule s = mDataset.get(position);

            if ((s.hour & 1) == 1) {
                holder.time_left.setText(s.toString());
                holder.time_right.setText("");
            }
            else {
                holder.time_right.setText(s.toString());
                holder.time_left.setText("");
            }
        holder.line.setBackgroundColor(color);
    }

    public void add(Schedule item) {
        int position = mDataset.size();
        mDataset.add(position, item);
        notifyItemInserted(position);
        Collections.sort(mDataset);
        notifyItemRangeChanged(0, mDataset.size());
    }

    public List<Schedule> getDataSet(){
        return mDataset;
    }

    public void removeAll() {
        mDataset.clear();
        notifyDataSetChanged();
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        // each data item is just a string in this case
        TextView time_left, time_right;
        Schedule scheduleItem;
        View line;

        public ViewHolder(RelativeLayout lyt_main) {
            super(lyt_main);
            time_left = (TextView) lyt_main.findViewById(R.id.time_left);
            time_right = (TextView) lyt_main.findViewById(R.id.time_right);

            line = lyt_main.findViewById(R.id.line);
        }

        public void setItem(Schedule item) {
            scheduleItem = item;
        }
    }
}