package com.toni.lipafare.Operator;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.pdf.PdfDocument;
import android.os.Build;
import android.os.Bundle;
import android.os.CancellationSignal;
import android.os.ParcelFileDescriptor;
import android.print.PageRange;
import android.print.PrintAttributes;
import android.print.PrintDocumentAdapter;
import android.print.PrintDocumentInfo;
import android.print.PrintManager;
import android.print.pdf.PrintedPdfDocument;
import android.support.annotation.RequiresApi;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.toni.lipafare.Operator.Adapter.AllFundsAdapter;
import com.toni.lipafare.Operator.Adapter.FundsAdapter;
import com.toni.lipafare.Operator.Model.AllFundsModel;
import com.toni.lipafare.R;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class AllFundsActivity extends AppCompatActivity implements SearchView.OnQueryTextListener {

    private TextView total;
    private LinearLayout indicator, bottom;
    private RecyclerView rv;
    private String matatuKey;
    private DatabaseReference mTickets;
    private List<AllFundsModel> mList;
    private double totalIncome = 0;
    private AllFundsAdapter allFundsAdapter;


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
                                    allFundsAdapter = new AllFundsAdapter(AllFundsActivity.this, mList);
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

        MenuItem searchViewItem = menu.findItem(R.id.search);


        final SearchView searchViewAndroidActionBar = (SearchView) MenuItemCompat.getActionView(searchViewItem);
        searchViewAndroidActionBar.setOnQueryTextListener(this);

        MenuItemCompat.setOnActionExpandListener(searchViewItem, new MenuItemCompat.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {

                //do something when collapsed...
                allFundsAdapter.setFilter(mList);
                return true; // Return true to collapse action view
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                return true;
            }
        });

        return true;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
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
            case R.id.funds_print:

                PrintManager printManager = (PrintManager) this
                        .getSystemService(Context.PRINT_SERVICE);

                String jobName = this.getString(R.string.app_name) +
                        " Document";

                printManager.print(jobName, new MyPrintDocumentAdapter(this, mList),
                        null);

                break;
        }

        return super.

                onOptionsItemSelected(item);
    }

    @Override
    public boolean onQueryTextSubmit(String query) {

        Log.d("sdfdsfd", String.valueOf(mList));

        final List<AllFundsModel> filteredModelList = filter(mList, query);
        allFundsAdapter.setFilter(filteredModelList);

        return true;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        final List<AllFundsModel> filteredModelList = filter(mList, newText);
        allFundsAdapter.setFilter(filteredModelList);
        return true;
    }

    private List<AllFundsModel> filter(List<AllFundsModel> models, String query) {

        query = query.toLowerCase();

        final List<AllFundsModel> filteredModelList = new ArrayList<>();

        for (AllFundsModel model : models) {

            final String n = model.getDate();
            if (n != null) {
                if (n.contains(query)) {
                    filteredModelList.add(model);
                }
            } else {
                Toast.makeText(this, "Unable to get dates", Toast.LENGTH_SHORT).show();
            }
        }
        return filteredModelList;
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


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public class MyPrintDocumentAdapter extends PrintDocumentAdapter {
        Context context;
        private int pageHeight;
        private int pageWidth;
        public PdfDocument myPdfDocument;
        public int totalpages = 5;
        private List<AllFundsModel> print = new ArrayList<>();

        public MyPrintDocumentAdapter(Context context, List<AllFundsModel> list) {
            this.context = context;

            print = list;
        }

        @Override
        public void onLayout(PrintAttributes oldAttributes,
                             PrintAttributes newAttributes,
                             CancellationSignal cancellationSignal,
                             LayoutResultCallback callback,
                             Bundle metadata) {

            myPdfDocument = new PrintedPdfDocument(context, newAttributes);

            pageHeight =
                    newAttributes.getMediaSize().getHeightMils()/1000 * 72;
            pageWidth =
                    newAttributes.getMediaSize().getWidthMils()/1000 * 72;

            if (cancellationSignal.isCanceled() ) {
                callback.onLayoutCancelled();
                return;
            }

            if (totalpages > 0) {
                PrintDocumentInfo.Builder builder = new PrintDocumentInfo
                        .Builder("funds.pdf")
                        .setContentType(PrintDocumentInfo.CONTENT_TYPE_DOCUMENT)
                        .setPageCount(totalpages);

                PrintDocumentInfo info = builder.build();
                callback.onLayoutFinished(info, true);
            } else {
                callback.onLayoutFailed("Page count is zero.");
            }
        }


        @Override
        public void onWrite(final PageRange[] pageRanges,
                            final ParcelFileDescriptor destination,
                            final CancellationSignal cancellationSignal,
                            final WriteResultCallback callback) {
            for (int i = 0; i < totalpages; i++) {
                if (pageInRange(pageRanges, i))
                {
                    PdfDocument.PageInfo newPage = new PdfDocument.PageInfo.Builder(pageWidth,
                            pageHeight, i).create();

                    PdfDocument.Page page =
                            myPdfDocument.startPage(newPage);

                    if (cancellationSignal.isCanceled()) {
                        callback.onWriteCancelled();
                        myPdfDocument.close();
                        myPdfDocument = null;
                        return;
                    }
                    drawPage(page, i);
                    myPdfDocument.finishPage(page);
                }
            }

            try {
                myPdfDocument.writeTo(new FileOutputStream(
                        destination.getFileDescriptor()));
            } catch (IOException e) {
                callback.onWriteFailed(e.toString());
                return;
            } finally {
                myPdfDocument.close();
                myPdfDocument = null;
            }

            callback.onWriteFinished(pageRanges);
        }

        private boolean pageInRange(PageRange[] pageRanges, int page)
        {
            for (int i = 0; i<pageRanges.length; i++)
            {
                if ((page >= pageRanges[i].getStart()) &&
                        (page <= pageRanges[i].getEnd()))
                    return true;
            }
            return false;
        }
        private void drawPage(PdfDocument.Page page,
                              int pagenumber) {
            Canvas canvas = page.getCanvas();

            pagenumber++; // Make sure page numbers start at 1

            int titleBaseLine = 72;
            int leftMargin = 54;

            Paint paint = new Paint();
            paint.setColor(Color.BLACK);
            paint.setTextSize(30);
            canvas.drawText(
                    "LipaFare transaction summary",
                    leftMargin,
                    titleBaseLine,
                    paint);

            paint.setTextSize(14);

            int total= 0;
            int sits=0;
            int p = titleBaseLine;

            for (int i=0; i<mList.size(); i++){
                AllFundsModel model= mList.get(i);


                canvas.drawText("",  leftMargin, p + 40 , paint);
                canvas.drawText("Sits : " + model.getSits(),  leftMargin, p + 50 , paint);
                canvas.drawText("Amount : " + model.getTotal(),  leftMargin, p + 70, paint);
                canvas.drawText("Date : " + model.getDate(),  leftMargin, p + 100, paint);

                sits+=model.getSits();
                total+=model.getTotal();

                p+=40;
            }

            canvas.drawText("Total Sits : " + sits,  leftMargin, p + 100, paint);
            canvas.drawText("Total Amount collected: " + total,  leftMargin, p + 120, paint);

            if (pagenumber % 2 == 0)
                paint.setColor(Color.RED);
            else
                paint.setColor(Color.GREEN);
//
//            PdfDocument.PageInfo pageInfo = page.getInfo();
//
//
//            canvas.drawCircle(pageInfo.getPageWidth()/2,
//                    pageInfo.getPageHeight()/2,
//                    150,
//                    paint);
        }

    }
}
