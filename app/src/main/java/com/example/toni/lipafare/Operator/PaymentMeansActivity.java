package com.example.toni.lipafare.Operator;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.Toast;

import com.example.toni.lipafare.Intro.SignInActivity;
import com.example.toni.lipafare.Operator.Dialog_mat.Dialog_Saf;
import com.example.toni.lipafare.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class PaymentMeansActivity extends AppCompatActivity implements View.OnClickListener {

    private FloatingActionButton fab;
    private CardView saf;

    private DatabaseReference mPays;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_means);


        //views
        fab = (FloatingActionButton) findViewById(R.id.fab_passangermatatupay);
        fab.setOnClickListener(this);
        saf = (CardView) findViewById(R.id.cardView_paymentmeans_mpesa);
        saf.setOnClickListener(this);

        //auth listener
        mAuth = FirebaseAuth.getInstance();
        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (firebaseAuth.getCurrentUser() == null) {
                    startActivity(new Intent(PaymentMeansActivity.this, SignInActivity.class));
                    finish();
                }
            }
        };

    }

    private String saccoKey() {
        return getIntent().getExtras().getString("KEY");
    }

    @Override
    protected void onStart() {
        super.onStart();

        mAuth.addAuthStateListener(mAuthStateListener);
    }

    @Override
    public void onClick(View v) {
        if (v == saf) {

            if (saccoKey() != null) {

                Dialog_Saf dialog_saf = new Dialog_Saf(PaymentMeansActivity.this, saccoKey());
                dialog_saf.setCanceledOnTouchOutside(false);
                dialog_saf.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog_saf.show();

            } else {
                Toast.makeText(PaymentMeansActivity.this, "Sacco key not found", Toast.LENGTH_SHORT).show();
            }

        } else if (v == fab) {

            //check if payment means is inserted
            mPays = FirebaseDatabase.getInstance().getReference().child("Paymeans");
            mPays.child(saccoKey()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    if (dataSnapshot.exists()){
                        startActivity(new Intent(PaymentMeansActivity.this, OperatorDashBoard.class));
                        finish();
                    }else{
                        Toast.makeText(PaymentMeansActivity.this, "You have to enter at least one payment means", Toast.LENGTH_SHORT).show();
                    }

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }

}
