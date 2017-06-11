package com.example.toni.lipafare.Operator.Dialog_mat;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.widget.TextView;

import com.example.toni.lipafare.R;

/**
 * Created by toni on 5/17/17.
 */

public class Dialog_sacco_routes extends Dialog {

    private Context ctx;
    private String from, to;

    public Dialog_sacco_routes(Context context, String from, String to) {
        super(context);

        this.ctx = context;
        this.from = from;
        this.to = to;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_routes);

        TextView tv_from = (TextView) findViewById(R.id.tv_dialog_sacco_from);
        TextView tv_to = (TextView) findViewById(R.id.tv_dialog_sacco_to);

        //set to
        tv_from.setText(from);
        tv_to.setText(to);
    }
}
