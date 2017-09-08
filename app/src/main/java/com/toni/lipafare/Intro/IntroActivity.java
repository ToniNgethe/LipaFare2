package com.toni.lipafare.Intro;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.toni.lipafare.Helper.Global;
import com.toni.lipafare.Operator.OperatorIntroActivity;
import com.toni.lipafare.Passanger.PassangerPanel;
import com.toni.lipafare.R;

public class IntroActivity extends AppCompatActivity {

    private TextView loading;
    private ImageView check;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    private DatabaseReference mUsers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);

        //view
        loading = (TextView) findViewById(R.id.tv_intro_indicator);
        check = (ImageView) findViewById(R.id.iv_intro_net);

        //firebase
        mAuth = FirebaseAuth.getInstance();
        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

                if (firebaseAuth.getCurrentUser() == null) {
                    startActivity(new Intent(IntroActivity.this, SignInActivity.class));
                    finish();
                }
            }
        };
        mUsers = FirebaseDatabase.getInstance().getReference().child("Users");

        check();


    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthStateListener);
    }

    private void check() {

        //check for internet
        if (Global.isConnected(this)) {

            //check if there is user
            if (mAuth.getCurrentUser() != null) {

                //user is logged in,,, so get category
                DatabaseReference cu = mUsers.child(mAuth.getCurrentUser().getUid());
                cu.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        try {
                            if (dataSnapshot.exists()) {
                                String category = dataSnapshot.child("category").getValue().toString();

                                if (category.equals("Operator")) {

                                    new Handler().postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            //user is an operator
                                            startActivity(new Intent(IntroActivity.this, OperatorIntroActivity.class));
                                            finish();
                                        }
                                    }, 2000);


                                } else {

                                    new Handler().postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            //user is a passanger
                                            startActivity(new Intent(IntroActivity.this, PassangerPanel.class));
                                            finish();
                                        }
                                    }, 2000);
                                }

                            } else {

                                //user is not found...redirect user to set up
                                //Toast.makeText(IntroActivity.this,"No user found..",Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(IntroActivity.this, SetUpActivity.class));
                                finish();
                            }
                        } catch (NullPointerException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Toast.makeText(IntroActivity.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

            }

        } else {
            loading.setText("Network Error..");
            check.setVisibility(View.VISIBLE);
        }
    }
}
