package com.toni.lipafare.Intro;

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
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

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
import com.toni.lipafare.Helper.Global;
import com.toni.lipafare.Operator.OperatorIntroActivity;
import com.toni.lipafare.Passanger.PassangerPanel;
import com.toni.lipafare.R;

public class SetUpActivity extends AppCompatActivity {

    private static final int GALLARY_INTENT = 100;
    private static final String TAG = SetUpActivity.class.getSimpleName();
    private static final int PROFILE_IMAGE_PERMISSION = 102;
    private Button submit;
    private EditText username;
    private RadioGroup category;
    private ImageView profile;
    private RadioButton selected;

    private FirebaseAuth mAuth;
    private DatabaseReference mUsers;
    private String userId;
    private Uri uri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_up);

        //views
        submit = (Button) findViewById(R.id.btn_setup_submit);
        username = (EditText) findViewById(R.id.et_setup_username);
        category = (RadioGroup) findViewById(R.id.setup_radioCategory);
        profile = (ImageView) findViewById(R.id.iv_setup_profile);


        //firebase
        mAuth = FirebaseAuth.getInstance();
        mUsers = FirebaseDatabase.getInstance().getReference().child("Users");

        if (mAuth.getCurrentUser() != null) {
            userId = mAuth.getCurrentUser().getUid();
        }

        //listener
        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (Build.VERSION.SDK_INT >= 23) {
                    // Here, thisActivity is the current activity
                    if (ContextCompat.checkSelfPermission(SetUpActivity.this,
                            android.Manifest.permission.READ_EXTERNAL_STORAGE)
                            != PackageManager.PERMISSION_GRANTED) {

                        // Should we show an explanation?
                        if (ActivityCompat.shouldShowRequestPermissionRationale(SetUpActivity.this,
                                android.Manifest.permission.READ_EXTERNAL_STORAGE)) {

                            // Show an expanation to the user *asynchronously* -- don't block
                            // this thread waiting for the user's response! After the user
                            // sees the explanation, try again to request the permission.

                        } else {

                            // No explanation needed, we can request the permission.

                            ActivityCompat.requestPermissions(SetUpActivity.this,
                                    new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE},
                                    PROFILE_IMAGE_PERMISSION);

                            // MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE is an
                            // app-defined int constant. The callback method gets the
                            // result of the request.
                        }
                    } else {
                        ActivityCompat.requestPermissions(SetUpActivity.this,
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
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (Global.isConnected(SetUpActivity.this)) {
                    submitData();
                }else {
                    Toast.makeText(SetUpActivity.this,"No internet connection", Toast.LENGTH_SHORT).show();
                }
            }
        });
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

                profile.setImageURI(uri);

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
                Log.d(TAG, error.getMessage());
            }
        }

    }

    private void submitData() {

        if (uri != null) {

            if (!username.getText().toString().isEmpty()) {

                final ProgressDialog progressDialog = new ProgressDialog(this);
                progressDialog.setTitle("This will take a sec");
                progressDialog.setMessage("Saving details");
                progressDialog.setCanceledOnTouchOutside(false);
                progressDialog.show();

                //get selected category
                selected = (RadioButton) findViewById(category.getCheckedRadioButtonId());

                //store image
                progressDialog.setMessage("Uploading your image");
                StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("Profile").child(userId);
                storageReference.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                        progressDialog.setMessage("Saving details...");
                        //store information
                        DatabaseReference cu = mUsers.child(userId);
                        cu.child("user").setValue(username.getText().toString());
                        cu.child("category").setValue(selected.getText().toString());
                        cu.child("image").setValue(taskSnapshot.getDownloadUrl().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                progressDialog.dismiss();
                                if (task.isSuccessful()){
                                    //lunch activity
                                    redirectUser();
                                }else {
                                    Toast.makeText(SetUpActivity.this,"Error:" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });


                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(SetUpActivity.this,"Error:" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

            } else {
                Toast.makeText(this, "Username cannot be empty", Toast.LENGTH_SHORT).show();
            }

        } else {
            Toast.makeText(this, "Select your profile pic", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PROFILE_IMAGE_PERMISSION) {
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

    private void redirectUser(){
        if (selected.getText().toString() == "Operator"){

            startActivity(new Intent(SetUpActivity.this, OperatorIntroActivity.class));
            finish();
            
        }else {

            startActivity(new Intent(SetUpActivity.this, PassangerPanel.class));
            finish();

        }
    }
}
