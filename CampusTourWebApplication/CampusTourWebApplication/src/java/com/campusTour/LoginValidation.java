package com.campusTour;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import java.io.IOException;
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
import javax.servlet.http.HttpSession;

/**
 *
 * @author Rakesh Chitturi
 */
@Named(value = "loginValidation")
@ManagedBean
public class LoginValidation {

    //created seesion
    HttpSession session = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(true);

    /**
     * Creates a new instance of LoginValidation
     */
    public LoginValidation() {
    }
    //Email of the user
    private String email;
    //password of the user
    private String password;
    //Error message to show
    private String outputMessage;
    //university name allocated to user
    private String universityName;

    /**
     * Method that gets the universityName
     *
     * @return universityName
     */
    public String getUniversityName() {
        return universityName;
    }

    /**
     * Method that sets the universityName
     *
     * @param universityName name of the university
     */
    public void setUniversityName(String universityName) {
        this.universityName = universityName;
    }

    /**
     * Method that gets the outputMessage
     *
     * @return
     */
    public String getOutputMessage() {
        return outputMessage;
    }

    /**
     * Method that sets the universityName
     *
     * @param outputMessage error message to the user
     */
    public void setOutputMessage(String outputMessage) {
        this.outputMessage = outputMessage;
    }

    /**
     * Method that gets the email
     *
     * @return email;
     */
    public String getEmail() {
        return email;
    }

    /**
     * Method that sets the email
     *
     * @param email email of the user
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Method that gets the password
     *
     * @return password
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

    /**
     * Method that validates username and password
     *
     * @return string
     */
    public String validatingUsernamePassword() {

        try {

            Class.forName("com.mysql.jdbc.Driver");
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost/campus_tour", "root", "");

            Statement st = con.createStatement();
            String a = this.email;
            String b = this.password;

            ResultSet result = st.executeQuery("select count(*) count,role from user where email='" + a + "' and password ='" + b + "'");

            while (result.next()) {
                String count = result.getString("count");
                String role = result.getString("role");

                if (Integer.parseInt(count) == 1) {
                    if ("admin".equals(role)) {
                        outputMessage = "";

                        session.setAttribute("name", email);
                        return "admin";
                    } else {
                        outputMessage = "";
                        ResultSet rs = st.executeQuery("select university_name from university where email = '" + a + "'");
                        rs.next();
                        session.setAttribute("name", email);

                        this.setUniversityName(rs.getString("university_name"));
                        session.setAttribute("university", this.getUniversityName());
                        return "user";
                    }
                } else {
                    outputMessage = "Username and Password don't match";
                    return "";
                }
            }

        } catch (Exception e) {
            outputMessage = "Error in connecting to database";
        }
        return "";

    }

    /**
     * Method to logout
     *
     * @return string
     */
    public String logout() {
        session.invalidate();
        return "loggedOut";
    }
    
    /**
     *Array list to store users
     */
    public List<LoginValidation> userList = new ArrayList<LoginValidation>() {};

    /**
     *retriving the list of users
     * @return list of users in database
     */
    public List<LoginValidation> userList() {
        userList.clear();
        try {

            Class.forName("com.mysql.jdbc.Driver");
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost/campus_tour", "root", "");

            Statement st = con.createStatement();
            ResultSet rs = st.executeQuery("select email,password from user");
            while (rs.next()) {
                LoginValidation ln = new LoginValidation();
                ln.setEmail(rs.getString("email"));
                ln.setPassword(rs.getString("password"));
                userList.add(ln);
            }
        } catch (Exception e) {

        }
        System.out.println(userList.isEmpty());
        return userList;
    }

}
