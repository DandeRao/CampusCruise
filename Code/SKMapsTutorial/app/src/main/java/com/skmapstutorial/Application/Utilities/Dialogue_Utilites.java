package com.skmapstutorial.Application.Utilities;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.nfc.Tag;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.skmapstutorial.Application.Activities.PhysicalTourActivity;
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
}
