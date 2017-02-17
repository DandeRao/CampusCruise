package com.skmapstutorial.Application.Activities;

import android.app.Application;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.skmapstutorial.Application.SKMapsApplication;
import com.skmapstutorial.R;
import com.skobbler.ngx.SKMaps;
import com.skobbler.ngx.SKMapsInitializationListener;
import com.skobbler.ngx.map.SKMapFragment;
import com.skobbler.ngx.map.SKMapSurfaceView;
import com.skobbler.ngx.map.SKMapViewHolder;
import com.skobbler.ngx.navigation.SKAdvisorSettings;
import com.skobbler.ngx.routing.SKRouteManager;

public class MainActivity extends AppCompatActivity implements SKMapsInitializationListener {
    SKMapSurfaceView mapView;
    SKMapViewHolder mapHolder;

    boolean hasAccesstoLocationServices = true;
    boolean isMapInitialized = false;
    int TAG_CODE_PERMISSION_LOCATION;
    Button mainActivity_launchTour;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            hasAccesstoLocationServices = false;
            ActivityCompat.requestPermissions(this, new String[] {
                            android.Manifest.permission.ACCESS_FINE_LOCATION,
                            android.Manifest.permission.ACCESS_COARSE_LOCATION },
                    TAG_CODE_PERMISSION_LOCATION);
            System.out.println("Permission Not Granted for Location services");
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
              hasAccesstoLocationServices = false;
                Toast.makeText(getApplicationContext(),"Permission for Location access not granted yet!!",Toast.LENGTH_LONG).show();
            }
            else{hasAccesstoLocationServices= true;}
        }

        SKMaps.getInstance().initializeSKMaps(getApplication(), MainActivity.this);


        mainActivity_launchTour = (Button) findViewById(R.id.mainActivity_launchTour);
        mainActivity_launchTour.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(hasAccesstoLocationServices && isMapInitialized){

                    Intent intent = new Intent(MainActivity.this, PhysicalTourActivity.class);
                    startActivity(intent);
                }
            }
        });

//        else {
//            SKMaps.getInstance().initializeSKMaps(getApplication(), this);
//        }



    }

    @Override
    public void onLibraryInitialized(boolean b) {
        System.out.println("Initialization passed? :"+b);
        isMapInitialized = b;
        if(b) {
            SKMapsApplication app = new SKMapsApplication();
            app.setMapResourcesDirPath(SKMaps.getInstance().getMapInitSettings().getMapResourcesPath());
            System.out.println("Map Resources Path"+app.getMapResourcesDirPath());
//            SKMapFragment mapFragment = (SKMapFragment) getFragmentManager().findFragmentById(R.id.mapfragment);
//             mapFragment.initialise();
//            Intent intent = new Intent(MainActivity.this, PhysicalTourActivity.class);
//            startActivity(intent);

//          SKMapViewHolder mapViewGroup = (SKMapViewHolder) findViewById(R.id.mapfragment);
//            SKMapSurfaceView mapView = mapViewGroup.getMapSurfaceView();
//
//            SKCoordinate universityCoordinate = new SKCoordinate(52.513086884218325, 13.34615707397461);
//            SKAnnotation annotation = new SKAnnotation(1);
//            annotation
//                    .setAnnotationType(SKAnnotation.SK_ANNOTATION_TYPE_GREEN);
//            annotation.setLocation(universityCoordinate);
//            mapView.addAnnotation(annotation,
//                    SKAnimationSettings.ANIMATION_NONE);
//            mapView.setZoom(11);
//            mapView.animateToLocation(universityCoordinate, 0);



        }

    }
}
