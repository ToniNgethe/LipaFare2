package com.example.toni.lipafare.Intro;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.example.toni.lipafare.Operator.OperatorIntroActivity;
import com.example.toni.lipafare.Passanger.PassangerPanel;
import com.example.toni.lipafare.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;

public class RegisterActivity extends AppCompatActivity {

    private static final int GALLARY_INTENT = 1;
    private static final String TAG = RegisterActivity.class.getSimpleName();
    private static final int PROFILE_IMAGE_PERMISSION = 100 ;
    private EditText username, useremail, userpass;
    private ImageView image;
    private Button submit;
    private RadioButton selectedcat;
    private RadioGroup categories;

    private Uri uri = null;

    private DatabaseReference mUsers;
    private FirebaseAuth mAUth;
    private StorageReference mStorage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        //views
        views();
        //Firebase
        firebase();
        //uploadpic
        chooseProfile();
        //uploadinfo
        uploadInfo();

    }

    private void uploadInfo() {

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (uri != null) {

                    if (!TextUtils.isEmpty(username.getText()) && !TextUtils.isEmpty(useremail.getText()) && !TextUtils.isEmpty(userpass.getText())) {

                        final ProgressDialog progressDialog = new ProgressDialog(RegisterActivity.this);
                        progressDialog.setMessage("Adding user..");
                        progressDialog.setCanceledOnTouchOutside(false);
                        progressDialog.show();
                        //get selected cat
                        final int selected = categories.getCheckedRadioButtonId();
                        selectedcat = (RadioButton) findViewById(selected);

                        mAUth.createUserWithEmailAndPassword(useremail.getText().toString().trim(), userpass.getText().toString().trim())
                                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {

                                        if (task.isSuccessful()) {

                                            //get userid
                                            final String userid = mAUth.getCurrentUser().getUid();

                                            //upload image
                                            StorageReference filepath = mStorage.child(userid);
                                            filepath.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                                @Override
                                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                                                    //add user details to database
                                                    DatabaseReference current = mUsers.child(userid);
                                                    current.child("user").setValue(username.getText().toString());
                                                    current.child("category").setValue(selectedcat.getText().toString());
                                                    current.child("image").setValue(taskSnapshot.getDownloadUrl().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {

                                                            if (task.isSuccessful()) {

                                                                progressDialog.dismiss();
                                                                Toast.makeText(RegisterActivity.this, "Data added successfully", Toast.LENGTH_SHORT).show();

                                                                if (selectedcat.getText().equals("Operator")) {

                                                                    startActivity(new Intent(RegisterActivity.this, OperatorIntroActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                                                                    finish();

                                                                }else {
                                                                    startActivity(new Intent(RegisterActivity.this, PassangerPanel.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                                                                    finish();
                                                                }
                                                            } else {

                                                                progressDialog.dismiss();
                                                                Toast.makeText(RegisterActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();

                                                            }
                                                        }
                                                    });
                                                }
                                            }).addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {

                                                    progressDialog.dismiss();
                                                    Toast.makeText(RegisterActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();

                                                }
                                            });

                                        } else {

                                            progressDialog.dismiss();
                                            Toast.makeText(RegisterActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();

                                        }

                                    }
                                });

                    } else {

                        Toast.makeText(RegisterActivity.this, R.string.field_message, Toast.LENGTH_SHORT).show();

                    }

                } else {
                    Toast.makeText(RegisterActivity.this, "Select an image", Toast.LENGTH_SHORT).show();
                }


            }
        });

    }

    private void chooseProfile() {

        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (Build.VERSION.SDK_INT >= 23) {
                    // Here, thisActivity is the current activity
                    if (ContextCompat.checkSelfPermission(RegisterActivity.this,
                            android.Manifest.permission.READ_EXTERNAL_STORAGE)
                            != PackageManager.PERMISSION_GRANTED) {

                        // Should we show an explanation?
                        if (ActivityCompat.shouldShowRequestPermissionRationale(RegisterActivity.this,
                                android.Manifest.permission.READ_EXTERNAL_STORAGE)) {

                            // Show an expanation to the user *asynchronously* -- don't block
                            // this thread waiting for the user's response! After the user
                            // sees the explanation, try again to request the permission.

                        } else {

                            // No explanation needed, we can request the permission.

                            ActivityCompat.requestPermissions(RegisterActivity.this,
                                    new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE},
                                    PROFILE_IMAGE_PERMISSION);

                            // MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE is an
                            // app-defined int constant. The callback method gets the
                            // result of the request.
                        }
                    } else {
                        ActivityCompat.requestPermissions(RegisterActivity.this,
                                new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE},
                                PROFILE_IMAGE_PERMISSION);
                    }
                } else {
                    Log.d(TAG, "DRIVER IMAGE PERMISSION GRANTED");
                    Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                    photoPickerIntent.setType("image/*");
                    startActivityForResult(photoPickerIntent, GALLARY_INTENT);
                }

            }
        });

    }

    private void firebase() {
        mUsers = FirebaseDatabase.getInstance().getReference().child("Users");
        mAUth = FirebaseAuth.getInstance();
        mStorage = FirebaseStorage.getInstance().getReference().child("Profile");
    }

    private void views() {

        username = (EditText) findViewById(R.id.et_setup_username);
        useremail = (EditText) findViewById(R.id.et_register_email);
        userpass = (EditText) findViewById(R.id.et_register_pass);

        image = (ImageView) findViewById(R.id.register_imageButton);
        submit = (Button) findViewById(R.id.btn_register_submit);

        categories = (RadioGroup) findViewById(R.id.radioCategory);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GALLARY_INTENT) {
            if (resultCode == RESULT_OK) {

                Uri imageUri = data.getData();

                CropImage.activity(imageUri)
                        .setGuidelines(com.theartofdev.edmodo.cropper.CropImageView.Guidelines.ON)
                        .setAspectRatio(1, 1)
                        .start(this);

            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(this, "Operation cancelled by user", Toast.LENGTH_SHORT).show();
            }
        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {

                uri = result.getUri();

                image.setImageURI(uri);

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
                Log.d(TAG, error.getMessage());
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PROFILE_IMAGE_PERMISSION){
            // If request is cancelled, the result arrays are empty.
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                // permission was granted, yay! Do the
                // contacts-related task you need to do.
                Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                photoPickerIntent.setType("image/*");
                startActivityForResult(photoPickerIntent, GALLARY_INTENT);

            } else {

                // permission denied, boo! Disable the
                // functionality that depends on this permission.
                Toast.makeText(this, "Permission needed to chose image", Toast.LENGTH_SHORT).show();
            }
            return;
        }
    }


}
