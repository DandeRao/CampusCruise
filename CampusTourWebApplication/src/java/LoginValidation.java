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
import javax.faces.bean.RequestScoped;
import javax.faces.bean.SessionScoped;

/**
 *
 * @author premsagarkondaparthy
 */
@Named(value = "loginValidation")
@RequestScoped
public class LoginValidation {

    /**
     * Creates a new instance of LoginValidation
     */
    public LoginValidation() {
    }
    
    private String email;
    private String password;
    private String outputMessage;

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
                            return "admin";
                        } else {
                            outputMessage = "";
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
    
}
