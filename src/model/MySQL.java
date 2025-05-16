package model;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.DriverManager;

public class MySQL {
    

    private static Connection connection;
    
    private static final String USERNAME = "root";
    private static final String PASSWORD = "Rasha5416/6145";
    private static final String DATABASE = "computer_shop";

    public static void createConnection() throws Exception {

        if (connection == null) {
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/"+DATABASE, USERNAME, PASSWORD);
        }

    }

    public static ResultSet executeSearch(String query) throws Exception {

        createConnection();
        return connection.createStatement().executeQuery(query);

    }

    public static Integer executeIUD(String query) throws Exception {

        createConnection();
        return connection.createStatement().executeUpdate(query);

    }
    
    public static Connection getConnection() {

        if (connection == null) {
            try {
                Class.forName("com.mysql.cj.jdbc.Driver");
                connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/" + DATABASE, USERNAME, PASSWORD);
            } catch (Exception e) {

                e.printStackTrace();
            }

        }
        return connection;

    }
    
}
