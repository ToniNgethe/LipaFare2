package com.example.toni.lipafare.Passanger.PassangerAdapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.toni.lipafare.Passanger.PassModel.PassangerMatatuModel;
import com.example.toni.lipafare.Passanger.PassangerDialog.PassangerMatatauDialog;
import com.example.toni.lipafare.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by toni on 6/7/17.
 */

public class ScheduleAdapter extends RecyclerView.Adapter<ScheduleAdapter.ScheduleViewHolder> {


    private Context ctx;
    private List<PassangerMatatuModel> matList = new ArrayList<>();
    private List<String> keyList = new ArrayList<>();
    private String from, to;

    public ScheduleAdapter(Context ctx, List<PassangerMatatuModel> matList, List<String> key, String from, String to) {
        if (matList.size() != this.matList.size() || !this.matList.containsAll(matList)) {
            this.ctx = ctx;
            this.matList = matList;
            this.keyList = key;
            this.from = from;
            this.to = to;
            notifyDataSetChanged();
        }
    }

    @Override
    public ScheduleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ScheduleViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.row_selectedsaccomats, parent, false));
    }

    @Override
    public void onBindViewHolder(final ScheduleViewHolder viewHolder, int position) {

        final PassangerMatatuModel model = matList.get(position);
        final String key = keyList.get(position);

        viewHolder.setMatName(model.getName());
        viewHolder.setMatPlate(model.getPlate());
        viewHolder.setMatFrom(model.getFrom());
        viewHolder.setMatTo(model.getTo());
        viewHolder.setMatStatus(model.getStatus());
        viewHolder.setMatImage(ctx, model.getLogo());
        viewHolder.setMatSits(model.getSits());

        DatabaseReference mQueu = FirebaseDatabase.getInstance().getReference().child("Queue").child(key);
        mQueu.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (dataSnapshot.exists()){
                    viewHolder.setMatStatus(dataSnapshot.child("sits").getValue().toString());
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PassangerMatatauDialog ps = new PassangerMatatauDialog(ctx, key, from, to);
                ps.getWindow().getAttributes().windowAnimations = R.style.MyAnimation_Window;
                ps.setCanceledOnTouchOutside(false);
                ps.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                ps.show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return matList.size();
    }

    public static class ScheduleViewHolder extends RecyclerView.ViewHolder {
        private View mView;


        public ScheduleViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
        }

        public void setMatName(String name) {
            TextView textView = (TextView) mView.findViewById(R.id.tv_selectedsacco_name);
            textView.setText(name);
        }

        public void setMatPlate(String plate) {
            TextView textView = (TextView) mView.findViewById(R.id.tv_selectedsacco_plate);
            textView.setText(plate);
        }

        public void setMatFrom(String from) {
            TextView textView = (TextView) mView.findViewById(R.id.tv_selectedsacco_from);
            textView.setText(from);
        }

        public void setMatTo(String to) {
            TextView textView = (TextView) mView.findViewById(R.id.tv_selectedsacco_to);
            textView.setText(to);
        }

        public void setMatStatus(String stat) {
            TextView textView = (TextView) mView.findViewById(R.id.tv_selectedsacco_status);
            textView.setText(" "+stat);
        }

        public void setMatSits(String sits) {
            TextView textView = (TextView) mView.findViewById(R.id.tv_selectedsacco_sits);
            textView.setText(sits + " Sits");
        }

        public void setMatImage(final Context ctx, final String url) {
            final ImageView image = (ImageView) mView.findViewById(R.id.iv_selectedsacco_image);
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
