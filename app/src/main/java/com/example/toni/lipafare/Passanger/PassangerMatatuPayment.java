package com.example.toni.lipafare.Passanger;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.toni.lipafare.Application.MySingleton;
import com.example.toni.lipafare.Passanger.PassangerAdapter.PaasangerQueryAdapter;
import com.example.toni.lipafare.Passanger.PassangerDialog.PassangerMatatauDialog;
import com.example.toni.lipafare.Passanger.PassangerDialog.SuccessPayment;
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
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class PassangerMatatuPayment extends AppCompatActivity {

    public static final String URL = "http://lipafare.crescentke.com/confirm";
    private static final String TAG = PassangerMatatuPayment.class.getSimpleName();
    private TextView plate, amount_pay;
    private FloatingActionButton fab;
    private String saccoKey, mat_key;
    private int sits;
    private int total;
    private String text2Qr;
    private Bitmap bitmap = null;
    private LinearLayout info, payment;
    private EditText paymentNumber, paymentTransactionCode;
    private FloatingActionButton readInfo;

    //fire
    private FirebaseAuth mAuth;
    private DatabaseReference mPaymeans, mSacco, mMatatu;
    private StorageReference mTicks;
    private String from, to;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_passanger_matatu_payment);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeAsUpIndicator(R.mipmap.ic_close_white_24dp);
        getSupportActionBar().setTitle("Payment");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        mat_key = getIntent().getExtras().getString(PassangerMatatauDialog.MATATU_KEY);
        sits = getIntent().getExtras().getInt(PassangerMatatauDialog.MATATU_SITS);
        from = getIntent().getExtras().getString(PaasangerQueryAdapter.FROM_ADD);
        to = getIntent().getExtras().getString(PaasangerQueryAdapter.TO_ADD);

        //views
        plate = (TextView) findViewById(R.id.tv_passangermatatu_plate);
        amount_pay = (TextView) findViewById(R.id.tv_passangermatatupay_amount);
        info = (LinearLayout) findViewById(R.id.linear_payment_info);
        payment = (LinearLayout) findViewById(R.id.linear_payment);
        paymentNumber = (EditText) findViewById(R.id.et_payment_number);
        readInfo = (FloatingActionButton) findViewById(R.id.fab_readInfo);
        paymentTransactionCode = (EditText) findViewById(R.id.et_payment_code);
        //firebase....
        mAuth = FirebaseAuth.getInstance();
        mSacco = FirebaseDatabase.getInstance().getReference().child("Sacco");
        mPaymeans = FirebaseDatabase.getInstance().getReference().child("Paymeans");

        fab = (FloatingActionButton) findViewById(R.id.fab_passangermatatupay);

        readInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                info.setVisibility(View.VISIBLE);
                payment.setVisibility(View.GONE);
                readInfo.setVisibility(View.GONE);

            }
        });


        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (readInfo.getVisibility() != View.VISIBLE) {

                    info.setVisibility(View.GONE);
                    payment.setVisibility(View.VISIBLE);
                    readInfo.setVisibility(View.VISIBLE);

                } else {
                    //

                    if (!paymentTransactionCode.getText().toString().isEmpty() && !paymentNumber.getText().toString().isEmpty()) {

                        final SweetAlertDialog pDialog = new SweetAlertDialog(PassangerMatatuPayment.this, SweetAlertDialog.PROGRESS_TYPE);
                        pDialog.getProgressHelper().setBarColor(Color.parseColor("#f50057"));
                        pDialog.setTitleText("Verifying payment...");
                        pDialog.setCancelable(false);
                        pDialog.show();

                        new Handler().post(new Runnable() {
                            @Override
                            public void run() {

                                StringRequest stringRequest = new StringRequest(Request.Method.POST, URL,
                                        new Response.Listener<String>() {
                                            @Override
                                            public void onResponse(String response) {

                                                Log.d(TAG, response);



                                                try {
                                                    JSONObject jsonObject = new JSONObject(response);
                                                    boolean error = jsonObject.getBoolean("error");

                                                    if (!error) {
                                                        pDialog.dismiss();
                                                        new SweetAlertDialog(PassangerMatatuPayment.this, SweetAlertDialog.SUCCESS_TYPE)
                                                                .setTitleText("Payment Verified")
                                                                .setContentText("Click to generate ticket")
                                                                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                                                    @Override
                                                                    public void onClick(final SweetAlertDialog sweetAlertDialog) {
                                                                        sweetAlertDialog.dismiss();


                                                                        generateTicket();

                                                                    }
                                                                })
                                                                .show();

                                                    } else {

                                                        pDialog.dismiss();
                                                        new SweetAlertDialog(PassangerMatatuPayment.this, SweetAlertDialog.ERROR_TYPE)
                                                                .setTitleText("Oops")
                                                                .setContentText(jsonObject.getString("msg"))
                                                                .show();
                                                    }

                                                } catch (JSONException e) {
                                                    e.printStackTrace();

                                                    new SweetAlertDialog(PassangerMatatuPayment.this, SweetAlertDialog.ERROR_TYPE)
                                                            .setTitleText(e.getMessage())
                                                            .show();
                                                }
                                            }
                                        },
                                        new Response.ErrorListener() {
                                            @Override
                                            public void onErrorResponse(VolleyError volleyError) {
                                                //Dismissing the progress dialog
                                                pDialog.dismiss();
                                              //  Log.d(TAG, volleyError.getMessage());
                                                //  showToast(volleyEchrror.getMessage());
                                                new SweetAlertDialog(PassangerMatatuPayment.this, SweetAlertDialog.ERROR_TYPE)
                                                        .setTitleText(volleyError.getMessage())
                                                        .show();

                                            }
                                        }) {
                                    @Override
                                    protected Map<String, String> getParams() throws AuthFailureError {

                                        //Creating parameters
                                        Map<String, String> params = new HashMap<>();

                                        //Adding parameters
                                        params.put("userid", mat_key);
                                        params.put("transactionNumber", paymentNumber.getText().toString());
                                        params.put("transactionCode", paymentTransactionCode.getText().toString());
                                        //  params.put("total",3);

                                        //returning parameters
                                        return params;
                                    }
                                };

                                MySingleton.getInstance(PassangerMatatuPayment.this).addToRequestQueue(stringRequest);


                            }
                        });
                    } else {
                        new SweetAlertDialog(PassangerMatatuPayment.this, SweetAlertDialog.ERROR_TYPE)
                                .setTitleText("Oops...")
                                .setContentText("Please eneter transaction code sent to your phone and number used")
                                .show();
                    }
                }
            }
        });


        //load mat data
        matatu();
    }

    private void generateTicket() {
        final SweetAlertDialog pDialog = new SweetAlertDialog(PassangerMatatuPayment.this, SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#00bcd4"));
        pDialog.setTitleText("Generating ticket...");
        pDialog.setCancelable(false);
        pDialog.show();
        pDialog.setTitleText("Saving Ticket details....");
        final DatabaseReference mTickets = FirebaseDatabase.getInstance().getReference().child("Tickets");
        final String key = mTickets.push().getKey();

        final MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
        pDialog.setTitleText("Generating QR code..");
        BitMatrix bitMatrix = null;
        try {
            bitMatrix = multiFormatWriter.encode(key, BarcodeFormat.QR_CODE, 200, 200);
        } catch (WriterException e) {
            e.printStackTrace();
        }

        BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
        bitmap = barcodeEncoder.createBitmap(bitMatrix);

        mTicks = FirebaseStorage.getInstance().getReference().child("Tickets");
        final StorageReference ss = mTicks.child(key);

        ss.putFile(getImageUri(bitmap)).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(final UploadTask.TaskSnapshot taskSnapshot) {

                pDialog.dismiss();

                mTickets.child(key).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        dataSnapshot.child("matatu").getRef().setValue(mat_key);
                        dataSnapshot.child("sits").getRef().setValue(sits);
                        dataSnapshot.child("total").getRef().setValue(total);
                        dataSnapshot.child("status").getRef().setValue(0);
                        dataSnapshot.child("from").getRef().setValue(from);
                        dataSnapshot.child("to").getRef().setValue(to);
                        dataSnapshot.child("date").getRef().setValue(currentDate());
                        dataSnapshot.child("user").getRef().setValue(mAuth.getCurrentUser().getUid());
                        dataSnapshot.child("image").getRef().setValue(taskSnapshot.getDownloadUrl().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                pDialog.dismiss();
                                if (task.isSuccessful()) {

                                    DatabaseReference mQueu = FirebaseDatabase.getInstance().getReference().child("Queue").child(mat_key);
                                    mQueu.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {

                                            if (dataSnapshot.exists()) {

                                                int a = Integer.valueOf(dataSnapshot.child("sits").getValue().toString());

                                                dataSnapshot.child("sits").getRef().setValue(a - sits).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {

                                                        if (task.isSuccessful()) {
                                                            SuccessPayment successPayment = new SuccessPayment(PassangerMatatuPayment.this, mat_key, sits, total, key, taskSnapshot.getDownloadUrl().toString());
                                                            successPayment.setCanceledOnTouchOutside(false);
                                                            successPayment.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                                                            successPayment.getWindow().getAttributes().windowAnimations = R.style.MyAnimation_Window;
                                                            successPayment.show();
                                                        } else {
                                                            new SweetAlertDialog(PassangerMatatuPayment.this, SweetAlertDialog.ERROR_TYPE)
                                                                    .setTitleText("Oops...")
                                                                    .setContentText(task.getException().getMessage())
                                                                    .show();
                                                        }
                                                    }
                                                });

                                            } else {
                                                new SweetAlertDialog(PassangerMatatuPayment.this, SweetAlertDialog.ERROR_TYPE)
                                                        .setTitleText("Oops...")
                                                        .setContentText("Unable to locate this matatu")
                                                        .show();
                                            }

                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {

                                        }
                                    });


                                } else {
                                    new SweetAlertDialog(PassangerMatatuPayment.this, SweetAlertDialog.ERROR_TYPE)
                                            .setTitleText("Oops...")
                                            .setContentText(task.getException().getMessage())
                                            .show();
                                }

                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                pDialog.dismiss();
                                new SweetAlertDialog(PassangerMatatuPayment.this, SweetAlertDialog.ERROR_TYPE)
                                        .setTitleText("Oops...")
                                        .setContentText(e.getMessage())
                                        .show();
                            }
                        });

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                pDialog.dismiss();
                new SweetAlertDialog(PassangerMatatuPayment.this, SweetAlertDialog.ERROR_TYPE)
                        .setTitleText("Oops...")
                        .setContentText(e.getMessage())
                        .show();

            }
        });


    }

    private void matatu() {

        mMatatu = FirebaseDatabase.getInstance().getReference().child("Matatu");
        mMatatu.child(mat_key).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (dataSnapshot.exists()) {
                    plate.setText(dataSnapshot.child("plate").getValue().toString());

                    mSacco.child(String.valueOf(dataSnapshot.child("sacco").getValue())).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                            if (dataSnapshot.exists()) {

                                String n = dataSnapshot.child("cost").getValue().toString();

                                total = sits * Integer.valueOf(n);
                                //load amount to pay
                                amount_pay.setText("Ksh. " + String.valueOf(total));

                            }

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }


    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return false;
    }

    private Uri getImageUri(Bitmap inImage) {
//        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
//        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
//        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Ticket", null);
//        return Uri.parse(path);

        Uri uri = null;
        String imageFileName = "JPEG_" + mAuth.getCurrentUser().getUid() + "_";
        File mFileTemp = null;
        String root = this.getDir("my_sub_dir", Context.MODE_PRIVATE).getAbsolutePath();
        File myDir = new File(root + "/Img");
        if (!myDir.exists()) {
            myDir.mkdirs();
        }
        try {
            mFileTemp = File.createTempFile(imageFileName, ".jpg", myDir.getAbsoluteFile());
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        File file = mFileTemp;
        if (file != null) {
            FileOutputStream fout;
            try {
                fout = new FileOutputStream(file);
                inImage.compress(Bitmap.CompressFormat.PNG, 70, fout);
                fout.flush();
            } catch (Exception e) {
                e.printStackTrace();
            }
            uri = Uri.fromFile(file);

        }
        return uri;
    }

    private String currentDate() {
        Date date = new Date();
        Date newDate = new Date(date.getTime() + (604800000L * 2) + (24 * 60 * 60));
        SimpleDateFormat dt = new SimpleDateFormat("dd-MM-yyyy");
        String stringdate = dt.format(newDate);


        return stringdate;
    }


}
