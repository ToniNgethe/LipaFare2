package com.example.toni.lipafare.Dialogs;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.widget.TextView;

import com.example.toni.lipafare.R;

/**
 * Created by toni on 5/5/17.
 */

public class Loading extends Dialog {

    private Context ctx;
    private String msg;
    private TextView loading;

    public Loading(@NonNull Context context, String msg) {
        super(context);

        this.ctx = context;
        this.msg = msg;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_loading);
        loading = (TextView) findViewById(R.id.tv_loading);

        loading.setText(msg);
    }
}

