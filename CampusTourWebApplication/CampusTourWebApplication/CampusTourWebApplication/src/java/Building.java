/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import javax.inject.Named;
import javax.enterprise.context.Dependent;
import javax.enterprise.context.RequestScoped;

/**
 *
 * @author Rakesh Chitturi
 */
@Named(value = "building")
@RequestScoped
public class Building {
    private String buildingImage;
    private String buildingVideo;
    private int buildingLattitude;
    private int buildingLongitude;
    private String buildingName;
    private String buildingDescription;

    public String getBuildingName() {
        return buildingName;
    }

    public void setBuildingName(String buildingName) {
        this.buildingName = buildingName;
    }

    public String getBuildingDescription() {
        return buildingDescription;
    }

    public void setBuildingDescription(String buildingDescription) {
        this.buildingDescription = buildingDescription;
    }

    public String getBuildingImage() {
        return buildingImage;
    }

    public void setBuildingImage(String buildingImage) {
        this.buildingImage = buildingImage;
    }

    public String getBuildingVideo() {
        return buildingVideo;
    }

    public void setBuildingVideo(String buildingVideo) {
        this.buildingVideo = buildingVideo;
    }

    public int getBuildingLattitude() {
        return buildingLattitude;
    }

    public void setBuildingLattitude(int buildingLattitude) {
        this.buildingLattitude = buildingLattitude;
    }

    public int getBuildingLongitude() {
        return buildingLongitude;
    }

    public void setBuildingLongitude(int buildingLongitude) {
        this.buildingLongitude = buildingLongitude;
    }
    public void GetBuildingDetails(){
        
    }
    
}
