package com.example.toni.lipafare.Passanger;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.toni.lipafare.Intro.SignInActivity;
import com.example.toni.lipafare.Passanger.Fragments.PassangerAccountFragment;
import com.example.toni.lipafare.Passanger.Fragments.PassangerHomeFragment;
import com.example.toni.lipafare.Passanger.Fragments.PassangerTripsFragment;
import com.example.toni.lipafare.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.roughike.bottombar.BottomBar;
import com.roughike.bottombar.OnTabSelectListener;

public class PassangerPanel extends AppCompatActivity {

    private BottomBar bottomBar;
    private ImageView profile;
    private TextView email, name;
    // index to identify current tab item
    public static int navItemIndex = 0;
    private Handler mHandler;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    private DatabaseReference mUsers;
    private View navHeader;
    private NavigationView navigationView;
    private DrawerLayout drawer;
    private ImageView imgProfile;
    private TextView txtName, txtWebsite;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.overall);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //Firebase

        mAuth = FirebaseAuth.getInstance();
        mUsers = FirebaseDatabase.getInstance().getReference().child("Users");
        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (firebaseAuth.getCurrentUser() == null) {
                    startActivity(new Intent(PassangerPanel.this, SignInActivity.class));
                    finish();
                }
            }
        };


        //views
        bottomBar = (BottomBar) findViewById(R.id.bottomBar);
        profile = (ImageView) findViewById(R.id.iv_passangerpanel_profile);
        email = (TextView) findViewById(R.id.tv_passanger_email);
        name = (TextView) findViewById(R.id.tv_passanger_name);
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        navigationView = (NavigationView) findViewById(R.id.nav_view);

        //handler
        mHandler = new Handler();


        // Navigation view header
        navHeader = navigationView.getHeaderView(0);
        txtName = (TextView) navHeader.findViewById(R.id.tv_passanger_email);
        txtWebsite = (TextView) navHeader.findViewById(R.id.tv_passanger_name);
        imgProfile = (ImageView) navHeader.findViewById(R.id.iv_passangerpanel_profile);

        //setup navigation view
        setUpNavigationView();

        //load tabs
        loadhomeFragments();
        //attachlistener to tab..
        tabListener();
        //load data
        loadUserData();
        //change text in status bar

    }

    @Override
    protected void onStart() {
        super.onStart();

        mAuth.addAuthStateListener(mAuthStateListener);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void loadUserData() {

        if (mAuth.getCurrentUser() != null) {

            DatabaseReference cu = mUsers.child(mAuth.getCurrentUser().getUid());
            cu.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(final DataSnapshot dataSnapshot) {

                    try {
                        //check for existance to avoid null pointers
                        if (dataSnapshot.exists()) {
                            try {
                                txtName.setText(dataSnapshot.child("user").getValue().toString());
                                txtWebsite.setText(mAuth.getCurrentUser().getEmail());
                                new Handler().post(new Runnable() {
                                    @Override
                                    public void run() {
                                        try {
                                            Glide.with(PassangerPanel.this).load(dataSnapshot.child("image").getValue().toString())
                                                    .crossFade()
                                                    .thumbnail(0.5f)
                                                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                                                    .error(R.mipmap.profile2)
                                                    .into(imgProfile);
                                        }catch (Exception e){
                                            e.printStackTrace();
                                        }
                                    }
                                });

                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        } else {
                            //Toast.makeText(PassangerPanel.this, "No user found", Toast.LENGTH_SHORT).show();
                        }
                    } catch (NullPointerException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }

    private void setUpNavigationView() {
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch (item.getItemId()) {
                    case R.id.passanger_ticks:

                        drawer.closeDrawers();
                        break;
                    case R.id.passanger_cancel:

                        startActivity(new Intent(PassangerPanel.this, CancelTicketActivity.class));

                        drawer.closeDrawers();
                        break;
                    case R.id.passanger_notification:

                        drawer.closeDrawers();
                        break;
                    case R.id.pass_about_us:

                        drawer.closeDrawers();
                        break;
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

        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.open, R.string.close) {

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
        drawer.setDrawerListener(actionBarDrawerToggle);

        //calling sync state is necessary or else your hamburger icon wont show up
        actionBarDrawerToggle.syncState();
    }

    private void tabListener() {
        bottomBar.setOnTabSelectListener(new OnTabSelectListener() {
            @Override
            public void onTabSelected(@IdRes int tabId) {
                switch (tabId) {
                    case R.id.tab_home:
                        getSupportActionBar().setTitle("Search Route");
                        navItemIndex = 0;
                        loadhomeFragments();
                        break;
                    case R.id.tab_account:
                        getSupportActionBar().setTitle("My Account");
                        navItemIndex = 1;
                        loadhomeFragments();
                        break;
                    case R.id.tab_trips:
                        getSupportActionBar().setTitle("Trips");
                        navItemIndex = 2;
                        loadhomeFragments();
                        break;
                    default:
                        navItemIndex = 0;
                        loadhomeFragments();
                }
            }
        });
    }

    private void loadhomeFragments() {

        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                // update the main content by replacing fragments

                Fragment fragment = getHomeFragment();
                FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                fragmentTransaction.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);
                fragmentTransaction.replace(R.id.frame_passangerlayout, fragment);
                fragmentTransaction.commitAllowingStateLoss();

            }
        };

        if (runnable != null) {

            mHandler.post(runnable);

        }
    }

    private Fragment getHomeFragment() {


        switch (navItemIndex) {
            case 0:

                return new PassangerHomeFragment();

            case 1:

                return new PassangerAccountFragment();

            case 2:

                return new PassangerTripsFragment();

            default:

                return new PassangerHomeFragment();
        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (navItemIndex != 0) {
            navItemIndex = 0;
            loadhomeFragments();
        }

        if (navItemIndex != 0) {
            navItemIndex = 0;
            loadhomeFragments();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_logout:
                mAuth.signOut();
                finish();
                break;

        }

        return super.onOptionsItemSelected(item);
    }
}
