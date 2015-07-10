package view.activity;

import android.annotation.TargetApi;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import com.greenwav.greenwav.R;

import model.Line;
import model.Network;
import model.Stop;
import model.db.internal.BusActivityCallBack;
import model.db.internal.JamboDAO;
import view.fragment.LineFragment;
import view.fragment.StopFragment;

/**
 * Informations on the application and the Greenwav' project
 * @author Antoine Sauray
 * @version 1.0
 */
public class BusActivity extends AppCompatActivity implements BusActivityCallBack{

    // ----------------------------------- UI
    /**
     * Unique identifier for this activity
     */
    private static final String TAG = "OPTION_ACTIVITY";

    // ----------------------------------- Constants
    /**
     * Toolbar widget
     */
    private Toolbar toolbar;

    private Network currentNetwork;

    private LineFragment line;
    private StopFragment stop;

    private ViewPager viewPager;
    private Line currentLine;

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bus);
        initInterface();

        setTitle(getString(R.string.bus));

        Bundle extras = this.getIntent().getExtras();
        currentNetwork = extras.getParcelable("NETWORK");
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setTabTextColors(Color.WHITE, Color.WHITE);
        viewPager = (ViewPager) findViewById(R.id.viewpager);

        viewPager.setAdapter(new SectionPagerAdapter(getSupportFragmentManager()));
        tabLayout.setupWithViewPager(viewPager);

        line = new LineFragment();
        stop = new StopFragment();
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
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void initInterface() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);
        //forgetme.setPaintFlags(forgetme.getPaintFlags() |   Paint.UNDERLINE_TEXT_FLAG);
        //fin.setText("Wav'on ainsi que ses developpeurs ne peuvent en aucun cas être tenu responsable de la non exactitude du contenu de l'application ainsi que de la non exhaustivité des informations.");
    }

    public void fabClick(View v) {

    }

    @Override
    public void lineSelected(Line l) {
        currentLine = l;
        stop.lineSelected(l);
        viewPager.setCurrentItem(1, true);
    }

    @Override
    public void stopSelected(Stop s) {
        Intent i = new Intent();
        JamboDAO dao = new JamboDAO(this);
        dao.open();
        currentLine.setRoutes(dao.findRoutes(currentLine.getIdBdd()));
        dao.close();
        i.putExtra("BUS_LINE", currentLine);
        i.putExtra("BUS_STOP", s);
        setResult(RESULT_OK, i);
        finish();
    }

    public class SectionPagerAdapter extends FragmentPagerAdapter {

        public SectionPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            Bundle b = new Bundle();
            b.putParcelable("NETWORK", currentNetwork);
            Fragment fragment = null;
            switch (position) {
                case 0:
                    return line;
                case 1:
                    return stop;
            }
            return fragment;
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "Ligne";
                case 1:
                    return "Arret";
                default:
                    return "";
            }
        }
    }
}
