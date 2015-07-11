package view.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.greenwav.greenwav.R;

import model.Line;
import model.Stop;

/**
 * Displays bus schedules
 * @see Line
 * @see Stop
 * @see model.Schedule
 * @author Antoine Sauray
 * @version 1.0
 */
public class ElectricalCarFragment extends Fragment{

    // ----------------------------------- UI
    /**
     * Unique identifier for this activity
     */
    private static final String TAG = "SCHEDULE_ACTIVITY";


    // ----------------------------------- Model

    private View root;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_electrical_car, container, false);
        Bundle extras = getActivity().getIntent().getExtras();
        initInterface();
        return root;
    }

    public void updateTimes() {
        //adapter.removeAll();
        //new GetSchedules(this.getActivity(), currrentNetwork, currentStop.getIdAppartient(), calendar.getSelectedItemPosition()+1, adapter, swipeRefreshLayout).execute();
    }

    private void initInterface() {
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
