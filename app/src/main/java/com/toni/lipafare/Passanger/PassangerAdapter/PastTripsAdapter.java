package com.toni.lipafare.Passanger.PassangerAdapter;

import android.content.Context;
import android.os.Handler;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.toni.lipafare.Passanger.PassModel.TicketModel;
import com.toni.lipafare.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by toni on 6/1/17.
 */

public class PastTripsAdapter extends RecyclerView.Adapter<PastTripsAdapter.PastTripsViewHolder> {

    private Context ctx;
    private List<TicketModel> myTickets = new ArrayList<>();
    private List<String> keys = new ArrayList<>();

    public PastTripsAdapter(Context ctx, List<TicketModel> tickets, List<String> tKeys) {

        if (tickets.size() != this.myTickets.size() || !this.myTickets.containsAll(tickets)) {
            this.ctx = ctx;
            this.myTickets = tickets;
            this.keys = tKeys;
            notifyDataSetChanged();
        }
    }

    @Override
    public PastTripsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_past_trips, parent, false);

        return new PastTripsViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final PastTripsViewHolder holder, final int position) {

        if (holder != null) {

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

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

//                    Intent i = new Intent(ctx, Tickets.class);
//                    i.putExtra("TICKET_KEY", key);
//                    i.putExtra("TICKET_QR", ticketModel.getImage());
//                    v.getContext().startActivity(i);

                }
            });
            holder.remove_past.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    myTickets.remove(position);
//                    notifyItemRemoved(position);
//                    notifyItemRangeChanged(position, myTickets.size());


                    //thread for removing ticket...
                    new Handler().post(new Runnable() {
                        @Override
                        public void run() {

                            DatabaseReference mTicks = FirebaseDatabase.getInstance().getReference().child("Tickets");
                            mTicks.child(key).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {

                                    if (dataSnapshot.exists()) {
                                        dataSnapshot.child("status").getRef().setValue(4);
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });

                        }
                    });
                }
            });

        }
    }

    @Override
    public int getItemCount() {
        return myTickets.size();
    }

    public static class PastTripsViewHolder extends RecyclerView.ViewHolder {

        public View mView;
        public ImageButton remove_past;

        public PastTripsViewHolder(View itemView) {
            super(itemView);

            this.mView = itemView;
            remove_past = (ImageButton) mView.findViewById(R.id.imageButton_past_close);
        }

        public void setNumber(String number) {
            TextView num = (TextView) mView.findViewById(R.id.tv_past_number);
            num.setText(number);
        }

        public void setRoute(String from) {
            TextView num = (TextView) mView.findViewById(R.id.tv_past_route);
            num.setText(from);
        }

        public void setRoute2(String to) {
            TextView num = (TextView) mView.findViewById(R.id.tv_past_r);
            num.setText(to);
        }

        public void setSits(int sits) {
            TextView num = (TextView) mView.findViewById(R.id.tv_past_sits);
            num.setText(String.valueOf(sits));
        }

        public void setPlateNumber(String plate) {
            TextView num = (TextView) mView.findViewById(R.id.tv_past_bus);
            num.setText(plate);
        }

    }
}
