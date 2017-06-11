package com.example.toni.lipafare.Helper;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by toni on 4/8/17.
 */

public class OperatorViewPagerAdapeter extends FragmentStatePagerAdapter {

    private List<Fragment> myFragmentList = new ArrayList<>();
    private List<String> mTitList = new ArrayList<>();

    public OperatorViewPagerAdapeter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        return myFragmentList.get(position);
    }

    @Override
    public int getCount() {
        return myFragmentList.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mTitList.get(position);
    }

    public void addFragment(Fragment f, String title){
        myFragmentList.add(f);
        mTitList.add(title);
    }
}
