package view.activity;


import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.annotation.TargetApi;
import android.app.SearchManager;
import android.content.ClipData;
import android.content.ClipDescription;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Point;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.speech.RecognizerIntent;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SearchViewCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.DisplayMetrics;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.TextView;

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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import model.Borne;
import model.Event;
import model.Network;
import model.PlaceInformation;
import model.Route;
import model.Station;
import model.Stop;
import model.User;
import model.db.external.didier.GetEventImage;
import model.db.external.didier.GetStationInformations;
import model.db.internal.JamboDAO;
import model.db.internal.async.DisplayBikeStations;
import view.custom.google.LatLngInterpolator;
import view.custom.google.MarkerAnimation;


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
    private View bottomSheet;

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
        sharedPref.edit().putInt("UI", pref_service).apply();

        if (currentNetwork != null) {
            initInterface();
            markers = new HashMap<Integer, Marker>();
            suggestions = new ArrayList<PlaceInformation>();
        } else {
            Intent intent = new Intent(HomeActivity.this, SplashScreenActivity.class);
            startActivity(intent);
            this.finish();
        }
        navigationView = (NavigationView) findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(this);



        floatingActionButton = (FloatingActionButton) findViewById(R.id.fab);
        floatingActionButton.setBaselineAlignBottom(true);

        currentMode = R.id.bus_mode;

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
                    if(bottomSheet != null){
                        bottomSheet.setVisibility(View.INVISIBLE);
                    }

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
    }

    @Override
    public boolean onMarkerClick(Marker marker) {

        if(marker.getData() == null){
            return false;
        }

        currentMarker = marker;
        String snippet = marker.getSnippet();
        lyt_main.removeView(bottomSheet);
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);


        Class c = marker.getData().getClass();

        if(c == Stop.class){
            currentStop = (Stop) marker.getData();
            bottomSheet = inflater.inflate(R.layout.card_schedule, null);

            radioButtons[0] = (RadioButton) bottomSheet.findViewById(R.id.sens1);
            radioButtons[1] = (RadioButton) bottomSheet.findViewById(R.id.sens2);

            JamboDAO dao = new JamboDAO(this);
            dao.open();

            int i=0;
            List<Route> routes = currentLine.getRoutes();
            for(Route r : routes){
                radioButtons[i].setText(r.toString());
                i++;
            }
        }
        else if (c == Station.class){
            currentStation = (Station) marker.getData();
            bottomSheet = inflater.inflate(R.layout.card_bike, null);
        }
        else if (c == Borne.class){
            currentBorne = (Borne) marker.getData();
            bottomSheet = inflater.inflate(R.layout.card_car, null);
        }
        else if (c == Event.class){
            currentEvent = (Event) marker.getData();
            bottomSheet = inflater.inflate(R.layout.card_event, null);
        }
        cardReveal(c);
        return false;
    }

    @Override
    public void onInfoWindowClick(Marker marker) {
        Class c = marker.getData().getClass();
        Intent intent = null;
        if(c == Stop.class){
            intent = new Intent(HomeActivity.this, ScheduleActivity.class);
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
        if (currentMarker != null) {
            currentMarker = null;
            cardUnreveal();
        }
        bottomSheetVisible = false;
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

        /*

        lyt_main.removeView(ui);
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        switch (uiId) {
            case 0:
                ui = inflater.inflate(R.layout.ui_bus, null);
                break;
            case 1:
                ui = inflater.inflate(R.layout.ui_bike, null);
                break;
            case 2:
                ui = inflater.inflate(R.layout.ui_car, null);
                break;
        }
        */
        //uiReveal();
    }

    /**
     * Initializes the visual aspect of the activity
     */
    private void initInterface() {

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        //toolbar.setTitle(this.getResources().getString(R.string.activity_home));
        //toolbar.setLogo(R.drawable.ic_directions_bus_black_48dp);
        //toolbar.getLogo().setAlpha(54);
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

        mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        mapFragment.getExtendedMapAsync(this);

        lyt_main = (RelativeLayout) this.findViewById(R.id.lyt_main);
    }
    /**
     * Called when a click on UI element is detected (The low right corner interface)
     */
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public void buttonClick(View v) {
        Intent intent = null;
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.LOLLIPOP) {
            v.animate().translationZ(6f);
        }
/*
        switch (v.getId()) {

            case R.id.fab:
                intent = null;

                if(bottomSheetVisible){
                    intent = new Intent(HomeActivity.this, ScheduleActivity.class);
                    if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.LOLLIPOP) {
                        intent.putExtra("NETWORK", currentNetwork);
                        intent.putExtra("BUS_LINE", currentLine);
                        intent.putExtra("BUS_ROUTE", currentRoute);
                        intent.putExtra("BUS_STOP", currentStop);
                        //ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(HomeActivity.this,
                        //        new Pair<View, String>(ui.findViewById(R.id.floatingActionButton), "fab"));
                        this.startActivity(intent);
                    }
                    else{
                        this.startActivity(intent);
                    }
                }
                else{
                    intent = new Intent(HomeActivity.this, BusActivity.class);
                    intent.putExtra("NETWORK", currentNetwork);
                    if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.LOLLIPOP) {
                        //ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(HomeActivity.this,
                        //        new Pair<View, String>(ui.findViewById(R.id.floatingActionButton), "fab"));
                        this.startActivityForResult(intent, LINE_SELECTION//, options.toBundle()
                        );
                    }
                    else{
                        this.startActivityForResult(intent, LINE_SELECTION);
                    }
                }
                break;
            case R.id.bikeButton:
                    intent = new Intent(HomeActivity.this, BikeActivity.class);
                    intent.putExtra("NETWORK", currentNetwork);
                    intent.putExtra("LOCATION", googleMap.getMyLocation());
                    this.startActivityForResult(intent, BIKE_SELECTION);
                break;
            case R.id.borneButton:
                intent = new Intent(HomeActivity.this, ElectricalActivity.class);
                intent.putExtra("NETWORK", currentNetwork);
                intent.putExtra("LOCATION", googleMap.getMyLocation());
                this.startActivityForResult(intent, BORNE_SELECTION);
                break;
        }
        */
    }

    /**
     * Called when a click on a contextual card is detected
     */
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public void cardClick(View v) {
        Intent intent = null;

        switch (v.getId()) {
            /*
            case R.id.card_schedule:
                intent = new Intent(HomeActivity.this, ScheduleActivity.class);
                intent.putExtra("BUS_STOP", currentStop);
                intent.putExtra("BUS_ROUTE", currentRoute);
                intent.putExtra("BUS_LINE", currentLine);
                intent.putExtra("NETWORK", currentNetwork);
                startActivity(intent);
                break;
            case R.id.card_event:
                Intent intentEvent = new Intent(HomeActivity.this, EventActivity.class);
                intentEvent.putExtra("EVENT", currentEvent);
                startActivity(intentEvent);
                break;
                */
        }
    }

    public void radioClick(View v){

        if(addMarkers != null && addMarkers.getStatus() == AsyncTask.Status.RUNNING){
            addMarkers.cancel(true);
        }
        addMarkers = new AddMarkers();
        addMarkers.execute();
    }

    private void cardReveal(final Class c){
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 300);
        params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);

        bottomSheet.setLayoutParams(params);
        ObjectAnimator animY = ObjectAnimator.ofFloat(bottomSheet, "translationY", 300f, 0f);
        animY.setDuration(500);//1.5sec
        animY.setRepeatCount(0);

        floatingActionButton.setBaseline(R.id.bottom_sheet);

        bottomSheet.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    switch (currentMode){
                        case R.id.bus_mode:
                            Intent intent = new Intent(HomeActivity.this, ScheduleActivity.class);
                            intent.putExtra("BUS_STOP", currentStop);
                            intent.putExtra("BUS_ROUTE", currentRoute);
                            intent.putExtra("BUS_LINE", currentLine);
                            intent.putExtra("NETWORK", currentNetwork);
                            if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.LOLLIPOP) {
                                ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(HomeActivity.this);
                                HomeActivity.this.startActivity(intent, options.toBundle()
                                );
                            }
                            else{
                                HomeActivity.this.startActivity(intent);
                            }
                            break;
                        case R.id.bike_mode:
                            break;
                    }
                    return true;
                } else if(event.getAction() == MotionEvent.ACTION_MOVE){

                    return true;
                }
                return false;
            }
        });
        lyt_main.addView(bottomSheet);
        bottomSheetVisible = true;
        floatingActionButton.bringToFront();
        animY.start();

        if(c == Stop.class){
            //new GetNextSchedule(HomeActivity.this, currentStop.getIdAppartient(), Schedule.getDayOfWeek(), bottomSheet).execute();
        }
        else if(c == Station.class){
            new GetStationInformations(HomeActivity.this, currentStation, currentNetwork, bottomSheet).execute();
        }
        else if(c == Borne.class){
            ((TextView)bottomSheet.findViewById(R.id.nom)).setText(currentBorne.getNom());
            ((TextView)bottomSheet.findViewById(R.id.typeCharge)).setText(currentBorne.getTypeChargeur());
        }
        else if(c == Event.class){
            new GetEventImage(this, currentEvent, bottomSheet).execute();
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void cardUnreveal(){
        if(bottomSheet != null && bottomSheetVisible) {
            ObjectAnimator animYDown = ObjectAnimator.ofFloat(bottomSheet, "translationY", 0f, 300f);
            animYDown.setDuration(500);//1.5sec
            animYDown.setRepeatCount(0);
            animYDown.start();
            bottomSheetVisible = false;
        }
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
        currentMode = menuItem.getItemId();
        menuItem.setChecked(true);
        cardUnreveal();
        googleMap.clear();
        markers.clear();
        switch (currentMode){
            case R.id.bus_mode:
                if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.LOLLIPOP) {
                    floatingActionButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_directions_bus_white_48dp, getTheme()));
                }
                else{
                    floatingActionButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_directions_bus_white_48dp));
                }
                break;
            case R.id.bike_mode:
                new DisplayBikeStations(this, googleMap, markers, currentNetwork.getIdBdd()).execute();
                if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.LOLLIPOP) {
                    floatingActionButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_directions_bike_white_48dp, getTheme()));
                }
                else{
                    floatingActionButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_directions_bike_white_48dp));
                }
                break;
            case R.id.walk_mode:
                if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.LOLLIPOP) {
                    floatingActionButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_directions_walk_white_48dp, getTheme()));
                }
                else{
                    floatingActionButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_directions_walk_white_48dp));
                }
                break;
            case R.id.electrical_car_mode:
                if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.LOLLIPOP) {
                    floatingActionButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_menu_line_car_w, getTheme()));
                }
                else{
                    floatingActionButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_menu_line_car_w));
                }
                break;
            case R.id.car_sharing_mode:
                if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.LOLLIPOP) {
                    floatingActionButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_directions_car_white_48dp, getTheme()));
                }
                else{
                    floatingActionButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_directions_car_white_48dp));
                }
                break;
            default:
                return false;
        }
        return true;
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
