package com.toni.lipafare.Dialogs;

import android.app.Dialog;
import android.content.Context;
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
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.toni.lipafare.R;

/**
 * Created by toni on 3/24/17.
 */

public class ForgotPasswordDialog extends Dialog {

    private Button submit;
    private EditText _email;
    private TextView _successText;
    private ProgressBar mProgressBar;
    private ImageButton btn_close;

    private FirebaseAuth mAuth;
    private Context ctx;

    public ForgotPasswordDialog(Context context) {
        super(context);

        this.ctx = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_forgot_password);

        submit = (Button) findViewById(R.id.btn_forgotpass_submit);
        _email = (EditText) findViewById(R.id.et_forgotpass_email);
        _successText = (TextView) findViewById(R.id.successText);
        mProgressBar = (ProgressBar) findViewById(R.id.progress_forgotpass);

        btn_close = (ImageButton) findViewById(R.id.ib_forgotpassword);
        btn_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        //firebase
        mAuth = FirebaseAuth.getInstance();

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!TextUtils.isEmpty(_email.getText())) {

                    mProgressBar.setVisibility(View.VISIBLE);

                    mAuth.sendPasswordResetEmail(_email.getText().toString().trim()).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if (task.isSuccessful()){

                                mProgressBar.setVisibility(View.GONE);
                                _successText.setVisibility(View.VISIBLE);
                                _successText.setText("Password reset instructions have been sent to your email");


                            }else {

                                mProgressBar.setVisibility(View.GONE);
                                _successText.setVisibility(View.VISIBLE);
                                _successText.setTextColor(Color.RED);
                                _successText.setText(task.getException().getMessage());

                            }

                        }
                    });

                }

            }
        });

    }
}
