

import java.util.ArrayList;

/**
 * Created by mkrao on 12/27/2016.
 */

public class University {
    String universityName;
    ArrayList<Building> buildings;
    String universityLocation;
    String tourPath;
    int universityId;

    public University() {
    }

    public University(String universityName, String universityLocation, String tourPath, int universityId) {
        this.universityName = universityName;
        this.universityLocation = universityLocation;
        this.tourPath = tourPath;
        this.universityId = universityId;
    }

    
    public University(String universityName, String universityLocation, String tourPath) {
        this.universityName = universityName;
        this.universityLocation = universityLocation;
        this.tourPath = tourPath;
    }

     public University(String universityName, ArrayList<Building> buildings, String universityLocation, String tourPath) {
        this.buildings = buildings;
        this.universityLocation = universityLocation;
        this.tourPath = tourPath;
        this.universityName = universityName;
    }

    public int getUniversityId() {
        return universityId;
    }

    public void setUniversityId(int universityId) {
        this.universityId = universityId;
    }

     
    public ArrayList<Building> getBuildings() {

        return buildings;
    }

    public void setBuildings(ArrayList<Building> buildings) {
        this.buildings = buildings;
    }

    public String getUniversityLocation() {
        return universityLocation;
    }

    public void setUniversityLocation(String universityLocation) {
        this.universityLocation = universityLocation;
    }

    public String getTourPath() {
        return tourPath;
    }

    public String getUniversityName() {
        return universityName;
    }

    public void setUniversityName(String universityName) {
        this.universityName = universityName;
    }

    public void setTourPath(String tourPath) {
        this.tourPath = tourPath;
    }
}
