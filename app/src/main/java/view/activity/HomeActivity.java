package view.activity;


import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.CursorAdapter;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

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

import model.Borne;
import model.Event;
import model.Network;
import model.PlaceInformation;
import model.Schedule;
import model.Station;
import model.Stop;
import model.db.external.didier.GetBornes;
import model.db.external.didier.GetEventImage;
import model.db.external.didier.GetEvents;
import model.db.external.didier.GetNextSchedule;
import model.db.external.didier.GetStationInformations;
import model.db.external.didier.GetStations;
import model.db.external.google.AutoCompletion;
import model.db.internal.JamboDAO;
import model.utility.NetworkUtil;
import view.fragment.NavigationDrawerFragment;


/**
 * The main activity of the program
 * @author Antoine Sauray
 * @version 2.0
 */
public class HomeActivity extends ActionBarActivity implements NavigationDrawerFragment.NavigationDrawerCallbacks, OnMapReadyCallback, GoogleMap.OnMarkerClickListener, GoogleMap.OnInfoWindowClickListener, GoogleMap.OnMapLongClickListener, GoogleMap.OnMapClickListener, View.OnFocusChangeListener, SearchView.OnQueryTextListener, SearchView.OnSuggestionListener {

    // ----------------------------------- UI

    /**
     * These variables are used to get result from other activities.
     *
     * @see BusComponentActivity
     * @see StopActivity
     */
    private static final int LINE_SELECTION = 1, STOP_SELECTION = 2, STATION_SELECTION = 3, BORNE_SELECTION =4;
    /**
     * Unique identifier for this activity
     */
    private static final String TAG = "HOME_ACTIVITY";
    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;
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
    /**
     * The main layout of the activity.
     */
    private RelativeLayout lyt_main;
    /**
     * The cursor adapter of the search suggestions
     */
    private CursorAdapter cursorAdapter;

    // ----------------------------------- Model
    /**
     * the cursor of the search suggestions
     */
    private Cursor cursor;
    /**
     * The search view. It provides a search function to the user. It uses Google Search API
     */
    private SearchView search;
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

    // Informations on current state
    private boolean bottomSheetVisible;

    // ----------------------------------- Constants
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Bundle extras = this.getIntent().getExtras();
        currentNetwork = extras.getParcelable("NETWORK");
        bottomSheetVisible = false;

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        int pref_service = Integer.parseInt(sharedPref.getString("pref_service", "0"));
        sharedPref.edit().putInt("UI", pref_service).apply();

        if (currentNetwork != null) {
            //MapsInitializer.initialize(getApplicationContext());
            initInterface();
            markers = new HashMap<Integer, Marker>();
            suggestions = new ArrayList<PlaceInformation>();
        } else {
            Intent intent = new Intent(HomeActivity.this, SplashScreenActivity.class);
            startActivity(intent);
            this.finish();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        toolbar.inflateMenu(R.menu.map_menu);

        MenuItem searchMenuItem = menu.findItem(R.id.action_search);


        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        search = (SearchView) MenuItemCompat.getActionView(searchMenuItem);
        if (search != null) {

            TextView searchText = (TextView) search.findViewById(android.support.v7.appcompat.R.id.search_src_text);
            searchText.setTextColor(Color.WHITE);
            searchText.setHintTextColor(Color.WHITE);

            search.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
            search.setOnQueryTextFocusChangeListener(this);
            search.setOnQueryTextListener(this);
            search.setOnSuggestionListener(this);

            cursorAdapter = new CursorAdapter(this, null, 0) {
                @Override
                public void bindView(View view, Context context, Cursor cursor) {
                    // TODO Auto-generated method stub

                    TextView name = (TextView) view.findViewById(R.id.text);
                    name.setTextSize(20);
                    name.setText(cursor.getString(1));
                }

                @Override
                public View newView(Context context, Cursor cursor,
                                    ViewGroup parent) {
                    LayoutInflater inflater = LayoutInflater.from(parent.getContext());
                    View retView = inflater.inflate(R.layout.item_suggestion, parent, false);
                    ((ImageView)retView.findViewById(R.id.icon)).setAlpha(0.54f);
                    retView.setBackgroundColor(getResources().getColor(R.color.light_blue));
                    return retView;
                }

            };
            search.setSuggestionsAdapter(cursorAdapter);
        }

        return super.onCreateOptionsMenu(menu);
    }

    /**
     * Called when a new mode has been selected by the user
     */
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onNavigationDrawerItemSelected(final int position) {
        // update the main content by replacing fragments
        if(mNavigationDrawerFragment.getModeAvailability(position)) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && ui != null) {
                // Unreveal animation
                ui.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
                int initialRadius = ui.getMeasuredWidth();
                Animator anim = ViewAnimationUtils.createCircularReveal(ui,
                        ui.getMeasuredWidth() / 2,
                        ui.getMeasuredHeight() / 2,
                        initialRadius,
                        0);
                anim.setDuration(400);
                anim.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        inflateUI(position);
                    }
                });
                anim.start();
            } else {
                inflateUI(position);
            }
        }
        else{
            AlertDialog.Builder builder = new AlertDialog.Builder(HomeActivity.this);
            builder.setTitle(R.string.mode_not_activated);
            builder.setMessage(getResources().getString(R.string.do_you_want_to_configure) + currentNetwork);
            builder.setPositiveButton(R.string.configure, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent mStartActivity = new Intent(HomeActivity.this, NetworkConfigurationActivity.class);
                    mStartActivity.putExtra("NETWORK", currentNetwork);
                    startActivity(mStartActivity);
                }
            });
            builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(HomeActivity.this);
                    int pref_service = Integer.parseInt(sharedPref.getString("pref_service", "0"));
                    if(mNavigationDrawerFragment.getModeAvailability(mNavigationDrawerFragment.getPreviousPosition())){
                        mNavigationDrawerFragment.selectItem(mNavigationDrawerFragment.getPreviousPosition());
                    }
                    else if(mNavigationDrawerFragment.getModeAvailability(pref_service)){
                        mNavigationDrawerFragment.selectItem(pref_service);
                    }
                    else{
                        int i=0;
                        boolean found=false;
                        while(i < 3 && !found){
                            if(mNavigationDrawerFragment.getModeAvailability(i)){
                                found=true;
                            }
                            else {
                                i++;
                            }
                        }
                        if(found){
                            mNavigationDrawerFragment.selectItem(i);
                        }
                        else{
                            Intent intent = new Intent(HomeActivity.this, NetworkConfigurationActivity.class);
                            intent.putExtra("NETWORK", currentNetwork);
                            HomeActivity.this.startActivity(intent);
                        }
                    }

                }
            });
            AlertDialog alertDialog = builder.create();
            alertDialog.show();
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check which request we're responding to
        switch (requestCode) {
            case LINE_SELECTION:
                if (resultCode == RESULT_OK) {
                    currentLine = data.getParcelableExtra("BUS_LINE");
                    currentRoute = data.getParcelableExtra("BUS_ROUTE");
                    // Remove UI if a new line has been selected
                    if(bottomSheet != null){
                        bottomSheet.setVisibility(View.INVISIBLE);
                    }
                    new AddMarkers().execute();
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
            case STATION_SELECTION:
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
            case BORNE_SELECTION:
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

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        int uiId = sharedPref.getInt("UI", -1);
        mNavigationDrawerFragment.selectItem(uiId);

        new GetEvents(this, currentNetwork.getIdBdd(), googleMap, markers).execute();
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        currentMarker = marker;
        bottomSheetVisible = true;
        String snippet = marker.getSnippet();
        lyt_main.removeView(bottomSheet);
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        Class c = marker.getData().getClass();

        if(c == Stop.class){
            currentStop = currentRoute.getStop().get(((Stop) marker.getData()).getIdBdd());
            bottomSheet = inflater.inflate(R.layout.card_schedule, null);
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
    public boolean onQueryTextSubmit(String s) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String s) {
        new AutoCompletion(this, suggestions, cursorAdapter).execute(s);
        return true;
    }

    @Override
    public boolean onSuggestionSelect(int i) {
        // TODO Auto-generated method stub
        cursorAdapter.getCursor().moveToPosition(i);
        PlaceInformation information = suggestions.get(i);
        //new GetDetails(this, googleMap, markers).execute(information);
        return false;
    }

    @Override
    public boolean onSuggestionClick(int i) {
        return onSuggestionSelect(i);
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
        uiReveal();
    }

    /**
     * Initializes the visual aspect of the activity
     */
    private void initInterface() {

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(this.getResources().getString(R.string.activity_home));
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);

        mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        mapFragment.getExtendedMapAsync(this);

        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);

        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout), currentNetwork);

        lyt_main = (RelativeLayout) this.findViewById(R.id.lyt_main);
    }

    public void actionMenu(View v){
        if(currentMarker != null){
            currentMarker.hideInfoWindow();
            cardUnreveal();
        }
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

        switch (v.getId()) {
            case R.id.lineButton:
                intent = new Intent(HomeActivity.this, BusComponentActivity.class);
                intent.putExtra("NETWORK", currentNetwork);
                this.startActivityForResult(intent, LINE_SELECTION);
                break;
            case R.id.stopButton:

                if(currentLine != null && currentRoute != null) {
                    intent = new Intent(HomeActivity.this, StopActivity.class);
                    intent.putExtra("BUS_LINE", currentLine);
                    intent.putExtra("BUS_ROUTE", currentRoute);
                    intent.putExtra("LOCATION", googleMap.getMyLocation());
                    this.startActivityForResult(intent, STOP_SELECTION);
                }
                break;
            case R.id.bikeButton:
                    intent = new Intent(HomeActivity.this, StationActivity.class);
                    intent.putExtra("NETWORK", currentNetwork);
                    intent.putExtra("LOCATION", googleMap.getMyLocation());
                    this.startActivityForResult(intent, STATION_SELECTION);
                break;
            case R.id.borneButton:
                intent = new Intent(HomeActivity.this, CarActivity.class);
                intent.putExtra("NETWORK", currentNetwork);
                intent.putExtra("LOCATION", googleMap.getMyLocation());
                this.startActivityForResult(intent, BORNE_SELECTION);
                break;
        }
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

    /**
     * Called when a button has been selected by the user
     */
    public void drawerClick(View v) {
        Intent intent = null;
        switch (v.getId()) {
            case R.id.network:
                intent = new Intent(HomeActivity.this, NetworkSelectionActivity.class);
                this.startActivity(intent);
                break;
            case R.id.configure:
                if(NetworkUtil.isConnected(this)) {
                    intent = new Intent(HomeActivity.this, NetworkConfigurationActivity.class);
                    intent.putExtra("NETWORK", currentNetwork);
                    this.startActivity(intent);
                }
                else{
                    Toast.makeText(this, R.string.connexion_required, Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.preferences:
                intent = new Intent(HomeActivity.this, PreferencesActivity.class);
                this.startActivity(intent);
                break;
        }
    }

    private void inflateUI(int position){
        lyt_main.removeView(ui);
        googleMap.clear();
        markers.clear();
        suggestions.clear();

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        sharedPref.edit().putInt("UI", position).apply();

        cardUnreveal();
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        switch (position) {
            case 0:
                ui = inflater.inflate(R.layout.ui_bus, null);
                break;
            case 1:
                ui = inflater.inflate(R.layout.ui_bike, null);
                new GetStations(this, currentNetwork.getIdBdd(), googleMap, markers).execute();
                break;
            case 2:
                ui = inflater.inflate(R.layout.ui_car, null);
                new GetBornes(this, currentNetwork.getIdBdd(), googleMap, markers).execute();
                break;
            default:
                ui = inflater.inflate(R.layout.ui_bus, null);
                break;
        }
        uiReveal();
    }

    private void cardReveal(final Class c){
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 200);
        params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);

        bottomSheet.setLayoutParams(params);
        ObjectAnimator animY = ObjectAnimator.ofFloat(bottomSheet, "translationY", 150f, 0f);
        animY.setDuration(500);//1.5sec
        animY.setRepeatCount(0);

        ObjectAnimator animFloatY = ObjectAnimator.ofFloat(ui, "translationY", -100f);
        animFloatY.setDuration(500);//1.5sec
        animFloatY.setRepeatCount(0);
        animFloatY.start();

        lyt_main.addView(bottomSheet);

        ui.bringToFront();

        animY.start();
        if(c == Stop.class){
            new GetNextSchedule(HomeActivity.this, currentStop.getIdAppartient(), Schedule.getDayOfWeek(), bottomSheet).execute();
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
            ObjectAnimator animYDown = ObjectAnimator.ofFloat(bottomSheet, "translationY", 0f, 200f);
            animYDown.setDuration(500);//1.5sec
            animYDown.setRepeatCount(0);
            animYDown.start();

            ObjectAnimator animFloatY = ObjectAnimator.ofFloat(findViewById(R.id.ui), "translationY", 16f);
            animFloatY.setDuration(500);//1.5sec
            animFloatY.setRepeatCount(0);
            animFloatY.start();
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void uiReveal(){
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE);
        params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
        lyt_main.addView(ui);
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.LOLLIPOP && ui.getParent()!=null) {
            params.rightMargin = 15;
            params.bottomMargin = 15;
            ui.setLayoutParams(params);

            ui.setVisibility(View.INVISIBLE);
            ui.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
            // get the center for the clipping circle
            int cx = ui.getMeasuredWidth() / 2;
            int cy = ui.getMeasuredHeight() / 2;
            // get the final radius for the clipping circle
            int finalRadius = Math.max(ui.getMeasuredWidth(), ui.getMeasuredHeight());
            try {
                Animator anim = ViewAnimationUtils.createCircularReveal(ui,
                        cx,
                        cy,
                        0,
                        finalRadius);
                anim.setDuration(500);
               ui.setVisibility(View.VISIBLE);
                anim.start();
            }
            catch (IllegalStateException e){
                ui.setVisibility(View.VISIBLE);
            }
        }
        else{
            ui.setLayoutParams(params);
            ObjectAnimator animY = ObjectAnimator.ofFloat(ui, "translationY", 170f, 0f);
            animY.setDuration(500);//1.5sec
            animY.setRepeatCount(0);
            animY.start();
        }
    }

    private class AddMarkers extends AsyncTask<Void, Stop, Void>{

        @Override
        protected void onPreExecute(){
            googleMap.clear();
        }

        @Override
        protected Void doInBackground(Void... params) {
            markers.clear();
            JamboDAO dao = new JamboDAO(HomeActivity.this);
            dao.open();
            HashMap<Integer, Stop> stops = dao.findAssociateArrets(currentRoute);
            currentRoute.setStop(stops);
            Iterator<Stop> it = stops.values().iterator();
            markers.clear();

            while (it.hasNext()) {
                publishProgress(it.next());

            }
            dao.close();
            return null;
        }

        protected void onProgressUpdate(Stop...result){
            Marker m = googleMap.addMarker(new MarkerOptions()
                    .position(result[0].getLatLng())
                    .alpha(0.7f)
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_maps_schedule))
                    .title(result[0].toString()));
            m.setData(result[0]);
            markers.put(result[0].getIdBdd(), m);
        }

        @Override
        protected void onPostExecute(Void result){
            if(markers.size()!=0) {
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(((Marker) markers.values().toArray()[markers.size() / 2]).getPosition(), 14.0f));
            }
        }

    }

    public Network getCurrentNetwork(){
        return currentNetwork;
    }

}
