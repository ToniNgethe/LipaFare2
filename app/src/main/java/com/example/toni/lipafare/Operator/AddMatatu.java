package com.example.toni.lipafare.Operator;

import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.toni.lipafare.Helper.InputImageCompressor;
import com.example.toni.lipafare.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.Calendar;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class AddMatatu extends AppCompatActivity implements View.OnClickListener {

    private static final int MATATU_LOGO = 100;
    private static final int DRIVER_PROFILE = 102;
    private static final int CONDUCTOR_PROFILE = 104;
    private static final String TAG = AddMatatu.class.getSimpleName();
    private static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 2;
    private static final int DRIVER_IMAGE_PERMISSION = 3;
    private static final int CONDUCTOR_IMAGE_PERMISSION = 4;
    public static final String ADD_MATATU = "MATATU_KEY";
    private ImageButton matLogo;
    private ImageView drive_pic, cond_pic;
    private Button submit;
    private EditText name, sits, plate, from, to;
    private Uri matUri = null;
    private Uri driverUri = null;
    private Uri condUri = null;
    private int mHour, mMinute;
    private RadioGroup status;
    private RadioButton chosen_status;

    //Firebase
    private FirebaseAuth mAuth;
    private DatabaseReference mSaccos, mUsers, mMatatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_matatu);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_addmatatu);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeAsUpIndicator(R.mipmap.ic_close_black_24dp);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        firebase();
        views();
        imageListeners();

    }

    private void firebase() {

        mAuth = FirebaseAuth.getInstance();
        mSaccos = FirebaseDatabase.getInstance().getReference().child("Sacco");
        mMatatus = FirebaseDatabase.getInstance().getReference().child("Matatu");

    }

    private void imageListeners() {

        matLogo.setOnClickListener(this);

        drive_pic.setOnClickListener(this);

        cond_pic.setOnClickListener(this);

        from.setOnClickListener(this);
        to.setOnClickListener(this);
        submit.setOnClickListener(this);


    }

    private void views() {
        status = (RadioGroup) findViewById(R.id.radiogroup_addmatatu);
        submit = (Button) findViewById(R.id.btn_addmatatu_submit);
        matLogo = (ImageButton) findViewById(R.id.imageButton_addmatatu_logo);
        drive_pic = (ImageView) findViewById(R.id.iv_addmatatu_driver);
        cond_pic = (ImageView) findViewById(R.id.iv_addmatatu_conductor);
        name = (EditText) findViewById(R.id.et_addmatatu_name);
        sits = (EditText) findViewById(R.id.et_addmatatu_sits);
        plate = (EditText) findViewById(R.id.et_addmatatu_plate);

        from = (EditText) findViewById(R.id.et_addmatatu_from);
        from.setInputType(InputType.TYPE_NULL);

        to = (EditText) findViewById(R.id.et_addmatatu_to);
        to.setInputType(InputType.TYPE_NULL);
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return false;
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.imageButton_addmatatu_logo:

                Log.d(TAG, "MATATU IMAGE INITIATED");
                pickMatImage();

                break;
            case R.id.iv_addmatatu_driver:

                Log.d(TAG, "DRIVER IMAGE INITIATED");
                picDriverImage();

                break;
            case R.id.iv_addmatatu_conductor:

                Log.d(TAG, "CONDUCTOR IMAGE INITIATED");
                pickConductorImage();

                break;

            case R.id.et_addmatatu_from:

                // Get Current Time
                final Calendar c = Calendar.getInstance();
                mHour = c.get(Calendar.HOUR_OF_DAY);
                mMinute = c.get(Calendar.MINUTE);

                // Launch Time Picker Dialog
                TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                        new TimePickerDialog.OnTimeSetListener() {

                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay,
                                                  int minute) {

                                from.setText(hourOfDay + ":" + minute);
                            }
                        }, mHour, mMinute, true);
                timePickerDialog.show();

                break;


            case R.id.et_addmatatu_to:

                // Get Current Time
                final Calendar c1 = Calendar.getInstance();
                mHour = c1.get(Calendar.HOUR_OF_DAY);
                mMinute = c1.get(Calendar.MINUTE);

                // Launch Time Picker Dialog
                TimePickerDialog timePickerDialog1 = new TimePickerDialog(this,
                        new TimePickerDialog.OnTimeSetListener() {

                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay,
                                                  int minute) {

                                to.setText(hourOfDay + ":" + minute);
                            }
                        }, mHour, mMinute, true);
                timePickerDialog1.show();

                break;

            case R.id.btn_addmatatu_submit:

                saveMatatu();

                break;
        }

    }

    private void picDriverImage() {

        if (Build.VERSION.SDK_INT >= 23) {
            // Here, thisActivity is the current activity
            if (ContextCompat.checkSelfPermission(AddMatatu.this,
                    android.Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {

                // Should we show an explanation?
                if (ActivityCompat.shouldShowRequestPermissionRationale(AddMatatu.this,
                        android.Manifest.permission.READ_EXTERNAL_STORAGE)) {

                    // Show an expanation to the user *asynchronously* -- don't block
                    // this thread waiting for the user's response! After the user
                    // sees the explanation, try again to request the permission.

                } else {

                    // No explanation needed, we can request the permission.

                    ActivityCompat.requestPermissions(AddMatatu.this,
                            new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE},
                            DRIVER_IMAGE_PERMISSION);

                    // MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE is an
                    // app-defined int constant. The callback method gets the
                    // result of the request.
                }
            } else {
                ActivityCompat.requestPermissions(AddMatatu.this,
                        new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE},
                        DRIVER_IMAGE_PERMISSION);
            }
        } else {
            Log.d(TAG, "DRIVER IMAGE PERMISSION GRANTED");
            Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
            photoPickerIntent.setType("image/*");
            startActivityForResult(photoPickerIntent, DRIVER_PROFILE);
        }


    }

    private void pickConductorImage() {

        if (Build.VERSION.SDK_INT >= 23) {
            // Here, thisActivity is the current activity
            if (ContextCompat.checkSelfPermission(AddMatatu.this,
                    android.Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {

                // Should we show an explanation?
                if (ActivityCompat.shouldShowRequestPermissionRationale(AddMatatu.this,
                        android.Manifest.permission.READ_EXTERNAL_STORAGE)) {

                    // Show an expanation to the user *asynchronously* -- don't block
                    // this thread waiting for the user's response! After the user
                    // sees the explanation, try again to request the permission.

                } else {

                    // No explanation needed, we can request the permission.

                    ActivityCompat.requestPermissions(AddMatatu.this,
                            new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE},
                            CONDUCTOR_IMAGE_PERMISSION);

                    // MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE is an
                    // app-defined int constant. The callback method gets the
                    // result of the request.
                }
            } else {
                ActivityCompat.requestPermissions(AddMatatu.this,
                        new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE},
                        CONDUCTOR_IMAGE_PERMISSION);
            }
        } else {
            Log.d(TAG, "CONDUCTOR IMAGE PERMISSION GRANTED");
            Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
            photoPickerIntent.setType("image/*");
            startActivityForResult(photoPickerIntent, CONDUCTOR_PROFILE);
        }

    }

    private void saveMatatu() {

        if (matUri != null) {

            if (driverUri != null) {

                if (condUri != null) {

                    if (!name.getText().toString().isEmpty() && !sits.getText().toString().isEmpty() && !plate.getText().toString().isEmpty()
                            && !from.getText().toString().isEmpty() && !to.getText().toString().isEmpty()) {

                        //getSelected choice
                        chosen_status = (RadioButton) findViewById(status.getCheckedRadioButtonId());

                        //create thread for saving data to db
                        new Handler().post(new Runnable() {
                            @Override
                            public void run() {

                                final SweetAlertDialog progressDialog = new SweetAlertDialog(AddMatatu.this, SweetAlertDialog.PROGRESS_TYPE);
                                progressDialog.getProgressHelper().setBarColor(Color.parseColor("#f50057"));
                                progressDialog.setTitleText("Saving Matatu/Bus");
                                progressDialog.setCancelable(false);
                                progressDialog.show();

                                //get sacco id
                                if (mAuth.getCurrentUser() != null) {

                                    Query q = mSaccos.orderByChild("admin").equalTo(mAuth.getCurrentUser().getUid());
                                    q.addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {

                                            for (DataSnapshot ds : dataSnapshot.getChildren()) {

                                                final String sacco_key = ds.getKey();
                                                Log.d(TAG, sacco_key);

                                                final DatabaseReference cm = mMatatus.push();

                                                //save images
                                                StorageReference mLogo = FirebaseStorage.getInstance().getReference().child(cm.getKey() + "_matatu");
                                                final StorageReference mDriver = FirebaseStorage.getInstance().getReference().child(cm.getKey() + "_driver");
                                                final StorageReference mCond = FirebaseStorage.getInstance().getReference().child(cm.getKey() + "_cond");

                                                progressDialog.setTitleText("Saving matatu/bus picture..");

                                                mLogo.putFile(matUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                                    @Override
                                                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                                                        final String matatu_link = taskSnapshot.getDownloadUrl().toString();

                                                        progressDialog.setTitleText("Saving driver picture..");
                                                        mDriver.putFile(driverUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                                            @Override
                                                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                                                                final String driver_link = taskSnapshot.getDownloadUrl().toString();

                                                                progressDialog.setTitleText("Saving conductor picture..");

                                                                mCond.putFile(condUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                                                    @Override
                                                                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                                                                        progressDialog.setTitleText("Saving matatu/bus details..");

                                                                        cm.child("sacco").setValue(sacco_key);
                                                                        cm.child("name").setValue(name.getText().toString());
                                                                        cm.child("plate").setValue(plate.getText().toString());
                                                                        cm.child("sits").setValue(sits.getText().toString());
                                                                        cm.child("logo").setValue(matatu_link);
                                                                        cm.child("driver").setValue(driver_link);
                                                                        cm.child("conductor").setValue(taskSnapshot.getDownloadUrl().toString());
                                                                        cm.child("from").setValue(from.getText().toString());
                                                                        cm.child("to").setValue(to.getText().toString());
                                                                        cm.child("status").setValue(chosen_status.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                            @Override
                                                                            public void onComplete(@NonNull Task<Void> task) {

                                                                                progressDialog.dismiss();

                                                                                if (!task.isSuccessful()) {
                                                                                    Toast.makeText(AddMatatu.this, "Error:" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                                                                } else {

                                                                                    String key = cm.getKey();

                                                                                    Intent i = new Intent(AddMatatu.this,MatatuCredentials.class);
                                                                                    i.putExtra(ADD_MATATU, key);
                                                                                    startActivity(i);
                                                                                    finish();
                                                                                }
                                                                            }
                                                                        });

                                                                    }
                                                                }).addOnFailureListener(new OnFailureListener() {
                                                                    @Override
                                                                    public void onFailure(@NonNull Exception e) {
                                                                        Toast.makeText(AddMatatu.this, "Conductor : " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                                                    }
                                                                });

                                                            }
                                                        }).addOnFailureListener(new OnFailureListener() {
                                                            @Override
                                                            public void onFailure(@NonNull Exception e) {
                                                                Toast.makeText(AddMatatu.this, "Driver : " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                                            }
                                                        });

                                                    }
                                                }).addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        Toast.makeText(AddMatatu.this, "MatatuModel/Bus : " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                                    }
                                                });

                                            }

                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {

                                        }
                                    });

                                }
                            }
                        });


                    } else {
                        Toast.makeText(this, "Field(s) cannot be empty", Toast.LENGTH_SHORT).show();
                    }

                } else {
                    Toast.makeText(this, "Please attach Conductor Picture", Toast.LENGTH_SHORT).show();
                }

            } else {
                Toast.makeText(this, "Please attach Driver Picture", Toast.LENGTH_SHORT).show();
            }

        } else {
            Toast.makeText(this, "Please attach MatatuModel/Bus logo", Toast.LENGTH_SHORT).show();
        }

    }

    private void pickMatImage() {

        if (Build.VERSION.SDK_INT >= 23) {
            // Here, thisActivity is the current activity
            if (ContextCompat.checkSelfPermission(AddMatatu.this,
                    android.Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {

                // Should we show an explanation?
                if (ActivityCompat.shouldShowRequestPermissionRationale(AddMatatu.this,
                        android.Manifest.permission.READ_EXTERNAL_STORAGE)) {

                    // Show an expanation to the user *asynchronously* -- don't block
                    // this thread waiting for the user's response! After the user
                    // sees the explanation, try again to request the permission.

                } else {

                    // No explanation needed, we can request the permission.

                    ActivityCompat.requestPermissions(AddMatatu.this,
                            new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE},
                            MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);

                    // MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE is an
                    // app-defined int constant. The callback method gets the
                    // result of the request.
                }
            } else {
                ActivityCompat.requestPermissions(AddMatatu.this,
                        new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE},
                        MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
            }
        } else {
            Log.d(TAG, "MATATU PERMISSION GRANTED");
            Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
            photoPickerIntent.setType("image/*");
            startActivityForResult(photoPickerIntent, MATATU_LOGO);
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                    photoPickerIntent.setType("image/*");
                    startActivityForResult(photoPickerIntent, MATATU_LOGO);


                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(this, "Permission needed to chose image", Toast.LENGTH_SHORT).show();
                }
                return;
            }

            case DRIVER_IMAGE_PERMISSION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                    photoPickerIntent.setType("image/*");
                    startActivityForResult(photoPickerIntent, DRIVER_PROFILE);


                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(this, "Permission needed to chose image", Toast.LENGTH_SHORT).show();
                }
                return;
            }

            case CONDUCTOR_IMAGE_PERMISSION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                    photoPickerIntent.setType("image/*");
                    startActivityForResult(photoPickerIntent, CONDUCTOR_PROFILE);


                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(this, "Permission needed to chose image", Toast.LENGTH_SHORT).show();
                }
                return;
            }


        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case MATATU_LOGO:


                if (resultCode == RESULT_OK) {

                    InputImageCompressor.compressInputImage(data, AddMatatu.this, matLogo);
                    Log.d(TAG, "MATATU IMAGE PICKED");
                    matUri = data.getData();
                }

                break;

            case DRIVER_PROFILE:

                if (resultCode == RESULT_OK) {

                    InputImageCompressor.compressInputImage(data, AddMatatu.this, drive_pic);


                    Log.d(TAG, "DRIVER IMAGE PICKED");
                    driverUri = data.getData();

                }

                break;

            case CONDUCTOR_PROFILE:

                if (resultCode == RESULT_OK) {
//                    try {
                    Log.d(TAG, "CONDUCTOR IMAGE PICKED");
                    condUri = data.getData();
                    InputImageCompressor.compressInputImage(data, AddMatatu.this, cond_pic);

                }

                break;

        }


    }
}
