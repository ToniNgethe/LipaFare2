package com.toni.lipafare.Operator.Dialog_mat;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.toni.lipafare.R;

import java.util.HashMap;

/**
 * Created by toni on 5/16/17.
 */

public class Dialog_airtell extends Dialog {
    private String key;
    private Context ctx;
    private ImageButton close;
    private Button save;
    private EditText number;
    private ProgressBar progrees;
    private HashMap<String, Double> payMeans;

    public Dialog_airtell(Context context, String key) {
        super(context);

        this.key = key;
        this.ctx = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_airtell);

        close = (ImageButton) findViewById(R.id.close_airtell);
        save = (Button) findViewById(R.id.save_airtell);
        number = (EditText) findViewById(R.id.editText_airtell);
        progrees = (ProgressBar) findViewById(R.id.progressBar_airtell);

        //attach listeners
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(number.getText())) {

                    try {
                        int n = Integer.valueOf(number.getText().toString());
                        saveNumber(n);
                    } catch (NumberFormatException e) {
                        number.setError("Wrong number format");
                    }
                } else {
                    number.setError("Cannot be empty");
                }
            }
        });


    }

    private void saveNumber(final int n) {

        DatabaseReference mPays = FirebaseDatabase.getInstance().getReference().child("Paymeans");
        mPays.child(key).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                dataSnapshot.child("Airtel").getRef().setValue(n);

                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    payMeans = (HashMap<String, Double>) ds.getValue();
                }


                Log.v("HASJDSAHDJKASBD", payMeans.toString());

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
