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
public class BlankFragment extends Fragment{

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_blank, container, false);
        return root;
    }

    @Override
    public String toString(){
        return "Vélo";
    }

}
