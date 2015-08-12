package view.activity;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.greenwav.greenwav.R;

import model.db.external.didier.GetNetwork;
import view.custom.adapter.NetworkAdapter;

/**
 * Provides a list of Network available for selection to the user
 * @see model.Network
 * @author Antoine Sauray
 * @version 1.0
 */
public class NetworkSelectionActivity extends ActionBarActivity {

    // ----------------------------------- UI

    private Toolbar toolbar;

    private RecyclerView view;
    /**
     * The list of networks from the current network
     */
    private RecyclerView recyclerView;
    /**
     * The list of networks from the current network
     */
    private NetworkAdapter adapter;

    private GridLayoutManager layoutManager;

    private SwipeRefreshLayout swipeRefreshLayout;


    // ----------------------------------- Constants
    /**
     * Unique identifier for this activity
     */
    private static final String TAG = "NETWORKSELECTION_ACTIVITY";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_network);
        initInterface();
        new GetNetwork(this, adapter).execute();
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Refresh items
                refreshItems();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //getActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.inflateMenu(R.menu.network_menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Initializes the graphical interface of the activity
     */
    private void initInterface() {
        toolbar = (Toolbar) findViewById(R.id.mapToolbar);
        toolbar.setTitle(this.getResources().getString(R.string.activity_network));
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);

        Spinner calendar = new Spinner(getSupportActionBar().getThemedContext());


        ArrayAdapter<CharSequence> adapterCalendar = ArrayAdapter.createFromResource(getSupportActionBar().getThemedContext(),
                R.array.calendar_items, R.layout.support_simple_spinner_dropdown_item);
        adapterCalendar.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        calendar.setAdapter(adapterCalendar);

        recyclerView = (RecyclerView) this.findViewById(R.id.list);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        recyclerView.setHasFixedSize(true);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        // use a linear layout manager
        layoutManager = new GridLayoutManager(this, 2);
        recyclerView.setLayoutManager(layoutManager);

        // specify an adapter (see also next example)
        adapter = new NetworkAdapter(this, layoutManager);

        layoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                return adapter.getItemSize(position);
            }
        });

        recyclerView.setAdapter(adapter);

        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setColorSchemeResources(R.color.accent);
    }

    private void refreshItems() {
        int size = adapter.getItemCount();
        adapter.removeAll();
        adapter.notifyItemRangeRemoved(0, size);
        new GetNetwork(this, adapter, swipeRefreshLayout).execute();
    }


}
