package com.toni.lipafare.Operator.Dialog_mat;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
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

/**
 * Created by toni on 5/16/17.
 */

public class Dialog_orange extends Dialog {
    private String key;
    private Context ctx;
    private ImageButton close;
    private Button save;
    private EditText number;
    private ProgressBar progrees;

    public Dialog_orange(@NonNull Context context, String key) {
        super(context);
        this.key = key;
        this.ctx = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_orange);

        close = (ImageButton) findViewById(R.id.close_orange);
        save = (Button) findViewById(R.id.save_orange);
        number = (EditText) findViewById(R.id.editText_orange);
        progrees = (ProgressBar) findViewById(R.id.progressBar_orange);

        //listeners
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(number.getText())) {

                    try {
                        int n = Integer.valueOf(number.getText().toString());
                        saveNumber(n);
                    }catch (NumberFormatException e){
                        number.setError("Wrong number format");
                    }

                } else {
                    number.setError("Cannot be empty");
                }
            }
        });

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

    }

    private void saveNumber(final int n) {

        DatabaseReference mPays = FirebaseDatabase.getInstance().getReference().child("Paymeans");
        mPays.child(key).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                dataSnapshot.child("Orange").getRef().setValue(n);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
