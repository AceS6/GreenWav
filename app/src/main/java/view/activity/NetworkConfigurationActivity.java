package view.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;

import com.greenwav.greenwav.R;
import com.viewpagerindicator.TitlePageIndicator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import model.Line;
import model.Network;
import model.db.external.didier.DownloadNetwork;
import view.custom.adapter.GreenPagerAdapter;
import view.fragment.BikeFragment;
import view.fragment.BorneFragment;
import view.fragment.FinishConfiguration;
import view.fragment.LineFragment;

/**
 * Created by sauray on 14/03/15.
 */
public class NetworkConfigurationActivity extends ActionBarActivity{

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

    private ViewPager pager;

    private PagerAdapter mPagerAdapter;

    private Network currentNetwork;

    private List<Fragment> fragments;

    private HashMap<Integer, Line> configuration;
    private boolean usesBike, usesCar;

    private int versionBus, versionVelo, versionVoiture;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.activity_network_configuration);
        currentNetwork = getIntent().getExtras().getParcelable("NETWORK");
        initInterface();
        new GetNetwork(this, currentNetwork).execute();
    }


    private void initInterface(){
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(this.getResources().getString(R.string.activity_line));
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);

        configuration = new HashMap<Integer, Line>();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
        }
        return super.onOptionsItemSelected(item);
    }

    public Toolbar getToolbar(){
        return toolbar;
    }

    public Network getCurrentNetwork(){
        return currentNetwork;
    }

    public HashMap<Integer, Line> getConfiguration(){
        return configuration;
    }

    public void setUsesCar(boolean b){
        this.usesCar = b;
    }

    public void setUsesBike(boolean b){
        this.usesBike = b;
    }

    private class GetNetwork extends AsyncTask<Void, String, Integer[]> implements ViewPager.OnPageChangeListener {

        private Context context;
        private Network network;

        private int versionBus, versionVelo, versionVoiture;

        public GetNetwork(Context context, Network network) {
            this.context = context;
            this.network = network;
        }

        @Override
        protected Integer[] doInBackground(Void... params) {
            // TODO Auto-generated method stub
            StringBuilder jsonResult = new StringBuilder();
            final String BASE_URL = "http://sauray.me/greenwav/gorilla_network.php?";

            HttpURLConnection conn = null;
            try {
                StringBuilder sb = new StringBuilder(BASE_URL);
                sb.append("reseau=" + network.getIdBdd());

                URL url = new URL(sb.toString());
                conn = (HttpURLConnection) url.openConnection();
                InputStreamReader in = new InputStreamReader(conn.getInputStream());

                BufferedReader jsonReader = new BufferedReader(in);
                String lineIn;
                while ((lineIn = jsonReader.readLine()) != null) {
                    jsonResult.append(lineIn);
                }

                JSONObject jsonObj = new JSONObject(jsonResult.toString());

                JSONArray jsonMainNode = jsonObj.optJSONArray("reseau");
                JSONObject jsonChildNode = jsonMainNode.getJSONObject(0);

                versionBus = jsonChildNode.optInt("bus");
                versionVelo = jsonChildNode.optInt("velo");
                versionVoiture = jsonChildNode.optInt("voiture");

                network.setBus(versionBus);
                network.setVelo(versionVelo);
                network.setVoiture(versionVoiture);

            } catch (MalformedURLException e) {

            } catch (JSONException e) {

            } catch (IOException e) {
            } finally {
                if (conn != null) {
                    conn.disconnect();
                }
            }
            return new Integer[]{versionBus, versionVelo, versionVoiture};

        }

        @Override
        protected void onPostExecute(Integer[] result) {
            super.onPostExecute(result);
            boolean busOk = false, veloOk = false, voitureOk = false;

            // Création de la liste de Fragments que fera défiler le PagerAdapter
            fragments = new ArrayList<Fragment>();

            if (result[0] != 0) {
                fragments.add(Fragment.instantiate(NetworkConfigurationActivity.this, LineFragment.class.getName()));
            }

            if (result[1] != 0) {
                fragments.add(Fragment.instantiate(NetworkConfigurationActivity.this, BikeFragment.class.getName()));
            }

            if (result[2] != 0) {
                fragments.add(Fragment.instantiate(NetworkConfigurationActivity.this, BorneFragment.class.getName()));
            }

            fragments.add(Fragment.instantiate(NetworkConfigurationActivity.this, FinishConfiguration.class.getName()));
            // Création de l'adapter qui s'occupera de l'affichage de la liste de
            // Fragments
            mPagerAdapter = new GreenPagerAdapter(NetworkConfigurationActivity.this.getSupportFragmentManager(), fragments);

            pager = (ViewPager) NetworkConfigurationActivity.this.findViewById(R.id.viewpager);
            // Affectation de l'adapter au ViewPager
            pager.setAdapter(mPagerAdapter);


            //Bind the title indicator to the adapter
            TitlePageIndicator titleIndicator = (TitlePageIndicator) findViewById(R.id.titles);
            titleIndicator.setViewPager(pager);
            titleIndicator.setOnPageChangeListener(this);
        }

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            if(position == mPagerAdapter.getCount()-1) {
                AlertDialog.Builder builder = new AlertDialog.Builder(NetworkConfigurationActivity.this);
                builder.setTitle("Configuration");
                builder.setMessage("La configuration est terminée. Voulez-vous poursuivre ?");
                builder.setPositiveButton("Poursuivre", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        HashMap<Integer, Line> lines = null;
                        for (Fragment f : fragments) {
                            if (f instanceof LineFragment) {
                                lines = configuration;
                            }
                        }
                        int currentOrientation = getResources().getConfiguration().orientation;
                        if (currentOrientation == Configuration.ORIENTATION_PORTRAIT) {
                            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                        } else {
                            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                        };
                        Log.d(usesCar+"", "usesCar");
                        Log.d(usesBike+"", "usesBike");
                        new DownloadNetwork(NetworkConfigurationActivity.this, currentNetwork, lines, usesBike, usesCar).execute();
                    }
                });
                builder.setNegativeButton("Annuler", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        pager.setCurrentItem(0, true);
                    }
                });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }

        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    }
}
