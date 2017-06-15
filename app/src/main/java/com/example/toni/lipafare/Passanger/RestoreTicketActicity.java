package com.example.toni.lipafare.Passanger;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.toni.lipafare.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class RestoreTicketActicity extends AppCompatActivity {

    private String ticket;
    private TextView _ticket, _date, _plate, _sits, _route, _total;
    private Button restore;

    private DatabaseReference mTickets;
    private boolean clickd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restore_ticket_acticity);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Restore Ticket");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        //views
        _ticket = (TextView) findViewById(R.id.tv_restore_number);
        _date = (TextView) findViewById(R.id.tv_restore_date);
        _plate = (TextView) findViewById(R.id.tv_restore_plate);
        _sits = (TextView) findViewById(R.id.tv_restore_sits);
        _total = (TextView) findViewById(R.id.tv_restore_total);
        _route = (TextView) findViewById(R.id.tv_restore_route);
        restore = (Button) findViewById(R.id.btn_restore);

        //get ticket number
        ticket = getIntent().getExtras().getString("TICKET_KEY");

        //firebase
        mTickets = FirebaseDatabase.getInstance().getReference().child("Tickets");
        mTickets.child(ticket).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){

                    _ticket.setText(ticket);
                    _sits.setText(dataSnapshot.child("sits").getValue().toString() + "sit(s)");
                    _total.setText(dataSnapshot.child("total").getValue().toString() + " ksh");

                    DatabaseReference mMats = FirebaseDatabase.getInstance().getReference().child("Matatu");
                    mMats.child(dataSnapshot.child("matatu").getValue().toString()).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot ds) {
                            if (ds.exists()){

                                _plate.setText(ds.child("plate").getValue().toString());
                                _route.setText(ds.child("from").getValue().toString() +" TO/FROM "+ ds.child("from").getValue().toString() );


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

        restore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                clickd = true;
                final SweetAlertDialog pDialog = new SweetAlertDialog(RestoreTicketActicity.this, SweetAlertDialog.PROGRESS_TYPE);
                pDialog.getProgressHelper().setBarColor(Color.parseColor("#00bcd4"));
                pDialog.setTitleText("Restoring ticket...");
                pDialog.setCancelable(false);
                pDialog.show();

                DatabaseReference t = mTickets.child(ticket);
                t.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        if (dataSnapshot.exists()) {

                            if (clickd) {

                                dataSnapshot.child("status").getRef().setValue(0).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        pDialog.dismissWithAnimation();
                                        if (task.isSuccessful()) {

                                            try {
                                                new SweetAlertDialog(RestoreTicketActicity.this, SweetAlertDialog.SUCCESS_TYPE)
                                                        .setTitleText("Successfully restored...")
                                                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                                            @Override
                                                            public void onClick(SweetAlertDialog sweetAlertDialog) {
                                                                sweetAlertDialog.dismiss();
                                                                finish();
                                                            }
                                                        })
                                                        .show();

                                                clickd = false;
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                        } else {
                                            new SweetAlertDialog(RestoreTicketActicity.this, SweetAlertDialog.ERROR_TYPE)
                                                    .setTitleText("Oops...")
                                                    .setContentText(task.getException().getMessage())
                                                    .show();
                                        }
                                    }
                                });
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        pDialog.dismissWithAnimation();
                        new SweetAlertDialog(RestoreTicketActicity.this, SweetAlertDialog.ERROR_TYPE)
                                .setTitleText("Oops...")
                                .setContentText(databaseError.getMessage())
                                .show();
                    }
                });

            }
        });
    }

}
