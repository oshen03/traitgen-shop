package model;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.DriverManager;

public class SQL {
    
    private static Connection connection;
    
    public static void createConnection() throws Exception{
    
        if (connection == null) {
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/computer_shop","root","Vimandya@20030908");
        }                          
    }
    
    
     public static ResultSet executeSearch(String query) throws Exception{
        
         createConnection();
         return connection.createStatement().executeQuery(query);
         
        }
     public static Integer executeIUD(String query) throws Exception{
        
         createConnection();
         return connection.createStatement().executeUpdate(query);
         
        }
    
}
