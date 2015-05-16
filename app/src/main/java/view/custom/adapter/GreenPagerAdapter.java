package view.custom.adapter;

/**
 * Created by sauray on 14/03/15.
 */

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.viewpagerindicator.IconPagerAdapter;
import com.viewpagerindicator.TitlePageIndicator;

import java.util.List;

import view.fragment.LineFragment;

public class GreenPagerAdapter extends FragmentPagerAdapter{

    private final List<Fragment> fragments;

    //On fournit à l'adapter la liste des fragments à afficher
    public GreenPagerAdapter(FragmentManager fm, List fragments) {
        super(fm);
        this.fragments = fragments;
    }

    @Override
    public Fragment getItem(int position) {
        return this.fragments.get(position);
    }

    @Override
    public int getCount() {
        return this.fragments.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return fragments.get(position).toString();
    }
}
