package com.example.toni.lipafare.Intro;


import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.toni.lipafare.Dialogs.ForgotPasswordDialog;
import com.example.toni.lipafare.Helper.Global;
import com.example.toni.lipafare.Operator.OperatorIntroActivity;
import com.example.toni.lipafare.Passanger.PassangerPanel;
import com.example.toni.lipafare.R;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SignInActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int RC_SIGN_IN = 100;
    private static final String TAG = SignInActivity.class.getSimpleName();
    private EditText _useremail, _userpassword;
    private Button _login, signup, forgotPass;
    private SignInButton google;
    private GoogleSignInOptions gso;
    private GoogleApiClient mGoogleApiClient;

    //firebase
    private FirebaseAuth mAuth;
    private DatabaseReference mUsers;

    private ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        //google sign
        // Configure Google Sign In
        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        // Build a GoogleApiClient with access to the Google Sign-In API and the
// options specified by gso.
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                        Toast.makeText(SignInActivity.this, connectionResult.getErrorMessage(), Toast.LENGTH_SHORT).show();
                    }
                } /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        //InitiateViews && firebase
        views();


        google.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!Global.isConnected(SignInActivity.this)) {
                    startActivity(new Intent(SignInActivity.this, NoInternetActivity.class));
                    finish();
                } else {
                    mProgressDialog = new ProgressDialog(SignInActivity.this);
                    mProgressDialog.setMessage("Authenticating user");
                    mProgressDialog.setCanceledOnTouchOutside(false);
                    mProgressDialog.show();

                    Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
                    startActivityForResult(signInIntent, RC_SIGN_IN);
                }
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = result.getSignInAccount();
                firebaseAuthWithGoogle(account);
            } else {

                mProgressDialog.dismiss();
                Toast.makeText(this, "Google signin failed : " + result.getStatus().getStatusMessage(), Toast.LENGTH_SHORT).show();
            }
        }

    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "signInWithCredential:onComplete:" + task.isSuccessful());

                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {

                            mProgressDialog.dismiss();
                            Log.w(TAG, "signInWithCredential", task.getException());
                            Toast.makeText(SignInActivity.this, "Authentication failed.",

                                    Toast.LENGTH_SHORT).show();
                        } else {

                            checkUser();
                        }
                        // ...
                    }
                });

    }

    private void views() {

        //google btn
        google = (SignInButton) findViewById(R.id.googlesignin);

        _useremail = (EditText) findViewById(R.id.et_signin_email);
        _userpassword = (EditText) findViewById(R.id.et_signin_pass);

        _login = (Button) findViewById(R.id.btn_sigin_login);
        _login.setOnClickListener(this);
        signup = (Button) findViewById(R.id.btn_signin_signup);
        signup.setOnClickListener(this);
        forgotPass = (Button) findViewById(R.id.btn_sigin_forgotPass);
        forgotPass.setOnClickListener(this);

        //firebase...
        mAuth = FirebaseAuth.getInstance();
        mUsers = FirebaseDatabase.getInstance().getReference().child("Users");


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_sigin_login:

                if (!Global.isConnected(SignInActivity.this)) {
                    startActivity(new Intent(SignInActivity.this, NoInternetActivity.class));
                    finish();
                } else {
                    signinUser();
                }

                break;
            case R.id.btn_signin_signup:

                //open signup
                Intent signup = new Intent(SignInActivity.this, RegisterActivity.class);
                startActivity(signup);

                break;
            case R.id.btn_sigin_forgotPass:

                //open reset dialog
                ForgotPasswordDialog forgotPasswordDialog = new ForgotPasswordDialog(this);
                forgotPasswordDialog.setCanceledOnTouchOutside(false);
                forgotPasswordDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                forgotPasswordDialog.show();

                break;
        }
    }

    private void signinUser() {

        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("authenticating user..");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        if (!TextUtils.isEmpty(_useremail.getText().toString().trim()) && !TextUtils.isEmpty(_userpassword.getText().toString().trim())) {

            mAuth.signInWithEmailAndPassword(_useremail.getText().toString().trim(), _userpassword.getText().toString().trim())
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {

                            if (task.isSuccessful()) {

                                progressDialog.dismiss();

                                DatabaseReference currentUser = mUsers.child(mAuth.getCurrentUser().getUid());
                                currentUser.child("category").addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {

                                        try {
                                            //get category

                                            if (dataSnapshot.getValue().toString().equals("Passenger")) {

                                                //open passenger dashboard
                                                Toast.makeText(SignInActivity.this, "Passenger", Toast.LENGTH_SHORT).show();
                                                startActivity(new Intent(SignInActivity.this, PassangerPanel.class));
                                                finish();

                                            } else {

                                                //open operator dashboard
                                                startActivity(new Intent(SignInActivity.this, OperatorIntroActivity.class));
                                                finish();
                                            }
                                        }catch (Exception e){
                                            e.printStackTrace();
                                        }
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });

                            } else {

                                progressDialog.dismiss();
                                Toast.makeText(SignInActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();

                            }

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    progressDialog.dismiss();
                    Toast.makeText(SignInActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });

        } else {

            progressDialog.dismiss();

            Toast.makeText(this, R.string.field_message, Toast.LENGTH_SHORT).show();
        }

    }

    public void checkUser() {

        if (mAuth.getCurrentUser() != null) {
            DatabaseReference users = FirebaseDatabase.getInstance().getReference().child("Users");
            users.child(mAuth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {

                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    mProgressDialog.dismiss();

                    try {


                        if (!dataSnapshot.exists()) {
                            //user does not exist
                            Intent intent = new Intent(SignInActivity.this, SetUpActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                            finish();

                        } else {
                            //get category
                            if (dataSnapshot.child("category").getValue().equals("Operator")) {
                                startActivity(new Intent(SignInActivity.this, OperatorIntroActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                                finish();
                            } else {
                                startActivity(new Intent(SignInActivity.this, PassangerPanel.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                                finish();
                            }


                        }
                    }catch (NullPointerException e){
                        e.printStackTrace();
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }

}
