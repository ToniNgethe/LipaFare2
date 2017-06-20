package com.example.toni.lipafare.Operator.Fragments;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.toni.lipafare.Operator.Dialog_mat.Dialog_sacco_details;
import com.example.toni.lipafare.Operator.Dialog_mat.Dialog_sacco_routes;
import com.example.toni.lipafare.Operator.Dialog_mat.Dialog_saf_means;
import com.example.toni.lipafare.Operator.FundsActivity;
import com.example.toni.lipafare.Operator.OperatorDashBoard;
import com.example.toni.lipafare.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

/**
 * Created by toni on 3/25/17.
 */

public class Saccos extends Fragment implements View.OnClickListener{

    private static final int MY_PERMISSIONS_FINE_LOCATION = 100;

    private View mView;
    private ImageView logo;
    private CardView details, funds, means, route;

    private String name,email, number, from , to;
    private String saf, air, orange;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        mView = inflater.inflate(R.layout.fragment_saccos, container, false);
        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);
        //views
        logo = (ImageView) mView.findViewById(R.id.iv_sacco_logo);
        details = (CardView)mView.findViewById(R.id.cardView_sacco_details);
        funds = (CardView) mView.findViewById(R.id.cardView_sacco_funds);
        means = (CardView) mView.findViewById(R.id.cardview_sacco_means);
        route = (CardView) mView.findViewById(R.id.cardView_sacco_route);

        //
        populate();

        //listeners
        details.setOnClickListener(this);
        funds.setOnClickListener(this);
        means.setOnClickListener(this);
        route.setOnClickListener(this);

        return mView;
    }

    private void populate() {

        new Handler().post(new Runnable() {
            @Override
            public void run() {

                FirebaseAuth mAuth = FirebaseAuth.getInstance();
                final DatabaseReference mSacco = FirebaseDatabase.getInstance().getReference().child("Sacco");

                if (mAuth.getCurrentUser() != null) {

                    Query q = mSacco.orderByChild("admin").equalTo(mAuth.getCurrentUser().getUid());
                    q.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                            //get key
                            for (DataSnapshot ds : dataSnapshot.getChildren()) {

                                String key = ds.getKey();

                                //get sacco details
                                DatabaseReference cs = mSacco.child(key);
                                cs.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        if (dataSnapshot.exists()) {

                                            name = dataSnapshot.child("name").getValue().toString();
                                            email = dataSnapshot.child("email").getValue().toString();
                                            number = dataSnapshot.child("number").getValue().toString();

                                            //load images
                                            Glide.with(getActivity()).load(dataSnapshot.child("image").getValue().toString())
                                                    .crossFade()
                                                    .thumbnail(0.5f)
                                                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                                                    .error(R.mipmap.profile2)
                                                    .into(logo);

                                        }
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {
                                       // Toast.makeText(getActivity(), "Error:" + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });

                                //get route
                                DatabaseReference mRoutes = FirebaseDatabase.getInstance().getReference().child("Route");

                                Query q = mRoutes.orderByChild("sacco").equalTo(key);
                                q.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {

                                        String key = null;
                                        for (DataSnapshot ds : dataSnapshot.getChildren()){
                                            key = ds.getKey();
                                        }


                                        if (dataSnapshot.child(key).exists()){

                                            from = dataSnapshot.child(key).child("From_address").getValue().toString();
                                            to = dataSnapshot.child(key).child("To_address").getValue().toString() ;
                                        }

                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {
                                       // Toast.makeText(getActivity(), "Error:" + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });

                                //get means
                                DatabaseReference mMeans = FirebaseDatabase.getInstance().getReference().child("Paymeans");
                                mMeans.child(key).addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        if (dataSnapshot.exists()){

                                            //check fro saf
                                            if (dataSnapshot.child("Safaricom").exists()){
                                                saf = dataSnapshot.child("Safaricom").getValue().toString();
                                            }

                                        }else {
                                            Toast.makeText(getContext(), "No payment means found", Toast.LENGTH_SHORT).show();
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

                }
            }
        });

    }

    @Override
    public void onClick(View v) {
        if (v == details){

            Dialog_sacco_details dialog_sacco_details = new Dialog_sacco_details(getContext(), name, email, number);
            dialog_sacco_details.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog_sacco_details.show();

        }else if(v == funds){

            Intent i = new Intent(getActivity(), FundsActivity.class);
            startActivity(i);

        }else if(v == means){

            Dialog_saf_means  dm = new Dialog_saf_means(getContext(),saf,air,orange);
            dm.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dm.show();

        }else if (v == route){

            Dialog_sacco_routes dialog_sacco_routes
                     = new Dialog_sacco_routes(getContext(),from, to);
            dialog_sacco_routes.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog_sacco_routes.show();

        }
    }
}
