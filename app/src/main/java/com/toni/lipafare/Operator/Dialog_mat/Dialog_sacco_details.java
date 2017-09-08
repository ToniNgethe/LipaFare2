package com.toni.lipafare.Operator.Dialog_mat;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.widget.TextView;

import com.toni.lipafare.R;


/**
 * Created by toni on 5/17/17.
 */

public class Dialog_sacco_details extends Dialog {

    private String name, email, number;
    private Context ctx;

    public Dialog_sacco_details(Context context, String name, String email, String number) {
        super(context);
        this.ctx = context;
        this.name = name;
        this.email = email;
        this.number = number;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_details);

        TextView _name = (TextView) findViewById(R.id.tv_sacco_dialog_name);
        TextView _email = (TextView) findViewById(R.id.tv_sacco_dialog_email);
        TextView _number= (TextView) findViewById(R.id.tv_sacco_dialog_number);

        //set text
        _name.setText(name);
        _email.setText(email);
        _number.setText(number);
    }
}
