package com.skmapstutorial.Application;

import android.app.Application;
import android.content.Context;
import android.content.IntentFilter;

import com.skmapstutorial.Application.Activities.MainActivity;
import com.skmapstutorial.Application.Activities.PhysicalTourActivity;
import com.skmapstutorial.Application.Activities.SplashActivity;
import com.skmapstutorial.Application.Activities.VirtualTourActivity;
import com.skmapstutorial.Application.Model.University;
import com.skmapstutorial.Application.Services.NetworkChangeReceiver;
import com.skobbler.ngx.positioner.SKCurrentPositionListener;
import com.skobbler.ngx.positioner.SKCurrentPositionProvider;

import java.util.ArrayList;

/**
 * Created by mkrao on 1/28/2017.
 */

public class SKMapsApplication extends Application {
    public static  SKMapsApplication instance = new SKMapsApplication();
    String mapResourcesDirPath;
    public static ArrayList<University> universitiesList;
    public static University university;
    public static MainActivity mainActivity;
    public static PhysicalTourActivity physicalTourActivity;
    public static VirtualTourActivity virtualTourActivity;
    public static SplashActivity splashActivity;
    public static NetworkChangeReceiver interNetConnectionChecker;
    // mainActivity =0; PhysicalTourActivity =1; VirtualTourActivity = 2;
    public static  int activeActivity;

    public static SplashActivity getSplashActivity() {
        return splashActivity;
    }

    public static void setSplashActivity(SplashActivity splashActivity) {
        SKMapsApplication.splashActivity = splashActivity;
    }

    public static NetworkChangeReceiver getInterNetConnectionChecker() {
        return interNetConnectionChecker;
    }

    public static void setInterNetConnectionChecker(NetworkChangeReceiver interNetConnectionChecker) {
        SKMapsApplication.interNetConnectionChecker = interNetConnectionChecker;
    }

    public static ArrayList<University> getUniversitiesList() {
        return universitiesList;
    }

    public static void setUniversitiesList(ArrayList<University> universitiesList) {
        SKMapsApplication.universitiesList = universitiesList;
    }

    public static void setInstance(SKMapsApplication instance) {
        SKMapsApplication.instance = instance;
    }

    public static SKMapsApplication getInstance(){
        return instance;
    }
    public static University getUniversity() {
        return university;
    }

    public static void setUniversity(University universityInApplication) {
        university = universityInApplication;
    }

    public String getMapResourcesDirPath() {
        return mapResourcesDirPath;
    }

    public void setMapResourcesDirPath(String mapResourcesDirPath) {
        this.mapResourcesDirPath = mapResourcesDirPath;
    }

    public static int getActiveActivity() {
        return activeActivity;
    }

    public static void setActiveActivity(int activeActivity) {
       SKMapsApplication.activeActivity = activeActivity;
    }

    public static VirtualTourActivity getVirtualTourActivity() {
        return virtualTourActivity;
    }

    public static void setVirtualTourActivity(VirtualTourActivity virtualTourActivity) {
        SKMapsApplication.virtualTourActivity = virtualTourActivity;
    }

    public static PhysicalTourActivity getPhysicalTourActivity() {
        return physicalTourActivity;
    }

    public static void setPhysicalTourActivity(PhysicalTourActivity physicalTourActivity) {
        SKMapsApplication.physicalTourActivity = physicalTourActivity;
    }

    public static MainActivity getMainActivity() {
        return mainActivity;
    }

    public static void setMainActivity(MainActivity mainActivity) {
        SKMapsApplication.mainActivity = mainActivity;
    }


    @Override
    public void onCreate() {
        super.onCreate();
        IntentFilter interNetConnectivityFilter = new IntentFilter();
        interNetConnectivityFilter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        interNetConnectivityFilter.addAction("android.location.PROVIDERS_CHANGED");

        SKMapsApplication.setInterNetConnectionChecker(interNetConnectionChecker);
        registerReceiver(interNetConnectionChecker,interNetConnectivityFilter);


    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        unregisterReceiver(interNetConnectionChecker);
    }
}

