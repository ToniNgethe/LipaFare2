package com.toni.lipafare.Passanger.PassangerAdapter;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.toni.lipafare.Passanger.PassModel.TicketModel;
import com.toni.lipafare.Passanger.Tickets;
import com.toni.lipafare.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by toni on 5/25/17.
 */

public class UpcomingTripsAdapter extends RecyclerView.Adapter<UpcomingTripsAdapter.UpcomingTripsHolder> {

    private Context ctx;
    private List<TicketModel> myTickets = new ArrayList<>();
    private List<String> keys = new ArrayList<>();

    public UpcomingTripsAdapter(Context ctx, List<TicketModel> tickets, List<String> tKeys) {
        if (tickets.size() != this.myTickets.size() || !this.myTickets.containsAll(tickets)) {
            this.ctx = ctx;
            this.myTickets = tickets;
            this.keys = tKeys;
            notifyDataSetChanged();
        }
    }

    @Override
    public UpcomingTripsHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_upcoming_trips, parent, false);

        return new UpcomingTripsHolder(v);
    }

    @Override
    public void onBindViewHolder(final UpcomingTripsHolder holder, int position) {

        if (holder != null) {

            final TicketModel ticketModel = myTickets.get(position);
            final String key = keys.get(position);
            holder.setNumber(key);
            holder.setSits(ticketModel.getSits());
            // holder.setDate();
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

                    Intent i = new Intent(ctx, Tickets.class);
                    i.putExtra("TICKET_KEY", key);
                    i.putExtra("TICKET_QR", ticketModel.getImage());
                    v.getContext().startActivity(i);

                }
            });

        }
    }

    @Override
    public int getItemCount() {
        return myTickets.size();
    }

    public static class UpcomingTripsHolder extends RecyclerView.ViewHolder {

        public View mView;

        public UpcomingTripsHolder(View itemView) {
            super(itemView);

            this.mView = itemView;
        }

        public void setRoute(String route) {
            TextView rt = (TextView) mView.findViewById(R.id.tv_upcoming_route);
            rt.setText(route);
        }

        public void setRoute2(String route) {
            TextView rt = (TextView) mView.findViewById(R.id.tv_upcoming_r);
            rt.setText(route);
        }

        public void setNumber(String number) {
            TextView num = (TextView) mView.findViewById(R.id.tv_upcoming_number);
            num.setText(number);
        }

        public void setSits(int sits) {
            TextView num = (TextView) mView.findViewById(R.id.tv_upcoming_sits);
            num.setText(String.valueOf(sits) + " sits");
        }

        public void setPlateNumber(String plate) {
            TextView num = (TextView) mView.findViewById(R.id.tv_upcoming_bus);
            num.setText(plate);
        }
    }
}
