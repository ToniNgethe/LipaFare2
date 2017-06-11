package com.example.toni.lipafare.Operator;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.toni.lipafare.Helper.Global;
import com.example.toni.lipafare.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class AddSaccoActivity extends AppCompatActivity {

    private static final int GALLARY_INTENT = 100;
    private static final String TAG = AddSaccoActivity.class.getSimpleName();
    private ImageButton sacco_image;
    private EditText sacco_name, sacco_email, sacco_phone, sacco_cost;

    private Uri uri = null;

    private FirebaseAuth mAuth;
    private DatabaseReference mSacco;
    private StorageReference mSaccoPics;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_sacco);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //add title
        getSupportActionBar().setTitle("Add Sacco");

        //views
        sacco_image = (ImageButton) findViewById(R.id.imgbtn_addsacco);
        sacco_name = (EditText) findViewById(R.id.et_addsacco_name);
        sacco_email = (EditText) findViewById(R.id.et_addsacco_email);
        sacco_phone = (EditText) findViewById(R.id.et_addsacco_number);
        sacco_cost = (EditText) findViewById(R.id.et_addsacco_cost);

        //firebase
        mAuth = FirebaseAuth.getInstance();
        mSacco = FirebaseDatabase.getInstance().getReference().child("Sacco");
        mSaccoPics = FirebaseStorage.getInstance().getReference().child("Sacco_pics");

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab_addsacco);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (Global.isConnected(AddSaccoActivity.this)) {
                    submitDetails();
                } else {
                    Toast.makeText(AddSaccoActivity.this,"No connection :-(", Toast.LENGTH_SHORT).show();
                }


            }
        });


        //get image
        sacco_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent gallary = new Intent(Intent.ACTION_GET_CONTENT);
                gallary.setType("image/*");
                startActivityForResult(gallary, GALLARY_INTENT);
            }
        });
    }

    private void submitDetails() {

        if (uri != null) {

            if (isEmpty()) {

                if (checkNumber(sacco_phone.getText().toString())) {
                    final SweetAlertDialog mProgressDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
                    mProgressDialog.getProgressHelper().setBarColor(Color.parseColor("#f50057"));
                    mProgressDialog.setTitleText("Saving sacco details..");
                    mProgressDialog.setCancelable(false);
                    mProgressDialog.show();
                    //add data to db

                    final DatabaseReference cs = mSacco.push();

                    mSaccoPics.child(cs.getKey()).putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                            //add user details
                            cs.child("name").setValue(sacco_name.getText().toString());
                            cs.child("email").setValue(sacco_email.getText().toString());
                            cs.child("number").setValue(sacco_phone.getText().toString());
                            cs.child("admin").setValue(mAuth.getCurrentUser().getUid());
                            cs.child("cost").setValue(sacco_cost.getText().toString());
                            cs.child("image").setValue(taskSnapshot.getDownloadUrl().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {

                                    mProgressDialog.dismiss();

                                    if (task.isSuccessful()) {

                                        startActivity(new Intent(AddSaccoActivity.this, Operator.class));
                                        finish();

                                    } else {

                                        new SweetAlertDialog(AddSaccoActivity.this, SweetAlertDialog.ERROR_TYPE)
                                                .setTitleText("Oops something went wrong")
                                                .setContentText(task.getException().getMessage())
                                                .show();
                                    }
                                }
                            });


                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            mProgressDialog.dismiss();

                            new SweetAlertDialog(AddSaccoActivity.this, SweetAlertDialog.ERROR_TYPE)
                                    .setTitleText("Oops..")
                                    .setContentText(e.getMessage())
                                    .show();

                        }
                    });


                } else {
                    Toast.makeText(AddSaccoActivity.this, "Wrong mobile format", Toast.LENGTH_SHORT).show();
                }

            } else {
                Toast.makeText(AddSaccoActivity.this, "Field(s) cannot be empty", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(AddSaccoActivity.this, "Select Sacco logo", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GALLARY_INTENT) {

            if (resultCode == RESULT_OK) {

                Uri imageUri = data.getData();

                CropImage.activity(imageUri)
                        .setGuidelines(com.theartofdev.edmodo.cropper.CropImageView.Guidelines.ON)
                        .setAspectRatio(2, 1)
                        .start(this);

            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(AddSaccoActivity.this, "Operation cancelled by user", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(AddSaccoActivity.this, "Error in picking image", Toast.LENGTH_SHORT).show();
            }
        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {

                uri = result.getUri();

                sacco_image.setImageURI(uri);

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
                Log.d(TAG, error.getMessage());
            }
        }

    }

    private boolean isEmpty() {

        boolean empty = false;

        if (!TextUtils.isEmpty(sacco_name.getText()) || !TextUtils.isEmpty(sacco_email.getText()) || !TextUtils.isEmpty(sacco_phone.getText())) {
            empty = true;
        }

        return empty;
    }

    private boolean checkNumber(String sPhoneNumber) {

        boolean check = false;

        Pattern pattern = Pattern.compile("07\\d{2}\\d{6}");
        Matcher matcher = pattern.matcher(sPhoneNumber);

        if (matcher.matches()) {
            check = true;
        }

        return check;
    }
}
