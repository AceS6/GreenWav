package view.custom.adapter;

import android.annotation.TargetApi;
import android.app.Activity;
import android.graphics.Color;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.greenwav.greenwav.R;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import model.Line;
import model.db.internal.BusActivityCallBack;
import view.activity.BusActivity;
import view.custom.callback.LineCallBack;

/**
 * Custom adapter for Line
 * @see model.Line
 * @see android.widget.ArrayAdapter
 * @author Antoine Sauray
 * @version 1.0
 */

public class LineAdapter extends RecyclerView.Adapter<LineAdapter.ViewHolder> {

    private List<Line> mDataset;
    private Activity activity;
    private BusActivityCallBack callback;

    private static final String TAG="LINE_ADAPTER";

    // Provide a suitable constructor (depends on the kind of dataset)
    public LineAdapter(Activity activity, BusActivityCallBack callBack) {
        this.activity = activity;
        this.callback = callBack;
        mDataset = new ArrayList<Line>();
    }

    // Create new views (invoked by the layout manager)
    @Override
    public LineAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_line, parent, false);
        // set the view's size, margins, paddings and layout parameters
        ViewHolder vh = new ViewHolder(activity, (LinearLayout) v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        holder.setItem(mDataset.get(position));
        Line l = mDataset.get(position);
        holder.number.setText(l.getNumero());
        holder.number.setTextColor(Color.parseColor(l.getColor()));
        holder.direction.setText(l.getDescription()
        );
    }

    public void add(Line item) {
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

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        // each data item is just a string in this case
        TextView number, direction;
        Line lineItem;
        Activity activity;

        public ViewHolder(Activity activity, LinearLayout lyt_main) {
            super(lyt_main);
            lyt_main.setOnClickListener(this);
            this.activity = activity;
            number = (TextView) lyt_main.findViewById(R.id.number);
            direction = (TextView) lyt_main.findViewById(R.id.direction);
        }

        @Override
        public void onClick(View v) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                v.setTransitionName("lineItem");
            }
            callback.lineSelected(lineItem);
        }

        public void setItem(Line item) {
            lineItem = item;
        }
    }
}
