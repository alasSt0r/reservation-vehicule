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

   /* public boolean insertDemande(Demande demande) {
        String sql = "INSERT INTO demande (numero, datedebut, matricule, notype, immat, duree, etat) " +
                     "VALUES (DEFAULT, ?, ?, ?, NULL, ?, ?)";

        try (PreparedStatement stmt = connexion.prepareStatement(sql)) {
            stmt.setString(1, demande.getDateDebut());
            stmt.setString(2, demande.getPersonne().getMatricule());
            stmt.setInt(3, demande.getType().getNumero());
            stmt.setInt(4, demande.getDuree());
            stmt.setString(5, demande.getEtat());

            int lignes = stmt.executeUpdate();
            return lignes > 0; // true si au moins une ligne insérée
        } catch (SQLException e) {
            System.err.println("Erreur insertion demande : " + e.getMessage());
            return false;
        }    
    }*/
    /*public Personne authentifier(String matricule, String password) {
    // faire une requête SQL : SELECT * FROM personne WHERE matricule=? AND motdepasse=?
    // Si trouvé, renvoyer l'objet Personne
    // Sinon, null
}*/
}