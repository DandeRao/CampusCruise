package com.skmapstutorial.Application.Model;


import com.skobbler.ngx.SKCoordinate;

import java.net.URI;
import java.util.ArrayList;

/**
 * Created by mkrao on 12/27/2016.
 */

public class Building {
    String buildingName;
    SKCoordinate buildingCoordinates;
    boolean isVisited;
    int tourPositoin;
    ArrayList<String> news;
    ArrayList<URI> imageURIs;
    ArrayList<URI> videoURIs;
    ArrayList<URI> audioURIs;



    public Building(String buildingName, SKCoordinate buildingCoordinates) {
        this.buildingName = buildingName;
        this.buildingCoordinates = buildingCoordinates;
        this.isVisited = false;
    }

    public int getTourPositoin() {
        return tourPositoin;
    }

    public void setTourPositoin(int tourPositoin) {
        this.tourPositoin = tourPositoin;
    }

    public boolean isVisited() {
        return isVisited;
    }

    public void setVisited(boolean visited) {
        isVisited = visited;
    }

    public String getBuildingName() {
        return buildingName;
    }

    public void setBuildingName(String buildingName) {
        this.buildingName = buildingName;
    }

    public SKCoordinate getBuildingCoordinates() {
        return buildingCoordinates;
    }

    public ArrayList<String> getNews() {
        return news;
    }

    public void setNews(ArrayList<String> news) {
        this.news = news;
    }

    public void setBuildingCoordinates(SKCoordinate buildingCoordinates) {
        this.buildingCoordinates = buildingCoordinates;
    }

    public ArrayList<URI> getImageURIs() {
        return imageURIs;
    }

    public void setImageURIs(ArrayList<URI> imageURIs) {
        this.imageURIs = imageURIs;
    }

    public ArrayList<URI> getVideoURIs() {
        return videoURIs;
    }

    public void setVideoURIs(ArrayList<URI> videoURIs) {
        this.videoURIs = videoURIs;
    }

    public ArrayList<URI> getAudioURIs() {
        return audioURIs;
    }

    public void setAudioURIs(ArrayList<URI> audioURIs) {
        this.audioURIs = audioURIs;
    }
}
