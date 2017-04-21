package com.campusTour;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import static com.sun.org.apache.xalan.internal.xsltc.compiler.util.Type.Int;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javax.inject.Named;
import javax.faces.bean.ManagedBean;
import javax.faces.context.FacesContext;
import javax.servlet.ServletException;
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

    /**
     *To get building list
     * @return
     */
    public List<Building> getBuildingsList() {
        buildingsList = this.buildingsLists();
        return buildingsList;
    }

    /**
     *To set the buildings
     * @param buildingsList
     */
    public void setBuildingsList(List<Building> buildingsList) {
        this.buildingsList = buildingsList;
    }

    //List to store fetched buildings from database
    List<Building> buildingsList = new ArrayList<Building>() {
    };
    //Getting the request made
    HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
    //created seesion
    HttpSession session = request.getSession();
    //Images of building 
    private List<Part> buildingImage = new ArrayList<Part>();
    //viodeos of building
    private List<Part> buildingVideo = new ArrayList<Part>();
    private Map<String,Integer> buildingTourPosition = new TreeMap<>();
    //Name of the building
    private String buildingName;
    //Description of the building
    private String buildingDescription;
    //Audios of the building
    private List<Part> buildingAudio = new ArrayList<Part>();
    //Building Oject
    private Building tempBuilding;
    //Building coordinates
    private String buildingCordinates;
    //Error message
    private String outputMessage;
    //Optional building
    private String isOptional;

    /**
     *To get the building status
     * @return
     */
    public String getIsOptional() {
        return isOptional;
    }

    /**
     *To set whether building is optional
     * @param isOptional
     */
    public void setIsOptional(String isOptional) {
        this.isOptional = isOptional;
    }
    //Tour position
    private int tourPosition;
    //List of building positions
    private List<Integer> tourPositions = new ArrayList<>();

    /**
     *To get tour positions
     * @return ListOF building position
     */
    public List<Integer> getTourPositions() {
        tourPositions.clear();
        for (int i = 1; i <= this.getBuildingsList().size(); i++) {
            tourPositions.add(i);
        }
        return tourPositions;
    }

    /**
     *to set the tour positions 
     * @param tourPositions
     */
    public void setTourPositions(List<Integer> tourPositions) {
        for (int i = 1; i <= this.getBuildingsList().size(); i++) {
            tourPositions.add(i);
        }
    }

    /**
     *to get the building position
     * @return int
     */
    public int getTourPosition() {
        return tourPosition;
    }

    /**
     *to set the building position
     * @param tourPosition 
     */
    public void setTourPosition(int tourPosition) {
        this.tourPosition = tourPosition;
    }

    /**
     *to get error message
     * @return String
     */
    public String getOutputMessage() {
        return outputMessage;
    }

    /**
     *To set error message
     * @param outputMessage
     */
    public void setOutputMessage(String outputMessage) {
        this.outputMessage = outputMessage;
    }

    /**
     *to get building coordinates
     * @return building coordinates
     */
    public String getBuildingCordinates() {
        return buildingCordinates;
    }

    /**
     *To set building coordinates
     * @param buildingCordinates
     */
    public void setBuildingCordinates(String buildingCordinates) {
        this.buildingCordinates = buildingCordinates;
    }

    /**
     * Method that gets the building audio
     *
     * @return buildingAudio
     */
    public List<Part> getBuildingAudio() {
        return buildingAudio;
    }

    /**
     * Method that sets the image to buildingAudio
     *
     * @param buildingAudio audio of the building
     *
     */
    public void setBuildingAudio(List<Part> buildingAudio) {
        this.buildingAudio = buildingAudio;
    }

    /**
     * Method that gets the building image
     *
     * @return buildingImage
     */
    public List<Part> getBuildingImage() {
        return buildingImage;
    }

    /**
     * Method that sets the image to buildingImage
     *
     * @param buildingImage image of the building
     *
     */
    public void setBuildingImage(List<Part> buildingImage) {
        this.buildingImage = buildingImage;
    }

    /**
     * Method that gets the building video
     *
     * @return buildingVideo
     */
    public List<Part> getBuildingVideo() {
        return buildingVideo;
    }

    /**
     * Method that sets the video to buildingVideo
     *
     * @param buildingVideo vide of the building
     */
    public void setBuildingVideo(List<Part> buildingVideo) {
        this.buildingVideo = buildingVideo;
    }

    /**
     * Method that gets the buildingName
     *
     * @return buildingName
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
     * Method to add buildings to the database
     *
     * @return string
     */
    public String addBuilding() {

        try {
            Class.forName("com.mysql.jdbc.Driver");
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost/campustour", "root", "Teamc@1234");
            Statement st = con.createStatement();
            ResultSet result = st.executeQuery("select university_id from universities where university_name = '" + session.getAttribute("university") + "'");
            result.next();
            int id = result.getInt("university_id");
            ResultSet rs = st.executeQuery("select count(*) from buildings where building_name='" + this.getBuildingName() + "' and university_id='" + id + "'");
            rs.next();
            int count = rs.getInt(1);
            if (count != 0) {
                outputMessage = "Building Name already exists";
                return "";
            }

            String path = "C:\\CampusTourFiles\\RawFiles";
            String univ = ((String) session.getAttribute("university")).replace(" ", "");
            String path1 = path + "\\" + univ;
            String path0 = path1 + "\\" + (this.getBuildingName()).replaceAll(" ", "");
            String path2 = path0 + "\\Images";
            String path3 = path0 + "\\Audio";
            String path4 = path0 + "\\Video";

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
            InputStream fp = null;
            byte[] data;
            FileOutputStream fout;
            for (int i = 0; i < buildingImage.size(); i++) {
                fp = buildingImage.get(i).getInputStream();
                data = new byte[fp.available()];
                fp.read(data);
                fout = new FileOutputStream(new File(path2 + "\\\\" + this.buildingName.replaceAll(" ", "") + i + new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss.SSSSSSSSSSS").format(new Date()) + "." + buildingImage.get(i).getContentType().split("/")[1])); //+buildingImage.getContentType().split("/")[1]));
                fout.write(data);
                fp.close();
                fout.close();
            }
            for (int i = 0; i < buildingAudio.size(); i++) {

                fp = buildingAudio.get(i).getInputStream();
                data = new byte[fp.available()];
                fp.read(data);
                fout = new FileOutputStream(new File(path3 + "\\\\" + this.buildingName.replaceAll(" ", "") + i + new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss.SSSSSSSSSSS").format(new Date()) + "." + "3gpp"));//+buildingImage.getContentType().split("/")[1]));
                fout.write(data);
                fp.close();
                fout.close();
            }
            for (int i = 0; i < buildingVideo.size(); i++) {
                fp = buildingVideo.get(i).getInputStream();
                data = new byte[fp.available()];
                fp.read(data);
                fout = new FileOutputStream(new File(path4 + "\\\\" + this.buildingName.replaceAll(" ", "") + i + new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss.SSSSSSSSSSS").format(new Date()) + "." + "mp4"));//+buildingImage.getContentType().split("/")[1]));
                fout.write(data);
                fp.close();
                fout.close();

            }
            int checkBuilding = 0;
            int isInTour = 0;
            if(isOptional.equals("Yes")){
                checkBuilding = -1;
                isInTour = 0;
            } else {
                isInTour = 1;
                rs = st.executeQuery("select max(tour_position) from buildings where university_id = '"+id+"'");
                rs.next();
                checkBuilding = rs.getInt(1);
                checkBuilding+=1;

            }

            rs = st.executeQuery("select max(building_id) from buildings");
            rs.next();
            count = rs.getInt(1);
            st.executeUpdate("insert into buildings values ('" + (count + 1) + "','" + this.getBuildingName() + "','" + this.getBuildingCordinates() + "','" + id + "','" + 1 + "','" + checkBuilding + "','" + isInTour + "')");
            String OUTPUT_ZIP_FILE = "C:\\CampusTourFiles\\ZipFiles\\" + session.getAttribute("university").toString().replaceAll(" ", "") + "\\" + buildingName.replace(" ", "") + ".zip";
            String SOURCE_FOLDER = "C:\\CampusTourFiles\\RawFiles\\" + session.getAttribute("university").toString().replaceAll(" ", "") + "\\" + buildingName.replace(" ", "");

            AppZip appZip = new AppZip(OUTPUT_ZIP_FILE, SOURCE_FOLDER);

            appZip.generateFileList(
                    new File(SOURCE_FOLDER));
            appZip.zipIt(OUTPUT_ZIP_FILE);

        } catch (ClassNotFoundException | SQLException | IOException e) {

        }
        return "inserted";
    }

    /**
     *To set the building position in tour
     * @param b
     * @param a
     */
    public void setTourPositions(Building b,int a){
        buildingTourPosition.put(buildingName, a);
    }

    /**
     *To update tour position
     * @return String to redirect page
     */
    public String updateTourPosition() {
        for(int i=0;i<buildingsList.size();i++){
            try {
            Class.forName("com.mysql.jdbc.Driver");
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost/campustour", "root", "Teamc@1234");
            Statement st = con.createStatement();
            ResultSet result = st.executeQuery("select university_id from universities where university_name = '" + session.getAttribute("university") + "'");
            result.next();
            int id = result.getInt("university_id");
            
            result = st.executeQuery("select building_id from buildings where building_name='" + buildingsList.get(i).buildingName + "' and university_id='" + id + "'");
            result.next();
            int a = result.getInt(1);
            st = con.createStatement();
            //st.executeUpdate("update buildings set building_name = '" + this.getBuildingName() +  "',building_coordinates='" + this.getBuildingCordinates() + "' where building_id='" + a + "' and university_id='" + universityId + "'");
            st.executeUpdate("update buildings set tour_position="+buildingsList.get(i).tourPosition+" where building_id = "+a+"");
            } catch (ClassNotFoundException ex) {
                Logger.getLogger(Building.class.getName()).log(Level.SEVERE, null, ex);
            } catch (SQLException ex) {
                Logger.getLogger(Building.class.getName()).log(Level.SEVERE, null, ex);
            }
            
        }
        return "home";
    }

    /**
     * Method to fetch buildings from database
     *
     * @return buildingList
     */
    public List<Building> buildingsLists() {
        buildingsList.clear();

        try {

            Class.forName("com.mysql.jdbc.Driver");
            try (Connection con = DriverManager.getConnection("jdbc:mysql://localhost/campustour", "root", "Teamc@1234")) {
                Statement st = con.createStatement();
                ResultSet rs = st.executeQuery("select * from buildings where university_id=(select university_id from universities where university_name = '" + session.getAttribute("university") + "')");
                while (rs.next()) {
                    Building build = new Building();
                    build.setBuildingName(rs.getString("building_name"));
                    build.setTourPosition(rs.getInt("tour_position"));

                    build.setBuildingCordinates(rs.getString("building_coordinates"));

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
        this.setBuildingCordinates(tempBuilding.getBuildingCordinates());

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
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost/campustour", "root", "Teamc@1234");
            Statement st = con.createStatement();
            String path = "C:\\CampusTourFiles\\RawFiles";
            String univ = ((String) session.getAttribute("university")).replace(" ", "");
            String path1 = path + "\\" + univ;
            String path0 = path1 + "\\" + (buildingName).replaceAll(" ", "");
            File folder = new File(path0);
            if (folder.exists()) {

                if (deleteDir(folder)) {

                }

            }
            ResultSet rs = st.executeQuery("select university_id from universities where university_name = '" + session.getAttribute("university") + "'");
            rs.next();
            String universityId = rs.getString("university_id");
            st.executeUpdate("delete from buildings where building_name = '" + buildingName + "' and university_id='" + universityId + "'");

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
            String path = "C:\\CampusTourFiles\\ZipFiles";
            String univ = ((String)session.getAttribute("university")).replace(" ","");
            String path1 = path + "\\" + univ;
            String path0 = path1 + "\\" + (this.getBuildingName()).replaceAll(" ", "");
            File folder = new File(path0+"\\"+buildingName.replaceAll(" ", "")+"\\Images");
            Class.forName("com.mysql.jdbc.Driver");
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost/campustour", "root", "Teamc@1234");
            Statement st = con.createStatement();
            ResultSet rs = st.executeQuery("select university_id from universities where university_name = '" + session.getAttribute("university") + "'");
            rs.next();
            String universityId = rs.getString("university_id");
            
             rs = st.executeQuery("select building_id from buildings where building_name='" + session.getAttribute("editbuildingName") +"' and university_id='" + universityId + "' "); 
             rs.next(); 
             int a = rs.getInt("building_id"); 
             rs = st.executeQuery("select count(*) from buildings where building_name='" + this.getBuildingName() + "' and building_id!='" + a + "'"); 
            rs.next(); 
             int count = rs.getInt(1); 
             if (count != 0) { 
                 outputMessage = "Building Name already exists"; 
                 return ""; 
             } 
             if (this.buildingName != session.getAttribute("editBuildingName")) {
                new File("C:\\CampusTourFiles\\RawFiles\\" + session.getAttribute("editBuildingName") + "\\" + session.getAttribute("editBuildingName")).renameTo(new File("C:\\CampusTourFiles\\RawFiles\\" + session.getAttribute("editBuildingName") + "\\" + this.getBuildingName()));
                new File("C:\\CampusTourFiles\\RawFiles\\" + session.getAttribute("editBuildingName")).renameTo(new File("C:\\CampusTourFiles\\RawFiles\\" + this.getBuildingName()));
                new File("C:\\CampusTourFiles\\ZipFiles\\" + session.getAttribute("editBuildingName") + "\\" + session.getAttribute("editBuildingName") + ".zip").renameTo(new File("C:\\CampusTourFiles\\ZipFiles\\" + session.getAttribute("editBuildingName") + "\\" + this.getBuildingName()+ ".zip"));
                new File("C:\\CampusTourFiles\\ZipFiles\\" + session.getAttribute("editBuildingName")).renameTo(new File("C:\\CampusTourFiles\\ZipFiles\\" + this.getBuildingName()));

            }
            
             rs = st.executeQuery("select building_id from buildings where building_name='" + session.getAttribute("editbuildingName") + "' and university_id='" + universityId + "'");
            rs.next();
             a = rs.getInt("building_id");
            
            
            
            
            st.executeUpdate("update buildings set building_name = '" + this.getBuildingName() +  "',building_coordinates='" + this.getBuildingCordinates() + "' where building_id='" + a + "' and university_id='" + universityId + "'");

        } catch (ClassNotFoundException | SQLException e) {

        }
        return "updated";
    }
    /**
     *To delete directory
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

    /**
     *
     * @param part The uploaded file
     * @return parts collections
     * @throws ServletException
     * @throws IOException
     */
    public static Collection<Part> getAllParts(Part part) throws ServletException, IOException {
        HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
        return request.getParts().stream().filter(p -> part.getName().equals(p.getName())).collect(Collectors.toList());
    }

}
