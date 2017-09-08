package com.toni.lipafare.Passanger.Fragments;

import android.content.res.Resources;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.toni.lipafare.Passanger.PassModel.TicketModel;
import com.toni.lipafare.Passanger.PassangerAdapter.UpcomingTripsAdapter;
import com.toni.lipafare.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by toni on 4/28/17.
 */

public class Upcoming extends Fragment {

    private static final String TAG = Upcoming.class.getSimpleName();
    private View mView;
    private LinearLayout no_trips;
    private RecyclerView rv;

    private DatabaseReference mTicket;
    private FirebaseAuth mAuth;

    private List<TicketModel> myTickets;
    private List<String> ticketKeys;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_upcoming, container, false);

        //views
        no_trips = (LinearLayout) mView.findViewById(R.id.linear_upcoming);
        rv = (RecyclerView) mView.findViewById(R.id.rv_cancelled_frag);

        RecyclerView.LayoutManager lm = new GridLayoutManager(getActivity(), 2);

        rv.setLayoutManager(lm);
        rv.addItemDecoration(new GridSpacingItemDecoration(2, dpToPx(6), true));
        rv.setItemAnimator(new DefaultItemAnimator());

        //firebase
        mAuth = FirebaseAuth.getInstance();
        mTicket = FirebaseDatabase.getInstance().getReference().child("Tickets");

        return mView;
    }

    @Override
    public void onStart() {
        super.onStart();

        //check if ticket exists

        Query sw = mTicket.orderByChild("user").equalTo(mAuth.getCurrentUser().getUid());
        sw.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (!dataSnapshot.exists()) {
                    rv.setVisibility(View.GONE);
                    no_trips.setVisibility(View.VISIBLE);
                } else {
                    rv.setVisibility(View.VISIBLE);
                    no_trips.setVisibility(View.GONE);
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        //

        new Handler().post(new Runnable() {
            @Override
            public void run() {


                myTickets = new ArrayList<>();
                ticketKeys = new ArrayList<>();

                Query q = mTicket.orderByChild("user").equalTo(mAuth.getCurrentUser().getUid());
                q.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        myTickets.clear();
                        ticketKeys.clear();
                        Log.d(TAG, dataSnapshot.getKey());
                        // Log.d(TAG, dataSnapshot.getValue().toString());

                        for (final DataSnapshot ds : dataSnapshot.getChildren()) {
                            Log.d(TAG, ds.getKey());
                            Log.d(TAG, ds.getValue().toString());

                            DatabaseReference cs = mTicket.child(ds.getKey());
                            cs.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {

                                    //only display keys belonging to this user
                                    if (Integer.valueOf(dataSnapshot.child("status").getValue().toString()) == 0) {

                                        rv.setVisibility(View.VISIBLE);
                                        no_trips.setVisibility(View.GONE);

                                        myTickets.add(ds.getValue(TicketModel.class));
                                        ticketKeys.add(ds.getKey());
                                        UpcomingTripsAdapter upcomingTripsAdapter = new UpcomingTripsAdapter(getActivity(), myTickets, ticketKeys);
                                        rv.setAdapter(upcomingTripsAdapter);

                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });

                        }
                        if (myTickets.isEmpty()) {
                            rv.setVisibility(View.GONE);
                            no_trips.setVisibility(View.VISIBLE);
                        }

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        });

    }

    /**
     * RecyclerView item decoration - give equal margin around grid item
     */
    public class GridSpacingItemDecoration extends RecyclerView.ItemDecoration {

        private int spanCount;
        private int spacing;
        private boolean includeEdge;

        public GridSpacingItemDecoration(int spanCount, int spacing, boolean includeEdge) {
            this.spanCount = spanCount;
            this.spacing = spacing;
            this.includeEdge = includeEdge;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            int position = parent.getChildAdapterPosition(view); // item position
            int column = position % spanCount; // item column

            if (includeEdge) {
                outRect.left = spacing - column * spacing / spanCount; // spacing - column * ((1f / spanCount) * spacing)
                outRect.right = (column + 1) * spacing / spanCount; // (column + 1) * ((1f / spanCount) * spacing)

                if (position < spanCount) { // top edge
                    outRect.top = spacing;
                }
                outRect.bottom = spacing; // item bottom
            } else {
                outRect.left = column * spacing / spanCount; // column * ((1f / spanCount) * spacing)
                outRect.right = spacing - (column + 1) * spacing / spanCount; // spacing - (column + 1) * ((1f /    spanCount) * spacing)

                if (position >= spanCount) {
                    outRect.top = spacing; // item top
                }
            }
        }
    }

    /**
     * Converting dp to pixel
     */
    private int dpToPx(int dp) {
        Resources r = getResources();
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics()));
    }

}
