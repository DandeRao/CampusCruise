package com.skmapstutorial.Application.Activities;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.nfc.Tag;
import android.os.Build;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
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
import com.skobbler.ngx.map.SKMapSettings;
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
import com.skobbler.ngx.positioner.SKPositionerManager;
import com.skobbler.ngx.routing.SKRouteInfo;
import com.skobbler.ngx.routing.SKRouteJsonAnswer;
import com.skobbler.ngx.routing.SKRouteListener;
import com.skobbler.ngx.routing.SKRouteManager;
import com.skobbler.ngx.routing.SKRouteSettings;
import com.skobbler.ngx.sdktools.navigationui.SKToolsAdvicePlayer;

import com.skobbler.ngx.sdktools.navigationui.SKToolsMapOperationsManager;
import com.skobbler.ngx.sdktools.navigationui.SKToolsNavigationConfiguration;
import com.skobbler.ngx.sdktools.navigationui.SKToolsNavigationListener;
import com.skobbler.ngx.sdktools.navigationui.SKToolsNavigationManager;
import com.skobbler.ngx.sdktools.navigationui.SKToolsNavigationUIManager;

import com.skobbler.ngx.sdktools.navigationui.autonight.SKToolsAutoNightManager;
import com.skobbler.ngx.trail.SKTrailSettings;
import com.skobbler.ngx.util.SKLogging;



import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import static android.view.View.GONE;

public class PhysicalTourActivity extends AppCompatActivity implements SensorEventListener,SKMapSurfaceListener, SKRouteListener, SKNavigationListener, SKToolsNavigationListener, SKCurrentPositionListener {
    Activity currentActivity;
    // private SKToolsNavigationManager navigationManager;
    SKCurrentPositionProvider currentPositionProvider;
    RelativeLayout navigationUI;
    University university;
    int nearestBuilingToUserId;
    Double universityRadius;
    ArrayList<SKAnnotation> buildingAnnotations;
    Button campusTour;
    Button startTourButton;
    Button skipBuilding;
    Button QRScanButton;
    TextView buildingNameTV;
    SKCoordinate currentLocation;
    float[] orientationValues;
    long lastTimeWhenReceivedGpsSignal;
    float currentCompassValue;
    int lastExactScreenOrientation = -1;
    static final int MINIMUM_TIME_UNTILL_MAP_CAN_BE_UPDATED = 30;
    static final float SMOOTH_FACTOR_COMPASS = 0.1f;
    boolean headingOn;
    SKCoordinate navigationStartCoordinate;
    SKCoordinate navigationEndCoordinate;

    Button continueTourInDialogue;
    Button qrScanButtonInDialogue;


    SKNavigationSettings navigationSettings = new SKNavigationSettings();
    SKNavigationManager navigationManager = SKNavigationManager.getInstance();
    SKToolsNavigationManager skToolsNavigationManager;



    int buildingBeingVisited;
    SKMapsApplication app;
    SKMapViewHolder mapHolder;
    SKMapSurfaceView mapView;
    final String TAG = "Physical Tour Activity";
    ArrayList<String> buildingNames = new ArrayList<>();


    public PhysicalTourActivity() {
        //ArrayList<Building> buildings, LatLng universityLocation, ArrayList<LatLng> tourPath
        university = new University("Northwest Missouri State University", getBuildingsInUniversity(), new SKCoordinate(40.352611, -94.883933), getCampusTourRoute());

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_physical_tour);


        currentPositionProvider = new SKCurrentPositionProvider(getApplicationContext());
        if (Dialogue_Utilites.hasGpsModule(getApplicationContext())) {
            currentPositionProvider.requestLocationUpdates(true, false, true);
        } else if (Dialogue_Utilites.hasNetworkModule(getApplicationContext())) {
            currentPositionProvider.requestLocationUpdates(false, true, true);
        }
        currentPositionProvider.setCurrentPositionListener(this);

        app = (SKMapsApplication) getApplication();
        setBuildingNamesInOrderOfTour();
          mapHolder = (SKMapViewHolder)
                findViewById(R.id.map_surface_holder);
        mapHolder.setMapSurfaceListener(this);

        //mapView = mapHolder.getMapSurfaceView();


        final SKMapsApplication skMapsApplication = (SKMapsApplication) getApplicationContext();
        skMapsApplication.setUniversity(university);
        buildingNameTV = (TextView) findViewById(R.id.building_name);
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
                QRScanButton.setVisibility(GONE);
                //((Button) findViewById(R.id.QRScanButton)).setVisibility(GONE);
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
                QRScanButton.setVisibility(View.VISIBLE);
                firstTimeRouteSettings();

            }
        });
        skipBuilding.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               // onDestinationReached();
            routeToNextBuilding();
            }
        });

        QRScanButton = (Button) findViewById(R.id.QRScanButton);
        QRScanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              scanQR();
            }
        });
    }

    public void scanQR(){
        // QR Scan Handling
        IntentIntegrator integrator = new IntentIntegrator(PhysicalTourActivity.this);
        integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE_TYPES);
        integrator.setPrompt("Scan");
        integrator.setCameraId(0);
        integrator.setOrientationLocked(true);
        integrator.setBarcodeImageEnabled(false);
        integrator.initiateScan();
    }
    public void firstTimeRouteSettings() {
        currentPositionProvider.requestUpdateFromLastPosition();

        calculateUniversityRadius();
        try {
            System.out.println("Current Location is: " + currentLocation.toString());
            if (isUserInUniversity(currentLocation)) {
                findNearestBuildingToUser(currentLocation);
                navigationStartCoordinate = currentLocation;
                navigationEndCoordinate = university.getBuildings().get(0).getBuildingCoordinates();
                buildingBeingVisited = 0;

            } else {

               // Dialogue_Utilites.showNotInUniversityDialogue(PhysicalTourActivity.this);
                notInUniversityDialogue();
                navigationStartCoordinate = currentLocation;
                navigationEndCoordinate = university.getBuildings().get(0).getBuildingCoordinates();
                buildingBeingVisited = 0;
            }
            setBuildingNameTextView(buildingBeingVisited);
            mapView.getMapSettings().setMapDisplayMode(SKMapSettings.SKMapDisplayMode.MODE_3D);
            //mapView.setZoom(18.0f);


            // Uncomment this to use SKNavigationManager

            routeSettings(navigationStartCoordinate, navigationEndCoordinate);
          // startNavigation(navigationStartCoordinate,navigationEndCoordinate);

        }
        catch(NullPointerException e){
            Toast.makeText(PhysicalTourActivity.this," Sorry!! Location Fix not available yet",Toast.LENGTH_LONG).show();

            notInUniversityDialogue();
            e.printStackTrace();
        }
        //Use the below to use SKToolsNavigationManager
        //startNavigation(navigationStartCoordinate,navigationEndCoordinate);

    }

    public void notInUniversityDialogue(){


        final Dialog dialog = new Dialog(PhysicalTourActivity.this);

         dialog.setContentView(R.layout.not_in_university_dialogue_view);
        dialog.show();
        final Button virtualTour = (Button) dialog.findViewById(R.id.virtual_tour_not_in_university_dialogue_button);
        Button goThere = (Button) dialog.findViewById(R.id.go_there_not_in_university_dialogue_button);

        TextView t =  ((TextView) dialog.findViewById(R.id.not_in_university_university_name));
        t.setText(university.getUniversityName());

        virtualTour.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                Intent virtualTourIntent = new Intent(PhysicalTourActivity.this, VirtualTourActivity.class);
                startActivity(virtualTourIntent);

            }
        });

        goThere.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });


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


        SKTrailSettings trailType = new SKTrailSettings();
        trailType.setPedestrianTrailEnabled(true, 1);
        // Set the route listener to be notified of route calculation
        // events
        SKRouteManager.getInstance().setRouteListener(PhysicalTourActivity.this);
        // Pass the route to the calculation routine
        SKRouteManager.getInstance().calculateRoute(route);


    }


    @Override
    public void onAllRoutesCompleted() {
        startNavigationUsingNavigationManager();
  //  copiedNavigation();
    }

    public void startNavigationUsingNavigationManager() {


        navigationManager.setNavigationListener(PhysicalTourActivity.this);


        //navigationSettings.setCcpAsCurrentPosition(true);
        navigationSettings.setNavigationType(SKNavigationSettings.SKNavigationType.REAL);
        navigationSettings.setNavigationMode(SKNavigationSettings.SKNavigationMode.PEDESTRIAN);
        navigationSettings.setShowRealGPSPositions(true);


       // mapView.setPositionAsCurrent(currentLocation,1.0f,false);
        mapView.setZoom(SKMapSurfaceView.MINIMUM_ZOOM_LEVEL);
        //SKMapSurfaceView navMapView = mapHolder.getMapSurfaceView();



        app.setMapResourcesDirPath(SKMaps.getInstance().getMapInitSettings().getMapResourcesPath());
        SKAdvisorSettings advisorSettings = new SKAdvisorSettings();
        advisorSettings.setLanguage(SKAdvisorSettings.SKAdvisorLanguage.LANGUAGE_EN);
        advisorSettings.setAdvisorConfigPath(app.getMapResourcesDirPath() + "/Advisor");
        advisorSettings.setResourcePath(app.getMapResourcesDirPath() + "/Advisor/Languages");
        advisorSettings.setAdvisorVoice("en");
        advisorSettings.setAdvisorType(SKAdvisorSettings.SKAdvisorType.AUDIO_FILES);
        System.out.println("Audio Advisor Settings set? " + SKRouteManager.getInstance().setAdvisorSettings(advisorSettings));
        buildingNameTV.setVisibility(View.VISIBLE);

       // SKToolsMapOperationsManager.getInstance().setMapView(mapView);
        mapView.getMapSettings().setFollowPositions(true);
        mapView.getMapSettings().setHeadingMode(SKMapSettings.SKHeadingMode.HISTORIC_POSITIONS);


        navigationManager = SKNavigationManager.getInstance();
        navigationManager.setMapView(mapView);
        currentPositionProvider.requestUpdateFromLastPosition();
        navigationManager.setNavigationListener(PhysicalTourActivity.this);
        setHeading(true);
        navigationManager.startNavigation(navigationSettings);


    }

    public void addPlolyLine() {

        SKPolyline tourRoute = new SKPolyline();
        tourRoute.setNodes(university.getTourPath());
        tourRoute.setColor((new float[]{0f, 0f, 1f, 0.9f}));
        mapView.addPolyline(tourRoute);
    }

    @Override
    public void onSurfaceCreated(SKMapViewHolder skMapViewHolder) {
        mapHolder = (SKMapViewHolder)
                findViewById(R.id.map_surface_holder);
        mapHolder.setMapSurfaceListener(PhysicalTourActivity.this);
        mapView = mapHolder.getMapSurfaceView();

        addAllBuildingAnnotations();

    }

    public void addAllBuildingAnnotations() {

        SKAnnotation annotation = new SKAnnotation(0);
        annotation
                .setAnnotationType(SKAnnotation.SK_ANNOTATION_TYPE_GREEN);
        annotation.setLocation(university.getUniversityLocation());
        mapView.addAnnotation(annotation,SKAnimationSettings.ANIMATION_NONE);

        mapView.animateToLocation(university.getUniversityLocation(), 0);
        buildingAnnotations = new ArrayList<>(university.getBuildings().size());
        mapView.setZoom(15.5f);
        changeOrderOfAnnotationBasedOnStartPosition();
        addAnnotationsToMapView();
        setBuildingNamesInOrderOfTour();

    }


    public void addAnnotationsToMapView(){
        for(SKAnnotation annotation: buildingAnnotations){
            mapView.addAnnotation(annotation,SKAnimationSettings.ANIMATION_NONE);
        }
    }
    public void changeOrderOfAnnotationBasedOnStartPosition(){

        buildingAnnotations.clear();
        int tag = 1;
        for (Building b : getBuildingsInUniversity()) {
            SKAnnotation sk = new SKAnnotation(tag);
            sk.setAnnotationType(SKAnnotation.SK_ANNOTATION_TYPE_BLUE);
            sk.setLocation(b.getBuildingCoordinates());
            buildingAnnotations.add(sk);
            System.out.println("Building Annotatoins size :" + buildingAnnotations.size());
            tag++;
        }
    }
    public ArrayList<String> addBuildingNames() {
        return new ArrayList<String>() {{
            add("Colden Hall");
            add("Student Union");
            add("Valk Center");
            add("Admin Building");
            add("Garett Strong");
            add("B.D. Owens Library");
            add("Station");
            add("Lamkin Activity Center");
            add("Fine Arts Center");
            add("Recreation Center");
        }};
    }
    public void setBuildingNamesInOrderOfTour(){
        buildingNames.clear();
        for(Building b: university.getBuildings()){
            buildingNames.add(b.getBuildingName());
        }

    }
    public ArrayList<SKCoordinate> addBuildingCoordinates() {
        return new ArrayList<SKCoordinate>() {{
            add(new SKCoordinate(40.350997, -94.882519));
            add(new SKCoordinate(40.351647, -94.882948));
            add(new SKCoordinate(40.352561, -94.880598));
            add(new SKCoordinate(40.353153, -94.883455));
            add(new SKCoordinate(40.354241, -94.884824));
            add(new SKCoordinate(40.353538, -94.885606));
            add(new SKCoordinate(40.354365, -94.888023));
            add(new SKCoordinate(40.349514, -94.884891));
            add(new SKCoordinate(40.349216, -94.883809));
            add(new SKCoordinate(40.350478, -94.883816));
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
            add(new SKCoordinate(40.352739, -94.883345));
            add(new SKCoordinate(40.352636, -94.88358));
            add(new SKCoordinate(40.352618, -94.883709));
            add(new SKCoordinate(40.352635, -94.883738));
            add(new SKCoordinate(40.352884, -94.883908));
            add(new SKCoordinate(40.353588, -94.884456));
            add(new SKCoordinate(40.354036, -94.884702));
            add(new SKCoordinate(40.354064, -94.88471));
            add(new SKCoordinate(40.354087, -94.884688));
            add(new SKCoordinate(40.354124, -94.884667));
            add(new SKCoordinate(40.354165, -94.884698));
            add(new SKCoordinate(40.35419, -94.884765));
            add(new SKCoordinate(40.354207, -94.884799));
            add(new SKCoordinate(40.354241, -94.884824));
            add(new SKCoordinate(40.354226, -94.884863));
            add(new SKCoordinate(40.353847, -94.885059));
            add(new SKCoordinate(40.353598, -94.88517));
            add(new SKCoordinate(40.353516, -94.885251));
            add(new SKCoordinate(40.353496, -94.88538));
            add(new SKCoordinate(40.353538, -94.885606));
            add(new SKCoordinate(40.353529, -94.885555));
            add(new SKCoordinate(40.353611, -94.885574));
            add(new SKCoordinate(40.353701, -94.885644));
            add(new SKCoordinate(40.353788, -94.885595));
            add(new SKCoordinate(40.353852, -94.885558));
            add(new SKCoordinate(40.353842, -94.885722));
            add(new SKCoordinate(40.353841, -94.885912));
            add(new SKCoordinate(40.353954, -94.886055));
            add(new SKCoordinate(40.354349, -94.886558));
            add(new SKCoordinate(40.354359, -94.886633));
            add(new SKCoordinate(40.354365, -94.888023));
            add(new SKCoordinate(40.354308, -94.887985));
            add(new SKCoordinate(40.354284, -94.887839));
            add(new SKCoordinate(40.354052, -94.88776));
            add(new SKCoordinate(40.35395, -94.887708));
            add(new SKCoordinate(40.353788, -94.88768));
            add(new SKCoordinate(40.353667, -94.887666));
            add(new SKCoordinate(40.353597, -94.88765));
            add(new SKCoordinate(40.353594, -94.887311));
            add(new SKCoordinate(40.353148, -94.887296));
            add(new SKCoordinate(40.353027, -94.887167));
            add(new SKCoordinate(40.352614, -94.887221));
            add(new SKCoordinate(40.351975, -94.887305));
            add(new SKCoordinate(40.351266, -94.887387));
            add(new SKCoordinate(40.351195, -94.887339));
            add(new SKCoordinate(40.35115, -94.887269));
            add(new SKCoordinate(40.351115, -94.88714));
            add(new SKCoordinate(40.351056, -94.887006));
            add(new SKCoordinate(40.350951, -94.886883));
            add(new SKCoordinate(40.350863, -94.886806));
            add(new SKCoordinate(40.350731, -94.886747));
            add(new SKCoordinate(40.35054, -94.886747));
            add(new SKCoordinate(40.350421, -94.886746));
            add(new SKCoordinate(40.350352, -94.886714));
            add(new SKCoordinate(40.350315, -94.886701));
            add(new SKCoordinate(40.350022, -94.886726));
            add(new SKCoordinate(40.349806, -94.886758));
            add(new SKCoordinate(40.349739, -94.88676));
            add(new SKCoordinate(40.349602, -94.886628));
            add(new SKCoordinate(40.349557, -94.886609));
            add(new SKCoordinate(40.349526, -94.885577));
            add(new SKCoordinate(40.349531, -94.885359));
            add(new SKCoordinate(40.349518, -94.885168));
            add(new SKCoordinate(40.349511, -94.884869));
            add(new SKCoordinate(40.349502, -94.884449));
            add(new SKCoordinate(40.349495, -94.883863));
            add(new SKCoordinate(40.349385, -94.883931));
            add(new SKCoordinate(40.349307, -94.8839));
            add(new SKCoordinate(40.349244, -94.883864));
            add(new SKCoordinate(40.349216, -94.883809));
            add(new SKCoordinate(40.349237, -94.883725));
            add(new SKCoordinate(40.349304, -94.883672));
            add(new SKCoordinate(40.349426, -94.883687));
            add(new SKCoordinate(40.349487, -94.883711));
            add(new SKCoordinate(40.349687, -94.883702));
            add(new SKCoordinate(40.35041, -94.88368));
            add(new SKCoordinate(40.350412, -94.88382));
            add(new SKCoordinate(40.350478, -94.883816));
            add(new SKCoordinate(40.350476, -94.883667));
            add(new SKCoordinate(40.350425, -94.883666));
            add(new SKCoordinate(40.350497, -94.883568));
            add(new SKCoordinate(40.35059, -94.883442));
            add(new SKCoordinate(40.350715, -94.883291));
            add(new SKCoordinate(40.350871, -94.88313));
            add(new SKCoordinate(40.350944, -94.883058));
            add(new SKCoordinate(40.351018, -94.883173));
            add(new SKCoordinate(40.351124, -94.883236));
            add(new SKCoordinate(40.351148, -94.883235));
            add(new SKCoordinate(40.351165, -94.883176));
            add(new SKCoordinate(40.351246, -94.883066));
            add(new SKCoordinate(40.351334, -94.882921));
            add(new SKCoordinate(40.351305, -94.882827));
            add(new SKCoordinate(40.351248, -94.882676));
            add(new SKCoordinate(40.351149, -94.882571));
            add(new SKCoordinate(40.351069, -94.882518));
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

        Log.d(TAG, "isUserIn University premises? " + (calculateDistance(university.getUniversityLocation(), currentLocationofUser) <  universityRadius));
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
        changeOrderOfAnnotationBasedOnStartPosition();
        setBuildingNamesInOrderOfTour();
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
        dialog.setContentView(R.layout.gallery_view);
        int position = skAnnotation.getUniqueID() - 1;
        //   mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(university.getBuildings().get(position).getBuildingCoordinates(),18.0f));
        if (position == -1)
            dialog.setTitle(university.getUniversityName());
        if (position >= 0)
            dialog.setTitle(university.getBuildings().get(position).getBuildingName());
        TextView title = (TextView) dialog.findViewById(R.id.dialogue_Title);

        if (position == -1)
            title.setText(university.getUniversityName());
        if (position >= 0)
            title.setText(university.getBuildings().get(position).getBuildingName());

        dialog.show();
        LinearLayout videoLayout = (LinearLayout) dialog.findViewById(R.id.video_slider);

        for (int i = 0; i < 3; i++) {
            ImageView imageView = new ImageView(PhysicalTourActivity.this);
            imageView.setId(i);
            imageView.setPadding(2, 2, 2, 2);
            imageView.setImageBitmap(BitmapFactory.decodeResource(
                    getResources(), R.drawable.novideo));
            imageView.setScaleType(ImageView.ScaleType.CENTER);
            videoLayout.addView(imageView);
        }

        LinearLayout audioLayout = (LinearLayout) dialog.findViewById(R.id.audio_slider);

        for (int i = 0; i < 3; i++) {
            ImageView imageView = new ImageView(PhysicalTourActivity.this);
            imageView.setId(i);
            imageView.setPadding(2, 2, 2, 2);
            imageView.setImageBitmap(BitmapFactory.decodeResource(
                    getResources(), R.drawable.noaudio));
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
            audioLayout.addView(imageView);
        }


        LinearLayout ImagesLayout = (LinearLayout) dialog.findViewById(R.id.images_slider);
        for (int i = 0; i < 3; i++) {
            ImageView imageView = new ImageView(PhysicalTourActivity.this);
            imageView.setId(i);
            imageView.setPadding(2, 2, 2, 2);
            imageView.setImageBitmap(BitmapFactory.decodeResource(
                    getResources(), R.drawable.noimages));
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
            ImagesLayout.addView(imageView);
        }


        Button dialogButton = (Button) dialog.findViewById(R.id.dialogue_ok_button);
        // if button is clicked, close the custom dialog
        dialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

    }

    public void routeToNextBuilding(){
        currentPositionProvider.requestUpdateFromLastPosition();
        if (buildingBeingVisited < university.getBuildings().size() - 1) {
            if (!university.getBuildings().get(buildingBeingVisited + 1).isVisited()) {

                Log.d(TAG, "Next Building Not Visited");
                Log.d(TAG, "Current Building: " + university.getBuildings().get(buildingBeingVisited).getBuildingName());
                Log.d(TAG, "Next Building: " + university.getBuildings().get(buildingBeingVisited + 1).getBuildingName());
                Log.d(TAG, "buildingBeingVisited : " + buildingBeingVisited);
                Log.d(TAG, "Next buildingBeingVisited : " + (buildingBeingVisited + 1));

                setBuildingNameTextView(buildingBeingVisited+1);
                // Uncomment below line to use SKNavigationManager
             //   routeSettings(university.getBuildings().get(buildingBeingVisited).getBuildingCoordinates(), university.getBuildings().get(buildingBeingVisited + 1).getBuildingCoordinates());
               routeSettings(currentLocation,university.getBuildings().get(buildingBeingVisited + 1).getBuildingCoordinates());
                skipBuilding.setText("Skip Building");
                // startNavigation(university.getBuildings().get(buildingBeingVisited).getBuildingCoordinates(), university.getBuildings().get(buildingBeingVisited + 1).getBuildingCoordinates());



                //  startNavigation(university.getBuildings().get(buildingBeingVisited).getBuildingCoordinates(), university.getBuildings().get(buildingBeingVisited + 1).getBuildingCoordinates());
               // startNavigation(currentLocation,university.getBuildings().get(buildingBeingVisited + 1).getBuildingCoordinates());
                buildingBeingVisited += 1;
            }
        }else{
            onNavigationEnded();
            removeItemsFromMap();

            SKCoordinateRegion universityRegion = new SKCoordinateRegion(university.getUniversityLocation(),15.5f);
            mapView.changeMapVisibleRegion(universityRegion,true);
            //mapView.zoomInAt(mapView.coordinateToPoint(university.getUniversityLocation()));
            Toast.makeText(this, "Tour Finished Calculating your total activity", Toast.LENGTH_SHORT).show();
            addAllBuildingAnnotations();
            addPlolyLine();
        }
    }

    @Override
    public void onDestinationReached() {
        setHeading(false);
        Toast.makeText(this, "Destination Reached", Toast.LENGTH_SHORT).show();
        destinationReachedDialogue();
        Dialogue_Utilites.showBuildingDetialsDialouge(this);
        // Scan QR code Here
        skipBuilding.setText("Continue Tour");
        System.out.println("Building Being Visited");
        university.getBuildings().get(buildingBeingVisited).setVisited(true);



    }

public void destinationReachedDialogue(){
    final Dialog dialog = new Dialog(PhysicalTourActivity.this);
    dialog.setContentView(R.layout.destination_reached_dialogue_view);
    dialog.setTitle("Destination Reached");
    TextView title = (TextView) dialog.findViewById(R.id.destination_reached_building_name);

    title.setText(university.getBuildings().get(buildingBeingVisited).getBuildingName());

    dialog.show();

    qrScanButtonInDialogue = (Button) dialog.findViewById(R.id.qr_scan_dialogue_button);
    continueTourInDialogue = (Button) dialog.findViewById(R.id.continue_tour_dialogue_button);

        qrScanButtonInDialogue.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
        scanQR();
        qrScanButtonInDialogue.setVisibility(GONE);

    }
});

    continueTourInDialogue.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            routeToNextBuilding();
            dialog.dismiss();
        }
    });

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
        System.out.println("Advices are : " + Arrays.asList(audioFiles).toString());
//        Toast.makeText(this, "OnSignalNewAdviceCalled", Toast.LENGTH_SHORT).show();
        SKLogging.writeLog(TAG, " onSignalNewAdviceWithAudioFiles " + Arrays.asList(audioFiles), Log.DEBUG);
        SKToolsAdvicePlayer.getInstance().playAdvice(audioFiles, SKToolsAdvicePlayer.PRIORITY_NAVIGATION);

    }

    @Override
    public void onNavigationEnded() {
    mapView.getMapSettings().setMapDisplayMode(SKMapSettings.SKMapDisplayMode.MODE_2D);
        mapView.setPositionAsCurrent(university.getUniversityLocation(),1.0f,false);
        SKNavigationManager.getInstance().stopNavigation();
       // skToolsNavigationManager.stopNavigation();
        buildingNameTV.setVisibility(GONE);

    }

    private void setHeading(boolean enabled) {
        if (enabled) {
            headingOn = true;
            mapView.getMapSettings().setHeadingMode(SKMapSettings.SKHeadingMode.ROTATING_MAP);
            startOrientationSensor();
        } else {
            headingOn = false;
            mapView.getMapSettings().setHeadingMode(SKMapSettings.SKHeadingMode.NONE);
            stopOrientationSensor();
        }
    }

    /**
     * Activates the orientation sensor
     */
    private void startOrientationSensor() {
        orientationValues = new float[3];
        SensorManager sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        Sensor orientationSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);
        sensorManager.registerListener(this, orientationSensor, SensorManager.SENSOR_DELAY_UI);
    }

    /**
     * Deactivates the orientation sensor
     */
    private void stopOrientationSensor() {
        orientationValues = null;
        SensorManager sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        sensorManager.unregisterListener(this);
    }

    @Override
    public void onCurrentPositionUpdate(SKPosition skPosition) {

//            try {
//                if (currentLocation.equals(null))
//                    currentLocation = skPosition.getCoordinate();
//            }catch (NullPointerException e){
//                currentLocation = skPosition.getCoordinate();
//            }
     //   if(calculateDistance(currentLocation, skPosition.getCoordinate() )>10  ) {
            currentLocation = skPosition.getCoordinate();
            SKPositionerManager.getInstance().reportNewGPSPosition(skPosition);
            System.out.println("Location Update Received"+currentLocation.toString());
       // }
        // Code here for checking proximity and giving out news

        // If in proximity of two buildings then tell u r near two buildings and give out their names and any news of two
    }

    public void startNavigation(SKCoordinate navigationStartCoordinate, SKCoordinate navigationEndCoordinate) {

        skToolsNavigationManager = new SKToolsNavigationManager(PhysicalTourActivity.this, R.id.activity_physical_tour);
        skToolsNavigationManager.setNavigationListener(PhysicalTourActivity.this);
        SKToolsNavigationConfiguration configuration = new SKToolsNavigationConfiguration();


        configuration.setNavigationType(SKNavigationSettings.SKNavigationType.REAL);
        // configuration.setNavigationType(SKNavigationSettings.SKNavigationType.SIMULATION);
        configuration.setStartCoordinate(navigationStartCoordinate);
        configuration.setDestinationCoordinate(navigationEndCoordinate);

        configuration.setRouteType(SKRouteSettings.SKRouteMode.PEDESTRIAN);
       // app.setMapResourcesDirPath(SKMaps.getInstance().getMapInitSettings().getMapResourcesPath());
        //        SKAdvisorSettings advisorSettings = new SKAdvisorSettings();
        //        advisorSettings.setLanguage(SKAdvisorSettings.SKAdvisorLanguage.LANGUAGE_EN);
        //        advisorSettings.setAdvisorConfigPath(app.getMapResourcesDirPath() +"/Advisor");
        //        advisorSettings.setResourcePath(app.getMapResourcesDirPath()+"/Advisor/Languages");
        //        advisorSettings.setAdvisorVoice("en");
        //        advisorSettings.setAdvisorType(SKAdvisorSettings.SKAdvisorType.AUDIO_FILES);
//        System.out.println("Audio Advisor Settings set? "+SKRouteManager.getInstance().setAdvisorSettings(advisorSettings));
        skToolsNavigationManager.launchRouteCalculation(configuration, mapHolder);
     //   mapView.getMapSettings().setFollowPositions(true);
        //skToolsNavigationManager.setNavigationListener();
        skToolsNavigationManager.startNavigation(configuration, mapHolder);

        SKToolsNavigationUIManager.getInstance().hideBottomAndLeftPanels();
        SKToolsNavigationUIManager.getInstance().hideSearchingForGPSPanel();
        SKToolsNavigationUIManager.getInstance().hideSettingsPanel();
        SKToolsNavigationUIManager.getInstance().hideTopPanels();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        try {
            if (result.getContents().equals(null)) {
                Toast.makeText(this, "QR Scan Cancelled", Toast.LENGTH_SHORT).show();
            } else {

                setBuildingNamesInOrderOfTour();
                String scannedBuildingName = result.getContents();
                int buildingPosition = buildingNames.indexOf(scannedBuildingName);
                if (buildingPosition >= 0) {
                    Toast.makeText(this, "Your are at " + result.getContents(), Toast.LENGTH_LONG).show();
                    onAnnotationSelected(buildingAnnotations.get(buildingPosition));

                } else {
                    Toast.makeText(this, "Invalid QRCode scanned", Toast.LENGTH_SHORT).show();
                }

            }
        } catch (NullPointerException e) {
            Toast.makeText(this, "QR Scan Cancelled", Toast.LENGTH_SHORT).show();
        }


        super.onActivityResult(requestCode, resultCode, data);
    }

    public void removeItemsFromMap() {
        mapView.deleteAllAnnotationsAndCustomPOIs();
        //mapView.clearAllOverlays();
        skipBuilding.setVisibility(GONE);
        SKRouteManager.getInstance().clearAllRoutesFromCache();
        SKRouteManager.getInstance().clearCurrentRoute();
        mapView.deleteAllAnnotationsAndCustomPOIs();
        QRScanButton.setVisibility(GONE);
        buildingNameTV.setVisibility(GONE);


    }

    public void setBuildingNameTextView(int buildingPosition){
        buildingNameTV.setText(university.getBuildings().get(buildingPosition).getBuildingName());
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
    mapView.getMapSettings().setMapDisplayMode(SKMapSettings.SKMapDisplayMode.MODE_3D);
        mapView.setZoom(1.2f);
    buildingNameTV.setVisibility(View.VISIBLE);
        setBuildingNameTextView(buildingBeingVisited+1);

    }

    @Override
    protected void onPause() {
        super.onPause();
        try{
        mapView.onPause();}catch(NullPointerException e){
            System.out.println("mapView is null in onPause, Continutin");
        }
        mapHolder.onPause();
        System.out.println("On pause called");


    }

    @Override
    protected void onResume() {
        super.onResume();
        mapHolder.onResume();
        try{
        mapView.onResume();}catch(NullPointerException e){
            System.out.println("mapView is Null in onResume, Continuing");
        }
        System.out.println("on resume called");

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

    public void copiedNavigation(){


            SKNavigationSettings navigationSettings = new SKNavigationSettings();
            this.mapHolder = mapHolder;
            mapView = mapHolder.getMapSurfaceView();
                //   Toast.makeText(this.currentActivity, "The map will turn based on your recent positions.", Toast.LENGTH_SHORT).show();
                mapView.getMapSettings().setFollowPositions(true);
                mapView.getMapSettings().setHeadingMode(SKMapSettings.SKHeadingMode.ROTATING_MAP);
                SKTrailSettings trailType = new SKTrailSettings();
                trailType.setPedestrianTrailEnabled(true, 1);
                mapView.getMapSettings().setTrailSettings(trailType);
                navigationSettings.setCcpAsCurrentPosition(true);
                mapView.getMapSettings().setCompassPosition(new SKScreenPoint(10, 70));
                mapView.getMapSettings().setCompassShown(true);
                navigationSettings.setNavigationMode(SKNavigationSettings.SKNavigationMode.PEDESTRIAN);



            mapView.getMapSettings().setStreetNamePopupsShown(true);
            mapView.getMapSettings().setMapZoomingEnabled(false);

            mapHolder.setMapSurfaceListener(this);


            navigationSettings.setNavigationType(SKNavigationSettings.SKNavigationType.REAL);
            navigationSettings.setPositionerVerticalAlignment(-0.25f);
            navigationSettings.setShowRealGPSPositions(false);
            navigationManager.setNavigationListener(this);
        navigationManager.setMapView(mapView);
        navigationManager.startNavigation(navigationSettings);


            // SKToolsNavigationUIManager.getInstance().inflateNavigationViews(currentActivity);
            // SKToolsNavigationUIManager.getInstance().reset(configuration.getDistanceUnitType());
            // SKToolsNavigationUIManager.getInstance().setFollowerMode();

    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        //mapView.reportNewHeading(t.values[0]);
        switch (event.sensor.getType()) {

            case Sensor.TYPE_ORIENTATION:
                if (orientationValues != null) {
                    for (int i = 0; i < orientationValues.length; i++) {
                        orientationValues[i] = event.values[i];

                    }
                    if (orientationValues[0] != 0) {
                        if ((System.currentTimeMillis() - lastTimeWhenReceivedGpsSignal) > MINIMUM_TIME_UNTILL_MAP_CAN_BE_UPDATED) {
                            applySmoothAlgorithm(orientationValues[0]);
                            int currentExactScreenOrientation = Dialogue_Utilites.getExactScreenOrientation(this);
                            if (lastExactScreenOrientation != currentExactScreenOrientation) {
                                lastExactScreenOrientation = currentExactScreenOrientation;
                                switch (lastExactScreenOrientation) {
                                    case ActivityInfo.SCREEN_ORIENTATION_PORTRAIT:
                                        mapView.reportNewDeviceOrientation(SKMapSurfaceView.SKOrientationType.PORTRAIT);
                                        break;
                                    case ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT:
                                        mapView.reportNewDeviceOrientation(SKMapSurfaceView.SKOrientationType.PORTRAIT_UPSIDEDOWN);
                                        break;
                                    case ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE:
                                        mapView.reportNewDeviceOrientation(SKMapSurfaceView.SKOrientationType.LANDSCAPE_RIGHT);
                                        break;
                                    case ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE:
                                        mapView.reportNewDeviceOrientation(SKMapSurfaceView.SKOrientationType.LANDSCAPE_LEFT);
                                        break;
                                }
                            }

                            // report to NG the new value
                            if (orientationValues[0] < 0) {
                                mapView.reportNewHeading(-orientationValues[0]);
                            } else {
                                mapView.reportNewHeading(orientationValues[0]);
                            }

                            lastTimeWhenReceivedGpsSignal = System.currentTimeMillis();
                        }
                    }
                }
                break;
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }


    /**
     * @param newCompassValue new z value returned by the sensors
     */
    private void applySmoothAlgorithm(float newCompassValue) {
        if (Math.abs(newCompassValue - currentCompassValue) < 180) {
            currentCompassValue = currentCompassValue + SMOOTH_FACTOR_COMPASS * (newCompassValue - currentCompassValue);
        } else {
            if (currentCompassValue > newCompassValue) {
                currentCompassValue = (currentCompassValue + SMOOTH_FACTOR_COMPASS * ((360 + newCompassValue - currentCompassValue) % 360) + 360) % 360;
            } else {
                currentCompassValue = (currentCompassValue - SMOOTH_FACTOR_COMPASS * ((360 - newCompassValue + currentCompassValue) % 360) + 360) % 360;
            }
        }
    }


}
