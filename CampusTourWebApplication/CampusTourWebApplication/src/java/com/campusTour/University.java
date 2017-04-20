package com.campusTour;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import java.io.File;
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

/**
 *
 * @author Rakesh Chitturi
 */
@Named(value = "university")
@ManagedBean
public class University {
    //List to store university
    List<University> universityList = new ArrayList<>();
    private  University tempUniversity;


    
    /**
     * Creates a new instance of University
     */
    public University() {

    }
    public String outputMessage;

    public String getOutputMessage() {
        return outputMessage;
    }

    public void setOutputMessage(String outputMessage) {
        this.outputMessage = outputMessage;
    }
    
    //Getting the current instance using face context
    HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
    //created seesion
    HttpSession session = request.getSession();

    String univName;
    //user to admin university
    private String user;

    /**
     * Method that gets the password
     *
     * @return password of user
     */
    public String getPassword() {
        return password;
    }

    /**
     * Method that sets the password
     *
     * @param password password of the user
     */
    public void setPassword(String password) {
        this.password = password;
    }
    private String password;
    private double universityLattitude;
    private double universityLongitude;

    /**
     * Method that gets the user
     *
     * @return user
     */
    public String getUser() {
        return user;
    }

    /**
     * Method that sets the user
     *
     * @param user email of the user
     */
    public void setUser(String user) {
        this.user = user;
    }

    

    /**
     * Method that sets the university name
     *
     * @param univName name of the university
     */
    public void setUnivName(String univName) {
        this.univName = univName;
    }

    /**
     * Method that gets the university name
     *
     * @return universityName
     */
    public String getUnivName() {
        return this.univName;
    }

    /**
     * Method that add the university to database
     *
     * @return string
     */
    public String addUniversity() {
        try {
            

            Class.forName("com.mysql.jdbc.Driver");
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost/campus_tour", "root", "");

            Statement st = con.createStatement();
            
            
            ResultSet r = st.executeQuery("select count(*) from university where university_name='" + this.getUnivName() + "'");
            r.next();
            int number = r.getInt(1);
            if (number != 0) {
            
            outputMessage="University Already exists";
            return "";
            
            }
            
            
            
            
            
            
            r = st.executeQuery("select count(*) from user where email='" + this.getUser() + "'");
            r.next();
             number = r.getInt(1);
            if (number == 0) {
                st.executeUpdate("insert into user values('" + this.getUser() + "','" + this.getPassword() + "','user')");
                ResultSet rs = st.executeQuery("select max(university_id) from university");
                rs.next();
                int count = rs.getInt(1);

                st.executeUpdate("insert into university values('" + (count + 1) + "','" + this.getUnivName() + "','" + this.getUser() + "', '" + this.getUniversityLattitude() + "','" + this.getUniversityLongitude() + "')");
                String path = "C:\\CampusTourFiles\\RawFiles";
                String univ = (this.getUnivName()).replace(" ","");
                path = path + "\\" + univ;
                File folder = new File(path);
                if (!folder.exists()) {
                    folder.mkdir();
                }
            } else {
                outputMessage="Email is already assigned to a university please give another email";
                return "";
            }
        } catch (ClassNotFoundException | SQLException e) {

        }
        return "inserted";
    }

    /**
     * Methods that updates university details
     *
     * @return string
     */
    public String updateUniversity() {
        
        try {
            
            Class.forName("com.mysql.jdbc.Driver");
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost/campus_tour", "root", "");
            Statement st = con.createStatement();
            
            ResultSet rs = st.executeQuery("select university_id from university where university_name='"+session.getAttribute("editUnivName")+"'");
            rs.next();
            int a = rs.getInt("university_id");
            ResultSet r = st.executeQuery("select count(*) from university where university_name='"+this.getUnivName()+"' and university_id!='" + a + "'");
            r.next();
            int number = r.getInt(1);
            if (number != 0) {
            
            outputMessage="University Already exists";
            return "";
            
            }
            
            st.executeUpdate("update university set university_name = '"+this.getUnivName()+"',university_lattitude = '"+this.getUniversityLattitude()+"',university_longitude= '"+this.getUniversityLongitude()+"' where university_id='"+a+"'");
            
        } catch (ClassNotFoundException | SQLException e) {
            
        }
        return "updated";
    }

    /**
     * Method to set university name to session
     *
     * @param university object of the university
     * @return string
     */
    public String getUniversityDetails(University university) {
        session.setAttribute("editUnivName", university.univName);
        session.setAttribute("editEmail", university.user);
        tempUniversity = university;
        this.setUnivName(tempUniversity.univName);
        this.setUser(tempUniversity.user);
        this.setUniversityLongitude(tempUniversity.universityLongitude);
        this.setUniversityLattitude(tempUniversity.universityLattitude);
        
        System.out.println(tempUniversity);
        return "edit";
    }

    /**
     *Gets the latitude of university
     * @return double value which is latitude
     */
    public double getUniversityLattitude() {
        return universityLattitude;
    }

    /**
     *sets the latitude of university
     * @param universityLattitude is set
     */
    public void setUniversityLattitude(double universityLattitude) {
        this.universityLattitude = universityLattitude;
    }

    /**
     *gets the university longitude
     * @return longitude of university
     */
    public double getUniversityLongitude() {
        return universityLongitude;
    }

    /**
     *sets the longitude of university
     * @param universityLongitude which is double value
     */
    public void setUniversityLongitude(double universityLongitude) {
        this.universityLongitude = universityLongitude;
    }

 
    /**
     * This method is used to delete university details from database
     *
     * @param universityName name of the university
     * @return string
     */
    public String deleteUniversityDetails(String universityName) {

        try {
            Class.forName("com.mysql.jdbc.Driver");
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost/campus_tour", "root", "");
            Statement st = con.createStatement();
            ResultSet rs = st.executeQuery("select email from university where university_name = '" + universityName + "'");
            rs.next();
            String email = rs.getString("email");
            String path = "C:\\CampusTourFiles\\RawFiles";
                String univ = (universityName).replace(" ","");
                path = path + "\\" + univ;
            File folder = new File(path);
            if (folder.exists()) {
                deleteDir(folder);
            }
            st.executeUpdate("delete from user where email = '" + email + "'");

        } catch (ClassNotFoundException | SQLException e) {

        }
        return "delete";
    }

    /**
     *Returns the list of university
     * @returnblist of university
     */
    public List<University> universityList() {
        universityList.clear();
        try {

            Class.forName("com.mysql.jdbc.Driver");
            try (Connection con = DriverManager.getConnection("jdbc:mysql://localhost/campus_tour", "root", "")) {
                Statement st = con.createStatement();
                ResultSet rs = st.executeQuery("select * from university");
                while (rs.next()) {
                    University un = new University();
                    un.setUnivName(rs.getString("university_name"));
                    un.setUniversityLattitude(rs.getDouble("university_lattitude"));
                    un.setUniversityLongitude(rs.getDouble("university_longitude"));
                    un.setUser(rs.getString("email"));
                    universityList.add(un);
                    
                }
            }
        } catch (ClassNotFoundException | SQLException e) {
            //con.close();
        } finally {

        }
        return universityList;
    }
    
    /**
     *Delete the file
     * @param file
     * @return  boolean whether file is deleted or not
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
