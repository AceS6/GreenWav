package view.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;

import com.greenwav.greenwav.R;

import java.util.ArrayList;

import model.Line;
import model.Network;
import model.db.external.didier.GetLines;
import model.db.external.didier.GetNetwork;
import model.db.internal.JamboDAO;
import view.activity.NetworkConfigurationActivity;
import view.custom.adapter.LineConfigurationAdapter;

/**
 * Created by sauray on 14/03/15.
 */
public class LineFragment extends Fragment{

    // ----------------------------------- UI
    /**
     * The list of lines from the current network
     */
    private RecyclerView recyclerView;
    /**
     * The list of lines from the current network
     */
    private LineConfigurationAdapter adapter;

    private LinearLayoutManager layoutManager;

    private Toolbar toolbar;

    private GetLines async;

    private SwipeRefreshLayout swipeRefreshLayout;


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
        toolbar = ((NetworkConfigurationActivity)getActivity()).getToolbar();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_line_configuration, container, false);

        recyclerView = (RecyclerView) root.findViewById(R.id.list);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        recyclerView.setHasFixedSize(true);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        // use a linear layout manager
        layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);

        swipeRefreshLayout = (SwipeRefreshLayout) root.findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setColorSchemeResources(R.color.accent_light);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Refresh items
                refreshItems();
            }
        });
        // specify an adapter (see also next example)
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                JamboDAO dao = new JamboDAO(LineFragment.this.getActivity());
                dao.open();
                adapter = new LineConfigurationAdapter((NetworkConfigurationActivity) LineFragment.this.getActivity(), new ArrayList<Line>(), dao.findLignes(((NetworkConfigurationActivity)getActivity()).getCurrentNetwork().getIdBdd()));
                recyclerView.setAdapter(adapter);
                recyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
                    int mLastFirstVisibleItem = 0;

                    @Override
                    public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                    }

                    @Override
                    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                        super.onScrolled(recyclerView, dx, dy);
                        final int currentFirstVisibleItem = layoutManager.findFirstVisibleItemPosition();
                        if (currentFirstVisibleItem > this.mLastFirstVisibleItem) {
                            hideViews();
                        } else if (currentFirstVisibleItem < this.mLastFirstVisibleItem) {
                            showViews();
                        }

                        this.mLastFirstVisibleItem = currentFirstVisibleItem;
                    }
                });
                refreshItems();
            }
        });
        t.start();
        try {
            t.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }



        return root;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        async.cancel(true);
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
        int size = adapter.getItemCount();
        adapter.removeAll();
        adapter.notifyItemRangeRemoved(0, size);
        async = new GetLines(((NetworkConfigurationActivity)getActivity()).getCurrentNetwork(), adapter, swipeRefreshLayout);
        async.execute();
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

}
