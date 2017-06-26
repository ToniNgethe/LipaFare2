package com.example.toni.lipafare.Passanger.PassangerAdapter;

import android.content.Context;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.toni.lipafare.Passanger.PassModel.TicketModel;
import com.example.toni.lipafare.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;

/**
 * Created by toni on 6/2/17.
 */

public class CancelTicketAtivityAdapter extends RecyclerView.Adapter<CancelTicketAtivityAdapter.CancelTicketAtivityViewHolder> {

    private Context ctx;
    private List<TicketModel> myTickets = new ArrayList<>();
    private List<String> keys = new ArrayList<>();
    private boolean clicked = false;

    public CancelTicketAtivityAdapter(Context ctx, List<TicketModel> tickets, List<String> tKeys) {
        if (tickets.size() != this.myTickets.size() || !this.myTickets.containsAll(tickets)) {
            this.ctx = ctx;
            this.myTickets = tickets;
            this.keys = tKeys;
            notifyDataSetChanged();
        }
    }


    @Override
    public CancelTicketAtivityViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_cancel_ticket, parent, false);

        return new CancelTicketAtivityViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final CancelTicketAtivityViewHolder holder, final int position) {
        final TicketModel ticketModel = myTickets.get(position);
        final String key = keys.get(position);
        holder.setNumber(key);
        holder.setSits(ticketModel.getSits());
        holder.setRoute(ticketModel.getFrom());
        holder.setRoute2(ticketModel.getTo());

        new Handler().post(new Runnable() {
            @Override
            public void run() {

                DatabaseReference mMats = FirebaseDatabase.getInstance().getReference().child("Matatu");
                mMats.child(ticketModel.getMatatu()).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        if (dataSnapshot.exists()) {
                            holder.setPlateNumber(dataSnapshot.child("plate").getValue().toString());
                        }

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

            }
        });

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new SweetAlertDialog(ctx, SweetAlertDialog.WARNING_TYPE)
                        .setTitleText("Are you sure you want to cancel this ticket")
                        .setConfirmText("Yes")
                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sDialog) {
                                sDialog.dismissWithAnimation();

                                //check if that matatu is in stage
                                DatabaseReference mMatatu = FirebaseDatabase.getInstance().getReference().child("Tickets");
                                mMatatu.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {

                                        if (dataSnapshot.exists()){
                                            String matatu = dataSnapshot.child("matatu").getValue().toString();
                                            final String sits = dataSnapshot.child("sits").getValue().toString();

                                            //now check in Queue...
                                            DatabaseReference mQueu = FirebaseDatabase.getInstance().getReference().child("Queue").child(matatu);
                                            mQueu.addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(DataSnapshot dataSnapshot) {
                                                    if (dataSnapshot.exists()){

                                                        //restore sit....
                                                        //get sits number first....
                                                        String a = dataSnapshot.child("sits").getValue().toString();

                                                        //restore number of tickets...
                                                        dataSnapshot.child("sits").getRef().setValue(Integer.valueOf(sits) + Integer.valueOf(a)).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                if (task.isSuccessful()){

                                                                }else {
                                                                    new SweetAlertDialog(ctx, SweetAlertDialog.ERROR_TYPE)
                                                                            .setTitleText("Ooops...")
                                                                            .setContentText(task.getException().getMessage())
                                                                            .show();
                                                                }
                                                            }
                                                        });


                                                    }else {
                                                        new SweetAlertDialog(ctx, SweetAlertDialog.ERROR_TYPE)
                                                                    .setTitleText("Sorry...")
                                                                    .setContentText("Matatu/Bus is not in queue...")
                                                                    .show();
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

//                                clicked = true;
//
//                                myTickets.remove(position);
//                                keys.remove(position);
//                                notifyItemRemoved(position);
//                                // notifyItemRangeChanged(position, myTickets.size());notifyItemRangeChanged(position, keys.size());
//
//                                DatabaseReference mTicks = FirebaseDatabase.getInstance().getReference().child("Tickets");
//                                mTicks.child(key).addListenerForSingleValueEvent(new ValueEventListener() {
//                                    @Override
//                                    public void onDataChange(DataSnapshot dataSnapshot) {
//                                        if (dataSnapshot.exists()) {
//                                            if (clicked) {
//                                                dataSnapshot.child("status").getRef().setValue(2).addOnCompleteListener(new OnCompleteListener<Void>() {
//                                                    @Override
//                                                    public void onComplete(@NonNull Task<Void> task) {
//
//                                                        if (task.isSuccessful()) {
//
//                                                            clicked = false;
//
//                                                        } else {
//                                                            new SweetAlertDialog(ctx, SweetAlertDialog.ERROR_TYPE)
//                                                                    .setTitleText("Oops...")
//                                                                    .setContentText(task.getException().getMessage())
//                                                                    .show();
//
//                                                            clicked = false;
//
//                                                        }
//                                                    }
//                                                });
//                                            }
//                                        }
//
//                                    }
//
//                                    @Override
//                                    public void onCancelled(DatabaseError databaseError) {
//
//                                    }
//                                });
                            }
                        })
                        .setCancelText("No")
                        .showCancelButton(true)
                        .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sDialog) {
                                sDialog.cancel();
                            }
                        })
                        .show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return myTickets.size();
    }

    public static class CancelTicketAtivityViewHolder extends RecyclerView.ViewHolder {

        public View mView;

        public CancelTicketAtivityViewHolder(View itemView) {
            super(itemView);

            this.mView = itemView;

        }

        public void setNumber(String number) {
            TextView num = (TextView) mView.findViewById(R.id.tv_upcoming_number);
            num.setText(number);
        }

        public void setRoute(String from) {
            TextView num = (TextView) mView.findViewById(R.id.tv_up_route);
            num.setText(from);
        }

        public void setRoute2(String to) {
            TextView num = (TextView) mView.findViewById(R.id.tv_up_r);
            num.setText(to);
        }

        public void setSits(int sits) {
            TextView num = (TextView) mView.findViewById(R.id.tv_upcoming_sits);
            num.setText(String.valueOf(sits));
        }

        public void setPlateNumber(String plate) {
            TextView num = (TextView) mView.findViewById(R.id.tv_upcoming_bus);
            num.setText(plate);
        }
    }
}
