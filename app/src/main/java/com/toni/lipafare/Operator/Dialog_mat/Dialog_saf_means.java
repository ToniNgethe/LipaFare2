package com.toni.lipafare.Operator.Dialog_mat;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.toni.lipafare.R;

/**
 * Created by toni on 5/17/17.
 */

public class Dialog_saf_means extends Dialog {

    private String saf, airtel, orange;
    private Context ctx;

    private LinearLayout l_saf, l_airtel, l_orange;

    public Dialog_saf_means(Context context, String saf, String airtell, String orange) {
        super(context);

        this.ctx = context;
        this.saf = saf;
        this.airtel = airtell;
        this.orange = orange;

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_means);

        //views
        l_saf = (LinearLayout) findViewById(R.id.linear_dialogmeans_saf);
        l_saf.setVisibility(View.GONE);
        l_airtel = (LinearLayout) findViewById(R.id.linear_dialogmeans_airtel);
        l_airtel.setVisibility(View.GONE);
        l_orange = (LinearLayout) findViewById(R.id.linear_dialogmeans_orange);
        l_orange.setVisibility(View.GONE);

        TextView t_saf = (TextView) findViewById(R.id.tv_dialogmeans_saf);
        TextView t_airtel = (TextView) findViewById(R.id.tv_dialogmeans_airtel);
        TextView t_orange = (TextView) findViewById(R.id.tv_dialogmeans_money);

        if (saf != null){

            l_saf.setVisibility(View.VISIBLE);
            t_saf.setText(saf);

        }else if(airtel != null){

            l_airtel.setVisibility(View.VISIBLE);
            t_airtel.setText(airtel);

        }else if( orange != null){
            l_orange.setVisibility(View.VISIBLE);
            t_orange.setText(orange);
        }
    }
}
