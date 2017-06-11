package com.example.toni.lipafare.Helper;

import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AlertDialog;

import com.example.toni.lipafare.R;

/**
 * Created by toni on 4/11/17.
 */

public class Global {

    public static void showDialog(String title, String message, Context a) {
        AlertDialog.Builder builder = new AlertDialog.Builder(a, R.style.MyDialogTheme);
        builder.setTitle(title);
        builder.setMessage(message);

        String positiveText = "Okay";
        builder.setPositiveButton(positiveText,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

//        String negativeText = String.valueOf(android.R.string.cancel);
//        builder.setNegativeButton(negativeText,
//                new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        // negative button logic
//                    }
//                });

        AlertDialog dialog = builder.create();
        // display dialog
        dialog.show();
    }

    //check connection...
    public static boolean isConnected(Context context){

        boolean connected = false;

        ConnectivityManager cm =
                (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();

        if (activeNetwork != null && activeNetwork.isConnected()){
            connected = true;
        }

        return connected;
    }
}
