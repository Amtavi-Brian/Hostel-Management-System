package database;

import java.sql.*;

public class DBConnection {
    
    private final String url="jdbc:postgresql://localhost:5432/hostel_db";
    private final String user="postgres";
    private final String password="postgres";
    
    public Connection con;
    
    public Connection getConnection(){
        //1. Load the driver
        try{
            Class.forName("org.postgresql.Driver");
            System.out.println("Driver Loaded Successful");
            
        }catch(ClassNotFoundException cnfe){
            System.out.println("Error: Driver failed to Load "+cnfe.getMessage());
        }
        
        //2. Establish a connection 
        try{
            con=DriverManager.getConnection(url, user, password);
            System.out.println("Connection establish Successful");
        }catch(SQLException e){
            System.out.println("Error: failed to connect " + e.getMessage());
        }
        return con;
    }
}