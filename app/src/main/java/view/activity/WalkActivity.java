package view.activity;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;

import com.greenwav.greenwav.R;

import model.Walk;
import view.custom.adapter.WalkAdapter;

/**
 * Provides a list of Stop available for selection to the user
 * @see model.Stop
 * @author Antoine Sauray
 * @version 2.0
 */
public class WalkActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    // ----------------------------------- Model
    /**
     * Unique identifier for this activity
     */
    private static final String TAG = "STOP_ACTIVITY";
    /**
     * The current route
     */
    private model.Network currentNetwork;

    // ----------------------------------- UI
    /**
     * The current location of the user
     */
    private Location currentLocation;
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
    private WalkAdapter adapter;

    private RecyclerView.LayoutManager layoutManager;

    private Walk returningWalk;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_walk);
        Bundle extras = this.getIntent().getExtras();
        currentNetwork = extras.getParcelable("NETWORK");
        currentLocation = extras.getParcelable("LOCATION");
        initInterface();
    }

    private void initInterface() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.activity_walk);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        recyclerView = (RecyclerView) findViewById(R.id.list);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        //recyclerView.setHasFixedSize(true);

        // use a linear layout manager
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        // specify an adapter (see also next example)
        adapter = new WalkAdapter(this);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        returningWalk = (Walk) parent.getItemAtPosition(position);
        Intent returnIntent = new Intent();
        returnIntent.putExtra("WALK", returningWalk);
        setResult(RESULT_OK, returnIntent);
        finish();
    }

    public void buttonClick(View v) {

    }
}
