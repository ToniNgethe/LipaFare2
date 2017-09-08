package com.toni.lipafare.Passanger.Fragments;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.toni.lipafare.Passanger.Helper.PassangerTripsVpAdapter;
import com.toni.lipafare.R;


/**
 * Created by toni on 4/28/17.
 */

public class PassangerTripsFragment extends Fragment{

    private View mView;
    private ViewPager mViewPager;
    private TabLayout mTabLayout;
    private Handler mHandler;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_passangertrips, container, false);

        //views
        mTabLayout = (TabLayout) mView.findViewById(R.id.tab_passangertrips);
        mViewPager = (ViewPager) mView.findViewById(R.id.vp_passangertrips);
        //setup viewpager
        setUpViewPager();

        mTabLayout.post(new Runnable() {
            @Override
            public void run() {
                mTabLayout.setupWithViewPager(mViewPager);
            }
        });
        return mView;
    }

    private void setUpViewPager() {
        mHandler = new Handler();


        Runnable runnable = new Runnable() {
            @Override
            public void run() {

                PassangerTripsVpAdapter myPostedJObsViewPAdapter = new PassangerTripsVpAdapter(getChildFragmentManager());
                myPostedJObsViewPAdapter.addFragment(new Upcoming(),"Upcoming");
                myPostedJObsViewPAdapter.addFragment(new Past(),"Past");
                myPostedJObsViewPAdapter.addFragment(new Cancelled(),"Cancelled");

                mViewPager.setAdapter(myPostedJObsViewPAdapter);
            }
        };

        if (runnable != null)
            mHandler.post(runnable);
    }
}
