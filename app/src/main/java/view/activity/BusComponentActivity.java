package view.activity;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.greenwav.greenwav.R;

import java.util.ArrayList;

import model.Network;
import model.db.internal.async.GetLocalLines;
import view.custom.adapter.BusComponentAdapter;
import view.custom.decoration.DividerItemDecoration;

/**
 * Provides a list of Line available for selection to the user
 * @see model.Line
 * @author Antoine Sauray
 * @version 2.0
 */
public class BusComponentActivity extends ActionBarActivity{

    // ----------------------------------- UI
    /**
     * Unique identifier for this activity
     */
    private static final String TAG = "LINE_ACTIVITY";
    /**
     * Unique identifier for this activity
     */
    private static final int REQUESTCODE = 2;
    /**
     * Toolbar widget
     */
    private Toolbar toolbar;
    /**
     * The list of lines from the current network
     */
    private RecyclerView recyclerView;
    /**
     * The list of lines from the current network
     */
    private BusComponentAdapter adapter;

    private LinearLayoutManager layoutManager;


    // ----------------------------------- Model

    // ----------------------------------- Constants
    /**
     * The current network
     */
    private Network currentNetwork;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bus_component);
        Bundle extras = this.getIntent().getExtras();
        currentNetwork = extras.getParcelable("NETWORK");
        initInterface();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        toolbar.inflateMenu(R.menu.line_menu);
        return super.onCreateOptionsMenu(menu);
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
     * Initializes the graphical interface of the fragment.
     */
    private void initInterface() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(this.getResources().getString(R.string.activity_line));
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);
        recyclerView = (RecyclerView) findViewById(R.id.list);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        recyclerView.setHasFixedSize(true);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        // use a linear layout manager
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        // specify an adapter (see also next example)
        adapter = new BusComponentAdapter(this, new ArrayList<model.BusComponent>(), currentNetwork);
        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL_LIST));

        recyclerView.setAdapter(adapter);

        //registerForContextMenu(list);
        new GetLocalLines(this, currentNetwork, adapter).execute();
    }

    @Override
    public String toString() {
        return "LINE";
    }

}
