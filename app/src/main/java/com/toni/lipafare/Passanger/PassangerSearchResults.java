package com.toni.lipafare.Passanger;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.toni.lipafare.Dialogs.Loading;
import com.toni.lipafare.Passanger.Fragments.PassangerHomeFragment;
import com.toni.lipafare.Passanger.PassModel.PassangerQuery;
import com.toni.lipafare.Passanger.PassangerAdapter.PaasangerQueryAdapter;
import com.toni.lipafare.R;

import java.util.ArrayList;
import java.util.List;

public class PassangerSearchResults extends AppCompatActivity {

    private static final String TAG = "QUERIEESSSS";
    private String from_add, from_city, to_add, to_city;
    private DatabaseReference mRoutes;
    private Loading loading;
    private RecyclerView rv;
    private PaasangerQueryAdapter paasangerQueryAdapter;
    private List<String> key;
    private List<PassangerQuery> myList;
    private LinearLayout linearLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_passanger_search_results);

        //views
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Search Results");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        linearLayout = (LinearLayout) findViewById(R.id.error_busesnotfound);

        myList = new ArrayList<>();
        key = new ArrayList<>();

        //rv
        rv = (RecyclerView) findViewById(R.id.rv_searchqueries);
        LinearLayoutManager lm = new LinearLayoutManager(this);
        lm.setStackFromEnd(true);
        lm.setReverseLayout(true);
        rv.setLayoutManager(lm);

        //loading
        loading = new Loading(this, "Querying saccos..");
        loading.setCanceledOnTouchOutside(false);
        loading.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        loading.show();

        //get lat and long
        searchLatLong();

    }

    @Override
    protected void onStart() {
        super.onStart();
        //
        getLocations();
    }

    private void getLocations() {
        mRoutes = FirebaseDatabase.getInstance().getReference().child("Route");

        //query from location first...
        Query query = mRoutes.orderByChild("From_address").equalTo(from_add);
        final Query query1 = mRoutes.orderByChild("To_address").equalTo(to_add);

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final PassangerQuery passangerQuery =
                        new PassangerQuery();

                if (dataSnapshot.exists()) {

                    for (DataSnapshot ds : dataSnapshot.getChildren()) {

                        //check if from location exists
                        if (ds.child("From_address").exists()) {

                            Log.v(TAG, "QUERIED FROM :" + ds.child("From_address").getValue().toString());
                            passangerQuery.setFrom_address(ds.child("From_address").getValue().toString());

                            //exists...query to address
                            query1.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot to_dataSnapshot) {
                                    myList.clear();
                                    key.clear();
                                    if (to_dataSnapshot.exists()) {

                                        for (DataSnapshot to_ds : to_dataSnapshot.getChildren()) {

                                            if (to_ds.child("To_address").exists()) {

                                                Log.v(TAG, "QUERIed KEY:" + to_ds.child("sacco").getValue().toString());
                                                Log.v(TAG, "QUERIED TO:" + to_ds.child("To_address").getValue().toString());

                                               //load into adapter
                                                passangerQuery.setTo_address(to_ds.child("To_address").getValue().toString());
                                                myList.add(passangerQuery);
                                                key.add(to_ds.child("sacco").getValue().toString());
                                                Log.v(TAG, "Size" + key.size());

                                                loading.dismiss();
                                                paasangerQueryAdapter = new PaasangerQueryAdapter(PassangerSearchResults.this, myList, key, from_add, to_add);

                                                rv.setAdapter(paasangerQueryAdapter);
                                                paasangerQueryAdapter.notifyDataSetChanged();

                                            } else {
                                               showInfo();
                                            }
                                        }
                                    } else {
                                       showInfo();
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });

                        } else {
                            //From location nt found
                           showInfo();
                        }
                    }
                } else {

                    //location not found
                    showInfo();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void showInfo() {
        loading.dismiss();
        rv.setVisibility(View.GONE);
        linearLayout.setVisibility(View.VISIBLE);
    }

    private void searchLatLong() {
        from_add = getIntent().getExtras().getString(PassangerHomeFragment.FROM_LOCATION_ADD);
        from_city = getIntent().getExtras().getString(PassangerHomeFragment.FROM_LOCATION_CITY);
        to_add = getIntent().getExtras().getString(PassangerHomeFragment.TO_LOCATION_ADD);
        to_city = getIntent().getExtras().getString(PassangerHomeFragment.TO_LOCATION_CITY);

        Log.v(TAG, "FROM ADD:" + from_add);
        Log.v(TAG, "FROM CITY:" + from_city);
        Log.v(TAG, "TO ADD:" + to_add);
        Log.v(TAG, "TO CITY:" + to_city);
    }



}
