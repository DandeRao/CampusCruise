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
@Named(value = "university")
@RequestScoped
public class University {

    /**
     * Creates a new instance of University
     */
    public University() {
    }
    public String univName;
    public String user;
    public int universityLattitude;
    public int universityLongitude;

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public int getUniversityLattitude() {
        return universityLattitude;
    }

    public void setUniversityLattitude(int universityLattitude) {
        this.universityLattitude = universityLattitude;
    }

    public int getUniversityLongitude() {
        return universityLongitude;
    }

    public void setUniversityLongitude(int universityLongitude) {
        this.universityLongitude = universityLongitude;
    }
   
    
    public void setUnivName(String univName){
        this.univName = univName;
    }
    public String getUnivName(){
        return this.univName;
    }
    
    public String addUniversity(){
        try{
                
                Class.forName("com.mysql.jdbc.Driver");
                Connection con = DriverManager.getConnection("jdbc:mysql://localhost/campus_tour","root","");
                
                Statement st = con.createStatement();
               // ResultSet rs = st.executeQuery("select count(*) from university");
                //rs.next();
                int count = 5;//rs.getInt(1);
                st.executeUpdate("insert into university values('"+(count+1)+"','"+this.getUnivName()+"','"+this.getUser()+"','"+this.getUniversityLattitude()+"','"+this.getUniversityLongitude()+"')");
                
        }
        catch(Exception e){
            
        }
        return "inserted";
    }
    
}

