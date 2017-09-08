package com.toni.lipafare.Passanger;

import android.content.res.Resources;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.View;
import android.widget.LinearLayout;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.toni.lipafare.Passanger.PassModel.TicketModel;
import com.toni.lipafare.Passanger.PassangerAdapter.CancelTicketAtivityAdapter;
import com.toni.lipafare.R;

import java.util.ArrayList;
import java.util.List;

public class CancelTicketActivity extends AppCompatActivity {

    private RecyclerView rv;
    private LinearLayout linear, info;

    private DatabaseReference mTicket;
    private FirebaseAuth mAuth;

    private List<TicketModel> myTickets;
    private List<String> ticketKeys;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cancel_ticket);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Upcoming Trips");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);


        //views
        rv = (RecyclerView) findViewById(R.id.rv_cancel_side);
        linear = (LinearLayout) findViewById(R.id.linear_cancel_side);
        info = (LinearLayout) findViewById(R.id.linearLayout_cancel_info);

        RecyclerView.LayoutManager lm = new GridLayoutManager(CancelTicketActivity.this, 2);

        rv.setLayoutManager(lm);
        rv.addItemDecoration(new GridSpacingItemDecoration(2, dpToPx(6), true));
        rv.setItemAnimator(new DefaultItemAnimator());
//firebase
        mAuth = FirebaseAuth.getInstance();
        mTicket = FirebaseDatabase.getInstance().getReference().child("Tickets");
    }

    @Override
    public void onStart() {
        super.onStart();

        //check if ticket exists

        Query sw = mTicket.orderByChild("user").equalTo(mAuth.getCurrentUser().getUid());
        sw.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (!dataSnapshot.exists()){
                    rv.setVisibility(View.GONE);
                    info.setVisibility(View.GONE);
                    linear.setVisibility(View.VISIBLE);
                }else {
                    rv.setVisibility(View.VISIBLE);
                    info.setVisibility(View.VISIBLE);
                    linear.setVisibility(View.GONE);
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        //

        myTickets = new ArrayList<>();
        ticketKeys = new ArrayList<>();

        Query q = mTicket.orderByChild("user").equalTo(mAuth.getCurrentUser().getUid());
        q.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                myTickets.clear();
                ticketKeys.clear();
                // Log.d(TAG, dataSnapshot.getValue().toString());

                for (final DataSnapshot ds:dataSnapshot.getChildren()){

                    DatabaseReference cs = mTicket.child(ds.getKey());
                    cs.child("status").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                            //only display keys belonging to this user
                            if (Integer.valueOf(dataSnapshot.getValue().toString()) == 0){

                                rv.setVisibility(View.VISIBLE);
                                info.setVisibility(View.VISIBLE);
                                linear.setVisibility(View.GONE);

                                myTickets.add(ds.getValue(TicketModel.class));
                                ticketKeys.add(ds.getKey());
                                CancelTicketAtivityAdapter upcomingTripsAdapter = new CancelTicketAtivityAdapter(CancelTicketActivity.this, myTickets, ticketKeys);
                                rv.setAdapter(upcomingTripsAdapter);
                            }
                        }
                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

                }
                if (myTickets.isEmpty()){
                    rv.setVisibility(View.GONE);
                    info.setVisibility(View.GONE);
                    linear.setVisibility(View.VISIBLE);
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

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
