package com.toni.lipafare.Operator.Dialog_mat;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.toni.lipafare.R;

/**
 * Created by toni on 4/20/17.
 */

public class MatatuDetails extends Dialog {

    private Context ctx;
    private TextView name, plate, sits, status, time;
    private ImageView logo, driver, conductor;
    private FloatingActionButton edit, delete;
    private ImageButton close;

    private DatabaseReference mMat;

    private String key;

    public MatatuDetails(@NonNull Context context, String key) {
        super(context);

        this.ctx = context;
        this.key = key;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.dialog_matatus);

        //views
        name = (TextView) findViewById(R.id.tv_dialogmatatu_name);
        plate = (TextView) findViewById(R.id.tv_dialogmatatu_plate);
        sits = (TextView) findViewById(R.id.tv_dialogmatatu_sits);
        status = (TextView) findViewById(R.id.tv_dialogmatatu_status);
        time = (TextView) findViewById(R.id.tv_dialogmatatu_time);
        logo = (ImageView) findViewById(R.id.iv_dialogmatatu_logo);
        driver = (ImageView) findViewById(R.id.iv_dialogmatatu_driver);
        conductor = (ImageView) findViewById(R.id.iv_dialogmatatu_conductor);
        close = (ImageButton) findViewById(R.id.imageButton_close);
        edit = (FloatingActionButton) findViewById(R.id.fab_dialogmatatu_edit);
        delete = (FloatingActionButton) findViewById(R.id.fab_delete);

        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.getContext().startActivity(new Intent(ctx, com.toni.lipafare.Operator.AddMatatu.class));
            }
        });

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMat = FirebaseDatabase.getInstance().getReference().child("Matatu");
                mMat.child(key).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()){
                            dataSnapshot.getRef().removeValue();
                            dismiss();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        });

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               // Toast.makeText(ctx, "CLicked", Toast.LENGTH_SHORT).show();
                hide();
            }
        });

        //firebase
        mMat = FirebaseDatabase.getInstance().getReference().child("Matatu");
        mMat.child(key).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(final DataSnapshot dataSnapshot) {

                if (dataSnapshot.exists()){

                    new Handler().post(new Runnable() {
                        @Override
                        public void run() {

                            Glide.with(ctx).load(dataSnapshot.child("logo").getValue().toString())
                                    .crossFade()
                                    .thumbnail(0.5f)
                                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                                    .error(R.mipmap.error_network)
                                    .into(logo);

                            Glide.with(ctx).load(dataSnapshot.child("driver").getValue().toString())
                                    .crossFade()
                                    .thumbnail(0.5f)
                                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                                    .error(R.mipmap.error_network)
                                    .into(driver);

                            Glide.with(ctx).load(dataSnapshot.child("conductor").getValue().toString())
                                    .crossFade()
                                    .thumbnail(0.5f)
                                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                                    .error(R.mipmap.error_network)
                                    .into(conductor);

                        }
                    });

                    plate.setText(dataSnapshot.child("plate").getValue().toString());
                    name.setText(dataSnapshot.child("name").getValue().toString());
                    sits.setText(dataSnapshot.child("sits").getValue().toString() + " sits");
                    status.setText(dataSnapshot.child("status").getValue().toString());
                    time.setText("From : " + dataSnapshot.child("from").getValue().toString() + ", To :" + dataSnapshot.child("to").getValue().toString());

                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(ctx,"Error:" + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }
}
