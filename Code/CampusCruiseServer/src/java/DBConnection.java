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

/**
 * DBConnection is a helper class to manage retrival and closing of db connection/resources
 * 
 * 
 */
public class DBConnection {
	
	
	/**
	 * Helper method to get db connection
	 * @return ConnectionDtls or error msg if any
	 */
	public static ConnectionDtls getConnection(){
		ConnectionDtls connDtls = new ConnectionDtls();
		
		try {
			Class.forName("com.mysql.jdbc.Driver");
			String username = "root";
			String password = "Teamc@1234";
			String url = "jdbc:mysql://localhost/campustour?autoReconnect=true&useSSL=false";

			Connection conn = DriverManager.getConnection(url, username, password);	
			connDtls.setConn(conn);
			
		} catch (ClassNotFoundException cnfe) {
			connDtls.setMessage("Can't perform the action. Error Message: "+cnfe.getMessage());
			cnfe.printStackTrace();
		} catch (SQLException sqle) {
			connDtls.setMessage("Can't perform the action. Error Message: "+sqle.getMessage());
			sqle.printStackTrace();
		}
				
		return connDtls;
	}	

	/**
	 * Helper method to close db resources.
	 * 
	 */	
	public static void close(ResultSet rs, Statement stat, Connection conn) {
		close(rs);
		close(stat);
		close(conn);
	}

	/**
	 * Helper method to close db resources.
	 * 
	 */
	public static void close(Statement stat, Connection conn) {
		close(stat);
		close(conn);
	}
	
	/**
	 * Helper method to close db resources.
	 * 
	 */	
	public static void close(Statement stat) {
		try {
			if( null != stat )
			stat.close();
		} catch (SQLException sqle) {
			sqle.printStackTrace();
		}
	}

	/**
	 * Helper method to close db resources.
	 * 
	 */	
	public static void close(ResultSet rs) {
		try {
			if( null != rs )
			rs.close();
		} catch (SQLException sqle) {
			sqle.printStackTrace();
		}
	}

	/**
	 * Helper method to close db resources.
	 * 
	 */	
	public static void close(Connection conn) {
		try {
			if( null != conn )
				conn.close();
		} catch (SQLException sqle) {
			sqle.printStackTrace();
		}
	}
	
}
