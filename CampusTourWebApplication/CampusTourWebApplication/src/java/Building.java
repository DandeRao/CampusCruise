/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Named;
import javax.faces.bean.ManagedBean;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.servlet.http.Part;

/**
 *
 * @author Rakesh Chitturi
 */
@Named(value = "building")
@ManagedBean
public class Building {

    //List to store fetched buildings from database
    List<Building> buildingsList = new ArrayList<Building>() {
    };
    //Getting the current instance using face context
    HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
    //created seesion
    HttpSession session = request.getSession();
    //Images of building 
    private Part buildingImage;
    //viodeos of building
    private Part buildingVideo;
    //Lattitude of the building
    private int buildingLattitude;
    //Longitude of the building
    private int buildingLongitude;
    //Name of the building
    private String buildingName;
    //Description of the building
    private String buildingDescription;
    //Audios of the building
    private Part buildingAudio;

    /**
     * Method that gets the building audio
     *
     * @return buildingAudio
     */
    public Part getBuildingAudio() {
        return buildingAudio;
    }

    /**
     * Method that sets the image to buildingAudio
     * @param buildingAudio audio of the building
     *
     */
    public void setBuildingAudio(Part buildingAudio) {
        this.buildingAudio = buildingAudio;
    }

    /**
     * Method that gets the building image
     *
     * @return buildingImage
     */
    public Part getBuildingImage() {
        return buildingImage;
    }

    /**
     * Method that sets the image to buildingImage
     *
     * @param buildingImage image of the building
     *
     */
    public void setBuildingImage(Part buildingImage) {
        this.buildingImage = buildingImage;
    }

    /**
     * Method that gets the building video
     *
     * @return buildingVideo
     */
    public Part getBuildingVideo() {
        return buildingVideo;
    }

    /**
     * Method that sets the video to buildingVideo
     *
     * @param buildingVideo vide of the building
     */
    public void setBuildingVideo(Part buildingVideo) {
        this.buildingVideo = buildingVideo;
    }

    /**
     * Method that gets the buildingName
     *
     * @return
     */
    public String getBuildingName() {
        return buildingName;
    }

    /**
     * Method that sets the buildingName
     *
     * @param buildingName name of the building
     */
    public void setBuildingName(String buildingName) {
        this.buildingName = buildingName;
    }

    /**
     * Method that gets the building description
     *
     * @return buildingDescription 
     */
    public String getBuildingDescription() {
        return buildingDescription;
    }

    /**
     * Method that sets the buldingDescription
     *
     * @param buildingDescription description of the building
     */
    public void setBuildingDescription(String buildingDescription) {
        this.buildingDescription = buildingDescription;
    }

    /**
     * Method that gets the building latitude
     *
     * @return buildingLattitude
     */
    public int getBuildingLattitude() {
        return buildingLattitude;
    }

    /**
     * Method that sets the building latitude
     *
     * @param buildingLattitude latitude of the building
     */
    public void setBuildingLattitude(int buildingLattitude) {
        this.buildingLattitude = buildingLattitude;
    }

    /**
     * Method that gets the building longitude
     *
     * @return buildingLongitude
     */
    public int getBuildingLongitude() {
        return buildingLongitude;
    }

    /**
     * Method that sets the building longitude
     *
     * @param  buildingLongitude longitude of the building
     */
    public void setBuildingLongitude(int buildingLongitude) {
        this.buildingLongitude = buildingLongitude;
    }

    /**
     * Method to add buildings to the database
     *
     * @return string 
     */
    public String addBuilding() {
        String path = FacesContext.getCurrentInstance().getExternalContext().getRealPath("resources");

        path = path.substring(0, path.indexOf("\\build"));
        path = path + "\\web\\resources\\";

        try {
            Class.forName("com.mysql.jdbc.Driver");
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost/campus_tour", "root", "");
            Statement st = con.createStatement();
            String path1 = path + "\\" + session.getAttribute("university");
            String path0 = path1 + "\\" + this.getBuildingName();

            File folder = new File(path1);
            File folder0 = new File(path0);

            if (folder.exists()) {
                folder0.mkdir();

            } else {
                folder.mkdir();
                folder0.mkdir();
            }
            ResultSet rs = st.executeQuery("select count(*) from building");
            rs.next();
            int count = rs.getInt(1);
            ResultSet result = st.executeQuery("select university_id from university where university_name = '" + session.getAttribute("university") + "'");
            result.next();
            int id = result.getInt("university_id");
            //String word = pathString(path0.split("\\"));
            String imageAddress = path0 + "\\" + this.buildingName + " Image.zip";
            String audioAddress = path0 + "\\" + this.buildingName + " Audio.zip";
            String videoAddress = path0 + "\\" + this.buildingName + " Video.zip";
            st.executeUpdate("insert into building values('" + (count + 1) + "','" + this.getBuildingName() + "','" + this.buildingDescription + "','" + imageAddress + "','" + audioAddress + "','" + videoAddress + "','" + this.getBuildingLattitude() + "','" + this.getBuildingLongitude() + "','" + id + "')");

            InputStream fp = buildingImage.getInputStream();
            byte[] data = new byte[fp.available()];
            fp.read(data);
            FileOutputStream fout = new FileOutputStream(new File(path0 + "\\" + this.buildingName + " Image.zip"));
            fout.write(data);
            fp = buildingAudio.getInputStream();
            data = new byte[fp.available()];
            fp.read(data);
            fout = new FileOutputStream(new File(path0 + "\\" + this.buildingName + " Audio.zip"));
            fout.write(data);
            fp = buildingVideo.getInputStream();
            data = new byte[fp.available()];
            fp.read(data);
            fout = new FileOutputStream(new File(path0 + "\\" + this.buildingName + " Video.zip"));
            fout.write(data);
            fp.close();
            fout.close();
        } catch (ClassNotFoundException | SQLException | IOException e) {

        }
        return "inserted";
    }

    /**
     *Method to fetch buildings from database
     * @return buildingList
     */
    public List<Building> buildingsList() {
        buildingsList.clear();

        try {

            Class.forName("com.mysql.jdbc.Driver");
            try (Connection con = DriverManager.getConnection("jdbc:mysql://localhost/campus_tour", "root", "")) {
                Statement st = con.createStatement();
                ResultSet rs = st.executeQuery("select building_name from building where university_id=(select university_id from university where university_name = '" + session.getAttribute("university") + "')");
                while (rs.next()) {
                    Building build = new Building();
                    build.setBuildingName(rs.getString("building_name"));
                    
                    buildingsList.add(build);
                    
                }
            }
        } catch (ClassNotFoundException | SQLException e) {

        }
        return buildingsList;
    }

    
}
