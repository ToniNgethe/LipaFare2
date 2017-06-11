package com.example.toni.lipafare.Passanger;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.toni.lipafare.R;

public class Tickets extends AppCompatActivity {

    private TextView _number;
    private ImageButton _close;
    private String ticket, file;
    private ImageView ticketImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tickets);

        //views
        _number = (TextView) findViewById(R.id.tv_ticket_number);
        _close = (ImageButton) findViewById(R.id.imageButton_ticket_close);
        ticketImage = (ImageView) findViewById(R.id.iv_ticket_qr);

        //get vars
        ticket = getIntent().getExtras().getString("TICKET_KEY");
        file = getIntent().getExtras().getString("TICKET_QR");

        new Handler().post(new Runnable() {
            @Override
            public void run() {

                Glide.with(Tickets.this).load(file)
                        .crossFade()
                        .thumbnail(0.5f)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .error(R.mipmap.error_network)
                        .into(ticketImage);
            }
        });

        //set to text
        _number.setText(ticket);

        //close ticket
        _close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Tickets.this, PassangerPanel.class));
                finish();
            }
        });
    }
}
