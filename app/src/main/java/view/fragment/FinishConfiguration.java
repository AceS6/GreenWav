package view.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.greenwav.greenwav.R;

/**
 * Created by sauray on 14/03/15.
 */
public class FinishConfiguration extends Fragment{

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_finish_configuration, container, false);
        setRetainInstance(true);
        return root;
    }

    @Override
    public String toString(){
        return "Fin";
    }
}
