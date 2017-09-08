package com.toni.lipafare.Passanger;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.toni.lipafare.Passanger.PassModel.PassangerMatatuModel;
import com.toni.lipafare.Passanger.PassangerAdapter.PaasangerQueryAdapter;
import com.toni.lipafare.Passanger.PassangerAdapter.ScheduleAdapter;
import com.toni.lipafare.R;

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

        final Query searc_Q = mMques.orderByChild("sacco").equalTo(saccoKey);
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
                        Log.d(TAG, "FROM USER" + from);
                        Log.d(TAG, "FROM LOCATION" + ds.child("stage").getValue().toString());

//                        matList.clear();
//                        matKeys.clear();

                        if (ds.child("stage").getValue().toString().equals(from)) {
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
                    }

                } else {
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
//            new SweetAlertDialog(MatatuSchedule.this, SweetAlertDialog.ERROR_TYPE)
//                    .setTitleText("Oops...")
//                    .setContentText("Sorry, no matatu at this stage")
//                    .show();
//        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return false;
    }
}
