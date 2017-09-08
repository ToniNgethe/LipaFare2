package com.toni.lipafare.Operator;

import android.content.res.Resources;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.LinearLayout;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.toni.lipafare.Operator.Adapter.OthersMatatu;
import com.toni.lipafare.Operator.Model.Matatu;
import com.toni.lipafare.R;

import java.util.ArrayList;
import java.util.List;

public class OtherUsersActivity extends AppCompatActivity {

    private static final String TAG = OtherUsersActivity.class.getCanonicalName();
    private LinearLayout linear;
    private FloatingActionButton add;
    private RecyclerView rv;
    private DatabaseReference mMats, mSacco;
    private FirebaseAuth mAuth;
    private List<Matatu> matatuList = new ArrayList<>();
    private List<String> matKey = new ArrayList<>();
    private OthersMatatu matatuAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_other_users);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        //viewws
        linear = (LinearLayout) findViewById(R.id.others_matatu_linear);
        add = (FloatingActionButton) findViewById(R.id.floatingAction_other_users);
        rv = (RecyclerView) findViewById(R.id.other_users_rv);
        RecyclerView.LayoutManager lm = new GridLayoutManager(this, 3);

        rv.setLayoutManager(lm);
        rv.addItemDecoration(new GridSpacingItemDecoration(3, dpToPx(6), true));
        rv.setItemAnimator(new DefaultItemAnimator());

    }


    @Override
    public void onStart() {
        super.onStart();

        if (mAuth.getCurrentUser() != null) {

            matatuList.clear();
            matKey.clear();
            Query q = mSacco.orderByChild("admin").equalTo(mAuth.getCurrentUser().getUid());
            q.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    for (DataSnapshot ds : dataSnapshot.getChildren()) {

                        final String key = ds.getKey();
                        Log.d(TAG, key);

                        Query q = mMats.orderByChild("sacco").equalTo(key);
                        q.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot1) {

                                for (DataSnapshot single : dataSnapshot1.getChildren()) {
                                    Log.d(TAG, "Key:" + single.getKey());

                                    Matatu mt = single.getValue(Matatu.class);
                                    matatuList.add(mt);
                                    matKey.add(single.getKey());

                                    matatuAdapter = new OthersMatatu(OtherUsersActivity.this, matatuList, matKey);
                                    rv.setAdapter(matatuAdapter);
                                    matatuAdapter.notifyDataSetChanged();
                                }

                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                //    Toast.makeText(getActivity(),databaseError.getMessage(),Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
//                    Toast.makeText(getActivity(), databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });

        }

        if (matatuList.size() == 0){
            rv.setVisibility(View.GONE);
            linear.setVisibility(View.VISIBLE);
        }

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
