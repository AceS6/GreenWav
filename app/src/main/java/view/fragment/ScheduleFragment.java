package view.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TextView;

import com.greenwav.greenwav.R;

import model.Line;
import model.Network;
import model.Stop;
import model.db.external.didier.GetSchedules;
import view.custom.adapter.ScheduleAdapter;

/**
 * Displays bus schedules
 * @see model.Line
 * @see model.Stop
 * @see model.Schedule
 * @author Antoine Sauray
 * @version 1.0
 */
public class ScheduleFragment extends Fragment{

    // ----------------------------------- UI
    /**
     * Unique identifier for this activity
     */
    private static final String TAG = "SCHEDULE_ACTIVITY";
    private Network currentNetwork;
    private Stop currentStop;

    private TableLayout tableLayout;
    private RelativeLayout quickViewLayout;


    // ----------------------------------- Model

    private View root;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_schedule, container, false);
        Bundle extras = getActivity().getIntent().getExtras();
        currentNetwork = extras.getParcelable("NETWORK");
        tableLayout = (TableLayout) root.findViewById(R.id.fullSchedules);
        initInterface();
        return root;
    }

    public void updateTimes(int stopIdAppartient, int calendar) {
        //adapter.removeAll();
        new GetSchedules(this.getActivity(), currentNetwork, stopIdAppartient, calendar, tableLayout, quickViewLayout).execute();
        //new GetSchedules(this.getActivity(), currentNetwork, currentStop.getIdAppartient(), calendar.getSelectedItemPosition()+1, adapter, swipeRefreshLayout).execute();
    }

    private void initInterface() {
        quickViewLayout = (RelativeLayout) root.findViewById(R.id.quickViewSchedule);
/*
        calendar = new Spinner(getSupportActionBar().getThemedContext());
        ArrayAdapter<CharSequence> set1Adapter = ArrayAdapter.createFromResource(getSupportActionBar().getThemedContext(),
                R.array.calendar_items, R.layout.template_spinner_item);
        calendar.setAdapter(set1Adapter);
        toolbar.addView(calendar, 0);

        calendar.setSelection(model.Schedule.getDayOfWeek()-1, true);
        calendar.setOnItemSelectedListener(this);

        recyclerView = (RecyclerView) this.findViewById(R.id.list);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        recyclerView.setHasFixedSize(true);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        // use a linear layout manager
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        // specify an adapter (see also next example)
        adapter = new ScheduleAdapter(currentLine.getColor());
        recyclerView.setAdapter(adapter);

        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setColorSchemeResources(R.color.accent);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Refresh items
                updateTimes();
            }
        });
        swipeRefreshLayout.setRefreshing(true);
        updateTimes();
        */
    }
}
