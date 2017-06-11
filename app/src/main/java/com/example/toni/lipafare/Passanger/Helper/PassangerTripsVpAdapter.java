package com.example.toni.lipafare.Passanger.Helper;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by toni on 4/28/17.
 */

public class PassangerTripsVpAdapter extends FragmentPagerAdapter {
    private List<String> fTitle = new ArrayList<>();
    private List<Fragment> fragments = new ArrayList<>();

    public PassangerTripsVpAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        return fragments.get(position);
    }

    @Override
    public int getCount() {

        return fragments.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return fTitle.get(position);
    }

    public void addFragment(Fragment m, String title) {
        fragments.add(m);
        fTitle.add(title);
    }
}
