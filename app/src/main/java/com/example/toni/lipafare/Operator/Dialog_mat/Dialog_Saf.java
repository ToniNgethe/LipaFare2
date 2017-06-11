package com.example.toni.lipafare.Operator.Dialog_mat;

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

import com.example.toni.lipafare.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * Created by toni on 5/16/17.
 */

public class Dialog_Saf extends Dialog {

    private String key;
    private Context ctx;
    private ImageButton close;
    private Button save;
    private EditText number;
    private ProgressBar progrees;

    public Dialog_Saf(@NonNull Context context, String key) {

        super(context);
        this.ctx = context;
        this.key = key;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_saf);

        close = (ImageButton) findViewById(R.id.close_saf);
        save = (Button) findViewById(R.id.save_saf);
        number = (EditText) findViewById(R.id.editText_saf);
        progrees = (ProgressBar) findViewById(R.id.progressBar_saf);

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
                    }catch (NumberFormatException e){
                        number.setError("Wrong number format");
                    }
                } else {
                    number.setError("Field cannot be empty");
                }
            }
        });

    }

    private void saveNumber(final int n) {

        DatabaseReference mPays = FirebaseDatabase.getInstance().getReference().child("Paymeans");
        mPays.child(key).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                dataSnapshot.child("Safaricom").getRef().setValue(n);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
