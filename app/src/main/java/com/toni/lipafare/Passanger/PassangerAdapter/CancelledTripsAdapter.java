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
import com.toni.lipafare.Passanger.RestoreTicketActicity;
import com.toni.lipafare.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by toni on 6/2/17.
 */

public class CancelledTripsAdapter extends RecyclerView.Adapter<CancelledTripsAdapter.CancelledTripsViewHolder> {

    private Context ctx;
    private List<TicketModel> myTickets = new ArrayList<>();
    private List<String> keys = new ArrayList<>();

    public CancelledTripsAdapter(Context ctx, List<TicketModel> tickets, List<String> tKeys) {
        if (tickets.size() != this.myTickets.size() || !this.myTickets.containsAll(tickets)) {
            this.ctx = ctx;
            this.myTickets = tickets;
            this.keys = tKeys;
            notifyDataSetChanged();
        }
    }

    @Override
    public CancelledTripsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_cancelled_frag, parent, false);

        return new CancelledTripsViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final CancelledTripsViewHolder holder, int position) {

        if (holder != null){
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

                            if (dataSnapshot.exists()){
                                holder.setPlateNumber(dataSnapshot.child("plate").getValue().toString());
                                //get Route
                                // DatabaseReference sac = FirebaseDatabase.getInstance().getReference().child("");
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

                    Intent i = new Intent(ctx, RestoreTicketActicity.class);
                    i.putExtra("TICKET_KEY", key);
                    v.getContext().startActivity(i);

                }
            });

        }
    }

    @Override
    public int getItemCount() {
        return myTickets.size();
    }

    public static class CancelledTripsViewHolder extends RecyclerView.ViewHolder {

        public View mView;
        public CancelledTripsViewHolder(View itemView) {
            super(itemView);

            this.mView = itemView;
        }

        public void setRoute(String from){
            TextView rt = (TextView) mView.findViewById(R.id.tv_cancelled_route);
            rt.setText(from);
        }
        public void setRoute2(String to){
            TextView rt = (TextView) mView.findViewById(R.id.tv_cancelled_r);
            rt.setText(to);
        }
        public void setNumber(String number){
            TextView num = (TextView) mView.findViewById(R.id.tv_cancelled_number);
            num.setText(number);
        }

        public void setSits(int sits){
            TextView num = (TextView) mView.findViewById(R.id.tv_cancelled_sits);
            num.setText(String.valueOf(sits));
        }
        public void setPlateNumber(String plate){
            TextView num = (TextView) mView.findViewById(R.id.tv_cancelled_bus);
            num.setText(plate);
        }
    }
}
