package com.example.toni.lipafare.Passanger.PassangerDialog;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.toni.lipafare.Passanger.PassangerAdapter.PaasangerQueryAdapter;
import com.example.toni.lipafare.Passanger.PassangerMatatuPayment;
import com.example.toni.lipafare.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by toni on 5/8/17.
 */

public class PassangerMatatauDialog extends Dialog {

    public static final String MATATU_KEY = "MATATU_KEY";
    public static final String MATATU_SITS = "MATATU_SITS";
    private Context ctx;
    private String key;

    private ImageButton add, minus;
    private TextView sits;

    private DatabaseReference mMat, mUsers;
    private FirebaseAuth mAuth;

    private int counter = 0;
    private String sitsNUnber;

    private String from,to;

    public PassangerMatatauDialog(@NonNull Context context, String key, String from, String to) {
        super(context);
        this.key = key;
        this.ctx = context;
        this.from = from;
        this.to = to;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_passangermatatu);

        mAuth = FirebaseAuth.getInstance();
        mUsers = FirebaseDatabase.getInstance().getReference().child("Users");

        sits = (TextView) findViewById(R.id.tv_number_of_sits);
        add = (ImageButton) findViewById(R.id.imageButton_add);
        minus = (ImageButton) findViewById(R.id.imageButton_remove);

        //implement counrse
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                counter++;
                sits.setText(String.valueOf(counter));


            }
        });

        minus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                counter--;
                sits.setText(String.valueOf(counter));
            }
        });

        final ImageView driver = (ImageView) findViewById(R.id.iv_passangermatatudialog_driver);
        final ImageView conductor = (ImageView) findViewById(R.id.iv_passangermatatudialog_conductor);
        final TextView plate = (TextView) findViewById(R.id.tv_passangermatatudialog_plate);
        final TextView name = (TextView) findViewById(R.id.tv_passangermatatudialog_name);
        final ImageButton close = (ImageButton) findViewById(R.id.imageButton_close);

        final LinearLayout linear_name = (LinearLayout) findViewById(R.id.linear_name);
        final LinearLayout linear_number = (LinearLayout) findViewById(R.id.linear_number);

        final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.floatin_passangermatatudialog_book);
        final FloatingActionButton save_number = (FloatingActionButton) findViewById(R.id.floating_passangermatatudialog_savenumber);
        final EditText number = (EditText) findViewById(R.id.et_passangermatatudialog_number);

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        DatabaseReference mQueue = FirebaseDatabase.getInstance().getReference().child("Queue");
        mQueue.child(key).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (dataSnapshot.exists()){
                    sitsNUnber = dataSnapshot.child("sits").getValue().toString();
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mMat = FirebaseDatabase.getInstance().getReference().child("Matatu");
        mMat.child(key).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(final DataSnapshot dataSnapshot) {

                if (dataSnapshot.exists()) {

                    plate.setText(dataSnapshot.child("plate").getValue().toString());
                    name.setText(dataSnapshot.child("name").getValue().toString());
                   // sitsNUnber = dataSnapshot.child("sits").getValue().toString();

                    new Handler().post(new Runnable() {
                        @Override
                        public void run() {
                            Glide.with(ctx).load(dataSnapshot.child("driver").getValue().toString())
                                    .crossFade()
                                    .thumbnail(0.5f)
                                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                                    .error(R.mipmap.profile2)
                                    .into(driver);
                        }
                    });
                    new Handler().post(new Runnable() {
                        @Override
                        public void run() {
                            Glide.with(ctx).load(dataSnapshot.child("conductor").getValue().toString())
                                    .crossFade()
                                    .thumbnail(0.5f)
                                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                                    .error(R.mipmap.profile2)
                                    .into(conductor);
                        }
                    });

                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        //book ticket
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {

                //check if number exists
                mUsers.child(mAuth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (!dataSnapshot.child("number").exists()) {

                            //number not found...
                            //show lines
                            linear_name.setVisibility(View.GONE);
                            linear_number.setVisibility(View.VISIBLE);
                            fab.setVisibility(View.GONE);
                            save_number.setVisibility(View.VISIBLE);

                        } else {

                            //check number of sist
                            if (Integer.valueOf(sits.getText().toString()) > 0) {

                                if (!(Integer.valueOf(sits.getText().toString()) > Integer.valueOf(sitsNUnber))) {

                                    Intent i = new Intent(ctx, PassangerMatatuPayment.class);
                                    i.putExtra(MATATU_KEY, key);
                                    i.putExtra(MATATU_SITS, Integer.valueOf(sits.getText().toString()));
                                    i.putExtra(PaasangerQueryAdapter.FROM_ADD,from);
                                    i.putExtra(PaasangerQueryAdapter.TO_ADD, to);
                                    v.getContext().startActivity(i);

                                } else {

                                    Toast.makeText(ctx, "Number of sits indicated is bigger than the available sits", Toast.LENGTH_SHORT).show();
                                }

                            } else {
                                Toast.makeText(ctx, "Indicate number of sits", Toast.LENGTH_SHORT).show();
                            }


                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });


            }
        });

        //save numbr
        save_number.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {

                if (!TextUtils.isEmpty(number.getText())) {
                    //check if number is valid

                    if (Integer.valueOf(sits.getText().toString()) > 0) {

                        //check if number is valid
                        if (checkNumber(number.getText().toString())) {

                            Toast.makeText(ctx, "Saving number..", Toast.LENGTH_SHORT).show();

                            mUsers.child(mAuth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {

                                    dataSnapshot.child("number").getRef().setValue(number.getText().toString());
                                    Toast.makeText(ctx, "Number saved successfully", Toast.LENGTH_SHORT).show();

                                    //redirect

                                            Intent i = new Intent(ctx, PassangerMatatuPayment.class);
                                            i.putExtra(MATATU_KEY, key);
                                            i.putExtra(MATATU_SITS, Integer.valueOf(sits.getText().toString()));
                                            i.putExtra(PaasangerQueryAdapter.FROM_ADD,from);
                                            i.putExtra(PaasangerQueryAdapter.TO_ADD, to);
                                            v.getContext().startActivity(i);
                                            dismiss();
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });

                        } else {
                            Toast.makeText(ctx, "Invalid number..", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(ctx, "Indicate number of sits", Toast.LENGTH_SHORT).show();
                    }


                } else {
                    Toast.makeText(ctx, "Field cannot be empty", Toast.LENGTH_SHORT).show();
                }

            }
        });

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
