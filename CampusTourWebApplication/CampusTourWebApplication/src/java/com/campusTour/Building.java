package com.campusTour;

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
    //Getting the request made
    HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
    //created seesion
    HttpSession session = request.getSession();
    //Images of building 
    private Part buildingImage;
    //viodeos of building
    private Part buildingVideo;
    //Lattitude of the building
    private double buildingLattitude;
    //Longitude of the building
    private double buildingLongitude;
    //Name of the building
    private String buildingName;
    //Description of the building
    private String buildingDescription;
    //Audios of the building
    private Part buildingAudio;
    //Building Oject
    private Building tempBuilding;

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
     *
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
    public double getBuildingLattitude() {
        return buildingLattitude;
    }

    /**
     * Method that sets the building latitude
     *
     * @param buildingLattitude latitude of the building
     */
    public void setBuildingLattitude(double buildingLattitude) {
        this.buildingLattitude = buildingLattitude;
    }

    /**
     * Method that gets the building longitude
     *
     * @return buildingLongitude
     */
    public double getBuildingLongitude() {
        return buildingLongitude;
    }

    /**
     * Method that sets the building longitude
     *
     * @param buildingLongitude longitude of the building
     */
    public void setBuildingLongitude(double buildingLongitude) {
        this.buildingLongitude = buildingLongitude;
    }

    /**
     * Method to add buildings to the database
     *
     * @return string
     */
    public String addBuilding() {
        

        

        try {
            Class.forName("com.mysql.jdbc.Driver");
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost/campus_tour", "root", "");
            Statement st = con.createStatement();
            String path = "C:\\CampusTourFiles\\RawFiles";
            String univ = ((String)session.getAttribute("university")).replace(" ","");
            String path1 = path + "\\" + univ;
            String path0 = path1 + "\\" + (this.getBuildingName()).replaceAll(" ", "");
            String path2 = path0+"\\Images";
            String path3 = path0+"\\Audio";
            String path4 = path0+"\\Video";

            File folder = new File(path1);
            File folder0 = new File(path0);
            File folder2 = new File(path2);
            File folder3 = new File(path3);
            File folder4 = new File(path4);
            if (folder.exists()) {
                folder0.mkdir();
                folder2.mkdir();
                folder3.mkdir();
                folder4.mkdir();
            } else {
                folder.mkdir();
                folder0.mkdir();
                folder2.mkdir();
                folder3.mkdir();
                folder4.mkdir();
            }
            InputStream fp = buildingImage.getInputStream();
            
            byte[] data = new byte[fp.available()];
            fp.read(data);
            FileOutputStream fout = new FileOutputStream(new File(path2 + "\\" + this.buildingName.replaceAll(" ", "") + "Image."+buildingImage.getContentType().split("/")[1]));
            fout.write(data);
            fp = buildingAudio.getInputStream();
            data = new byte[fp.available()];
            fp.read(data);
            fout = new FileOutputStream(new File(path3 + "\\" + this.buildingName.replaceAll(" ", "") + "Audio."+buildingAudio.getContentType().split("/")[1]));
            fout.write(data);
            fp = buildingVideo.getInputStream();
            data = new byte[fp.available()];
            fp.read(data);
            fout = new FileOutputStream(new File(path4 + "\\" + this.buildingName.replaceAll(" ", "") + "Video."+buildingVideo.getContentType().split("/")[1]));
            fout.write(data);
            fp.close();
            fout.close();
            ResultSet rs = st.executeQuery("select count(*) from building");
            rs.next();
            int count = rs.getInt(1);
            ResultSet result = st.executeQuery("select university_id from university where university_name = '" + session.getAttribute("university") + "'");
            result.next();
            int id = result.getInt("university_id");
            //String word = pathString(path0.split("\\"));
            String imageAddress = path0 + "\\Images\\" + this.buildingName.replaceAll(" ", "") + "Image";
            String audioAddress = path0 + "\\Audio\\" + this.buildingName.replaceAll(" ", "") + "Audio";
            String videoAddress = path0 + "\\Video\\" + this.buildingName.replaceAll(" ", "") + "Video";
            st.executeUpdate("insert into building values('" + (count + 1) + "','" + this.getBuildingName() + "','" + this.buildingDescription + "','" + imageAddress + "','" + audioAddress + "','" + videoAddress + "','" + this.getBuildingLattitude() + "','" + this.getBuildingLongitude() + "','" + id + "')");

            
        } catch (ClassNotFoundException | SQLException | IOException e) {

        }
        return "inserted";
    }

    /**
     * Method to fetch buildings from database
     *
     * @return buildingList
     */
    public List<Building> buildingsList() {
        buildingsList.clear();

        try {

            Class.forName("com.mysql.jdbc.Driver");
            try (Connection con = DriverManager.getConnection("jdbc:mysql://localhost/campus_tour", "root", "")) {
                Statement st = con.createStatement();
                ResultSet rs = st.executeQuery("select * from building where university_id=(select university_id from university where university_name = '" + session.getAttribute("university") + "')");
                while (rs.next()) {
                    Building build = new Building();
                    build.setBuildingName(rs.getString("building_name"));
                    build.setBuildingDescription(rs.getString("building_description"));
                    build.setBuildingLattitude(rs.getDouble("building_lattitude"));
                    build.setBuildingLongitude(rs.getDouble("building_longitude"));
                    String imageLocation = rs.getString("building_image");
                    String audioLocation = rs.getString("building_audio");
                    String videoLocation = rs.getString("building_video");
                    buildingsList.add(build);

                }
            }
        } catch (ClassNotFoundException | SQLException e) {

        }
        return buildingsList;
    }

    /**
     * This method is used to set the session attribute edit building name
     *
     * @param building object of the building
     * @return string
     */
    public String getBuildingDetails(Building building) {
        session.setAttribute("editbuildingName", building.buildingName);
        tempBuilding = building;
        this.setBuildingName(tempBuilding.getBuildingName());
        this.setBuildingDescription(tempBuilding.getBuildingDescription());
        this.setBuildingAudio(tempBuilding.getBuildingAudio());
        this.setBuildingImage(tempBuilding.getBuildingImage());
        this.setBuildingVideo(tempBuilding.getBuildingVideo());
        this.setBuildingLattitude(tempBuilding.getBuildingLattitude());
        this.setBuildingLongitude(tempBuilding.getBuildingLongitude());
        return "edit";
    }


    /**
     * This method deletes the building details from database
     *
     * @param buildingName name of the building
     * @return string
     */
    public String deleteBuildingDetails(String buildingName) {

        try {
            Class.forName("com.mysql.jdbc.Driver");
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost/campus_tour", "root", "");
            Statement st = con.createStatement();
            String path = "C:\\CampusTourFiles\\RawFiles";
            String univ = ((String)session.getAttribute("university")).replace(" ","");
            String path1 = path + "\\" + univ;
            String path0 = path1 + "\\" + (buildingName).replaceAll(" ", "");
            File folder = new File(path0);
            if (folder.exists()) {

               if( deleteDir(folder)){
                   
               }

            }
            ResultSet rs = st.executeQuery("select university_id from university where university_name = '" + session.getAttribute("university") + "'");
            rs.next();
            String universityId = rs.getString("university_id");
            st.executeUpdate("delete from building where building_name = '" + buildingName + "' and university_id='" + universityId + "'");

        } catch (Exception e) {

        }
        return "delete";
    }

    /**
     * Methods that updates university details
     *
     * @return string
     * @throws java.io.IOException
     */
    public String updateBuilding() throws IOException {

        try {
            String path = "C:\\CampusTourFiles\\RawFiles";
            String univ = ((String)session.getAttribute("university")).replace(" ","");
            String path1 = path + "\\" + univ;
            String path0 = path1 + "\\" + (this.getBuildingName()).replaceAll(" ", "");
            File folder = new File(path0+"\\Images\\"+buildingName.replaceAll(" ", "")+"Image");
            File folder1 = new File(path0+"\\Audio\\"+buildingName.replaceAll(" ", "")+"Audio");
            File folder2 = new File(path0+"\\Video\\"+buildingName.replaceAll(" ", "")+"Video");
            Class.forName("com.mysql.jdbc.Driver");
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost/campus_tour", "root", "");
            Statement st = con.createStatement();
            ResultSet rs = st.executeQuery("select building_id from building where building_name='" + session.getAttribute("editbuildingName") + "'");
            rs.next();
            int a = rs.getInt("building_id");
            if (this.getBuildingImage() != null) {
                if(folder.exists()){
                deleteDir(folder);
                }
                InputStream fp = buildingImage.getInputStream();
                byte[] data = new byte[fp.available()];
                fp.read(data);
                FileOutputStream fout = new FileOutputStream(new File(path0 + "\\Images\\" + this.buildingName.replaceAll(" ", "") +"Image."+buildingImage.getContentType().split("/")[1]));
                fout.write(data);
            }
            if (this.getBuildingAudio() != null) {
                if(folder1.exists()){
                deleteDir(folder1);
                }
                InputStream fp = buildingAudio.getInputStream();
                byte[] data = new byte[fp.available()];
                fp.read(data);
                FileOutputStream fout = new FileOutputStream(new File(path0 + "\\Audio\\" + this.buildingName.replaceAll(" ", "") + "Audio."+buildingAudio.getContentType().split("/")[1]));
                fout.write(data);
            }
            if (this.getBuildingVideo() != null) {
                if(folder2.exists()){
                deleteDir(folder2);
                }
                InputStream fp = buildingVideo.getInputStream();
                byte[] data = new byte[fp.available()];
                fp.read(data);
                FileOutputStream fout = new FileOutputStream(new File(path0 + "\\Video\\" + this.buildingName.replaceAll(" ", "") + "Video."+buildingVideo.getContentType().split("/")[1]));
                fout.write(data);
            }
            st.executeUpdate("update building set building_name = '" + this.getBuildingName() + "',building_description='" + this.getBuildingDescription() + "',building_image='" + this.getBuildingImage() + "',building_audio='" + this.getBuildingAudio() + "',building_video='" + this.getBuildingVideo() + "',building_lattitude='" + this.getBuildingLattitude() + "',building_longitude='" + this.getBuildingLongitude() + "' where building_id='" + a + "'");

        } catch (ClassNotFoundException | SQLException e) {

        }
        return "updated";
    }

    /**
     *
     * @param file that need to be deleted
     * @return whether the file is deleted or not
     */
    public boolean deleteDir(File file) {
        File[] contents = file.listFiles();
        if (contents != null) {
            for (File f : contents) {
                deleteDir(f);
            }
        }
        return file.delete();
    }

}
