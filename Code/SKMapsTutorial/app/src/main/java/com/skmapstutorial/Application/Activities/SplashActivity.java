package com.skmapstutorial.Application.Activities;


import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.skmapstutorial.Application.Model.Building;
import com.skmapstutorial.Application.Model.University;
import com.skmapstutorial.Application.SKMapsApplication;
import com.skmapstutorial.Application.Services.NetworkChangeReceiver;
import com.skmapstutorial.R;
import com.skobbler.ngx.SKCoordinate;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.crypto.spec.DESedeKeySpec;

public class SplashActivity extends Activity {
    String message;
    ArrayList<University> univ = new ArrayList<>();

    NetworkChangeReceiver interNetConnectionChecker;
    Dialog closeApplicationDialog;
    // Dialog to show that services have been disabled
    Dialog waitingForCurrentLocation;
    //Progress dialog to show when waiting for GPS to turn on
    ProgressDialog fetchingLocation;
    SplashActivity splashActivity;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        SKMapsApplication.setSplashActivity(this);
        SKMapsApplication.setActiveActivity(3);
        waitingForCurrentLocation = new Dialog(this);
        fetchingLocation = new ProgressDialog(this);
        closeApplicationDialog = new Dialog(this);
        splashActivity = this;
        new Thread(new Runnable() {
            public void run() {

                try{
                    Log.d("Try", "reached");
                    URL url = new URL("http://198.209.246.84:8080/CampusCruiseServer/Fetch");
                    URLConnection connection = url.openConnection();

                    connection.setDoOutput(true);
                    BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));

                    String returnString="";
                    while ((returnString = in.readLine()) != null)
                    {
                        message = returnString;

                    }

                    in.close();

                }catch(Exception e)
                {
                    Log.d("Exception",e.toString());
                }

                System.out.println("Finished Fetching data");
                parseJson(message);




            }
        }).start();
    }

    @Override
    protected void onResume() {
        super.onResume();
        SKMapsApplication.setActiveActivity(3);
    }


    public void parseJson(String m){

        ArrayList<Building> buil = new ArrayList<>();
        System.out.println("Parse Json Reached");
        try {
            System.out.println(m);
  //          JSONObject universities = new JSONObject(message);
//            JSONArray universitiesArray = universities.getJSONArray("universities");
            JSONArray universitiesArray = new JSONArray(message);

            for(int universityArraySize =0;universityArraySize < universitiesArray.length();universityArraySize++) {
                buil = new ArrayList<>();
                for(University u:univ){
                    System.out.println("University: "+u.getUniversityName());
                    System.out.println("Buildings in Each University: "+u.getBuildings().size());
                }

                System.out.println("Outside Iteration "+universityArraySize);
              //  JSONObject universityJson = universitiesArray.getJSONObject(universityArraySize);
               // JSONObject universityJson = universitiesArray.getJSONObject(universityArraySize);
                JSONObject universityJson = universitiesArray.getJSONObject(universityArraySize);
                try{
                JSONArray buildingArrayJson = universityJson.getJSONArray("buildings");
                for (int i = 0; i < buildingArrayJson.length(); i++) {

                        System.out.println("Inside Iteration " + i);
                        JSONObject oneObject = buildingArrayJson.getJSONObject(i);
                        // Pulling items from the array
                        String buildingName = oneObject.getString("buildingName");
                        String buildingCoordinates = oneObject.getString("buildingCoordinates");
                        buil.add(new Building(buildingName, parseStringToSKCoordinates(buildingCoordinates)));


                }
                } catch(JSONException f){f.printStackTrace();
                    System.out.println("Buildings is Empty");}

                SKCoordinate universityLocation = parseStringToSKCoordinates(universityJson.getString("universityLocation"));

                String tourPathString = universityJson.getString("tourPath");
                List<String> tourPathInSplittedString = Arrays.asList(tourPathString.split(";"));
                ArrayList<SKCoordinate> tourCoordinates = new ArrayList<>();
                for (String s : tourPathInSplittedString) {
                    tourCoordinates.add(parseStringToSKCoordinates(s));
                }
                System.out.println("Number Of Buildings in the uinversity "+buil.size());
               univ.add(new University(universityJson.getString("universityName"), buil, universityLocation, tourCoordinates));

            }
            SKMapsApplication.setUniversitiesList(univ);
            System.out.println("Universities set in Application: "+SKMapsApplication.getUniversitiesList().size());
            for(University u:SKMapsApplication.getUniversitiesList()){
                System.out.println("University: "+u.getUniversityName());
                System.out.println("Buildings in Each University: "+u.getBuildings().size());
            }
            Intent selectUniversity = new Intent(this,MainActivity.class);
            startActivity(selectUniversity);

        } catch (NullPointerException e){
            e.printStackTrace();
            System.out.println("Null Pointer Exception Caught!!.");
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    closeApplicationDialog.setContentView(R.layout.connecting_to_server_failed);
                    closeApplicationDialog.show();

                    Button close = (Button) closeApplicationDialog.findViewById(R.id.connecting_to_server_failed_button);

                    close.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            System.exit(0);
                        }
                    });
                }
            });

        }catch (JSONException e) {
            e.printStackTrace();
            System.out.println("Json Exception caught");
        }


    }

    public SKCoordinate parseStringToSKCoordinates(String coordianteString){
        SKCoordinate coordinate;
        String[] splittedStringCoordinates = coordianteString.split(",");
        coordinate = new SKCoordinate(Double.parseDouble(splittedStringCoordinates[0]),Double.parseDouble(splittedStringCoordinates[1]));
        return coordinate;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public ArrayList<University> getUniv() {
        return univ;
    }

    public void setUniv(ArrayList<University> univ) {
        this.univ = univ;
    }

    public NetworkChangeReceiver getInterNetConnectionChecker() {
        return interNetConnectionChecker;
    }

    public void setInterNetConnectionChecker(NetworkChangeReceiver interNetConnectionChecker) {
        this.interNetConnectionChecker = interNetConnectionChecker;
    }

    public Dialog getWaitingForCurrentLocation() {
        return waitingForCurrentLocation;
    }

    public void setWaitingForCurrentLocation(Dialog waitingForCurrentLocation) {
        this.waitingForCurrentLocation = waitingForCurrentLocation;
    }

    public ProgressDialog getFetchingLocation() {
        return fetchingLocation;
    }

    public void setFetchingLocation(ProgressDialog fetchingLocation) {
        this.fetchingLocation = fetchingLocation;
    }
}
