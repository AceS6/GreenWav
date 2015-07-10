package view.custom.adapter;

import android.app.Activity;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.greenwav.greenwav.R;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import model.Line;
import model.Route;
import view.activity.NetworkConfigurationActivity;

/**
 * Created by sauray on 14/03/15.
 */
public class LineConfigurationAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<Line> mDataset;
    private HashMap<Integer, Line> localLines;
    private NetworkConfigurationActivity activity;

    private static final String TAG = "LINE_ADATER";


    // Provide a suitable constructor (depends on the kind of dataset)
    public LineConfigurationAdapter(NetworkConfigurationActivity activity, List<Line> myDataset, HashMap<Integer, Line> localLines) {
        this.activity = activity;
        mDataset = myDataset;
        this.localLines = localLines;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View v = null;
        RecyclerView.ViewHolder vh = null;
        v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_line_configuration, parent, false);
        // set the view's size, margins, paddings and layout parameters
        vh = new LineHolder(activity, (RelativeLayout) v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        Line l = (Line) mDataset.get(position);
        LineHolder lineHolder = (LineHolder) holder;
        lineHolder.setLineItem(l);

        if(activity.getConfiguration().get(l.getIdBdd())!=null){
            lineHolder.checkBox.setChecked(true);
        }
        else{
            lineHolder.checkBox.setChecked(false);
        }

        lineHolder.number.setText(l.getNumero());
        lineHolder.number.setTextColor(Color.WHITE);
        lineHolder.number.setBackgroundColor((Color.parseColor(l.getColor())));

        lineHolder.routes.removeAllViews();

        Iterator<Route> it = l.getRoutes().iterator();
        while(it.hasNext()){
            TextView r = new TextView(activity);
            r.setText(it.next().toString());
            lineHolder.routes.addView(r);
        }
    }

    public void add(Line item) {
        int size = mDataset.size();
        int i=0;
        Iterator<Line> it = mDataset.iterator();
        Line next=null;
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

    public void removeAll(){
        int size = mDataset.size();
        mDataset.clear();
        notifyItemRangeRemoved(0, size);
    }

    public HashMap<Integer, Line> getConfiguration(){
        return activity.getConfiguration();
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

    public class LineHolder extends RecyclerView.ViewHolder implements CompoundButton.OnCheckedChangeListener {
        // each data item is just a string in this case
        TextView number;
        RelativeLayout lyt_main;
        LinearLayout routes;
        Line lineItem;
        Activity activity;
        CheckBox checkBox;

        public LineHolder(Activity activity, RelativeLayout lyt_main) {
            super(lyt_main);
            this.lyt_main = lyt_main;
            this.activity = activity;
            number = (TextView) lyt_main.findViewById(R.id.numero);

            routes = (LinearLayout) lyt_main.findViewById(R.id.routes);

            checkBox = (CheckBox) lyt_main.findViewById(R.id.checkBoxConfig);
            checkBox.setOnCheckedChangeListener(this);
        }

        public void setLineItem(Line l) {
            this.lineItem = l;
        }

        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

            if(isChecked){
                getConfiguration().put(lineItem.getIdBdd(), lineItem);
            }
            else{
                getConfiguration().remove(lineItem.getIdBdd());
            }
        }
    }
}
