package com.example.toni.lipafare.Passanger;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.example.toni.lipafare.Passanger.PassModel.PassangerMatatuModel;
import com.example.toni.lipafare.Passanger.PassangerAdapter.PaasangerQueryAdapter;
import com.example.toni.lipafare.Passanger.PassangerAdapter.ScheduleAdapter;
import com.example.toni.lipafare.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MatatuSchedule extends AppCompatActivity {

    private static final String TAG = MatatuSchedule.class.getSimpleName();
    private RecyclerView rv;
    private LinearLayout linearLayout, indicator;
    private String saccoKey, to, from;
    private RelativeLayout relativeLayout;
    private DatabaseReference mMatatu, mMques;

    private List<PassangerMatatuModel> matList;
    private List<String> matKeys;
    private ScheduleAdapter scheduleAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_matatu_schedule);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Matatu/Bus Line up");
        getSupportActionBar().setHomeAsUpIndicator(R.mipmap.ic_close_white_24dp);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        //views
        rv = (RecyclerView) findViewById(R.id.rv_schedule);
        linearLayout = (LinearLayout) findViewById(R.id.linear_schedule);
        indicator = (LinearLayout) findViewById(R.id.linearLayout_indicator);
        relativeLayout = (RelativeLayout) findViewById(R.id.linearlayout_selectedsaccomat_svhedulr);

        LinearLayoutManager lm = new LinearLayoutManager(this);
        lm.setReverseLayout(true);
        lm.setStackFromEnd(true);
        rv.setLayoutManager(lm);

        //get key from intent
        saccoKey = getIntent().getExtras().getString(PaasangerQueryAdapter.SACCO_KEY);
        from = getIntent().getExtras().getString(PaasangerQueryAdapter.FROM_ADD);
        to = getIntent().getExtras().getString(PaasangerQueryAdapter.TO_ADD);

        //firebase
        mMatatu = FirebaseDatabase.getInstance().getReference().child("Matatu");
        mMques = FirebaseDatabase.getInstance().getReference().child("Queue");

    }

    @Override
    protected void onStart() {
        super.onStart();

        matKeys = new ArrayList<>();
        matList = new ArrayList<>();
        Query q = mMatatu.orderByChild("sacco").equalTo(saccoKey);

        q.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists()) {
                    rv.setVisibility(View.GONE);
                    linearLayout.setVisibility(View.GONE);
                    indicator.setVisibility(View.GONE);
                    relativeLayout.setVisibility(View.VISIBLE);
                } else {
//
//                    matKeys.clear();
//                    matList.clear();
//                    for (DataSnapshot ds: dataSnapshot.getChildren()){
//
//                        DatabaseReference cs = mMques.child(ds.getKey());
//                        cs.addValueEventListener(new ValueEventListener() {
//                            @Override
//                            public void onDataChange(final DataSnapshot dataSnapshot) {
//
//                                matKeys.clear();
//                                matList.clear();
//
//                                if (dataSnapshot.exists()){
//                                    indicator.setVisibility(View.VISIBLE);
//                                    rv.setVisibility(View.VISIBLE);
//                                    linearLayout.setVisibility(View.GONE);
//                                    relativeLayout.setVisibility(View.GONE);
//
//
//                                    Log.d(TAG,"sasdsad"+ dataSnapshot.getKey());
//
//
//                                    DatabaseReference cm = mMatatu.child(dataSnapshot.getKey());
//                                    cm.addValueEventListener(new ValueEventListener() {
//                                        @Override
//                                        public void onDataChange(DataSnapshot dt) {
//
//                                                Log.d(TAG,"sasdsad"+ dt.getValue().toString());
//
//                                                matList.add(dt.getValue(PassangerMatatuModel.class));
//                                                matKeys.add(dataSnapshot.getKey());
//                                                ScheduleAdapter scheduleAdapter = new ScheduleAdapter(MatatuSchedule.this, matList, matKeys, from, to);
//                                                rv.setAdapter(scheduleAdapter);
//
//                                        }
//
//                                        @Override
//                                        public void onCancelled(DatabaseError databaseError) {
//
//                                        }
//                                    });
//
//                                }
//                               // scheduleAdapter.notifyDataSetChanged();
//                                //else {
////                                    indicator.setVisibility(View.GONE);
////                                    rv.setVisibility(View.GONE);
////                                    linearLayout.setVisibility(View.VISIBLE);
////                                    relativeLayout.setVisibility(View.GONE);
//                              //  }
//
//                            }
//
//                            @Override
//                            public void onCancelled(DatabaseError databaseError) {
//
//                            }
//                        });
//
//                    }

                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        Query searc_Q = mMques.orderByChild("sacco").equalTo(saccoKey);
        searc_Q.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {


                if (dataSnapshot.exists()) {
                    rv.setVisibility(View.VISIBLE);
                    linearLayout.setVisibility(View.GONE);
                    indicator.setVisibility(View.VISIBLE);
                    relativeLayout.setVisibility(View.GONE);


                    for (final DataSnapshot ds : dataSnapshot.getChildren()) {
                        Log.d(TAG, "Queried matatu key" + ds.getKey());

                        matList.clear();
                        matKeys.clear();

                        DatabaseReference cm = mMatatu.child(ds.getKey());
                        cm.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {


                                Log.d(TAG, "Queried matatus" + dataSnapshot.getValue());

                                matList.add(dataSnapshot.getValue(PassangerMatatuModel.class));
                                matKeys.add(ds.getKey());

                                scheduleAdapter = new ScheduleAdapter(MatatuSchedule.this, matList, matKeys, from, to);
                                rv.setAdapter(scheduleAdapter);

                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });


                    }
                }else {
                    indicator.setVisibility(View.GONE);
                    rv.setVisibility(View.GONE);
                    linearLayout.setVisibility(View.VISIBLE);
                    relativeLayout.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


//        if (matList.isEmpty()) {
//            indicator.setVisibility(View.GONE);
//            rv.setVisibility(View.GONE);
//            linearLayout.setVisibility(View.VISIBLE);
//            relativeLayout.setVisibility(View.GONE);
//        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return false;
    }
}