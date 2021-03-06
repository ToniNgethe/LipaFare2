package com.toni.lipafare.Passanger.PassangerDialog;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.toni.lipafare.R;

/**
 * Created by toni on 2/15/17.
 */

public class ChangePassword extends Dialog {

    private Activity mActivity;

    private TextView successText;
    private EditText newEmail;
    private Button submit;
    private ImageButton imageButton;
    private ProgressBar progressBar;

    private FirebaseAuth mAuth;

    public ChangePassword(Activity context) {
        super(context);

        this.mActivity = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.change_pass);

        //setup views.......
        successText = (TextView) findViewById(R.id.successTextEmail);
        newEmail = (EditText) findViewById(R.id.changeEmailAddress);
        submit = (Button) findViewById(R.id.changeEmailBtn);
        imageButton = (ImageButton) findViewById(R.id.cancelEmailBtn);

        progressBar = (ProgressBar) findViewById(R.id.changeEmailAddressBar);

        //setup firebase.....
        mAuth = FirebaseAuth.getInstance();

        //onclicked
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                hide();

            }
        });

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                progressBar.setVisibility(View.VISIBLE);

                //if empty
                if (!TextUtils.isEmpty(newEmail.getText().toString())){

                    if (mAuth.getCurrentUser().getUid() != null){

                        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

                        mAuth.sendPasswordResetEmail(newEmail.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                //check if update was a success
                                if (task.isSuccessful()){

                                    progressBar.setVisibility(View.GONE);
                                    successText.setVisibility(View.VISIBLE);
                                    successText.setTextColor(Color.WHITE);
                                    successText.setText("New password sent to your email");
                                    mAuth.signOut();
                                }else {

                                    progressBar.setVisibility(View.GONE);
                                    successText.setVisibility(View.VISIBLE);
                                    successText.setTextColor(Color.RED);
                                    successText.setText(task.getException().getMessage());

                                }
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                progressBar.setVisibility(View.GONE);
                                successText.setVisibility(View.VISIBLE);
                                successText.setTextColor(Color.RED);
                                successText.setText(e.getMessage());
                            }
                        });

                    }

                }else {

                    progressBar.setVisibility(View.GONE);
                    successText.setVisibility(View.VISIBLE);
                    successText.setTextColor(Color.RED);
                    successText.setText("Field cannot be empty..");
                }

            }
        });

    }

}
