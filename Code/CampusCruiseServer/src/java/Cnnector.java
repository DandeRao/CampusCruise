import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.codehaus.jackson.map.ObjectMapper;

public class Cnnector {
	public static void main(String args[]){
		
		System.out.println("Message is: "+fetch());
		
	}
public static String fetch(){
	ConnectionDtls connDtls = DBConnection.getConnection();
    String retMsg = null;
    Connection conn = connDtls.getConn();
    PreparedStatement pstmt = null;
    ResultSet rs = null;
    String message="";
    String university_coordinate="";
    String tour_path="";
    ArrayList<String> tour_coordinates_demilted;
    ArrayList<Building> buildings = new ArrayList<>();
    University u = new University();
    ArrayList<University> universitiesList = new ArrayList<>();
    String jsonString = "";
    int universityId = 0;
    
     try {
      
         
         
         pstmt = conn.prepareStatement("select * from universities;");
               
         rs = pstmt.executeQuery();
         
         while (rs.next()) {
        	 message = rs.getString("university_name");
                 university_coordinate = rs.getString("university_coordinates");
                  tour_path = rs.getString("tour_path");
                  universityId = rs.getInt("university_id");
               universitiesList.add(new University(message, university_coordinate, tour_path,universityId));  
         }
         
         for (int i =0 ;i<universitiesList.size()-1;i++){
             buildings.clear();
         pstmt = conn.prepareStatement("select * from buildings where university_id = "+universitiesList.get(i).getUniversityId());
         
          rs = pstmt.executeQuery();
         
         while (rs.next()) {
             
             buildings.add(new Building(rs.getString("building_name"), rs.getString("building_coordinates"),rs.getInt("university_id")));
             
                 
         }
         
         
         universitiesList.get(i).setBuildings(buildings);
         }
         
         
         
     } catch (SQLException sqle) {
         retMsg = sqle.getMessage();
         sqle.printStackTrace();
         System.out.println("SQL Exception Caught");
     } finally {
         DBConnection.close(pstmt, conn);
     }
     ObjectMapper objectMapper = new ObjectMapper();
            try {
                jsonString = objectMapper.writeValueAsString(universitiesList);
            } catch (IOException ex) {
                System.out.println("IO Exception caught");
            }
     System.out.println("Json String: "+jsonString);
     return jsonString;
}
}
