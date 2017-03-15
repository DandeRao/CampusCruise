/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import java.io.File;
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
    /**
     * Creates a new instance of University
     */
    public University() {
    
    }
    //Getting the current instance using face context
    HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
    //created seesion
    HttpSession session = request.getSession();
    
    String univName;
    //user to admin university
    private String user;

    /**
     *Method that gets the password
     * @return password of user
     */
    public String getPassword() {
        return password;
    }

    /**
     *Method that sets the password
     * @param password password of the user
     */
    public void setPassword(String password) {
        this.password = password;
    }
    private String password;
    private int universityLattitude;
    private int universityLongitude;

    /**
     *Method that gets the user
     * @return user
     */
    public String getUser() {
        return user;
    }

    /**
     *Method that sets the user
     * @param user email of the user
     */
    public void setUser(String user) {
        this.user = user;
    }

    /**
     *Method that gets the university lattitude
     * @return universityLattitude
     */
    public int getUniversityLattitude() {
        return universityLattitude;
    }

    /**
     *Method that sets the university latitude
     * @param universityLattitude latitude of the university
     */
    public void setUniversityLattitude(int universityLattitude) {
        this.universityLattitude = universityLattitude;
    }

    /**
     *Method that gets the university longitude
     * @return universityLongitude
     */
    public int getUniversityLongitude() {
        return universityLongitude;
    }

    /**
     *Method that sets the university longitude
     * @param universityLongitude longitude of the university
     */
    public void setUniversityLongitude(int universityLongitude) {
        this.universityLongitude = universityLongitude;
    }
   
    /**
     *Method that sets the university name
     * @param univName name of the university
     */
    public void setUnivName(String univName){
        this.univName = univName;
    }

    /**
     *Method that gets the university name
     * @return universityName
     */
    public String getUnivName(){
        return this.univName;
    }
    
    /**
     *Method that add the university to database
     * @return string 
     */
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
                 String path = FacesContext.getCurrentInstance().getExternalContext().getRealPath("resources");
                 path=path.substring(0,path.indexOf("\\build"));
                 path=path+"\\web\\resources\\"+this.getUnivName();
                 File folder = new File(path);
                 if(!folder.exists()){
                     folder.mkdir();
                 }                 
        }
        catch(Exception e){
            
        }
        return "inserted";
    }

    /**
     *Methods that updates university details
     * @return string
     */
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

    /**
     *Method to set university name to session 
     * @param universityName name of the university
     * @return string
     */
    public String getUniversityDetails(String universityName){
        session.setAttribute("editUnivName",universityName);
        System.out.println(universityName);
        
        return "edit";
    }

    /**
     *Method to edit university  details
     * @return University
     */
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
        
      //  session.removeAttribute("editUnivName");
        return un;
    }
}

