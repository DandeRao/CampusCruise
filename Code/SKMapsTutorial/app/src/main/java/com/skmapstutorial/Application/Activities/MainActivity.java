package com.skmapstutorial.Application.Activities;

import android.app.Activity;
import android.app.Application;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.skmapstutorial.Application.Model.Building;
import com.skmapstutorial.Application.Model.University;
import com.skmapstutorial.Application.SKMapsApplication;
import com.skmapstutorial.Application.Services.NetworkChangeReceiver;
import com.skmapstutorial.Application.Utilities.Dialogue_Utilites;
import com.skmapstutorial.R;
import com.skobbler.ngx.SKCoordinate;
import com.skobbler.ngx.SKMaps;
import com.skobbler.ngx.SKMapsInitializationListener;
import com.skobbler.ngx.map.SKMapFragment;
import com.skobbler.ngx.map.SKMapSurfaceView;
import com.skobbler.ngx.map.SKMapViewHolder;
import com.skobbler.ngx.navigation.SKAdvisorSettings;
import com.skobbler.ngx.positioner.SKCurrentPositionListener;
import com.skobbler.ngx.positioner.SKCurrentPositionProvider;
import com.skobbler.ngx.positioner.SKPosition;
import com.skobbler.ngx.routing.SKRouteManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;

import static com.skmapstutorial.Application.SKMapsApplication.university;

public class MainActivity extends Activity implements SKMapsInitializationListener {

    SKCurrentPositionProvider currentPositionProvider;
    boolean hasAccesstoLocationServices = true;
    SKCoordinate currentLocation;
    University university;
    boolean isMapInitialized = false;
    int TAG_CODE_PERMISSION_LOCATION;
    Button mainActivity_launchTour;
   static final String TAG = "MainActivity";
    Spinner spinner;
    // Dialog to show that services have been disabled
    Dialog waitingForCurrentLocation;
    //Progress dialog to show when waiting for GPS to turn on
    ProgressDialog fetchingLocation;

    // Dialog to show when GPS is enabled and current location fix is not available
    Dialog locationFixNotAvailable;

    // to display while waiting for location
    ProgressDialog gettingLocation;
    boolean goThereFlag = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SKMapsApplication.setActiveActivity(0);
        SKMapsApplication.setMainActivity(this);
        SKMaps.getInstance().initializeSKMaps(getApplication(), MainActivity.this);

        final ArrayList<University> universityList = SKMapsApplication.getUniversitiesList();
        spinner = (Spinner) findViewById(R.id.universities_list);
        ArrayAdapter<String> adapter;
        List<String> list;

        list = new ArrayList<String>();
        for(University u:universityList){
            list.add(u.getUniversityName());
        }
        adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, list);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        waitingForCurrentLocation = new Dialog(MainActivity.this);

        locationFixNotAvailable = new Dialog(MainActivity.this);
        fetchingLocation = new ProgressDialog(MainActivity.this );
        gettingLocation = new ProgressDialog(MainActivity.this );


        // Setting up current position provider to check if user is in campus premises.
        currentPositionProvider = new SKCurrentPositionProvider(getApplicationContext());

        // Checking if the application has access ti gather location
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

        // Listening for position access
        currentPositionProvider.setCurrentPositionListener(new SKCurrentPositionListener() {
            @Override
            public void onCurrentPositionUpdate(SKPosition skPosition) {
                currentLocation = skPosition.getCoordinate();
                if(locationFixNotAvailable.isShowing()|| gettingLocation.isShowing()) {
                    if(locationFixNotAvailable.isShowing())
                        locationFixNotAvailable.dismiss();
                    if(gettingLocation.isShowing())
                        gettingLocation.dismiss();
                    locationReceived();
                }
                currentPositionProvider.stopLocationUpdates();
            }
        });

        if (Dialogue_Utilites.hasGpsModule(getApplicationContext())) {
            currentPositionProvider.requestLocationUpdates(true, false, true);
        } else if (Dialogue_Utilites.hasNetworkModule(getApplicationContext())) {
            currentPositionProvider.requestLocationUpdates(false, true, true);
        }

        mainActivity_launchTour = (Button) findViewById(R.id.mainActivity_launchTour);
        mainActivity_launchTour.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(hasAccesstoLocationServices && isMapInitialized){

                    //university = SKMapsApplication.getUniversity();

                    String selectedUniversity = spinner.getSelectedItem().toString();
                    for(University u: universityList){
                        if(u.getUniversityName().equals(selectedUniversity)){
                            SKMapsApplication.setUniversity(u);
                            university = u;
                            System.out.println("Universitu Selected: "+u.getUniversityName()+" Number of Buildings: "+u.getBuildings().size());
                        }
                    }


                    try {
                        System.out.println("Current Location : "+currentLocation.toString());
                        if (isUserInUniversity(currentLocation)) {
                            goThereFlag = false;
                            startPhysicalTour();

                        } else {
                            userNotInUniversity();
                        }
                    }catch (NullPointerException e){

                        currentLocationUnAvailableDIalogue();

                    }
                }
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        SKMapsApplication.setActiveActivity(0);
    }

    public void userNotInUniversity(){


    final Dialog dialog = new Dialog(MainActivity.this);

    dialog.setContentView(R.layout.not_in_university_dialogue_view);
    dialog.show();
    final Button virtualTour = (Button) dialog.findViewById(R.id.virtual_tour_not_in_university_dialogue_button);
    final Button goThere = (Button) dialog.findViewById(R.id.go_there_not_in_university_dialogue_button);

    TextView t =  ((TextView) dialog.findViewById(R.id.not_in_university_university_name));
    t.setText(university.getUniversityName());

    virtualTour.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            dialog.dismiss();
            startVirtualTour();

        }
    });

    goThere.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            goThereFlag = true;
            startPhysicalTour();
        }
    });


}

    public void currentLocationUnAvailableDIalogue(){
        locationFixNotAvailable.setContentView(R.layout.current_location_not_available_dialogue);
        locationFixNotAvailable.show();
        Button virtualTourInDialogue = (Button) locationFixNotAvailable.findViewById(R.id.virtual_tour_not_in_university_dialogue_button);
        virtualTourInDialogue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startVirtualTour();
            }
        });

        Button fetchLocation = (Button) locationFixNotAvailable.findViewById(R.id.fetch_location_button);
        fetchLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gettingLocation.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                gettingLocation.setMessage("Fetching Location. \nPlease wait...");
                gettingLocation.setIndeterminate(true);
                gettingLocation.setCanceledOnTouchOutside(false);
                gettingLocation.show();

            }
        });
    }

    public void locationReceived(){
        if(isUserInUniversity(currentLocation)){
            startPhysicalTour();

        }
        else{
            userNotInUniversity();
        }
    }


    public void startPhysicalTour(){
        Intent intent = new Intent(MainActivity.this, PhysicalTourActivity.class);
        intent.putExtra("goThere",goThereFlag);
        startActivity(intent);
    }

    public void startVirtualTour(){
        Intent intent = new Intent(MainActivity.this, VirtualTourActivity.class);
        startActivity(intent);
    }
    @Override
    public void onLibraryInitialized(boolean b) {
        System.out.println("Initialization passed? :"+b);
        isMapInitialized = b;

    }

    public boolean isUserInUniversity(SKCoordinate currentLocationofUser) {
        double universityRadius=calculateUniversityRadius();
        Log.d(TAG, "isUserIn University premises? " + (calculateDistance(university.getUniversityLocation(), currentLocationofUser) <  universityRadius));
        System.out.println("Distance between User and University: "+calculateDistance(university.getUniversityLocation(), currentLocationofUser));
        System.out.println("University Radius: "+calculateUniversityRadius());
        return calculateDistance(university.getUniversityLocation(), currentLocationofUser) < universityRadius;

    }
    public double calculateUniversityRadius() {
        ArrayList<Double> distanceOfEachBuildingFromCenterOfUniversity = new ArrayList<>();
        for (Building b : university.getBuildings()) {
            distanceOfEachBuildingFromCenterOfUniversity.add(calculateDistance(university.getUniversityLocation(), b.getBuildingCoordinates()));
            Log.d(TAG, "Distance between " + b.getBuildingName() + " and center Of University :" + distanceOfEachBuildingFromCenterOfUniversity.get(distanceOfEachBuildingFromCenterOfUniversity.size() - 1));
        }
        try {
            return Collections.max(distanceOfEachBuildingFromCenterOfUniversity) + 100.0;
        }catch(NoSuchElementException e){
            return  100.0;
        }

    }
    public Double calculateDistance(SKCoordinate building1Coordinate, SKCoordinate building2Coordinate) {
        final int R = 6371; // Radious of the earth
        Double lat1 = building1Coordinate.getLatitude();
        Double lon1 = building1Coordinate.getLongitude();
        Double lat2 = building2Coordinate.getLatitude();
        Double lon2 = building2Coordinate.getLongitude();
        Double latDistance = toRad(lat2 - lat1);
        Double lonDistance = toRad(lon2 - lon1);
        Double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2) +
                Math.cos(toRad(lat1)) * Math.cos(toRad(lat2)) *
                        Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        Double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        Double distanceinmeters = R * c * 1000;

        return distanceinmeters;

    }

    private static Double toRad(Double value) {
        return value * Math.PI / 180;
    }






    public ProgressDialog getFetchingLocation() {
        return fetchingLocation;
    }

    public void setFetchingLocation(ProgressDialog fetchingLocation) {
        this.fetchingLocation = fetchingLocation;
    }

    public Dialog getWaitingForCurrentLocation() {
        return waitingForCurrentLocation;
    }

    public void setWaitingForCurrentLocation(Dialog waitingForCurrentLocation) {
        this.waitingForCurrentLocation = waitingForCurrentLocation;
    }
}
