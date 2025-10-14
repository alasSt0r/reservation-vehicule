package com.example.reservation;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
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

    // Authenticate user
    public Personne login(String matricule, String password) {
        String sql = "SELECT * FROM personne WHERE matricule = ? AND password = ?";
        Personne user;
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, matricule);
            stmt.setString(2, password);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                // User found, create Personne object
                // Fetch service details

                user = new Personne(
                        rs.getString("matricule"),
                        rs.getString("nom"),
                        rs.getString("telephone"),
                        getServiceByNumero(rs.getInt("noservice")),
                        rs.getString("password"));
                return user;
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la connexion : " + e.getMessage());
        }
        return null; // Authentication failed
    }

    // Retrieve a service by its numero
    public Service getServiceByNumero(int numero) {
        String sql = "SELECT * FROM service WHERE numero = ?";
        Service service = null;

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, numero);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                service = new Service(
                        rs.getInt("numero"),
                        rs.getString("libelle"));
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération du service : " + e.getMessage());
        }
        return service;
    }

    public boolean insertDemande(Demande demande) {
        String sql = "INSERT INTO demande (numero, datereserv, datedebut, matricule, notype, immat, duree, etat) " +
                "VALUES (?, ?, ?, ?, ?, NULL, ?, ?)";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, demande.getNumero()); // numero calculé par getNextNumero()
            stmt.setObject(2, demande.getDateReserv()); // datereserv
            stmt.setObject(3, demande.getDateDebut()); // dateDebut
            stmt.setString(4, demande.getPersonne().getMatricule()); // matricule
            stmt.setInt(5, demande.getType().getNumero()); // notype
            stmt.setInt(6, demande.getDuree()); // duree
            stmt.setString(7, demande.getEtat()); // etat

            int lignes = stmt.executeUpdate();
            return lignes > 0; // true si au moins une ligne insérée
        } catch (SQLException e) {
            System.err.println("Erreur insertion demande : " + e.getMessage());
            return false;
        }
    }

    public int getNextNumero(LocalDate dateReserv) {
        String sql = "SELECT COALESCE(MAX(numero), 0) + 1 AS next_numero FROM demande WHERE datereserv = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setObject(1, dateReserv);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("next_numero");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 1;
    }

}
