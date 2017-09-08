package com.toni.lipafare.Passanger.Fragments;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;
import com.toni.lipafare.Passanger.PassangerDialog.ChangeEmailAddress;
import com.toni.lipafare.Passanger.PassangerDialog.ChangePassword;
import com.toni.lipafare.Passanger.PassangerDialog.ChangePhoneNumber;
import com.toni.lipafare.Passanger.PassangerDialog.ChangeUserNameDialog;
import com.toni.lipafare.R;

import cn.pedant.SweetAlert.SweetAlertDialog;

/**
 * Created by toni on 4/28/17.
 */

public class PassangerAccountFragment extends Fragment implements View.OnClickListener{

    private static final int PROFILE_IMAGE_PERMISSION = 1000;
    private static final String TAG = PassangerAccountFragment.class.getSimpleName();
    private static final int GALLARY_INTENT =1 ;
    private View mView;
    private Button change_pass;
    private ImageButton edit_email;
    private ImageButton edit_username;
    private ImageButton edit_phone;
    private FloatingActionButton change_image;
    private TextView name, email, number;
    private ImageView profile;
    private Uri imageUri;

    private FirebaseAuth mAuth;
    private DatabaseReference mUsers;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        mView = inflater.inflate(R.layout.fragment_passanger_acc, container,false);

        //views
        change_pass = (Button) mView.findViewById(R.id.btn_account_pass);
        edit_email = (ImageButton) mView.findViewById(R.id.imgbtn_edit_email);
        edit_username = (ImageButton) mView.findViewById(R.id.imgbtn_edit_name);
        edit_phone = (ImageButton) mView.findViewById(R.id.imgbtn_edit_number);
        change_image = (FloatingActionButton) mView.findViewById(R.id.fab_operatorProfile_pic);
        name = (TextView) mView.findViewById(R.id.tv_account_username);
        email = (TextView) mView.findViewById(R.id.tv_account_email);
        number = (TextView) mView.findViewById(R.id.tv_account_number);
        profile = (ImageView) mView.findViewById(R.id.iv_operatorProfile_profile);

        //firebase
        mAuth = FirebaseAuth.getInstance();
        mUsers = FirebaseDatabase.getInstance().getReference().child("Users");

        //listeners
        change_pass.setOnClickListener(this);
        edit_email.setOnClickListener(this);
        edit_username.setOnClickListener(this);
        edit_phone.setOnClickListener(this);
        change_image.setOnClickListener(this);

        //load details first....
        load();


        return mView;
    }

    private void load() {

        if (mAuth.getCurrentUser() != null){
            DatabaseReference cu = mUsers.child(mAuth.getCurrentUser().getUid());
            cu.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(final DataSnapshot dataSnapshot) {

                    try {
                        //check for existance to avoid null pointers
                        if (dataSnapshot.exists()) {

                                name.setText(dataSnapshot.child("user").getValue().toString());
                                email.setText(mAuth.getCurrentUser().getEmail());
                                new Handler().post(new Runnable() {
                                    @Override
                                    public void run() {
                                        try {
                                            Glide.with(getActivity()).load(dataSnapshot.child("image").getValue().toString())
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

                                if (!dataSnapshot.child("number").getValue().toString().isEmpty()){
                                    number.setText(dataSnapshot.child("number").getValue().toString());
                                }else{
                                    number.setText("Number not set");
                                }
                        } else {
                            //Toast.makeText(PassangerPanel.this, "No user found", Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }

    }

    @Override
    public void onClick(View v) {

        if (v == change_image){
            //change image..
            if (Build.VERSION.SDK_INT >= 23) {
                Log.d(TAG, "ENETERED CHECKING VERSION");
                // Here, thisActivity is the current activity
                if (ContextCompat.checkSelfPermission(getActivity(),
                        android.Manifest.permission.READ_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {

                    Log.d(TAG, "PERMISSION NOT GRANTED");
                    // Should we show an explanation?
                    if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                            android.Manifest.permission.READ_EXTERNAL_STORAGE)) {
                        Log.d(TAG, "qqqqqqqqqqqqqqqqqqqqqqqqqqq");
                        // Show an expanation to the user *asynchronously* -- don't block
                        // this thread waiting for the user's response! After the user
                        // sees the explanation, try again to request the permission.

                    } else {

                        // No explanation needed, we can request the permission.
                        Log.d(TAG, "REQUEDSFDFSDSF");
                        ActivityCompat.requestPermissions(getActivity(),
                                new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE},
                                PROFILE_IMAGE_PERMISSION);

                        // MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE is an
                        // app-defined int constant. The callback method gets the
                        // result of the request.
                    }
                } else {
                    Log.d(TAG, "REQUEST IMAGE PICKING");
                    ActivityCompat.requestPermissions(getActivity(),
                            new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE},
                            PROFILE_IMAGE_PERMISSION);
                }
            } else {
                Log.d(TAG, "DRIVER IMAGE PERMISSION GRANTED");
                Intent photoPickerIntent = new Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
           //     photoPickerIntent.setType("image/*");
                startActivityForResult(photoPickerIntent, GALLARY_INTENT);
            }

        } else if (v == edit_email){

            //change email
            ChangeEmailAddress changeEmailAddress = new ChangeEmailAddress(getActivity());
            changeEmailAddress.getWindow().getAttributes().windowAnimations = R.style.MyAnimation_Window;
            changeEmailAddress.setCanceledOnTouchOutside(false);
            changeEmailAddress.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            changeEmailAddress.show();

        } else if(v == edit_username){
            //change username
            ChangeUserNameDialog changeUsername = new ChangeUserNameDialog(getActivity());
            changeUsername.getWindow().getAttributes().windowAnimations = R.style.MyAnimation_Window;
            changeUsername.setCanceledOnTouchOutside(false);
            changeUsername.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            changeUsername.show();
        }else if (v == edit_phone){
            //change phone
            ChangePhoneNumber changeUsername = new ChangePhoneNumber(getActivity());
            changeUsername.getWindow().getAttributes().windowAnimations = R.style.MyAnimation_Window;
            changeUsername.setCanceledOnTouchOutside(false);
            changeUsername.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            changeUsername.show();
        }else if(v == change_pass){
            //change pass
            ChangePassword changeUsername = new ChangePassword(getActivity());
            changeUsername.getWindow().getAttributes().windowAnimations = R.style.MyAnimation_Window;
            changeUsername.setCanceledOnTouchOutside(false);
            changeUsername.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            changeUsername.show();
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        try {
            // When an Image is picked
            if (requestCode == GALLARY_INTENT && resultCode == Activity.RESULT_OK) {
                Uri selectedImage = data.getData();

                CropImage.activity(selectedImage)
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .setAspectRatio(1, 1)
                        .start(getContext(), this);
            }
            // when image is cropped
            else if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
                CropImage.ActivityResult result = CropImage.getActivityResult(data);
                Log.d("APP_DEBUG", result.toString());
                if (resultCode == Activity.RESULT_OK) {

                    imageUri = result.getUri();

                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), imageUri);
                    profile.setImageBitmap(bitmap);

                    uploadImage();

                } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                    Exception error = result.getError();
                }
            } else {
                Toast.makeText(getActivity(), "You haven't picked Image",
                        Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            Toast.makeText(getActivity(), "Something went wrong" + e.getMessage(), Toast.LENGTH_LONG)
                    .show();
        }
    }

    private void uploadImage() {

        if (imageUri != null) {
            final SweetAlertDialog pDialog = new SweetAlertDialog(getActivity(), SweetAlertDialog.PROGRESS_TYPE);
            pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
            pDialog.setTitleText("Uploading image....");
            pDialog.setCancelable(false);
            pDialog.show();

            // Create a storage reference from our app
            final StorageReference storageRef = FirebaseStorage.getInstance().getReference().child("Profile");
            // Create a reference to the file to delete
            StorageReference desertRef = storageRef.child(mAuth.getCurrentUser().getUid());

            // Delete the file
            desertRef.delete().addOnSuccessListener(new OnSuccessListener() {
                @Override
                public void onSuccess(Object o) {

                    pDialog.setTitleText("Deleting old image...");

                    StorageReference newImage = storageRef.child(mAuth.getCurrentUser().getUid());
                    newImage.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                            pDialog.setTitleText("Setting new image...");

                            DatabaseReference mine = mUsers.child(mAuth.getCurrentUser().getUid());

                            mine.child("image").setValue(taskSnapshot.getDownloadUrl().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {

                                    pDialog.setTitleText("Getting new Image...");


                                    if (task.isSuccessful()) {

                                        pDialog.dismiss();

                                    } else {

                                        pDialog.dismiss();
                                        new SweetAlertDialog(getActivity(), SweetAlertDialog.ERROR_TYPE)
                                                .setTitleText("Oops...")
                                                .setContentText(task.getException().getMessage())
                                                .show();

                                    }

                                }
                            });

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            pDialog.dismiss();
                            new SweetAlertDialog(getActivity(), SweetAlertDialog.ERROR_TYPE)
                                    .setTitleText("Oops...")
                                    .setContentText(e.getMessage())
                                    .show();
                        }
                    });

                }

            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    pDialog.dismiss();
                    new SweetAlertDialog(getActivity(), SweetAlertDialog.ERROR_TYPE)
                            .setTitleText("Oops...")
                            .setContentText(exception.getMessage())
                            .show();
                }
            });


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
                Intent photoPickerIntent = new Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
              //  photoPickerIntent.setType("image/*");
                startActivityForResult(photoPickerIntent, GALLARY_INTENT);

            } else {

                // permission denied, boo! Disable the
                // functionality that depends on this permission.
                Toast.makeText(getActivity(), "Permission needed to chose image", Toast.LENGTH_SHORT).show();
            }
            return;
        }
    }
}
