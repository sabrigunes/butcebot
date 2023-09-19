package com.sabrigunes.utils;

import java.sql.*;

public class DatabaseUtil {
    private final String HOST;
    private final int PORT;
    private final String USERNAME;
    private final String PASSWORD;
    private final String DATABASE;
    private final String URL;
    public DatabaseUtil(String host, int port, String username, String password, String database) {
        HOST = host;
        PORT = port;
        USERNAME = username;
        PASSWORD = password;
        DATABASE = database;
        URL = getDatabaseURL();

        System.out.println("Database object created.");

        testConnection();
    }

    public DatabaseUtil(String host,  String username, String password, String database) {
        this(host, 5432, username, password, database);
    }

    public DatabaseUtil(String username, String password, String database) {
        this("localhost", 5432, username, password, database);
    }

    public ResultSet fetchData(String statement) {
        try (Connection connection = DriverManager.getConnection(URL, USERNAME, PASSWORD)){
            return  connection.createStatement().executeQuery(statement);
        }
        catch (Exception ex){
            throw new RuntimeException(ex.getMessage());
        }
    }

    public int executeQuery(String query){
        try (Connection connection = DriverManager.getConnection(URL, USERNAME, PASSWORD)){
            var statement = connection.prepareStatement(query);
            return statement.executeUpdate();
        }
        catch(Exception ex){
            throw new RuntimeException(ex.getMessage());
        }
    }
    private void testConnection() {
        try (Connection connection = DriverManager.getConnection(URL, USERNAME, PASSWORD)){
            System.out.println("Database connection verified.");
        }
        catch (Exception ex){
            throw new RuntimeException(ex.getMessage());
        }
    }

    private String getDatabaseURL(){
        return String.format("jdbc:postgresql://%s:%s/%s", HOST, PORT, DATABASE);
    }
}
