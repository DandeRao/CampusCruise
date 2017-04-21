/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Named;
import javax.enterprise.context.Dependent;
import javax.enterprise.context.RequestScoped;

/**
 *
 * @author Rakesh Chitturi
 */
@Named(value = "databaseConnection")
@RequestScoped
public class databaseConnection {
    List<University> universityList = new ArrayList<University>() {};
    List<Building> buildingsList = new ArrayList<Building>() {};
    /**
     * Creates a new instance of databaseConnection
     */
    public databaseConnection() {
    }
    
    public List<University> universityList(){
        
        try{
                
                Class.forName("com.mysql.jdbc.Driver");
                Connection con = DriverManager.getConnection("jdbc:mysql://localhost/campus_tour","root","");
                
                Statement st = con.createStatement();
                ResultSet rs = st.executeQuery("select * from university");
                while(rs.next()){
                   University un = new University();
                   un.setUnivName(rs.getString("university_name"));
                   un.setUniversityLattitude(rs.getInt("university_lattitude"));
                   un.setUniversityLongitude(rs.getInt("university_longitude"));
                   un.setUser(rs.getString("email"));
                   universityList.add(un);
                   
                }
                con.close();
        }catch(Exception e){
            //con.close();
        }
        finally{
            
        }
        return universityList;
    }
    
    
    public List<Building> buildingsList(){
        
        try{
                
                Class.forName("com.mysql.jdbc.Driver");
                Connection con = DriverManager.getConnection("jdbc:mysql://localhost/campus_tour","root","");
                
                Statement st = con.createStatement();
                ResultSet rs = st.executeQuery("select building_Name from building");
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
    
    public String viewBuilding(String buildingName){
        try{
            Class.forName("com.mysql.jdbc.Driver");
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost/campus_tour","root","");
            Statement st = con.createStatement();
             ResultSet rs = st.executeQuery("select * from building where building_name = '"+buildingName+"'");
             while(rs.next()){
                 
             }
        }catch(Exception e){
            
        }
        return "viewBuilding";
    }
    
    
    
}
