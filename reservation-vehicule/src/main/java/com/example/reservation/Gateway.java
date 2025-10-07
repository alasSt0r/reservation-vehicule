package com.example.reservation;

import java.sql.*;
import java.util.ArrayList;

public class Gateway {
    private Connection connection;
    private static final String DB_URL = "jdbc:postgresql://192.168.1.245:5432/slam2026_AP_mariuswassimyasmine";
    private static final String DB_USER = "wartel";
    private static final String DB_PASSWORD = "wartel";

    public Gateway() {
        try {
            connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            System.out.println("Connected to the database successfully.");
        } catch (SQLException e) {
            System.out.println("Connection to database failed: \n" + e.getMessage());
        }
    }

    // Fetch all types from the database
    public ArrayList<Type> getAllTypes() {
        ArrayList<Type> types = new ArrayList<>();
        String query = "SELECT * FROM type";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                int id = rs.getInt("id");
                String libelle = rs.getString("libelle");
                types.add(new Type(id, libelle));
            }
        } catch (SQLException e) {
            System.out.println("Error fetching types: \n" + e.getMessage());
        }

        return types;
    }

}