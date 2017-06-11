package com.example.toni.lipafare.Passanger.PassangerDialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.Button;

import com.example.toni.lipafare.Passanger.Tickets;
import com.example.toni.lipafare.R;
import com.google.firebase.database.DatabaseReference;

/**
 * Created by toni on 5/23/17.
 */

public class SuccessPayment extends Dialog {

    public static final String TICKETKEY = "TICKET_KEY";
    private Context ctx;
    private String matKey;
    private String ticketKey;
    private DatabaseReference mMatatu;
    private String bitmap;

    public SuccessPayment(@NonNull Context context, String matKey, int sits, int total, String ticketKey, String bitmap) {
        super(context);

        this.ctx = context;
        this.matKey = matKey;
        this.ticketKey = ticketKey;
        this.bitmap = bitmap;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_success_payment);

        Button submit = (Button) findViewById(R.id.dialog_success_view);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ctx, Tickets.class);
                i.putExtra("TICKET_KEY", ticketKey);
                i.putExtra("TICKET_QR", bitmap);
                v.getContext().startActivity(i);
                dismiss();
                ((Activity) ctx).finish();
            }
        });
    }
}
