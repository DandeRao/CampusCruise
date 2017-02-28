/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Named;
import javax.enterprise.context.Dependent;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 *
 * @author Rakesh Chitturi
 */
@Named(value = "building")
@ManagedBean
public class Building {
    List<Building> buildingsList = new ArrayList<Building>() {};
    HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
        HttpSession session = request.getSession();
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
    public String addBuilding(){
        try{
                
                Class.forName("com.mysql.jdbc.Driver");
                Connection con = DriverManager.getConnection("jdbc:mysql://localhost/campus_tour","root","");
                
                Statement st = con.createStatement();
                ResultSet rs = st.executeQuery("select count(*) from building");
                rs.next();
                int count = rs.getInt(1);
               ResultSet result = st.executeQuery("select university_id from university where university_name = '"+session.getAttribute("university")+"'");
               result.next();
               int id = result.getInt("university_id");
                st.executeUpdate("insert into building values('"+(count+1)+"','"+this.getBuildingName()+"','"+this.buildingDescription+"','"+this.buildingImage+"','"+this.getBuildingVideo()+"','"+this.getBuildingLattitude()+"','"+this.getBuildingLongitude()+"','"+id+"')");
                
        }
        catch(Exception e){
            
        }
        return "inserted";
    }
    public List<Building> buildingsList(){
        buildingsList.clear();
        
        try{
                
                Class.forName("com.mysql.jdbc.Driver");
                Connection con = DriverManager.getConnection("jdbc:mysql://localhost/campus_tour","root","");
                
                Statement st = con.createStatement();
                ResultSet rs = st.executeQuery("select building_name from building where university_id=(select university_id from university where university_name = '"+session.getAttribute("university")+"')");
                while(rs.next()){
                   Building build = new Building();
                   build.setBuildingName(rs.getString("building_name"));
                   
                   buildingsList.add(build);
                   
                   
                }
                con.close();
        }catch(Exception e){
            
        }
        return buildingsList;
    }
}
