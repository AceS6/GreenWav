package view.activity;


import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TableLayout;

import com.androidmapsextensions.GoogleMap;
import com.androidmapsextensions.MapFragment;
import com.androidmapsextensions.Marker;
import com.androidmapsextensions.MarkerOptions;
import com.androidmapsextensions.OnMapReadyCallback;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.greenwav.greenwav.R;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import model.Borne;
import model.Event;
import model.Network;
import model.PlaceInformation;
import model.Route;
import model.Schedule;
import model.Station;
import model.Stop;
import model.User;
import model.db.external.didier.GetSchedules;
import model.db.external.didier.GetStationInformations;
import model.db.internal.JamboDAO;
import model.db.internal.async.DisplayBikeStations;
import view.custom.google.LatLngInterpolator;
import view.custom.google.MarkerAnimation;
import view.fragment.BikeFragment;
import view.fragment.BlankFragment;
import view.fragment.CarSharingFragment;
import view.fragment.ElectricalCarFragment;
import view.fragment.ScheduleFragment;
import view.fragment.WalkFragment;


/**
 * The main activity of the program
 * @author Antoine Sauray
 * @version 2.0
 */
public class HomeActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener, GoogleMap.OnInfoWindowClickListener, GoogleMap.OnMapLongClickListener, GoogleMap.OnMapClickListener, View.OnFocusChangeListener, NavigationView.OnNavigationItemSelectedListener {

    // ----------------------------------- UI

    /**
     * These variables are used to get result from other activities.
     *
     * @see
     * @see StopActivity
     */
    private static final int LINE_SELECTION = 1, STOP_SELECTION = 2, BIKE_SELECTION = 3, ELECTRICAL_SELECTION =4, CARSHARING_SELECTION=5, WALK_SELECTION=6;

    private static final String TAG = "HOME_ACTIVITY";
    /**
     * Displays the modes available for selection
     */
    private ListView mDrawerList;
    /**
     * Toolbar widget
     */
    private Toolbar toolbar;
    /**
     * The map
     */
    private GoogleMap googleMap;
    /**
     * The fragment which contains the map
     */
    private MapFragment mapFragment;
    /**
     * The user interface for the current mode selected
     */
    private View ui;
    /**
     * The card widget. It changes its visual aspecting depending on the selected marker's type.
     * It can show any content a view can show.
     */
    private SlidingUpPanelLayout slidingPanel;

    private NavigationView navigationView;
    /**
     * The main layout of the activity.
     */
    private RelativeLayout lyt_main;

    private FloatingActionButton floatingActionButton;

    // ----------------------------------- Model
    /**
     * Provides markers on a marker. The key is the marker title attribute
     */
    private HashMap<Integer, Marker> markers;
    /**
     * Contains the suggestions provided by the Google Search API
     *
     * @see model.db.external.google.AutoCompletion
     * @see model.db.external.google.GetDetails
     */
    private ArrayList<PlaceInformation> suggestions;
    /**
     * The current network used
     */
    private Network currentNetwork;
    /**
     * The current line used
     */
    private model.Line currentLine;
    /**
     * The current route used
     */
    private model.Route currentRoute;
    /**
     * The current stop used
     */
    private Stop currentStop;
    /**
     * The current bike station selected
     */
    private Station currentStation;
    /**
     * The current electrical borne
     */
    private Borne currentBorne;
    /**
     * The current marker selected. A marker is selected after the method onMarkerClick has been called
     * It becomes null when the user clicks somwhere on the map.
     */
    private Event currentEvent;

    private Marker currentMarker;

    private Route[] routes;

    // Informations on current state
    private boolean bottomSheetVisible;

    private RadioButton[] radioButtons;
    private Fragment fragment;

    private AddMarkers addMarkers;

    private int currentMode;
    private User currentUser;

    // ----------------------------------- Constants
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Bundle extras = this.getIntent().getExtras();
        currentNetwork = extras.getParcelable("NETWORK");
        currentUser = new User("Antoine");
        bottomSheetVisible = false;
        radioButtons = new RadioButton[2];

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        int pref_service = Integer.parseInt(sharedPref.getString("pref_service", "0"));
        currentMode = sharedPref.getInt("last_mode", R.id.bus_mode);
        sharedPref.edit().putInt("UI", pref_service).apply();

        if (currentNetwork != null) {
            initInterface(R.id.toolbar);
            markers = new HashMap<Integer, Marker>();
            suggestions = new ArrayList<PlaceInformation>();
        } else {
            Intent intent = new Intent(HomeActivity.this, SplashScreenActivity.class);
            startActivity(intent);
            this.finish();
        }
        navigationView = (NavigationView) findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(this);

        navigationView.getMenu().findItem(currentMode).setChecked(true);

        mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        mapFragment.getExtendedMapAsync(this);

        lyt_main = (RelativeLayout) this.findViewById(R.id.lyt_main);

        slidingPanel = (SlidingUpPanelLayout) findViewById(R.id.sliding_layout);
        slidingPanel.setAnchorPoint(0.6f);

        slidingPanel.setPanelSlideListener(new SlidingUpPanelLayout.PanelSlideListener() {
            @Override
            public void onPanelSlide(View view, float v) {

            }

            @Override
            public void onPanelCollapsed(View view) {
                initInterface(R.id.toolbar);
            }

            @Override
            public void onPanelExpanded(View view) {

            }

            @Override
            public void onPanelAnchored(View view) {
                if(toolbar.getId() != R.id.toolbarHidden) {
                    initInterface(R.id.toolbarHidden);
                }
            }

            @Override
            public void onPanelHidden(View view) {

            }
        });

        floatingActionButton = (FloatingActionButton) findViewById(R.id.fab);
        floatingActionButton.setBaselineAlignBottom(true);

        updateMode();
    }

    @Override
    public boolean onSearchRequested() {

        return super.onSearchRequested();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check which request we're responding to
        switch (requestCode) {
            case LINE_SELECTION:
                if (resultCode == RESULT_OK) {
                    currentLine = data.getParcelableExtra("BUS_LINE");
                    currentRoute = currentLine.getRoutes().get(0);
                    addMarkers = new AddMarkers();
                    addMarkers.execute(true);
                    // Remove UI if a new line has been selected

                }
                break;
            case STOP_SELECTION:
                if (resultCode == RESULT_OK) {
                    currentStop = data.getParcelableExtra("BUS_STOP");
                    Marker marker = markers.get(currentStop.getIdBdd());
                    googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentStop.getLatLng(), 14.0f));
                    if(marker != null) {
                        onMarkerClick(marker);
                        marker.showInfoWindow();
                    }
                }
                break;
            case BIKE_SELECTION:
                if (resultCode == RESULT_OK) {
                    currentStation = data.getParcelableExtra("STATION");
                    Marker marker = markers.get(currentStation.getIdBdd());
                    googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentStation.getLatLng(), 14.0f));
                    if(marker != null) {
                        onMarkerClick(marker);
                        marker.showInfoWindow();
                    }
                }
                break;
            case ELECTRICAL_SELECTION:
                if (resultCode == RESULT_OK) {
                    currentBorne = data.getParcelableExtra("BORNE");
                    Marker marker = markers.get(currentBorne.getIdBdd());
                    googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentBorne.getLatLng(), 14.0f));
                    if(marker != null) {
                        onMarkerClick(marker);
                        marker.showInfoWindow();
                    }
                }
                break;
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;
        googleMap.setOnMarkerClickListener(this);
        googleMap.setOnInfoWindowClickListener(this);
        googleMap.setOnMapLongClickListener(this);
        googleMap.setOnMapClickListener(this);
        googleMap.setMyLocationEnabled(true);
        googleMap.setOnInfoWindowClickListener(this);


        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(currentNetwork.getPosition())      // Sets the center of the map to Mountain View
                .zoom(14)                   // Sets the zoom
                        //.tilt(30)                   // Sets the tilt of the camera to 30 degrees
                .build();                   // Creates a CameraPosition from the builder
        googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        googleMap.getUiSettings().setMapToolbarEnabled(false);
        googleMap.getUiSettings().setMyLocationButtonEnabled(false);

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        int uiId = sharedPref.getInt("UI", -1);

        //new GetEvents(this, currentNetwork.getIdBdd(), googleMap, markers).execute();
        updateMap();
    }

    @Override
    public boolean onMarkerClick(Marker marker) {

        if(marker.getData() == null){
            return false;
        }

        slidingPanel.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);

        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.LOLLIPOP) {

            // get the center for the clipping circle
            int cx = floatingActionButton.getMeasuredWidth() / 2;
            int cy = floatingActionButton.getMeasuredHeight() / 2;

            // get the initial radius for the clipping circle
            int initialRadius = floatingActionButton.getWidth() / 2;

            // create the animation (the final radius is zero)
            Animator anim =
                    ViewAnimationUtils.createCircularReveal(floatingActionButton, cx, cy, initialRadius, 0);

            // make the view invisible when the animation is done
            anim.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    floatingActionButton.setVisibility(View.INVISIBLE);
                }
            });

            // start the animation
            anim.start();
        }
        else{

        }

        currentMarker = marker;
        String snippet = marker.getSnippet();


        Class c = marker.getData().getClass();
        if( c == Stop.class){
            Stop s = marker.getData();
            new GetSchedules(this, currentNetwork, s.getIdAppartient(), Schedule.getDayOfWeek(), (TableLayout)fragment.getView().findViewById(R.id.fullSchedules), fragment.getView()).execute();
        }
        else if (c == Station.class){
            new GetStationInformations(this, (Station)marker.getData(), currentNetwork, fragment.getView()).execute();
        }
        return false;
    }

    @Override
    public void onInfoWindowClick(Marker marker) {
        Class c = marker.getData().getClass();
        Intent intent = null;
        if(c == Stop.class){
            intent = new Intent(HomeActivity.this, ScheduleFragment.class);
            intent.putExtra("BUS_STOP", currentStop);
            intent.putExtra("BUS_ROUTE", currentRoute);
            intent.putExtra("BUS_LINE", currentLine);
            intent.putExtra("NETWORK", currentNetwork);
            startActivity(intent);
        }
        else if (c == Event.class){
            intent = new Intent(HomeActivity.this, EventActivity.class);
            intent.putExtra("EVENT", currentEvent);
            startActivity(intent);
        }

    }

    @Override
    public void onMapLongClick(LatLng latLng) {

    }

    @Override
    public void onMapClick(LatLng latLng) {
        /*
        if (currentMarker != null) {
            currentMarker = null;
            cardUnreveal();
        }
        */
        slidingPanel.setPanelState(SlidingUpPanelLayout.PanelState.HIDDEN);

        bottomSheetVisible = false;
        if(floatingActionButton.getVisibility() != View.VISIBLE){

            if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.LOLLIPOP) {
                // get the center for the clipping circle
                int cx = floatingActionButton.getMeasuredWidth() / 2;
                int cy = floatingActionButton.getMeasuredHeight() / 2;

                // get the final radius for the clipping circle
                int finalRadius = Math.max(floatingActionButton.getWidth(), floatingActionButton.getHeight()) / 2;

                // create the animator for this view (the start radius is zero)
                Animator anim =
                        ViewAnimationUtils.createCircularReveal(floatingActionButton, cx, cy, 0, finalRadius);

                // make the view visible and start the animation
                floatingActionButton.setVisibility(View.VISIBLE);
                anim.start();
            }
            else{

            }
        }

    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {

    }

    @Override
    public void onResume() {
        super.onResume();

        if (googleMap == null) {
            return;
        }

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        String pref_map_type = sharedPref.getString("pref_map_type", "Normale");
        int uiId = sharedPref.getInt("UI", -1);



        if (pref_map_type.equals("Normale")) {
            googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        } else if (pref_map_type.equals("Hybride")) {
            googleMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        } else {
            googleMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
        }

    }

    /**
     * Initializes the visual aspect of the activity
     * * @param id the id of the toolbar
     */
    private void initInterface(final int id) {


        if(id == R.id.toolbarHidden){
            toolbar = (Toolbar) findViewById(id);
            toolbar.setTitle(currentMarker.getTitle());
            Animation fadeIn = new AlphaAnimation(0, 1);
            fadeIn.setInterpolator(new DecelerateInterpolator()); //add this
            fadeIn.setDuration(300);
            fadeIn.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                    toolbar.setVisibility(View.VISIBLE);
                }

                @Override
                public void onAnimationEnd(Animation animation) {

                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }

            });
            toolbar.setAnimation(fadeIn);
            fadeIn.start();
            setSupportActionBar(toolbar);
            ActionBar actionBar = getSupportActionBar();
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        else{
            if(toolbar != null && toolbar.getId() == R.id.toolbarHidden){toolbar.setVisibility(View.INVISIBLE);}

            toolbar = (Toolbar) findViewById(id);
            setSupportActionBar(toolbar);
            ActionBar actionBar = getSupportActionBar();
            actionBar.setDisplayHomeAsUpEnabled(true);

            DrawerLayout drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
            ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this,drawerLayout,toolbar,R.string.openDrawer, R.string.closeDrawer){

                @Override
                public void onDrawerClosed(View drawerView) {
                    // Code here will be triggered once the drawer closes as we dont want anything to happen so we leave this blank
                    super.onDrawerClosed(drawerView);
                }

                @Override
                public void onDrawerOpened(View drawerView) {
                    // Code here will be triggered once the drawer open as we dont want anything to happen so we leave this blank

                    super.onDrawerOpened(drawerView);
                }
            };

            //Setting the actionbarToggle to drawer layout
            drawerLayout.setDrawerListener(actionBarDrawerToggle);

            //calling sync state is necessay or else your hamburger icon wont show up
            actionBarDrawerToggle.syncState();

        }

    }

    public void radioClick(View v) {

        if (addMarkers != null && addMarkers.getStatus() == AsyncTask.Status.RUNNING) {
            addMarkers.cancel(true);
        }
        addMarkers = new AddMarkers();
        addMarkers.execute();
    }

    public void toolbarClick(View v) {

        Intent i = new Intent(this, SearchActivity.class);

        if(v.getId() == R.id.speechRecognition){
            i.putExtra("SPEECH", true);
        }
        else{
            i.putExtra("SPEECH", false);
        }

        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.LOLLIPOP) {
            ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(HomeActivity.this,
                    findViewById(R.id.appBar), "search");
            HomeActivity.this.startActivity(i, options.toBundle());
        }
        else{
            startActivity(i);
        }


    }

    public void fabClick(View v){
        Intent intent = null;
        switch (currentMode){
            case R.id.bus_mode:
                intent = new Intent(HomeActivity.this, BusActivity.class);
                intent.putExtra("NETWORK", currentNetwork);
                if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.LOLLIPOP) {
                    ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(HomeActivity.this);
                    this.startActivityForResult(intent, LINE_SELECTION, options.toBundle()
                    );
                }
                else{
                    this.startActivityForResult(intent, LINE_SELECTION);
                }
                break;
            case R.id.bike_mode:
                intent = new Intent(HomeActivity.this, BikeActivity.class);
                intent.putExtra("NETWORK", currentNetwork);
                if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.LOLLIPOP) {
                    ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(HomeActivity.this);
                    this.startActivityForResult(intent, BIKE_SELECTION, options.toBundle()
                    );
                }
                else{
                    this.startActivityForResult(intent, BIKE_SELECTION);
                }
                break;
            case R.id.walk_mode:
                intent = new Intent(HomeActivity.this, WalkActivity.class);
                intent.putExtra("NETWORK", currentNetwork);
                if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.LOLLIPOP) {
                    ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(HomeActivity.this);
                    this.startActivityForResult(intent, WALK_SELECTION, options.toBundle()
                    );
                }
                else{
                    this.startActivityForResult(intent, WALK_SELECTION);
                }
                break;
            case R.id.electrical_car_mode:
                intent = new Intent(HomeActivity.this, ElectricalActivity.class);
                intent.putExtra("NETWORK", currentNetwork);
                if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.LOLLIPOP) {
                    ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(HomeActivity.this);
                    this.startActivityForResult(intent, ELECTRICAL_SELECTION, options.toBundle()
                    );
                }
                else{
                    this.startActivityForResult(intent, ELECTRICAL_SELECTION);
                }
                break;
            case R.id.car_sharing_mode:
                intent = new Intent(HomeActivity.this, CarSharingActivity.class);
                intent.putExtra("NETWORK", currentNetwork);
                if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.LOLLIPOP) {
                    ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(HomeActivity.this);
                    this.startActivityForResult(intent, CARSHARING_SELECTION, options.toBundle()
                    );
                }
                else{
                    this.startActivityForResult(intent, CARSHARING_SELECTION);
                }
                break;
        }
    }

    @Override
    public void onBackPressed() {
        if(slidingPanel.getPanelState() == SlidingUpPanelLayout.PanelState.EXPANDED){
            slidingPanel.setPanelState(SlidingUpPanelLayout.PanelState.ANCHORED);
        }
        else if(slidingPanel.getPanelState() == SlidingUpPanelLayout.PanelState.ANCHORED){
            slidingPanel.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
        }
        else if(slidingPanel.getPanelState() == SlidingUpPanelLayout.PanelState.COLLAPSED){
            slidingPanel.setPanelState(SlidingUpPanelLayout.PanelState.HIDDEN);
        }
        else{
            finish();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                slidingPanel.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
        }
        return super.onOptionsItemSelected(item);
    }

    public void drawerHeaderClick(View v){
        Intent intent = new Intent(HomeActivity.this, ProfileActivity.class);
        intent.putExtra("USER", currentUser);
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.LOLLIPOP) {
            ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(HomeActivity.this);
            startActivity(intent, options.toBundle());
        }
        else{
            startActivity(intent);
        }
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem menuItem) {
        ((DrawerLayout)findViewById(R.id.drawer_layout)).closeDrawer(GravityCompat.START);
        if(menuItem.getItemId() != currentMode){
            currentMode = menuItem.getItemId();
            menuItem.setChecked(true);
            SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
            sharedPref.edit().putInt("last_mode", currentMode).apply();

            slidingPanel.setPanelState(SlidingUpPanelLayout.PanelState.HIDDEN);
            if(floatingActionButton.getVisibility() != View.VISIBLE){
                floatingActionButton.setVisibility(View.VISIBLE);
            }

            googleMap.clear();
            markers.clear();
            updateMap();
            return updateMode();
        }
        return true;
    }

    private boolean updateMode(){
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();

        switch (currentMode){
            case R.id.bus_mode:
                fragment = new ScheduleFragment();
                if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.LOLLIPOP) {
                    floatingActionButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_directions_bus_white_48dp, getTheme()));
                }
                else{
                    floatingActionButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_directions_bus_white_48dp));
                }
                break;
            case R.id.bike_mode:
                fragment = new BikeFragment();
                if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.LOLLIPOP) {
                    floatingActionButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_directions_bike_white_48dp, getTheme()));
                }
                else{
                    floatingActionButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_directions_bike_white_48dp));
                }
                break;
            case R.id.walk_mode:
                fragment = new WalkFragment();
                if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.LOLLIPOP) {
                    floatingActionButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_directions_walk_white_48dp, getTheme()));
                }
                else{
                    floatingActionButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_directions_walk_white_48dp));
                }
                break;
            case R.id.electrical_car_mode:
                fragment = new ElectricalCarFragment();
                if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.LOLLIPOP) {
                    floatingActionButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_menu_line_car_w, getTheme()));
                }
                else{
                    floatingActionButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_menu_line_car_w));
                }
                break;
            case R.id.car_sharing_mode:
                fragment = new CarSharingFragment();
                if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.LOLLIPOP) {
                    floatingActionButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_directions_car_white_48dp, getTheme()));
                }
                else{
                    floatingActionButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_directions_car_white_48dp));
                }
                break;
            default:
                fragment = new BlankFragment();
                return false;
        }
        transaction.replace(R.id.slidingFragment, fragment);
        transaction.commit();
        return true;
    }

    private void updateMap(){
        switch(currentMode){
            case R.id.bus_mode:
                break;
            case R.id.bike_mode:
                new DisplayBikeStations(this, googleMap, markers, currentNetwork.getIdBdd()).execute();
                break;
        }

    }

    private class AddMarkers extends AsyncTask<Boolean, Stop, Boolean>{

        int markerId;
        ArrayList<Stop> stops;
        Marker bus;
        LatLngInterpolator.LinearFixed interpolator;
        LatLng initialPosition;

        @Override
        protected void onPreExecute(){
            googleMap.clear();
            stops = null;

            interpolator = new LatLngInterpolator.LinearFixed();
        }

        @Override
        protected Boolean doInBackground(Boolean... params) {

            if((params.length==0 || params[0]==false) && currentMarker != null){
                markerId = ((Stop)currentMarker.getData()).getIdBdd();
            }

            String orderby="ASC";
            if(radioButtons[1] != null && radioButtons[1].isChecked()){
                orderby="DESC";
            }

            markers.clear();
            JamboDAO dao = new JamboDAO(HomeActivity.this);
            dao.open();
            stops = dao.findAssociateArrets(currentRoute, orderby);
            currentRoute.setStop(stops);
            Iterator<Stop> it = stops.iterator();
            markers.clear();

            Stop previous = it.next();
            initialPosition = it.next().getLatLng();
            publishProgress(previous);

            for(Stop s : stops){
                publishProgress(s, previous);
                previous = s;
                try {
                    Thread.sleep(5);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            dao.close();
            return (params.length==1)&&params[0];
        }

        protected void onProgressUpdate(Stop...result){
            Marker m = googleMap.addMarker(new MarkerOptions()
                    .position(result[0].getLatLng())
                    .alpha(0.8f)
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE))
                    .title(result[0].toString()));
            m.setData(result[0]);
            markers.put(result[0].getIdBdd(), m);
            //MarkerAnimation.animateMarkerToICS(m, result[0].getLatLng(), interpolator);
        }

        @Override
        protected void onPostExecute(final Boolean result){

            bus = googleMap.addMarker(new MarkerOptions()
                    .position(stops.get(0).getLatLng())
                    .flat(true)
                    .rotation(45f)
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));

            final Iterator<Stop> it = stops.iterator();

            Animator.AnimatorListener listener = new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {

                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    if(it.hasNext()){
                        MarkerAnimation.animateMarkerToICS(bus, it.next().getLatLng(), interpolator, this);
                    }
                }

                @Override
                public void onAnimationCancel(Animator animation) {

                }

                @Override
                public void onAnimationRepeat(Animator animation) {

                }
            };
            MarkerAnimation.animateMarkerToICS(bus,it.next().getLatLng(), interpolator, listener);

            if(result && markers.size()!=0) {
                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(stops.get(stops.size()/2).getLatLng(), 14.0f));
            }
            else{
                Marker m = markers.get(markerId);
                if(m != null){
                    m.showInfoWindow();
                }

            }
        }

    }

    public Network getCurrentNetwork(){
        return currentNetwork;
    }

}
