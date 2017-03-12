package com.skmapstutorial.Application.Services;

/**
 * Created by mkrao on 3/8/2017.
 */

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.skmapstutorial.Application.Activities.MainActivity;
import com.skmapstutorial.Application.Activities.PhysicalTourActivity;
import com.skmapstutorial.Application.Activities.SplashActivity;
import com.skmapstutorial.Application.Activities.VirtualTourActivity;
import com.skmapstutorial.Application.SKMapsApplication;
import com.skmapstutorial.R;


public class NetworkChangeReceiver extends BroadcastReceiver {
    ProgressDialog progressDialog;
    String title;
    String message;
    String processDialogueMessage;
    MainActivity mainActivity;
    VirtualTourActivity virtualTourActivity;
    PhysicalTourActivity physicalTourActivity;
    SplashActivity splashActivity;
    Dialog dialog;
    static int activeActivity = 0;
    boolean hasInternet = true;
    boolean hasLocation = true;
    String TAG = "Connectivity Changed";

    @Override
    public void onReceive(final Context context, Intent intent) {
        Log.d(TAG, "Broadcast recieved");
        activeActivity = SKMapsApplication.getActiveActivity();

        switch (activeActivity) {
            case 0:
                mainActivity = SKMapsApplication.getMainActivity();
                dialog = mainActivity.getWaitingForCurrentLocation();
                progressDialog = mainActivity.getFetchingLocation();
                break;
            case 1:
                physicalTourActivity = SKMapsApplication.getPhysicalTourActivity();
                dialog = physicalTourActivity.getWaitingForCurrentLocation();
                progressDialog = physicalTourActivity.getFetchingLocation();
                break;
            case 2:
                virtualTourActivity = SKMapsApplication.getVirtualTourActivity();
                dialog = virtualTourActivity.getWaitingForCurrentLocation();
                progressDialog = virtualTourActivity.getFetchingLocation();
                break;
            case 3:
                splashActivity = SKMapsApplication.getSplashActivity();
                dialog = splashActivity.getWaitingForCurrentLocation();
                progressDialog = splashActivity.getFetchingLocation();
                break;
        }

        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        dismissDialogsIfShowing();


        if (activeNetwork != null) { // connected to the internet
            title = "";
            message = "";
            processDialogueMessage = "";
            hasInternet = true;
            Toast.makeText(context, activeNetwork.getTypeName(), Toast.LENGTH_SHORT).show();

        } else {
            Log.d(TAG, "wifi de activated");
            // not connected to the internet
            Toast.makeText(context, "Disconnected Internet", Toast.LENGTH_SHORT).show();
            title = " Internet Connection ";
            message = " Internet ";
            processDialogueMessage = " Internet ";
            hasInternet = false;
        }


        if(activeActivity== 0||activeActivity ==1) {
            LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);

            if (!(locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER))) {
                Log.d(TAG, "Location de activated");
                title = title.isEmpty() ? "GPS connection" : title + " and GPS connection";
                message = message.isEmpty() ? " location services " : message + " and location services ";
                processDialogueMessage = processDialogueMessage.isEmpty() ? "GPS Connection" : processDialogueMessage + " and GPS Connection";
                hasLocation = false;
                Toast.makeText(context, "Location Not Available", Toast.LENGTH_SHORT).show();
            } else {
                Log.d(TAG, "Location activated");
                hasLocation = true;
            }

            Log.d(TAG, "has Location and hasInternet " + hasLocation + " , " + hasInternet);
            Log.d(TAG, "is showing progressDialog? " + progressDialog.isShowing());
            Log.d(TAG, "is Showing Dialog? " + dialog.isShowing());
            Log.d(TAG, "dismissing dialogs");

        }
        dismissDialogsIfShowing();


        if (!(hasInternet && hasLocation)) {
            Log.d(TAG, "has Location and hasInternet " + hasLocation + " , " + hasInternet);
            Log.d(TAG, "about to show dialogs");
            displayAlert();
        }

    }

    public void displayAlert() {
        Log.d(TAG, "entered show display alert");
        dialog.setContentView(R.layout.location_internet_not_available);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
        Log.d(TAG, "Showing Dialog? " + dialog.isShowing());

        TextView titleTV = (TextView) dialog.findViewById(R.id.location_network_not_avaialble_heading);
        titleTV.setText(title + " connection not available");
        TextView messageTV = (TextView) dialog.findViewById(R.id.location_internet_not_available_message);
        messageTV.setText("Please connect to " + message + " to continue");

        Button okButton = (Button) dialog.findViewById(R.id.location_internet_not_available_ok);
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!(hasLocation && hasInternet)) {
                    dialog.dismiss();
                    progressDialog.setMessage("Waiting for " + processDialogueMessage + " ");
                    progressDialog.setIndeterminate(true);
                    progressDialog.setCanceledOnTouchOutside(false);
                    progressDialog.show();
                    Log.d(TAG, "Showing Dialog? " + progressDialog.isShowing());
                }
            }
        });


    }

    public void dismissDialogsIfShowing() {
        Log.d(TAG, "entered dismiss dialog");
        if (progressDialog.isShowing()) {
            progressDialog.dismiss();
            Log.d(TAG, "process dialog is showing and dismissed");
        }
        if (dialog.isShowing()) {
            dialog.dismiss();
            Log.d(TAG, "dialog is showing and dismissed");
        }
    }
}