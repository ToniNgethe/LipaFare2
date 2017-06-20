package com.example.toni.lipafare.Operator;

import android.content.res.Resources;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.toni.lipafare.Operator.Adapter.AllFundsAdapter;
import com.example.toni.lipafare.Operator.Adapter.FundsAdapter;
import com.example.toni.lipafare.Operator.Model.AllFundsModel;
import com.example.toni.lipafare.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class AllFundsActivity extends AppCompatActivity {

    private TextView total;
    private LinearLayout indicator, bottom;
    private RecyclerView rv;
    private String matatuKey;
    private DatabaseReference mTickets;
    private List<AllFundsModel> mList;
    private double totalIncome = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_funds);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        //views
        total = (TextView) findViewById(R.id.tv_total);
        indicator = (LinearLayout) findViewById(R.id.linear_allfundsactivity);
        bottom = (LinearLayout) findViewById(R.id.linearLayout5);
        rv = (RecyclerView) findViewById(R.id.rv_allfundsActivity);
        RecyclerView.LayoutManager lm = new GridLayoutManager(this, 3);

        rv.setLayoutManager(lm);
        rv.addItemDecoration(new GridSpacingItemDecoration(3, dpToPx(6), true));
        rv.setItemAnimator(new DefaultItemAnimator());

        //get matatu key
        matatuKey = getIntent().getExtras().getString(FundsAdapter.MATATU_KEY_FUNDS);

        //Firebase
        mTickets = FirebaseDatabase.getInstance().getReference().child("Tickets");
    }

    @Override
    protected void onStart() {
        super.onStart();

        mList = new ArrayList<>();
        mList.clear();
        Query qqQuery = mTickets.orderByChild("matatu").equalTo(matatuKey);
        qqQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {


                if (dataSnapshot.exists()) {
                    indicator.setVisibility(View.GONE);
                    bottom.setVisibility(View.VISIBLE);
                    rv.setVisibility(View.VISIBLE);


                    for (final DataSnapshot ds : dataSnapshot.getChildren()) {

                        DatabaseReference cd = mTickets.child(ds.getKey());
                        cd.child("status").addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {

                                if (Integer.valueOf(dataSnapshot.getValue().toString()) != 2) {

                                    totalIncome = totalIncome + Double.valueOf(ds.child("total").getValue().toString());
                                    mList.add(ds.getValue(AllFundsModel.class));
                                    AllFundsAdapter allFundsAdapter = new AllFundsAdapter(AllFundsActivity.this, mList);
                                    rv.setAdapter(allFundsAdapter);

                                    total.setText(String.valueOf(totalIncome) + " Ksh");
                                }

                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });

                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }

        });


        if (mList.isEmpty()) {
            indicator.setVisibility(View.VISIBLE);
            bottom.setVisibility(View.GONE);
            rv.setVisibility(View.GONE);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.allfunds_menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.funds_clear:

                new SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
                        .setTitleText("Are you sure you want to clear all Transactions? ")
                        .setContentText("Proceed with caution!")
                        .setConfirmText("Yes")
                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sweetAlertDialog) {

                                sweetAlertDialog.dismiss();

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
                break;
            case R.id.funds_date:


                break;
        }

        return super.

                onOptionsItemSelected(item);
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
