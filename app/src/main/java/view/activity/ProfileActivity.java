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

import java.util.Iterator;

import model.Station;
import model.User;
import model.db.external.didier.GetStations;
import model.db.internal.JamboDAO;
import view.custom.adapter.BikeAdapter;

/**
 * Provides a list of Stop available for selection to the user
 * @see model.Stop
 * @author Antoine Sauray
 * @version 2.0
 */
public class ProfileActivity extends AppCompatActivity {

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

    private Station returningStation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        Bundle extras = this.getIntent().getExtras();
        User user = extras.getParcelable("USER");
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(user.getName());
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
