package com.skmapstutorial.Application.Activities;

import android.app.Dialog;
import android.graphics.Bitmap;
import android.nfc.Tag;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.skmapstutorial.Application.Model.Building;
import com.skmapstutorial.Application.Model.University;
import com.skmapstutorial.Application.SKMapsApplication;
import com.skmapstutorial.Application.Utilities.Dialogue_Utilites;
import com.skmapstutorial.R;
import com.skobbler.ngx.SKCoordinate;
import com.skobbler.ngx.SKMaps;
import com.skobbler.ngx.map.SKAnimationSettings;
import com.skobbler.ngx.map.SKAnnotation;
import com.skobbler.ngx.map.SKAnnotationView;
import com.skobbler.ngx.map.SKCoordinateRegion;
import com.skobbler.ngx.map.SKMapCustomPOI;
import com.skobbler.ngx.map.SKMapPOI;
import com.skobbler.ngx.map.SKMapSurfaceListener;
import com.skobbler.ngx.map.SKMapSurfaceView;
import com.skobbler.ngx.map.SKMapViewHolder;
import com.skobbler.ngx.map.SKPOICluster;
import com.skobbler.ngx.map.SKPolyline;
import com.skobbler.ngx.map.SKScreenPoint;
import com.skobbler.ngx.navigation.SKAdvisorSettings;
import com.skobbler.ngx.navigation.SKNavigationListener;
import com.skobbler.ngx.navigation.SKNavigationManager;
import com.skobbler.ngx.navigation.SKNavigationSettings;
import com.skobbler.ngx.navigation.SKNavigationState;
import com.skobbler.ngx.positioner.SKCurrentPositionListener;
import com.skobbler.ngx.positioner.SKCurrentPositionProvider;
import com.skobbler.ngx.positioner.SKPosition;
import com.skobbler.ngx.routing.SKRouteInfo;
import com.skobbler.ngx.routing.SKRouteJsonAnswer;
import com.skobbler.ngx.routing.SKRouteListener;
import com.skobbler.ngx.routing.SKRouteManager;
import com.skobbler.ngx.routing.SKRouteSettings;
import com.skobbler.ngx.sdktools.navigationui.SKToolsAdvicePlayer;
import com.skobbler.ngx.sdktools.navigationui.SKToolsNavigationConfiguration;
import com.skobbler.ngx.sdktools.navigationui.SKToolsNavigationListener;
import com.skobbler.ngx.sdktools.navigationui.SKToolsNavigationManager;
import com.skobbler.ngx.util.SKLogging;


import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import static android.view.View.GONE;

public class PhysicalTourActivity extends AppCompatActivity implements SKMapSurfaceListener, SKRouteListener, SKNavigationListener, SKToolsNavigationListener, SKCurrentPositionListener {
    SKMapSurfaceView mapView;
    // private SKToolsNavigationManager navigationManager;
    private RelativeLayout navigationUI;
    University university;
    int nearestBuilingToUserId;
    Double universityRadius;
    ArrayList<SKAnnotation> buildingAnnotations;
    Button campusTour;
    Button startTourButton;
    Button skipBuilding;
    SKCoordinate currentLocation;
    SKNavigationManager navigationManager;
    SKNavigationSettings navigationSettings;
    int buildingBeingVisited;
    SKMapsApplication app;
    final String TAG = "Physical Tour Activity";

    public PhysicalTourActivity() {
        //ArrayList<Building> buildings, LatLng universityLocation, ArrayList<LatLng> tourPath
        university = new University("Northwest Missouri State University", getBuildingsInUniversity(), new SKCoordinate(40.352611, -94.883933), getCampusTourRoute());

    }

    private SKMapViewHolder mapHolder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_physical_tour);
        app = (SKMapsApplication) getApplication();
        final SKCurrentPositionProvider currentLocationProvider = new SKCurrentPositionProvider(getApplicationContext());
        currentLocationProvider.setCurrentPositionListener(this);
        currentLocationProvider.requestLocationUpdates(true, true, true);
        navigationSettings = new SKNavigationSettings();
        navigationManager = SKNavigationManager.getInstance();
        mapHolder = (SKMapViewHolder)
                findViewById(R.id.map_surface_holder);
        mapHolder.setMapSurfaceListener(this);
        mapView = mapHolder.getMapSurfaceView();
        final SKMapsApplication skMapsApplication = (SKMapsApplication) getApplicationContext();
        skMapsApplication.setUniversity(university);
        campusTour = (Button) findViewById(R.id.tourButton);
        skipBuilding = (Button) findViewById(R.id.skipBuildingButton);
        startTourButton = (Button) findViewById(R.id.startTourButton);
        //   final Intent intent = new Intent(this,TourOverViewActivity.class);

        campusTour.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //   startActivity(intent);
                startTourButton.setVisibility(View.VISIBLE);
                campusTour.setVisibility(GONE);
                ((Button) findViewById(R.id.QRScanButton)).setVisibility(GONE);
                addPlolyLine();

            }
        });
//        final SKToolsNavigationConfiguration configuration = new SKToolsNavigationConfiguration();
//        configuration.setNavigationType(SKNavigationSettings.SKNavigationType.REAL);
//        configuration.setStartCoordinate(university.getBuildings().get(1).getBuildingCoordinates());
//        configuration.setDestinationCoordinate(university.getBuildings().get(2).getBuildingCoordinates());
        //  SKToolsNavigationManager navigationManager = new SKToolsNavigationManager(this,R.id.activity_physical_tour);
        startTourButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mapView.clearAllOverlays();
                mapView.deleteAllAnnotationsAndCustomPOIs();
                startTourButton.setVisibility(GONE);
                skipBuilding.setVisibility(View.VISIBLE);
                firstTimeRouteSettings();

            }
        });
        skipBuilding.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onDestinationReached();
            }
        });
    }

    public void firstTimeRouteSettings() {
        SKCoordinate navigationStartCoordinate;
        SKCoordinate navigationEndCoordinate;
        calculateUniversityRadius();
        if (isUserInUniversity(currentLocation)) {
            findNearestBuildingToUser(currentLocation);
            navigationStartCoordinate = currentLocation;
            navigationEndCoordinate = university.getBuildings().get(0).getBuildingCoordinates();
            buildingBeingVisited = 0;
        } else {
            navigationStartCoordinate = currentLocation;
            navigationEndCoordinate = university.getBuildings().get(0).getBuildingCoordinates();
        }


        //routeSettings(navigationStartCoordinate, navigationEndCoordinate);

        startNavigation(navigationStartCoordinate,navigationEndCoordinate);

    }

    public void routeSettings(SKCoordinate navigationStartCoordinate, SKCoordinate navigationEndCoordinate) {
        // startNavigation(navigationStartCoordinate,navigationEndCoordinate);
        // Get a route object and populate it with the desired properties
        SKRouteSettings route = new SKRouteSettings();
        // Set start and destination points
        route.setStartCoordinate(navigationStartCoordinate);
        route.setDestinationCoordinate(navigationEndCoordinate);
        // Set the number of routes to be calculated
        route.setMaximumReturnedRoutes(1);
        // Set the route mode
        route.setRouteMode(SKRouteSettings.SKRouteMode.PEDESTRIAN);
        // Set whether the route should be shown on the map after it's computed
        route.setRouteExposed(true);
        // Set the route listener to be notified of route calculation
        // events
        SKRouteManager.getInstance().setRouteListener(PhysicalTourActivity.this);
        // Pass the route to the calculation routine
        SKRouteManager.getInstance().calculateRoute(route);
    }

    public void addPlolyLine() {

        SKPolyline tourRoute = new SKPolyline();
        tourRoute.setNodes(university.getTourPath());
        tourRoute.setColor((new float[]{0f, 0f, 1f, 0.9f}));
        mapView.addPolyline(tourRoute);
    }

    @Override
    public void onSurfaceCreated(SKMapViewHolder skMapViewHolder) {
        mapView = mapHolder.getMapSurfaceView();
//          SKMapViewHolder mapViewGroup = (SKMapViewHolder) findViewById(R.id.mapfragment);
//            SKMapSurfaceView mapView = mapViewGroup.getMapSurfaceView();

        // SKCoordinate universityCoordinate = new SKCoordinate(52.513086884218325, 13.34615707397461);

//        Custom VIew for Annotation
//        View customView =
//                (LinearLayout) ((LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(
//                        R.layout.annotation_building_name, null, false);
//        TextView tvCaption = (TextView) customView.findViewById(R.id.buildingName);
//        tvCaption.setText("Northwest Missouri State University");
//        annotationView.setView(customView);
//        annotation.setAnnotationView(annotationView);

        addAllBuildingAnnotations();

    }

    public void addAllBuildingAnnotations(){

        SKAnnotation annotation = new SKAnnotation(0);
        annotation
                .setAnnotationType(SKAnnotation.SK_ANNOTATION_TYPE_GREEN);
        annotation.setLocation(university.getUniversityLocation());
        SKAnnotationView annotationView = new SKAnnotationView();
        mapView.addAnnotation(annotation,
                SKAnimationSettings.ANIMATION_NONE);
        mapView.setZoom(16);

        mapView.animateToLocation(university.getUniversityLocation(), 0);
        buildingAnnotations = new ArrayList<>(university.getBuildings().size());
        int tag = 1;
        for (Building b : getBuildingsInUniversity()) {
            SKAnnotation sk = new SKAnnotation(tag);
            sk.setAnnotationType(SKAnnotation.SK_ANNOTATION_TYPE_BLUE);
            sk.setLocation(b.getBuildingCoordinates());
            buildingAnnotations.add(sk);
            mapView.addAnnotation(sk, SKAnimationSettings.ANIMATION_NONE);
            tag++;
        }
    }

    public ArrayList<String> addBuildingNames() {
        return new ArrayList<String>() {{
            add("Colden Hall");
            add("Student Union");
            add("Valk Center");
            add("Admin Building");
            add("B.D. Owens Library");
            add("ECSC");
            add("Lamkin Activity Center");
            add("Recreation Center");
        }};
    }

    public ArrayList<SKCoordinate> addBuildingCoordinates() {
        return new ArrayList<SKCoordinate>() {{
            add(new SKCoordinate(40.350997, -94.882519));
            add(new SKCoordinate(40.351647, -94.882948));
            add(new SKCoordinate(40.352561, -94.880598));
            add(new SKCoordinate(40.353153, -94.883455));
            add(new SKCoordinate(40.353538, -94.885606));
            add(new SKCoordinate(40.352777, -94.887028));
            add(new SKCoordinate(40.349514, -94.884891));
            add(new SKCoordinate(40.350412, -94.883824));
        }};
    }

    public ArrayList<SKCoordinate> getCampusTourRoute() {
        return new ArrayList<SKCoordinate>() {{
            add(new SKCoordinate(40.350997, -94.882519));
            add(new SKCoordinate(40.351078, -94.882531));
            add(new SKCoordinate(40.35116, -94.882585));
            add(new SKCoordinate(40.351246, -94.882658));
            add(new SKCoordinate(40.35129, -94.882767));
            add(new SKCoordinate(40.351337, -94.88292));
            add(new SKCoordinate(40.351444, -94.88294));
            add(new SKCoordinate(40.351554, -94.882949));
            add(new SKCoordinate(40.351647, -94.882948));
            add(new SKCoordinate(40.351647, -94.882948));
            add(new SKCoordinate(40.351672, -94.882949));
            add(new SKCoordinate(40.351646, -94.882755));
            add(new SKCoordinate(40.351671, -94.882287));
            add(new SKCoordinate(40.351688, -94.881964));
            add(new SKCoordinate(40.351881, -94.881839));
            add(new SKCoordinate(40.352116, -94.881276));
            add(new SKCoordinate(40.352155, -94.881126));
            add(new SKCoordinate(40.352328, -94.881073));
            add(new SKCoordinate(40.35235, -94.881053));
            add(new SKCoordinate(40.352561, -94.880598));
            add(new SKCoordinate(40.352561, -94.880598));
            add(new SKCoordinate(40.352587, -94.880648));
            add(new SKCoordinate(40.35262, -94.88114));
            add(new SKCoordinate(40.352412, -94.881435));
            add(new SKCoordinate(40.352648, -94.881791));
            add(new SKCoordinate(40.352786, -94.881988));
            add(new SKCoordinate(40.352792, -94.882071));
            add(new SKCoordinate(40.352943, -94.882566));
            add(new SKCoordinate(40.353048, -94.882759));
            add(new SKCoordinate(40.353066, -94.882935));
            add(new SKCoordinate(40.353029, -94.882959));
            add(new SKCoordinate(40.35291, -94.882987));
            add(new SKCoordinate(40.352818, -94.88318));
            add(new SKCoordinate(40.353153, -94.883455));
            add(new SKCoordinate(40.353153, -94.883455));
            add(new SKCoordinate(40.352909, -94.883674));
            add(new SKCoordinate(40.352847, -94.883884));
            add(new SKCoordinate(40.353055, -94.88406));
            add(new SKCoordinate(40.352944, -94.884405));
            add(new SKCoordinate(40.35297, -94.884576));
            add(new SKCoordinate(40.353034, -94.88476));
            add(new SKCoordinate(40.353178, -94.884902));
            add(new SKCoordinate(40.35334, -94.885042));
            add(new SKCoordinate(40.353457, -94.885193));
            add(new SKCoordinate(40.353499, -94.885224));
            add(new SKCoordinate(40.353517, -94.885276));
            add(new SKCoordinate(40.353523, -94.885362));
            add(new SKCoordinate(40.353528, -94.885499));
            add(new SKCoordinate(40.353538, -94.885606));
            add(new SKCoordinate(40.353538, -94.885606));
            add(new SKCoordinate(40.353405, -94.885534));
            add(new SKCoordinate(40.353335, -94.88557));
            add(new SKCoordinate(40.353265, -94.885717));
            add(new SKCoordinate(40.353224, -94.885811));
            add(new SKCoordinate(40.353226, -94.886112));
            add(new SKCoordinate(40.353225, -94.88658));
            add(new SKCoordinate(40.353225, -94.887123));
            add(new SKCoordinate(40.353015, -94.887171));
            add(new SKCoordinate(40.352793, -94.887199));
            add(new SKCoordinate(40.352778, -94.887162));
            add(new SKCoordinate(40.352777, -94.887028));
            add(new SKCoordinate(40.352777, -94.887028));
            add(new SKCoordinate(40.3526, -94.887026));
            add(new SKCoordinate(40.352595, -94.887213));
            add(new SKCoordinate(40.352364, -94.887254));
            add(new SKCoordinate(40.352106, -94.887287));
            add(new SKCoordinate(40.351891, -94.887316));
            add(new SKCoordinate(40.351687, -94.887344));
            add(new SKCoordinate(40.351484, -94.887372));
            add(new SKCoordinate(40.351276, -94.887391));
            add(new SKCoordinate(40.35123, -94.887383));
            add(new SKCoordinate(40.351144, -94.887395));
            add(new SKCoordinate(40.351039, -94.887401));
            add(new SKCoordinate(40.350925, -94.887413));
            add(new SKCoordinate(40.350855, -94.887443));
            add(new SKCoordinate(40.350775, -94.887476));
            add(new SKCoordinate(40.35069, -94.887508));
            add(new SKCoordinate(40.350609, -94.887521));
            add(new SKCoordinate(40.350554, -94.887529));
            add(new SKCoordinate(40.350423, -94.887523));
            add(new SKCoordinate(40.350283, -94.887516));
            add(new SKCoordinate(40.350189, -94.887533));
            add(new SKCoordinate(40.350135, -94.887532));
            add(new SKCoordinate(40.350076, -94.887496));
            add(new SKCoordinate(40.350022, -94.887446));
            add(new SKCoordinate(40.349985, -94.887366));
            add(new SKCoordinate(40.349937, -94.887335));
            add(new SKCoordinate(40.349882, -94.887288));
            add(new SKCoordinate(40.349838, -94.887182));
            add(new SKCoordinate(40.34982, -94.887106));
            add(new SKCoordinate(40.349796, -94.887069));
            add(new SKCoordinate(40.349766, -94.887038));
            add(new SKCoordinate(40.349572, -94.886802));
            add(new SKCoordinate(40.349557, -94.88677));
            add(new SKCoordinate(40.349548, -94.886566));
            add(new SKCoordinate(40.349544, -94.886273));
            add(new SKCoordinate(40.349538, -94.885979));
            add(new SKCoordinate(40.349526, -94.885384));
            add(new SKCoordinate(40.349534, -94.885353));
            add(new SKCoordinate(40.349526, -94.885201));
            add(new SKCoordinate(40.349514, -94.884891));
            add(new SKCoordinate(40.349514, -94.884891));
            add(new SKCoordinate(40.349509, -94.884458));
            add(new SKCoordinate(40.349496, -94.883855));
            add(new SKCoordinate(40.350027, -94.883847));
            add(new SKCoordinate(40.350412, -94.883824));
            add(new SKCoordinate(40.350412, -94.883824));
            add(new SKCoordinate(40.350412, -94.883648));
            add(new SKCoordinate(40.350493, -94.883566));
            add(new SKCoordinate(40.350543, -94.883513));
            add(new SKCoordinate(40.350748, -94.88327));
            add(new SKCoordinate(40.350942, -94.883062));
            add(new SKCoordinate(40.351007, -94.883155));
            add(new SKCoordinate(40.351143, -94.883236));
            add(new SKCoordinate(40.351278, -94.883019));
            add(new SKCoordinate(40.351305, -94.882897));
            add(new SKCoordinate(40.351283, -94.882763));
            add(new SKCoordinate(40.351215, -94.882625));
            add(new SKCoordinate(40.351095, -94.882546));
            add(new SKCoordinate(40.35107, -94.882519));
        }};
    }

    public ArrayList<Building> getBuildingsInUniversity() {
        ArrayList<Building> buildingsList = new ArrayList<>();
        ArrayList<String> buildingNames = addBuildingNames();
        ArrayList<SKCoordinate> buildingCoordinates = addBuildingCoordinates();

        for (int i = 0; i < buildingNames.size(); i++)
            buildingsList.add(new Building(buildingNames.get(i), buildingCoordinates.get(i)));


        return buildingsList;

    }

    public boolean isUserInUniversity(SKCoordinate currentLocationofUser) {

        Log.d(TAG, "isUserIn University premises? " + (calculateDistance(university.getUniversityLocation(), currentLocationofUser) < universityRadius));
        return calculateDistance(university.getUniversityLocation(), currentLocationofUser) < universityRadius;

    }

    public void calculateUniversityRadius() {
        ArrayList<Double> distanceOfEachBuildingFromCenterOfUniversity = new ArrayList<>();
        for (Building b : university.getBuildings()) {
            distanceOfEachBuildingFromCenterOfUniversity.add(calculateDistance(university.getUniversityLocation(), b.getBuildingCoordinates()));
            Log.d(TAG, "Distance between " + b.getBuildingName() + " and center Of University :" + distanceOfEachBuildingFromCenterOfUniversity.get(distanceOfEachBuildingFromCenterOfUniversity.size() - 1));
        }
        universityRadius = Collections.max(distanceOfEachBuildingFromCenterOfUniversity) + 100.0;

    }

    public void findNearestBuildingToUser(SKCoordinate currentLocationOfUser) {
        ArrayList<Double> distanceOfEachBuildingFromUser = new ArrayList<>();
        for (Building b : university.getBuildings()) {
            distanceOfEachBuildingFromUser.add(calculateDistance(currentLocationOfUser, b.getBuildingCoordinates()));
            Log.d(TAG, "Distance between " + b.getBuildingName() + " and User's current Location :" + distanceOfEachBuildingFromUser.get(distanceOfEachBuildingFromUser.size() - 1));
        }
        nearestBuilingToUserId = distanceOfEachBuildingFromUser.indexOf(Collections.min(distanceOfEachBuildingFromUser));
        Log.d(TAG, "Nearest Building to user :" + university.getBuildings().get(nearestBuilingToUserId).getBuildingName());
        generateCampusTourOrderBasedOnStartPosition();
    }

    public void generateCampusTourOrderBasedOnStartPosition() {
        ArrayList<Building> buildingsToBeOrdered = university.getBuildings();
        Collections.rotate(buildingsToBeOrdered, (buildingsToBeOrdered.size() - nearestBuilingToUserId));
        for (Building b : buildingsToBeOrdered)
            Log.d(TAG, "Buildings Order: " + b.getBuildingName());
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


    @Override
    public void onAnnotationSelected(SKAnnotation skAnnotation) {

        final Dialog dialog = new Dialog(PhysicalTourActivity.this);
        dialog.setContentView(R.layout.sample_gallaery_dialogue_view);
        int position = skAnnotation.getUniqueID() - 1;
        //   mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(university.getBuildings().get(position).getBuildingCoordinates(),18.0f));
        if (position == -1)
            dialog.setTitle(university.getUniversityName());
        if (position >= 0)
            dialog.setTitle(university.getBuildings().get(position).getBuildingName());
        TextView title = (TextView) dialog.findViewById(R.id.dialogueTitle);

        if (position == -1)
            title.setText(university.getUniversityName());
        if (position >= 0)
            title.setText(university.getBuildings().get(position).getBuildingName());
        // set the custom dialog components - text, image and button
        TextView text = (TextView) dialog.findViewById(R.id.text);
        text.setText("Images");
        ImageView image = (ImageView) dialog.findViewById(R.id.image);
        image.setImageResource(R.drawable.norecordimg);

        Button dialogButton = (Button) dialog.findViewById(R.id.dialogButtonOK);
        // if button is clicked, close the custom dialog
        dialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();

    }


    @Override
    public void onDestinationReached() {
        Dialogue_Utilites.showBuildingDetialsDialouge(this);
        // Scan QR code Here
        university.getBuildings().get(buildingBeingVisited).setVisited(true);
        if (buildingBeingVisited < university.getBuildings().size() - 1) {
            // isNextBuildingVisited
            if (!university.getBuildings().get(buildingBeingVisited + 1).isVisited()) {

                Log.d(TAG, "Next Building Not Visited");
                Log.d(TAG, "Current Building: " + university.getBuildings().get(buildingBeingVisited).getBuildingName());
                Log.d(TAG, "Next Building: " + university.getBuildings().get(buildingBeingVisited + 1).getBuildingName());
                Log.d(TAG, "buildingBeingVisited : " + buildingBeingVisited);
                Log.d(TAG, "Next buildingBeingVisited : " + (buildingBeingVisited + 1));
                //routeSettings(university.getBuildings().get(buildingBeingVisited).getBuildingCoordinates(), university.getBuildings().get(buildingBeingVisited + 1).getBuildingCoordinates());
               startNavigation(university.getBuildings().get(buildingBeingVisited).getBuildingCoordinates(), university.getBuildings().get(buildingBeingVisited + 1).getBuildingCoordinates());
              //  startNavigation(currentLocation,university.getBuildings().get(buildingBeingVisited + 1).getBuildingCoordinates());
                buildingBeingVisited += 1;
            }

        } else {
            mapView.deleteAllAnnotationsAndCustomPOIs();
            mapView.clearAllOverlays();
            skipBuilding.setVisibility(GONE);
            Toast.makeText(this, "Tour Finished Calculating your total activity", Toast.LENGTH_SHORT).show();
            addAllBuildingAnnotations();
            addPlolyLine();
        }


    }


    @Override
    public void onSignalNewAdviceWithInstruction(String instruction) {
        //SKToolsAdvicePlayer.getInstance().playAdvice(audioFiles, SKToolsAdvicePlayer.PRIORITY_NAVIGATION);

        SKLogging.writeLog(TAG, " onSignalNewAdviceWithInstruction " + instruction, Log.DEBUG);
        // textToSpeechEngine.speak(instruction, TextToSpeech.QUEUE_ADD, null);
    }


    @Override
    public void onSignalNewAdviceWithAudioFiles(String[] audioFiles, boolean b) {
        // a new navigation advice was received
        System.out.println("onSignalNewAdivce Called");
//        Toast.makeText(this, "OnSignalNewAdviceCalled", Toast.LENGTH_SHORT).show();
//        SKLogging.writeLog(TAG, " onSignalNewAdviceWithAudioFiles " + Arrays.asList(audioFiles), Log.DEBUG);
//        SKToolsAdvicePlayer.getInstance().playAdvice(audioFiles, SKToolsAdvicePlayer.PRIORITY_NAVIGATION);

    }

    @Override
    public void onNavigationEnded() {

    }


    @Override
    public void onCurrentPositionUpdate(SKPosition skPosition) {
        currentLocation = skPosition.getCoordinate();
		// Code here for checking proximity and giving out news
		
		// If in proximity of two buildings then tell u r near two buildings and give out their names and any news of two
    }


    @Override
    public void onAllRoutesCompleted() {

        navigationSettings.setNavigationType(SKNavigationSettings.SKNavigationType.REAL);
        navigationSettings.setNavigationMode(SKNavigationSettings.SKNavigationMode.PEDESTRIAN);
        navigationSettings.setShowRealGPSPositions(true);
        SKNavigationManager navigationManager = SKNavigationManager.getInstance();
         navigationManager.setMapView(mapView);
        //        app.setMapResourcesDirPath(SKMaps.getInstance().getMapInitSettings().getMapResourcesPath());
//        SKAdvisorSettings advisorSettings = new SKAdvisorSettings();
//        advisorSettings.setLanguage(SKAdvisorSettings.SKAdvisorLanguage.LANGUAGE_EN);
//        advisorSettings.setAdvisorConfigPath(app.getMapResourcesDirPath() + "/Advisor");
//        advisorSettings.setResourcePath(app.getMapResourcesDirPath() + "/Advisor/Languages");
//        advisorSettings.setAdvisorVoice("en");
//        advisorSettings.setAdvisorType(SKAdvisorSettings.SKAdvisorType.AUDIO_FILES);
//        System.out.println("Audio Advisor Settings set? " + SKRouteManager.getInstance().setAdvisorSettings(advisorSettings));
         navigationManager.setNavigationListener(PhysicalTourActivity.this);
        navigationManager.startNavigation(navigationSettings);

    }

    public void startNavigation(SKCoordinate navigationStartCoordinate,SKCoordinate navigationEndCoordinate){

        SKToolsNavigationManager skToolsNavigationManager = new SKToolsNavigationManager(PhysicalTourActivity.this,R.id.activity_virtual_tour);
        skToolsNavigationManager.setNavigationListener(PhysicalTourActivity.this);
        SKToolsNavigationConfiguration configuration = new SKToolsNavigationConfiguration();

        //configuration.setNavigationType(SKNavigationSettings.SKNavigationType.REAL);
        configuration.setNavigationType(SKNavigationSettings.SKNavigationType.SIMULATION);
        configuration.setStartCoordinate(navigationStartCoordinate);
        configuration.setDestinationCoordinate(navigationEndCoordinate);
        //configuration.setRouteType(SKRouteSettings.SKRouteMode.PEDESTRIAN);
        configuration.setRouteType(SKRouteSettings.SKRouteMode.PEDESTRIAN);
        app.setMapResourcesDirPath(SKMaps.getInstance().getMapInitSettings().getMapResourcesPath());
    //        SKAdvisorSettings advisorSettings = new SKAdvisorSettings();
    //        advisorSettings.setLanguage(SKAdvisorSettings.SKAdvisorLanguage.LANGUAGE_EN);
    //        advisorSettings.setAdvisorConfigPath(app.getMapResourcesDirPath() +"/Advisor");
    //        advisorSettings.setResourcePath(app.getMapResourcesDirPath()+"/Advisor/Languages");
    //        advisorSettings.setAdvisorVoice("en");
    //        advisorSettings.setAdvisorType(SKAdvisorSettings.SKAdvisorType.AUDIO_FILES);
//        System.out.println("Audio Advisor Settings set? "+SKRouteManager.getInstance().setAdvisorSettings(advisorSettings));
        skToolsNavigationManager.launchRouteCalculation(configuration, mapHolder);
        skToolsNavigationManager.startNavigation(configuration,mapHolder);
    }


    @Override
    public void onCustomPOISelected(SKMapCustomPOI skMapCustomPOI) {

    }

    @Override
    public void onCompassSelected() {

    }

    @Override
    public void onCurrentPositionSelected() {

    }

    @Override
    public void onObjectSelected(int i) {

    }

    @Override
    public void onInternationalisationCalled(int i) {

    }

    @Override
    public void onBoundingBoxImageRendered(int i) {

    }

    @Override
    public void onGLInitializationError(String s) {

    }

    @Override
    public void onScreenshotReady(Bitmap bitmap) {

    }

    @Override
    public void onRouteCalculationStarted() {

    }

    @Override
    public void onRouteCalculationCompleted() {

    }

    @Override
    public void onRouteCalculationCanceled() {

    }

    @Override
    public void onRouteCalculationCompleted(SKRouteInfo skRouteInfo) {

    }

    @Override
    public void onRouteCalculationFailed(SKRoutingErrorCode skRoutingErrorCode) {

    }

    @Override
    public void onServerLikeRouteCalculationCompleted(SKRouteJsonAnswer skRouteJsonAnswer) {

    }

    @Override
    public void onOnlineRouteComputationHanging(int i) {

    }

    @Override
    public void onSpeedExceededWithAudioFiles(String[] strings, boolean b) {

    }

    @Override
    public void onSpeedExceededWithInstruction(String s, boolean b) {

    }

    @Override
    public void onUpdateNavigationState(SKNavigationState skNavigationState) {

    }

    @Override
    public void onReRoutingStarted() {

    }

    @Override
    public void onFreeDriveUpdated(String s, String s1, String s2, SKNavigationState.SKStreetType skStreetType, double v, double v1) {

    }

    @Override
    public void onViaPointReached(int i) {

    }

    @Override
    public void onVisualAdviceChanged(boolean b, boolean b1, SKNavigationState skNavigationState) {

    }

    @Override
    public void onTunnelEvent(boolean b) {

    }

    @Override
    public void onNavigationStarted() {

    }

    @Override
    protected void onPause() {
        super.onPause();
        mapHolder.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapHolder.onResume();
    }

    @Override
    public void onActionPan() {

    }

    @Override
    public void onActionZoom() {

    }

    @Override
    public void onMapRegionChanged(SKCoordinateRegion skCoordinateRegion) {

    }

    @Override
    public void onMapRegionChangeStarted(SKCoordinateRegion skCoordinateRegion) {

    }

    @Override
    public void onMapRegionChangeEnded(SKCoordinateRegion skCoordinateRegion) {

    }

    @Override
    public void onDoubleTap(SKScreenPoint skScreenPoint) {

    }

    @Override
    public void onSingleTap(SKScreenPoint skScreenPoint) {

    }

    @Override
    public void onRotateMap() {

    }

    @Override
    public void onLongPress(SKScreenPoint skScreenPoint) {

    }

    @Override
    public void onInternetConnectionNeeded() {

    }

    @Override
    public void onMapActionDown(SKScreenPoint skScreenPoint) {

    }

    @Override
    public void onMapActionUp(SKScreenPoint skScreenPoint) {

    }

    @Override
    public void onPOIClusterSelected(SKPOICluster skpoiCluster) {

    }

    @Override
    public void onMapPOISelected(SKMapPOI skMapPOI) {

    }

}
