package com.example.toni.lipafare.Operator;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.toni.lipafare.Helper.OperatorViewPagerAdapeter;
import com.example.toni.lipafare.Intro.SignInActivity;
import com.example.toni.lipafare.Operator.Fragments.Matatus;
import com.example.toni.lipafare.Operator.Fragments.Saccos;
import com.example.toni.lipafare.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class OperatorDashBoard extends AppCompatActivity {

    private static final String TAG = OperatorDashBoard.class.getSimpleName();
    private Toolbar mToolbar;
    private TabLayout mTabLayout;
    private ViewPager mViewPager;

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private View navHeader;
    private TextView txtName;
    private TextView txtWebsite;
    private ImageView imgProfile;

    private FirebaseAuth mAuth;
    private DatabaseReference mUsers, mSacco;
    private FloatingActionButton addMats;
    private FirebaseAuth.AuthStateListener mAuthStateListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_operator_dash_board);

        //Firebase..
        firebase();
        //setup views
        views();


        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (firebaseAuth.getCurrentUser() == null) {
                    startActivity(new Intent(OperatorDashBoard.this, SignInActivity.class));
                    finish();
                }
            }
        };

        if (mAuth.getCurrentUser() != null) {
            loadNavHeader();
            // initializing navigation menu
            setUpNavigationView();
            //setData
            getUserData();
        }
    }

    private void firebase() {

        mAuth = FirebaseAuth.getInstance();
        mUsers = FirebaseDatabase.getInstance().getReference().child("User");
        mSacco = FirebaseDatabase.getInstance().getReference().child("Sacco");

    }

    private void getUserData() {

        Query q = mSacco.orderByChild("admin").equalTo(mAuth.getCurrentUser().getUid());

        q.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (dataSnapshot.exists()) {

                    for (DataSnapshot ds : dataSnapshot.getChildren()) {

                        String key = ds.getKey();
                        Log.d(TAG, key);


                    }

                } else {
                    Log.d(TAG, "Nothing found");
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

                Toast.makeText(OperatorDashBoard.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });

        //load image, email, displayame
        new Handler().post(new Runnable() {
            @Override
            public void run() {

                String userkey = mAuth.getCurrentUser().getUid();

                DatabaseReference mUsers = FirebaseDatabase.getInstance().getReference().child("Users");
                mUsers.child(userkey).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(final DataSnapshot dataSnapshot) {

                        if (dataSnapshot.exists()) {

                            txtName.setText(dataSnapshot.child("user").getValue().toString());
                            txtWebsite.setText(mAuth.getCurrentUser().getEmail());

                            //Op
                            new Handler().post(new Runnable() {
                                @Override
                                public void run() {
                                    Glide.with(OperatorDashBoard.this).load(dataSnapshot.child("image").getValue().toString())
                                            .crossFade()
                                            .thumbnail(0.5f)
                                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                                            .error(R.mipmap.profile2)
                                            .into(imgProfile);
                                }
                            });
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Toast.makeText(OperatorDashBoard.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });


            }
        });

    }

    private void setUpNavigationView() {

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch (item.getItemId()) {

                    case R.id.nav_funds:

                        Toast.makeText(OperatorDashBoard.this, "Funds Clicked", Toast.LENGTH_SHORT).show();
                        drawerLayout.closeDrawers();

                        break;
                    case R.id.nav_myacc:

                        Toast.makeText(OperatorDashBoard.this, "My Acc Clicked", Toast.LENGTH_SHORT).show();
                        drawerLayout.closeDrawers();

                        break;
                    case R.id.nav_others:

                       // Toast.makeText(OperatorDashBoard.this, "My Others Clicked", Toast.LENGTH_SHORT).show();
                        drawerLayout.closeDrawers();

                        break;

                    case R.id.nav_logout:


                        mAuth.signOut();
                        finish();

                        drawerLayout.closeDrawers();

                        return true;
                }

                //Checking if the item is in checked state or not, if not make it in checked state
                if (item.isChecked()) {
                    item.setChecked(false);
                } else {
                    item.setChecked(true);
                }
                item.setChecked(true);

                return true;
            }
        });


        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, mToolbar, R.string.open, R.string.close) {

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

        //calling sync state is necessary or else your hamburger icon wont show up
        actionBarDrawerToggle.syncState();

    }

    private void loadNavHeader() {

    }

    private void views() {
        addMats = (FloatingActionButton) findViewById(R.id.fab_operatorDashBoard);

        addMats.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addMatatu();
            }
        });

        mToolbar = (Toolbar) findViewById(R.id.toolbar_operatordashboard);
        mTabLayout = (TabLayout) findViewById(R.id.tabs_operatorDashBoard);
        mViewPager = (ViewPager) findViewById(R.id.viewpager_operatorDashBoard);

        //nav items
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navHeader = navigationView.getHeaderView(0);

        txtName = (TextView) navHeader.findViewById(R.id.tv_operatordashboard_email);
        txtWebsite = (TextView) navHeader.findViewById(R.id.tv_operatordashboard_user);
        imgProfile = (ImageView) navHeader.findViewById(R.id.iv_opertordashboard_profile);


        //setToolBar
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //setup vp
        setupVp();
        mTabLayout.setupWithViewPager(mViewPager);
        mTabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                switch (tab.getPosition()) {
                    case 0:
                        addMats.setVisibility(View.GONE);
                        break;
                    case 1:
                        addMats.setVisibility(View.VISIBLE);
                        break;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthStateListener);
    }

    private void addMatatu() {
        startActivity(new Intent(OperatorDashBoard.this, AddMatatu.class));
    }

    private void setupVp() {

        new Handler().post(new Runnable() {
            @Override
            public void run() {
                OperatorViewPagerAdapeter operatorViewPagerAdapeter = new OperatorViewPagerAdapeter(getSupportFragmentManager());
                operatorViewPagerAdapeter.addFragment(new Saccos(), "Sacco");
                operatorViewPagerAdapeter.addFragment(new Matatus(), "Matatus");

                mViewPager.setAdapter(operatorViewPagerAdapeter);
            }
        });

    }
}
