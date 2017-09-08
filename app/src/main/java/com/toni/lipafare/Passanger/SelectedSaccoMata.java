package com.toni.lipafare.Passanger;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.toni.lipafare.Passanger.PassModel.PassangerMatatuModel;
import com.toni.lipafare.Passanger.PassangerAdapter.PaasangerQueryAdapter;
import com.toni.lipafare.Passanger.PassangerDialog.PassangerMatatauDialog;
import com.toni.lipafare.R;

public class SelectedSaccoMata extends AppCompatActivity {

    private RecyclerView rv;
    private String saccoKey;
    private DatabaseReference mMatatus, mSacco;
    private RelativeLayout linearLayout;
    private String from, to;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selected_sacco_mata);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeAsUpIndicator(R.mipmap.ic_close_white_24dp);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        linearLayout = (RelativeLayout) findViewById(R.id.linearlayout_selectedsaccomat);
        //rv
        rv = (RecyclerView) findViewById(R.id.rv_selectedsaccomat);
        LinearLayoutManager lm = new LinearLayoutManager(this);
        lm.setStackFromEnd(true);
        lm.setReverseLayout(true);
        rv.setLayoutManager(lm);

        //get key from intent
        saccoKey = getIntent().getExtras().getString(PaasangerQueryAdapter.SACCO_KEY);
        from = getIntent().getExtras().getString(PaasangerQueryAdapter.FROM_ADD);
        to = getIntent().getExtras().getString(PaasangerQueryAdapter.TO_ADD);

        //sacco name
        mSacco = FirebaseDatabase.getInstance().getReference().child("Sacco");
        mSacco.child(saccoKey).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    getSupportActionBar().setTitle(dataSnapshot.child("name").getValue().toString() + " Buses/Mat");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        //firebase
        mMatatus = FirebaseDatabase.getInstance().getReference().child("Matatu");

    }

    @Override
    protected void onStart() {
        super.onStart();

        Query q = mMatatus.orderByChild("sacco").equalTo(saccoKey);

        q.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists()){
                    rv.setVisibility(View.GONE);
                    linearLayout.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        FirebaseRecyclerAdapter<PassangerMatatuModel, SelectedSaccoMatViewHolde> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<PassangerMatatuModel, SelectedSaccoMatViewHolde>(

                PassangerMatatuModel.class,
                R.layout.row_selectedsaccomats,
                SelectedSaccoMatViewHolde.class,
                q

        ) {
            @Override
            protected void populateViewHolder(SelectedSaccoMatViewHolde viewHolder, PassangerMatatuModel model, int position) {

                final String key = getRef(position).getKey();
                    viewHolder.setMatName(model.getName());
                    viewHolder.setMatPlate(model.getPlate());
                    viewHolder.setMatFrom(model.getFrom());
                    viewHolder.setMatTo(model.getTo());
                    viewHolder.setMatStatus(model.getStatus());
                    viewHolder.setMatImage(SelectedSaccoMata.this, model.getLogo());
                    viewHolder.setMatSits(model.getSits());

                    viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            PassangerMatatauDialog ps = new PassangerMatatauDialog(SelectedSaccoMata.this,key,from,to);
                            ps.getWindow().getAttributes().windowAnimations = R.style.MyAnimation_Window;
                            ps.setCanceledOnTouchOutside(false);
                            ps.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                            ps.show();
                        }
                    });


            }
        };

        rv.setAdapter(firebaseRecyclerAdapter);
        firebaseRecyclerAdapter.notifyDataSetChanged();
    }

    public static class SelectedSaccoMatViewHolde extends RecyclerView.ViewHolder{

        private View mView;


        public SelectedSaccoMatViewHolde(View itemView) {
            super(itemView);
            mView = itemView;
        }

        public void setMatName(String name){
            TextView textView = (TextView) mView.findViewById(R.id.tv_selectedsacco_name);
            textView.setText(name);
        }
        public void setMatPlate(String plate){
            TextView textView = (TextView) mView.findViewById(R.id.tv_selectedsacco_plate);
            textView.setText(plate);
        }
        public void setMatFrom(String from){
            TextView textView = (TextView) mView.findViewById(R.id.tv_selectedsacco_from);
            textView.setText(from);
        }
        public void setMatTo(String to){
            TextView textView = (TextView) mView.findViewById(R.id.tv_selectedsacco_to);
            textView.setText(to);
        }
        public void setMatStatus(String stat){
            TextView textView = (TextView) mView.findViewById(R.id.tv_selectedsacco_status);
            textView.setText(stat);
        }
        public void setMatSits(String sits){
            TextView textView = (TextView) mView.findViewById(R.id.tv_selectedsacco_sits);
            textView.setText(sits + " Sits");
        }
        public void setMatImage(final Context ctx, final String url){
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

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return false;
    }

}
