package view.activity;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.greenwav.greenwav.R;

/**
 * Informations on the application and the Greenwav' project
 * @author Antoine Sauray
 * @version 1.0
 */
public class AboutActivity extends ActionBarActivity {

    // ----------------------------------- UI
    /**
     * Unique identifier for this activity
     */
    private static final String TAG = "ABOUT_ACTIVITY";

    // ----------------------------------- Constants
    /**
     * Toolbar widget
     */
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        initInterface();
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
     * Initializes the visual aspect of the activity
     */
    private void initInterface() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(this.getResources().getString(R.string.activity_network));
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);
        //forgetme.setPaintFlags(forgetme.getPaintFlags() |   Paint.UNDERLINE_TEXT_FLAG);
        //fin.setText("Wav'on ainsi que ses developpeurs ne peuvent en aucun cas être tenu responsable de la non exactitude du contenu de l'application ainsi que de la non exhaustivité des informations.");
    }

}
