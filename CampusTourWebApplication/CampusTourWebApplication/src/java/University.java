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
@Named(value = "university")
@ManagedBean
public class University {
    HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
        HttpSession session = request.getSession();
        University universityToEdit = new University();
    /**
     * Creates a new instance of University
     */
    public University() {
    }
    String univName;
    private String user;

    public String getPassword() {
        return password;
    }

    public University getUniversityToEdit() {
        return universityToEdit;
    }

    public void setUniversityToEdit(University universityToEdit) {
        this.universityToEdit = universityToEdit;
    }

    public void setPassword(String password) {
        this.password = password;
    }
    private String password;
    private int universityLattitude;
    private int universityLongitude;

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
                 st.executeUpdate("insert into user values('"+this.getUser()+"','"+this.getPassword()+"','user')");
                ResultSet rs = st.executeQuery("select max(university_id) from university");
                rs.next();
                int count = rs.getInt(1);
               
                st.executeUpdate("insert into university values('"+(count+1)+"','"+this.getUnivName()+"','"+this.getUser()+"','"+this.getPassword()+"','"+this.getUniversityLattitude()+"','"+this.getUniversityLongitude()+"')");
                
        }
        catch(Exception e){
            
        }
        return "inserted";
    }
    public String updateUniversity(){
        
        try{
                
                Class.forName("com.mysql.jdbc.Driver");
                Connection con = DriverManager.getConnection("jdbc:mysql://localhost/campus_tour","root","");
                
                Statement st = con.createStatement();
                ResultSet rs = st.executeQuery("select email from university where university='"+session.getAttribute("editUnivName")+"'");
                rs.next();
                st.executeUpdate("delete from user where email = '"+rs.getString("email")+"'");
                  st.executeUpdate("insert into user values('"+this.getUser()+"','"+this.getPassword()+"','user')");
                ResultSet r = st.executeQuery("select max(university_id) from university");
                r.next();
                int count = r.getInt(1);
               
                st.executeUpdate("insert into university values('"+(count+1)+"','"+this.getUnivName()+"','"+this.getUser()+"','"+this.getPassword()+"','"+this.getUniversityLattitude()+"','"+this.getUniversityLongitude()+"')");
                
        }
        
        catch(Exception e){
            
        }
        return "updated";
    }
    public String getUniversityDetails(String universityName){
        session.setAttribute("editUnivName",universityName);
        System.out.println(universityName);
        
        return "edit";
    }
    public University editUniversityDetails(){
        University un = new University();
        try{
            Class.forName("com.mysql.jdbc.Driver");
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost/campus_tour","root","");
            Statement st = con.createStatement();
            String a = (String) session.getAttribute("editUnivName");
            ResultSet rs = st.executeQuery("select * from university where university_name = '"+a+"'");
            rs.next();
            un.setUnivName(rs.getString("university_name"));
            un.setUniversityLattitude(rs.getInt("university_lattitude"));
            un.setUniversityLongitude(rs.getInt("university_longitude"));
            un.setUser(rs.getString("email"));
            un.setPassword(rs.getString("password"));
            con.close();
        }catch(Exception e){
            
        }
        universityToEdit = un;
      //  session.removeAttribute("editUnivName");
        return un;
    }
}

