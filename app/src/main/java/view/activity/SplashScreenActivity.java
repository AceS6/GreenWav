package view.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.widget.ImageView;

import com.greenwav.greenwav.R;

import model.db.external.didier.GetNetworkVersion;
import model.db.internal.JamboDAO;
import model.utility.NetworkUtil;

/**
 * SplashScreen of the application
 * It shows up when the application is started to perform operations in the background.
 * @author Antoine Sauray
 * @version 1.0
 */
public class SplashScreenActivity extends Activity{

    // ----------------------------------- UI
    /**
     * Unique identifier for this activity
     */
    private static final String TAG = "SPLASHSCREEN_ACTIVITY";

    // ----------------------------------- Constants
    /**
     * Allows to check if this is the first time the user launches the application
     */
    private final String PREFS_FL = "firstLaunch";
    /**
     * The animation for the Greenwav' logo
     */
    private AnimationDrawable frameAnimation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splashscreen);
        ImageView img = (ImageView) findViewById(R.id.animation_vague);
        img.setBackgroundResource(R.drawable.greenwav_ico);

        Boolean isFirstRun = getSharedPreferences("PREFERENCE", MODE_PRIVATE).getBoolean("isfirstrun", true);
        if (isFirstRun) {
            getSharedPreferences("PREFERENCE", MODE_PRIVATE).edit().putBoolean("isfirstrun", false).commit();
            this.finish();
        }

        SharedPreferences preferences = getSharedPreferences("PREFERENCE", MODE_PRIVATE);
        if (NetworkUtil.isConnected(this)) {
            JamboDAO dao = new JamboDAO(this);
            dao.open();
            if (dao.findReseaux().size() != 0) {
                dao.close();
                new GetNetworkVersion(this).execute();
            } else {
                Intent home = new Intent(SplashScreenActivity.this, NetworkSelectionActivity.class);
                startActivity(home);
                this.finish();
            }
        }
        else {
            JamboDAO dao = new JamboDAO(this);
            dao.open();
            if (dao.findReseaux().size() != 0) {
                dao.close();
                new GetNetworkVersion(this).execute();
            } else {
                Intent home = new Intent(SplashScreenActivity.this, NetworkSelectionActivity.class);
                startActivity(home);
                this.finish();
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

}
