/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import static java.lang.System.console;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
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
@Named(value = "databaseConnection")
@ManagedBean
public class databaseConnection {
    List<University> universityList = new ArrayList<University>() {};
    HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
        HttpSession session = request.getSession();
    /**
     * Creates a new instance of databaseConnection
     */
    public databaseConnection() {
    }
    
    public List<University> universityList(){
        universityList.clear();
        try{
                
                Class.forName("com.mysql.jdbc.Driver");
                Connection con = DriverManager.getConnection("jdbc:mysql://localhost/campus_tour","root","admin");
                
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
    
    public String getBuildingDetails(String buildingName){
        session.setAttribute("editbuildingName",buildingName);
        System.out.println(buildingName);
        return "edit";
    }
    public String deleteUniversityDetails(String universityName){
        session.setAttribute("deleteUnivName",universityName);
        return "delete";
    }
    
    public Building editBuildingDetails(){
        Building b = new Building();
        try{
            Class.forName("com.mysql.jdbc.Driver");
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost/campus_tour","root","admin");
            Statement st = con.createStatement();
            String a = (String) session.getAttribute("editbuildingName");
            ResultSet rs = st.executeQuery("select * from building where building_name = '"+a+"'");
            rs.next();
            b.setBuildingName(rs.getString("building_name"));
            b.setBuildingDescription(rs.getString("building_description"));
            b.setBuildingImage(rs.getString("building_image"));
            b.setBuildingVideo(rs.getString("building_video"));
            b.setBuildingLattitude(rs.getInt("building_lattitude"));
            b.setBuildingLongitude(rs.getInt("building_longitude"));
            con.close();
        }catch(Exception e){
            
        }
      //  session.removeAttribute("editUnivName");
        return b;
    }
    public String deleteUniversityDetails(){
        
        try{
            Class.forName("com.mysql.jdbc.Driver");
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost/campus_tour","root","admin");
            Statement st = con.createStatement();
            ResultSet rs = st.executeQuery("select email from university where university_name = '"+session.getAttribute("deleteUnivName")+"'");
            rs.next();
            String a = rs.getString("email");
            st.executeUpdate("delete  from user where email = '"+a+"'");
             
        }catch(Exception e){
            
        }
        return "delete";
    }
    
   public String deleteBuildingDetails(String buildingName){
        
        try{
            Class.forName("com.mysql.jdbc.Driver");
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost/campus_tour","root","");
            Statement st = con.createStatement();
            
            st.executeUpdate("delete from building where building_name = '"+buildingName+"'");
           // st.executeUpdate("delete from university where university_name='"+session.getAttribute("deleteUnivName")+"'");
             
        }catch(Exception e){
            
        }
        return "delete";
    }
    
    
    
}
