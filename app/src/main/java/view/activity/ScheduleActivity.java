package view.activity;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

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
public class ScheduleActivity extends ActionBarActivity implements AdapterView.OnItemSelectedListener {

    // ----------------------------------- UI
    /**
     * Unique identifier for this activity
     */
    private static final String TAG = "SCHEDULE_ACTIVITY";
    /**
     * Toolbar Widget
     */
    private Toolbar toolbar;
    /**
     * The list of lines from the current network
     */
    private RecyclerView recyclerView;
    /**
     * The list of lines from the current network
     */
    private ScheduleAdapter adapter;

    private LinearLayoutManager layoutManager;

    private SwipeRefreshLayout swipeRefreshLayout;


    // ----------------------------------- Model
    /**
     * Allows to select the date of the schedules
     */
    private Spinner calendar;
    /**
     * The current stop
     */
    private Stop currentStop;

    // ----------------------------------- Constants
    /**
     * The current route
     */
    private model.Route currentRoute;

    private Network currrentNetwork;

    private Line currentLine;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule);
        Bundle extras = getIntent().getExtras();
        currrentNetwork = extras.getParcelable("NETWORK");
        currentLine = extras.getParcelable("BUS_LINE");
        currentRoute = extras.getParcelable("BUS_ROUTE");
        currentStop = extras.getParcelable("BUS_STOP");
        initInterface();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        toolbar.inflateMenu(R.menu.horaire_menu);
        return super.onCreateOptionsMenu(menu);
    }

    public void updateTimes() {
        adapter.removeAll();
        new GetSchedules(this, currrentNetwork, currentStop.getIdAppartient(), calendar.getSelectedItemPosition()+1, adapter, swipeRefreshLayout).execute();
    }

    private void initInterface() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(this.getResources().getString(R.string.activity_schedule));
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);

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
    }



    public void cardClick(View v){

    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        this.updateTimes();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public void onBackPressed() {
        this.finish();
    }
}
