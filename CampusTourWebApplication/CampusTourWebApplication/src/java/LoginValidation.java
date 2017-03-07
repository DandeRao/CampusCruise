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
import javax.servlet.http.HttpSession;

/**
 *
 * @author premsagarkondaparthy
 */
@Named(value = "loginValidation")
@ManagedBean
public class LoginValidation {
    FacesContext facesContext = FacesContext.getCurrentInstance();
    HttpSession session = (HttpSession) facesContext.getExternalContext().getSession(true);
    /**
     * Creates a new instance of LoginValidation
     */
    public LoginValidation() {
    }
    
    private String email;
    private String password;
    private String outputMessage;
    private  String universityName;
    public  String getUniversityName() {
        return universityName;
    }

    public  void setUniversityName(String universityName) {
        this.universityName = universityName;
    }
    public String getOutputMessage() {
        return outputMessage;
    }

    public void setOutputMessage(String outputMessage) {
        this.outputMessage = outputMessage;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
    public String validatingUsernamePassword(){
        outputMessage="wtf";
        try{
                
                Class.forName("com.mysql.jdbc.Driver");
                Connection con = DriverManager.getConnection("jdbc:mysql://localhost/campus_tour","root","admin");
                Connection con = DriverManager.getConnection("jdbc:mysql://localhost/campus_tour","root","");
                
                Statement st = con.createStatement();
                String a = this.email;
                String b = this.password;
                
                ResultSet result = st.executeQuery("select count(*) count,role from user where email='"+ a +"' and password ='"+b+"'");
                
                while(result.next()){
                    String count = result.getString("count");
                    String role = result.getString("role");
                    
                    if(Integer.parseInt(count) == 1){
                        if("admin".equals(role)){
                            outputMessage = "";
                            
                            session.setAttribute("name",email);
                            return "admin";
                        } else {
                            outputMessage = "";
                            ResultSet rs = st.executeQuery("select university_name from university where email = '"+a+"'");
                            rs.next();
                            session.setAttribute("name",email);
                            
                            this.setUniversityName(rs.getString("university_name"));
                            session.setAttribute("university",this.getUniversityName());
                            return "user";
                        }
                   } else{
                        outputMessage = "Username and Password don't match";
                        return "";
                    }
                }
                
                
            } catch(Exception e){
               outputMessage = "Error in connecting to database";
            }
            return "";
            
    }
    public String logout(){
        session.invalidate();
        return "loggedOut";
    }
    
}
