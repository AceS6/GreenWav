package view.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.Toast;

import com.greenwav.greenwav.R;

import model.Line;
import model.Network;
import model.Stop;
import model.db.internal.BusActivityCallBack;
import model.db.internal.async.GetLocalStops;
import view.activity.BusActivity;
import view.custom.adapter.StopAdapter;
import view.custom.callback.StopCallBack;

/**
 * Created by sauray on 14/03/15.
 */
public class StopFragment extends Fragment{

    // ----------------------------------- UI
    /**
     * The list of lines from the current network
     */
    private RecyclerView recyclerView;
    /**
     * The list of lines from the current network
     */
    private StopAdapter adapter;

    private LinearLayoutManager layoutManager;

    private SwipeRefreshLayout swipeRefreshLayout;

    private Line currentLine;

    private BusActivityCallBack callback;


    // ----------------------------------- Model

    // ----------------------------------- Constants
    /**
     * The current network
     */
    private Network currentNetwork;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // Indicate that this fragment would like to influence the set of actions in the action bar.
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_stop, container, false);

        Bundle bundle = getActivity().getIntent().getExtras();
        currentNetwork = bundle.getParcelable("NETWORK");
        callback = (BusActivity)getActivity();

        recyclerView = (RecyclerView) root.findViewById(R.id.list);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        recyclerView.setHasFixedSize(true);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        // use a linear layout manager
        layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);

        swipeRefreshLayout = (SwipeRefreshLayout) root.findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setColorSchemeResources(R.color.accent);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Refresh items
                refreshItems();
            }
        });
        // use a linear layout manager
        layoutManager = new LinearLayoutManager(this.getActivity());
        recyclerView.setLayoutManager(layoutManager);
        // specify an adapter (see also next example)
        adapter = new StopAdapter(this.getActivity(), callback);

        recyclerView.setAdapter(adapter);

        return root;
    }

    public void selectAll(){
        int childCount = recyclerView.getChildCount();
        for(int i=0;i<childCount;i++){
            View v = recyclerView.getChildAt(i);
            ((CheckBox)v.findViewById(R.id.checkBoxConfig)).setChecked(true);
        }
    }

    public void deselectAll(){
        int childCount = recyclerView.getChildCount();
        for(int i=0;i<childCount;i++){
            View v = recyclerView.getChildAt(i);
            ((CheckBox)v.findViewById(R.id.checkbox)).setChecked(false);
        }
    }

    private void refreshItems() {

    }

    public void hideViews() {
        //toolbar.animate().translationY(-toolbar.getHeight()).setInterpolator(new AccelerateInterpolator(2));
    }

    public void showViews() {
        //toolbar.animate().translationY(0).setInterpolator(new DecelerateInterpolator(2));
    }

    @Override
    public String toString(){
        return "Bus";
    }

    public void lineSelected(Line l){
        // Called from activity
        new GetLocalStops(this.getActivity(), l, adapter).execute();
    }
}
