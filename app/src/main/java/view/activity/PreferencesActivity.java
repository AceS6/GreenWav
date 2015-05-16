package view.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.view.Menu;
import android.view.MenuItem;

import com.greenwav.greenwav.R;

import java.util.ArrayList;
import java.util.Iterator;

import model.Network;
import model.db.internal.JamboDAO;

public class PreferencesActivity extends PreferenceActivity {

    // ----------------------------------- Constants
    /**
     * Unique identifier for this activity
     */
    private static final String TAG = "PREFERENCES_ACTIVITY";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getFragmentManager().beginTransaction().replace(android.R.id.content, new InnerPreferenceFragment()).commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getActionBar().setDisplayHomeAsUpEnabled(true);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item_suggestion clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == android.R.id.home) {    // actions on "previous" button
            this.finish();
        }
        return super.onOptionsItemSelected(item);
    }

    public static class InnerPreferenceFragment extends PreferenceFragment implements OnSharedPreferenceChangeListener {
        @Override
        public void onCreate(final Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.preferences);
            Preference connectionPref = findPreference("pref_map_type");
            // Set summary to be the user-description for the selected value
            connectionPref.setSummary(getPreferenceScreen().getSharedPreferences().getString("pref_map_type", this.getActivity().getResources().getString(R.string.no_network)));

            ListPreference listPref = (ListPreference) findPreference("pref_reseau");
            listPref.setSummary(getPreferenceScreen().getSharedPreferences().getString("pref_reseau", getResources().getString(R.string.no_network)));

            ListPreference servicePref = (ListPreference) findPreference("pref_service");
            int service = Integer.parseInt(getPreferenceScreen().getSharedPreferences().getString("pref_service", "-1"));
            switch(service){
                case -1:
                    servicePref.setSummary(getResources().getString(R.string.no_service));
                    break;
                case 0:
                    servicePref.setSummary(getResources().getString(R.string.bus));
                    break;
                case 1:
                    servicePref.setSummary(getResources().getString(R.string.bike));
                    break;
                case 2:
                    servicePref.setSummary(getResources().getString(R.string.car));
                    break;
            }

            new loadNetwork(this.getActivity(), listPref).execute();
        }


        @Override
        public void onResume() {
            super.onResume();
            getPreferenceScreen().getSharedPreferences()
                    .registerOnSharedPreferenceChangeListener(this);
        }

        @Override
        public void onPause() {
            super.onPause();
            getPreferenceScreen().getSharedPreferences()
                    .unregisterOnSharedPreferenceChangeListener(this);
        }

        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
                                              String key) {
            // TODO Auto-generated method stub
            if (key.equals("pref_map_type")) {
                Preference connectionPref = findPreference(key);
                // Set summary to be the user-description for the selected value
                connectionPref.setSummary(sharedPreferences.getString(key, this.getActivity().getResources().getString(R.string.no_network)));
            } else if (key.equals("pref_reseau")) {
                Preference connectionPref = (Preference) findPreference(key);
                // Set summary to be the user-description for the selected value
                connectionPref.setSummary(sharedPreferences.getString(key, this.getActivity().getResources().getString(R.string.no_network)));
            }
            else if (key.equals("pref_service")) {
                Preference connectionPref = (Preference) findPreference(key);
                // Set summary to be the user-description for the selected value
                int service = Integer.parseInt(getPreferenceScreen().getSharedPreferences().getString("pref_service", "-1"));
                switch(service){
                    case -1:
                        connectionPref.setSummary(getResources().getString(R.string.no_service));
                        break;
                    case 0:
                        connectionPref.setSummary(getResources().getString(R.string.bus));
                        break;
                    case 1:
                        connectionPref.setSummary(getResources().getString(R.string.bike));
                        break;
                    case 2:
                        connectionPref.setSummary(getResources().getString(R.string.car));
                        break;
                }
            }

        }

        private class loadNetwork extends AsyncTask<Void, Void, Void> {

            ProgressDialog pd;
            Context context;
            ListPreference listPref;

            loadNetwork(Context context, ListPreference listPref) {
                this.context = context;
                this.listPref = listPref;
            }

            @Override
            public void onPreExecute() {
                pd = new ProgressDialog(context);
                pd.setTitle(getResources().getString(R.string.looking_for_network_locally));
                pd.show();
            }

            @Override
            protected Void doInBackground(Void... params) {
                JamboDAO dao = new JamboDAO(context);
                dao.open();
                ArrayList<Network> reseaux = dao.findReseaux();
                ArrayList<String> listId = new ArrayList<String>();
                ArrayList<String> listNom = new ArrayList<String>();
                Iterator<Network> it = reseaux.iterator();
                while (it.hasNext()) {
                    Network r = it.next();
                    listId.add(r.getIdBdd() + "");
                    listNom.add(r.toString());
                }
                dao.close();
                CharSequence[] csNom = listNom.toArray(new CharSequence[listNom.size()]);
                listPref.setEntries(csNom);
                listPref.setEntryValues(csNom);
                return null;
            }

            @Override
            public void onPostExecute(Void result) {
                pd.hide();
            }
        }

    }


}
