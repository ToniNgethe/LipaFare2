package com.example.toni.lipafare.Operator;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.toni.lipafare.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class MatatuCredentials extends AppCompatActivity {

    private EditText _driverEmail, _driverPass, _conDriver, _conPassword;
    private FloatingActionButton submit;

    private DatabaseReference mMatatus, mOthers;
    private FirebaseAuth mAuth,mAuth2,mAuth3;
    private String matatuKey;

    private  FirebaseApp myApp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_matatu_credentials);

        //get matatu key
        matKey();
        //views
        views();
        //firebase...
        firebase();
        //listeners
        listeners();
    }

    private void listeners() {
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!_driverEmail.getText().toString().isEmpty() && !_driverPass.getText().toString().isEmpty() && !_conDriver.getText().toString().isEmpty()
                        && !_conPassword.getText().toString().isEmpty()) {

                    //loading....
                    final SweetAlertDialog pDialog = new SweetAlertDialog(MatatuCredentials.this, SweetAlertDialog.PROGRESS_TYPE);
                    pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
                    pDialog.setTitleText("Please wait..");
                    pDialog.setContentText("Saving driver details");
                    pDialog.setCancelable(false);
                    pDialog.show();

                    //create account for driver...
                    mAuth2 = FirebaseAuth.getInstance(myApp);
                    mAuth2.createUserWithEmailAndPassword(_driverEmail.getText().toString(), _driverPass.getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {

                            if (task.isSuccessful()){

                                DatabaseReference cd = mOthers.child(mAuth2.getCurrentUser().getUid());
                                cd.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {

                                        dataSnapshot.child("role").getRef().setValue("driver");
                                        dataSnapshot.child("mat").getRef().setValue(matatuKey).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {

                                                if (task.isSuccessful()){
                                                    mAuth2.signOut();

                                                    final FirebaseAuth mAuth3 = FirebaseAuth.getInstance(myApp);
                                                    mAuth3.createUserWithEmailAndPassword(_conDriver.getText().toString(),_conPassword.getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<AuthResult> task) {

                                                            if (task.isSuccessful()){

                                                                DatabaseReference cc = mOthers.child(mAuth3.getCurrentUser().getUid());
                                                                cc.addListenerForSingleValueEvent(new ValueEventListener() {
                                                                    @Override
                                                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                                                        dataSnapshot.child("role").getRef().setValue("conductor");
                                                                        dataSnapshot.child("matatu").getRef().setValue(matatuKey).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                            @Override
                                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                                if (task.isSuccessful()){
                                                                                    mAuth3.signOut();
                                                                                    pDialog.dismiss();
                                                                                    Intent i = new Intent(MatatuCredentials.this, OperatorDashBoard.class);
                                                                                    startActivity(i);

                                                                                }else {
                                                                                    mAuth3.signOut();
                                                                                    pDialog.dismiss();
                                                                                    new SweetAlertDialog(MatatuCredentials.this, SweetAlertDialog.ERROR_TYPE)
                                                                                            .setTitleText("Oops...")
                                                                                            .setContentText(task.getException().getMessage())
                                                                                            .show();
                                                                                }
                                                                            }
                                                                        });
                                                                    }

                                                                    @Override
                                                                    public void onCancelled(DatabaseError databaseError) {
                                                                        mAuth3.signOut();
                                                                        pDialog.dismiss();
                                                                        new SweetAlertDialog(MatatuCredentials.this, SweetAlertDialog.ERROR_TYPE)
                                                                                .setTitleText("Oops...")
                                                                                .setContentText(databaseError.getMessage())
                                                                                .show();
                                                                    }
                                                                });

                                                            }else{
                                                                mAuth3.signOut();
                                                                pDialog.dismiss();
                                                                new SweetAlertDialog(MatatuCredentials.this, SweetAlertDialog.ERROR_TYPE)
                                                                        .setTitleText("Oops...")
                                                                        .setContentText(task.getException().getMessage())
                                                                        .show();
                                                            }
                                                        }
                                                    });
                                                }else {
                                                    mAuth2.signOut();
                                                    pDialog.dismiss();
                                                    new SweetAlertDialog(MatatuCredentials.this, SweetAlertDialog.ERROR_TYPE)
                                                            .setTitleText("Oops...")
                                                            .setContentText(task.getException().getMessage())
                                                            .show();
                                                }

                                            }
                                        });

                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {
                                        pDialog.dismiss();
                                        new SweetAlertDialog(MatatuCredentials.this, SweetAlertDialog.ERROR_TYPE)
                                                .setTitleText("Oops...")
                                                .setContentText(databaseError.getMessage())
                                                .show();
                                    }
                                });

                            }else {
                                pDialog.dismiss();
                                new SweetAlertDialog(MatatuCredentials.this, SweetAlertDialog.ERROR_TYPE)
                                        .setTitleText("Oops...")
                                        .setContentText(task.getException().getMessage())
                                        .show();
                            }

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            pDialog.dismiss();
                            new SweetAlertDialog(MatatuCredentials.this, SweetAlertDialog.ERROR_TYPE)
                                    .setTitleText("Oops...")
                                    .setContentText(e.getMessage())
                                    .show();
                        }
                    });

                } else {
                    Toast.makeText(MatatuCredentials.this, "Field(s) empty", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void firebase() {
        mAuth = FirebaseAuth.getInstance();
        mMatatus = FirebaseDatabase.getInstance().getReference().child("Matatu");
        mOthers = FirebaseDatabase.getInstance().getReference().child("Matatu_Credentials");

        //

        FirebaseOptions firebaseOptions = new FirebaseOptions.Builder()
                .setDatabaseUrl("https://lipafare-96ab1.firebaseio.com/")
                .setApiKey("AIzaSyDYHIYrD90k4qqJ7Uv8Foq6STUjz9REuSM")
                .setApplicationId("lipafare-96ab1").build();

        myApp = FirebaseApp.initializeApp(getApplicationContext(),firebaseOptions,
                "LipaFare");

    }

    private void matKey() {
        matatuKey = getIntent().getExtras().getString(AddMatatu.ADD_MATATU);
    }

    private void views() {
        _driverEmail = (EditText) findViewById(R.id.et_matatu_driverEmail);
        _driverPass = (EditText) findViewById(R.id.et_matatu_driverPass);
        _conDriver = (EditText) findViewById(R.id.et_matatu_conEmail);
        _conPassword = (EditText) findViewById(R.id.et_matatu_conPass);
        submit = (FloatingActionButton) findViewById(R.id.fab_matatuCredentials_submit);
    }
}
