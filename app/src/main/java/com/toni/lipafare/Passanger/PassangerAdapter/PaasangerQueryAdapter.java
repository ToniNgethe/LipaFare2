package com.toni.lipafare.Passanger.PassangerAdapter;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.toni.lipafare.Passanger.MatatuSchedule;
import com.toni.lipafare.Passanger.PassModel.PassangerQuery;
import com.toni.lipafare.R;

import java.util.List;

/**
 * Created by toni on 5/6/17.
 */

public class PaasangerQueryAdapter extends RecyclerView.Adapter<PaasangerQueryAdapter.PassangerQueryViewHolder> {

    public static final String SACCO_KEY= "SACCO_KEY";
    public static final String FROM_ADD = "FROM_ADDRESS";
    public static final String TO_ADD = "TO_ADDRESS";
    private List<PassangerQuery> myList;
    private List<String> keyList;
    private Context ctx;

    private String from,to;

    public PaasangerQueryAdapter(Context ctx, List<PassangerQuery> myList, List<String> key, String from, String to) {
        this.myList = myList;
        this.keyList = key;
        this.ctx = ctx;
        this.from = from;
        this.to = to;
    }


    @Override
    public PassangerQueryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new PassangerQueryViewHolder(ctx, LayoutInflater.from(parent.getContext()).inflate(R.layout.row_passangerquery, parent, false));
    }

    @Override
    public void onBindViewHolder(final PassangerQueryViewHolder holder, int position) {
        if (holder != null) {

            final PassangerQuery ps = myList.get(position);
            final String key = keyList.get(position);
            new Handler().post(new Runnable() {
                @Override
                public void run() {

                    DatabaseReference mSaccos = FirebaseDatabase.getInstance().getReference().child("Sacco");
                    mSaccos.child(key).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()){

                                holder.setSaccoName(dataSnapshot.child("name").getValue().toString());
                                holder.loadSaccoImage(dataSnapshot.child("image").getValue().toString());
                                holder.setSaccoTime(ps.getFrom_address(), ps.getTo_address());
                                holder.setSaccoCost(dataSnapshot.child("cost").getValue().toString());

                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

                }
            });

            //open matatu
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent a = new Intent(ctx, MatatuSchedule.class);
                    a.putExtra(SACCO_KEY, key);
                    a.putExtra(FROM_ADD, from);
                    a.putExtra(TO_ADD, to);
                    v.getContext().startActivity(a);
                }
            });

        }
    }

    @Override
    public int getItemCount() {
        return myList.size();
    }

    public static class NoSaccoHolder extends RecyclerView.ViewHolder{

        public NoSaccoHolder(View itemView) {
            super(itemView);
        }
    }

    public static class PassangerQueryViewHolder extends RecyclerView.ViewHolder {

        private Context ctx ;
        private View mView;
        public PassangerQueryViewHolder(Context ctx, View itemView) {
            super(itemView);
            this.ctx = ctx;
            this.mView = itemView;
        }

        //setSacco name
        public void setSaccoName(String name){
            TextView tv = (TextView) mView.findViewById(R.id.tv_passsangerquery_name);
            tv.setText(name);
        }

        //setSacco time
        public void setSaccoTime(String from, String to){
            TextView tv = (TextView) mView.findViewById(R.id.tv_passsangerquery_from);
            tv.setText(from);
            TextView tv1 = (TextView) mView.findViewById(R.id.tv_passsangerquery_to);
            tv1.setText(to);
        }
        //setSacco name
        public void setSaccoCost(String cost){
            TextView tv = (TextView) mView.findViewById(R.id.tv_passsangerquery_cost);
            tv.setText(cost + " ksh/passanger");
        }

        public void loadSaccoImage(final String url){
            final ImageView image = (ImageView) mView.findViewById(R.id.iv_passsangerquery_sacco);
            new Handler().post(new Runnable() {
                @Override
                public void run() {

                    Glide.with(ctx).load(url)
                            .crossFade()
                            .thumbnail(0.5f)
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .error(R.mipmap.error_network)
                            .into(image);
                }
            });
        }

    }
}
