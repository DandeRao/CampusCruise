package com.skmapstutorial.Application;

import android.app.Application;
import android.content.Context;

import com.skmapstutorial.Application.Model.University;

/**
 * Created by mkrao on 1/28/2017.
 */

public class SKMapsApplication extends Application {
    University university;
    public static  SKMapsApplication instance = new SKMapsApplication();
    String mapResourcesDirPath;
    public static Context getContext(){
        return instance;
    }
    public University getUniversity() {
        return university;
    }

    public void setUniversity(University university) {
        this.university = university;
    }

    public String getMapResourcesDirPath() {
        return mapResourcesDirPath;
    }

    public void setMapResourcesDirPath(String mapResourcesDirPath) {
        this.mapResourcesDirPath = mapResourcesDirPath;
    }

    @Override
    public void onCreate() {
        super.onCreate();

    }
}

