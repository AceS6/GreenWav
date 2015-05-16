package view.fragment;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.greenwav.greenwav.R;

import model.db.internal.JamboDAO;
import view.activity.NetworkConfigurationActivity;

/**
 * Created by sauray on 14/03/15.
 */
public class BorneFragment extends Fragment implements CompoundButton.OnCheckedChangeListener {

    private CheckBox checkBox;
    private GetBorneState async;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_borne_configuration, container, false);
        checkBox = ((CheckBox)root.findViewById(R.id.useCar));
        checkBox.setOnCheckedChangeListener(this);
        async = new GetBorneState();
        async.execute();
        return root;
    }

    @Override
    public String toString(){
        return "Voiture";
    }

    @Override
    public void onDetach() {
        super.onDetach();
        async.cancel(true);
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        ((NetworkConfigurationActivity)getActivity()).setUsesCar(isChecked);
    }

    private class GetBorneState extends AsyncTask<Void, Void, Integer>{

        @Override
        protected Integer doInBackground(Void... params) {
            JamboDAO dao = new JamboDAO(BorneFragment.this.getActivity());
            dao.open();
            Integer ret = dao.findBorne(((NetworkConfigurationActivity)getActivity()).getCurrentNetwork().getIdBdd()).size();
            dao.close();
            return ret;
        }

        protected void onPostExecute(Integer result){
            checkBox.setChecked(result != 0);
        }
    }
}
