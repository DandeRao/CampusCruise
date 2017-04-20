package com.skmapstutorial.Application.Utilities;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.location.LocationManager;
import android.nfc.Tag;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Surface;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.skmapstutorial.Application.Activities.PhysicalTourActivity;
import com.skmapstutorial.Application.Activities.VirtualTourActivity;
import com.skmapstutorial.Application.Model.Building;
import com.skmapstutorial.R;

/**
 * Created by mkrao on 2/5/2017.
 */

public class Dialogue_Utilites {
    public static void showBuildingDetialsDialouge(Activity currentActivity){
        System.out.println("You have reached the destination, please scan the QR to know more about the building or click continue tour to go to the next building");
        Log.d("Physical Tour Activity","You have reached the destination, please scan the QR to know more about the building or click continue tour to go to the next building");
       // Toast.makeText(currentActivity,"You have reached the destination, please scan the QR to know more about the building or click continue tour to go to the next building",Toast.LENGTH_LONG).show();
//
//
//        final Dialog dialog = new Dialog(currentActivity);
//        dialog.setContentView(R.layout.sample_gallaery_dialogue_view);
//            dialog.setTitle("Destination Reached");
//        TextView title = (TextView) dialog.findViewById(R.id.dialogueTitle);
//           title.setText("You have reached the destination, please scan the QR to know more about the building or click continue tour to go to the next building");
//        // set the custom dialog components - text, image and button
//        TextView text = (TextView) dialog.findViewById(R.id.text);
//        text.setText("Images");
//        ImageView image = (ImageView) dialog.findViewById(R.id.image);
//        image.setImageResource(R.drawable.norecordimg);
//
//        Button dialogButton = (Button) dialog.findViewById(R.id.dialogButtonOK);
//        // if button is clicked, close the custom dialog
//        dialogButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                dialog.dismiss();
//            }
//        });
//
//        dialog.show();


    }


    public static void showNotInUniversityDialogue(final Activity currnetActivity){

//
//        final Dialog dialog = new Dialog(currnetActivity);
//
//        final Button virtualTour = (Button) dialog.findViewById(R.id.virtual_tour_not_in_university_dialogue_button);
//        Button goThere = (Button) dialog.findViewById(R.id.go_there_not_in_university_dialogue_button);
//        dialog.setContentView(R.layout.not_in_university_dialogue_view);
//        dialog.show();
//
//        virtualTour.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent virtualTourIntent = new Intent(currnetActivity, VirtualTourActivity.class);
//                currnetActivity.startActivity(virtualTourIntent);
//            }
//        });
//
//        goThere.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                currnetActivity.
//            }
//        });


    }


    /**
     * Checks if the current device has a GPS module (hardware)
     * @return true if the current device has GPS
     */
    public static boolean hasGpsModule(final Context context) {
        final LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        for (final String provider : locationManager.getAllProviders()) {
            if (provider.equals(LocationManager.GPS_PROVIDER)) {
                return true;
            }
        }
        return false;
    }
    /**
     * Checks if the current device has a  NETWORK module (hardware)
     * @return true if the current device has NETWORK
     */
    public static boolean hasNetworkModule(final Context context) {
        final LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        for (final String provider : locationManager.getAllProviders()) {
            if (provider.equals(LocationManager.NETWORK_PROVIDER)) {
                return true;
            }
        }
        return false;
    }

    public static int getExactScreenOrientation(Activity activity) {
        Display defaultDisplay = activity.getWindowManager().getDefaultDisplay();
        int rotation = defaultDisplay.getRotation();
        DisplayMetrics dm = new DisplayMetrics();
        defaultDisplay.getMetrics(dm);
        int width = dm.widthPixels;
        int height = dm.heightPixels;
        int orientation;
        // if the device's natural orientation is portrait:
        if ((rotation == Surface.ROTATION_0 || rotation == Surface.ROTATION_180) && height > width || (rotation == Surface.ROTATION_90 || rotation == Surface.ROTATION_270) &&
                width > height) {
            switch (rotation) {
                case Surface.ROTATION_0:
                    orientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
                    break;
                case Surface.ROTATION_90:
                    orientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
                    break;
                case Surface.ROTATION_180:
                    orientation = ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT;
                    break;
                case Surface.ROTATION_270:
                    orientation = ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE;
                    break;
                default:
                    // Logging.writeLog(TAG, "Unknown screen orientation. Defaulting to " + "portrait.", Logging.LOG_DEBUG);
                    orientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
                    break;
            }
        }
        // if the device's natural orientation is landscape or if the device
        // is square:
        else {
            switch (rotation) {
                case Surface.ROTATION_0:
                    orientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
                    break;
                case Surface.ROTATION_90:
                    orientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
                    break;
                case Surface.ROTATION_180:
                    orientation = ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE;
                    break;
                case Surface.ROTATION_270:
                    orientation = ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT;
                    break;
                default:
                    orientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
                    break;
            }
        }

        return orientation;
    }

}
