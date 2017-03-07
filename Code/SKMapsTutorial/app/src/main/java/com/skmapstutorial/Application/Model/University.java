package com.skmapstutorial.Application.Model;

import com.skobbler.ngx.SKCoordinate;

import java.util.ArrayList;

/**
 * Created by mkrao on 12/27/2016.
 */

public class University {
    String universityName;
    ArrayList<Building> buildings;
    SKCoordinate universityLocation;
    ArrayList<SKCoordinate> tourPath;

    public University() {
    }

    public University(String universityName, ArrayList<Building> buildings, SKCoordinate universityLocation, ArrayList<SKCoordinate> tourPath) {
        this.buildings = buildings;
        this.universityLocation = universityLocation;
        this.tourPath = tourPath;
        this.universityName = universityName;
    }

    public ArrayList<Building> getBuildings() {

        return buildings;
    }

    public void setBuildings(ArrayList<Building> buildings) {
        this.buildings = buildings;
    }

    public SKCoordinate getUniversityLocation() {
        return universityLocation;
    }

    public void setUniversityLocation(SKCoordinate universityLocation) {
        this.universityLocation = universityLocation;
    }

    public ArrayList<SKCoordinate> getTourPath() {
        return tourPath;
    }

    public String getUniversityName() {
        return universityName;
    }

    public void setUniversityName(String universityName) {
        this.universityName = universityName;
    }

    public void setTourPath(ArrayList<SKCoordinate> tourPath) {
        this.tourPath = tourPath;
    }
}
