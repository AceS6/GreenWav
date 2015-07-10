package view.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;

import com.greenwav.greenwav.R;

import java.util.Iterator;

import model.Stop;
import model.db.internal.JamboDAO;
import model.db.internal.async.GetLocalStops;
import view.custom.adapter.StopAdapter;

/**
 * Provides a list of Stop available for selection to the user
 * @see model.Stop
 * @author Antoine Sauray
 * @version 2.0
 */
public class StopActivity extends ActionBarActivity implements AdapterView.OnItemClickListener, Runnable {

    // ----------------------------------- Model
    /**
     * Unique identifier for this activity
     */
    private static final String TAG = "STOP_ACTIVITY";
    /**
     * The current route
     */
    private model.Line currentLine;
    /**
     * The current route
     */
    private model.Route currentRoute;
    /**
     * The stop which will be returned to the precedent activity
     */
    private model.Stop returningStop;

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
    private StopAdapter adapter;

    private RecyclerView.LayoutManager layoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stop);
        Bundle extras = this.getIntent().getExtras();
        currentLine = extras.getParcelable("BUS_LINE");
        currentRoute = extras.getParcelable("BUS_ROUTE");
        currentLocation = extras.getParcelable("LOCATION");
        initInterface();
    }

    private void initInterface() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(getResources().getString(R.string.activity_stop));
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);

        recyclerView = (RecyclerView) findViewById(R.id.list);

        if(currentLocation == null){
            findViewById(R.id.nearest).setVisibility(View.INVISIBLE);
        }

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        recyclerView.setHasFixedSize(true);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        // use a linear layout manager
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        if (currentRoute != null) {
            findViewById(R.id.pasDeLigne).setVisibility(View.INVISIBLE);

            TextView nb = (TextView) findViewById(R.id.numero);
            nb.setText(" " + currentLine.getNumero() + " ");
            nb.setTextColor(Color.WHITE);
            nb.setBackgroundColor(Color.parseColor(currentLine.getColor()));
        }
        // specify an adapter (see also next example)
        //adapter = new StopAdapter(this);
        recyclerView.setAdapter(adapter);
        //new GetLocalStops(this, currentRoute, adapter).execute();
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
    public void onResume() {
        super.onResume();
        // affichage de la ligne en haut
        findViewById(R.id.nearest).setVisibility(View.VISIBLE);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        returningStop = (model.Stop) parent.getItemAtPosition(position);
        Intent returnIntent = new Intent();
        returnIntent.putExtra("BUS_STOP", returningStop);
        setResult(RESULT_OK, returnIntent);
        finish();
    }

    public void buttonClick(View v) {
        switch (v.getId()) {
            case R.id.nearest:
                Thread t = new Thread(this);
                t.start();
                try {
                    t.join();
                    Intent returnIntent = new Intent();
                    returnIntent.putExtra("BUS_STOP", returningStop);
                    setResult(RESULT_OK, returnIntent);
                    finish();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                break;
        }
    }

    @Override
    public void run() {
        JamboDAO dao = new JamboDAO(StopActivity.this);
        dao.open();
        Iterator<Stop> it = dao.findAssociateArrets(currentRoute, "ASC").iterator();
        dao.close();
        float distance = 10000000;
        model.Stop nearest = null;
        while (it.hasNext()) {
            model.Stop nouvelStop = it.next();
            if (currentLocation.distanceTo(nouvelStop.getLocation()) < distance) {
                distance = currentLocation.distanceTo(nouvelStop.getLocation());
                nearest = nouvelStop;
            }
        }
        returningStop = nearest;

    }
}
