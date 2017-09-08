package com.toni.lipafare.Operator;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.toni.lipafare.Helper.Global;
import com.toni.lipafare.Intro.NoInternetActivity;
import com.toni.lipafare.R;

public class OperatorIntroActivity extends AppCompatActivity {

    private static final String TAG = OperatorIntroActivity.class.getSimpleName();
    private FirebaseAuth mAuth;
    private DatabaseReference mUsers, mOperators;
    private TextView name, email;
    private ImageView profile;
    private FloatingActionButton add;
    private boolean cot;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_operator_intro);

        //firebase
        mAuth = FirebaseAuth.getInstance();
        mUsers = FirebaseDatabase.getInstance().getReference().child("Users");
        mOperators = FirebaseDatabase.getInstance().getReference().child("Sacco");

        //check if any operator exists
        cot = true;
        check();

        //views
        name = (TextView) findViewById(R.id.tv_operatorintro_name);
        email = (TextView) findViewById(R.id.tv_operatorintro_email);
        profile = (ImageView) findViewById(R.id.iv_operatorintro);
        add = (FloatingActionButton) findViewById(R.id.floatingbtn_operatorintro);

        getData();

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!Global.isConnected(OperatorIntroActivity.this)) {

                    //if its not connected connected
                    startActivity(new Intent(OperatorIntroActivity.this, NoInternetActivity.class));
                    finish();

                }else {

                    //if connected
                    startActivity(new Intent(OperatorIntroActivity.this, AddSaccoActivity.class));
                    finish();

                }
            }
        });
    }

    private void check() {


        Query q = mOperators.orderByChild("admin").equalTo(mAuth.getCurrentUser().getUid());
        q.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (dataSnapshot.exists()) {

                    for (DataSnapshot ds : dataSnapshot.getChildren()){

                        final String key = ds.getKey();

                        Log.d(TAG, key);

                        DatabaseReference m = mOperators.child(key);
                        m.child("name").addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {

                                if (dataSnapshot.exists()) {

                                    DatabaseReference mRoutes = FirebaseDatabase.getInstance().getReference().child("Route");
                                    Query q = mRoutes.orderByChild("sacco").equalTo(key);

                                    q.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {

                                            if (dataSnapshot.exists()){

                                                //check payments
                                                DatabaseReference mPays = FirebaseDatabase.getInstance().getReference().child("Paymeans");
                                                mPays.child(key).addListenerForSingleValueEvent(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(DataSnapshot dataSnapshot) {

                                                        if (!dataSnapshot.exists()){
                                                            //redirect to map
                                                            Intent i = new Intent(OperatorIntroActivity.this, PaymentMeansActivity.class);
                                                            i.putExtra("KEY", key);
                                                            startActivity(i);
                                                            finish();
                                                        }else {
                                                            //redirect to main
                                                            startActivity(new Intent(OperatorIntroActivity.this, OperatorDashBoard.class));
                                                            finish();
                                                        }

                                                    }

                                                    @Override
                                                    public void onCancelled(DatabaseError databaseError) {

                                                    }
                                                });
                                            }else {
                                                //redirect to map
                                                startActivity(new Intent(OperatorIntroActivity.this, Operator.class));
                                                finish();
                                            }
                                        }
                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {
                                            Toast.makeText(OperatorIntroActivity.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    });

                                } else {
                                    //redirect to add
                                    startActivity(new Intent(OperatorIntroActivity.this, AddSaccoActivity.class));
                                    finish();
                                }
                            }
                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                Toast.makeText(OperatorIntroActivity.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(OperatorIntroActivity.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getData() {

        if (mAuth.getCurrentUser() != null) {

            DatabaseReference cu = mUsers.child(mAuth.getCurrentUser().getUid());
            cu.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(final DataSnapshot dataSnapshot) {

                    if (dataSnapshot.exists()) {

                        try {
                            name.setText(dataSnapshot.child("user").getValue().toString());
                            email.setText(mAuth.getCurrentUser().getEmail());


                            new Handler().post(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        Glide.with(OperatorIntroActivity.this).load(dataSnapshot.child("image").getValue().toString())
                                                .crossFade()
                                                .thumbnail(0.5f)
                                                .diskCacheStrategy(DiskCacheStrategy.ALL)
                                                .error(R.mipmap.profile2)
                                                .into(profile);
                                    }catch (Exception e){
                                        e.printStackTrace();
                                    }
                                }
                            });

                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    } else {
                        Toast.makeText(OperatorIntroActivity.this, "Error: User not found", Toast.LENGTH_SHORT).show();
                    }

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Toast.makeText(OperatorIntroActivity.this, "Error: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });

        }

    }
}
