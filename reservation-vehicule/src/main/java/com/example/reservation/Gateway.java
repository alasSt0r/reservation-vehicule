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
    // private static final String DB_URL = "jdbc:postgresql://localhost:5432/reservationVehicule";
    // private static final String DB_USER = "user";
    // private static final String DB_PASSWORD = "pwd";

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
        String sql = "SELECT matricule,nom,telephone,noservice,password FROM personne WHERE matricule = ? AND password = ?";
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
            System.err.println((Colors.boldRed("Erreur lors de la connexion : ") + e.getMessage()));
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

    // Get all demandes waiting for processing
    public ArrayList<Demande> getAllDemandesWaiting() {
        ArrayList<Demande> demandes = new ArrayList<>();
        String query = "SELECT * FROM demande WHERE etat = 'demandée'";
        try (Statement stmt = connection.createStatement();
                ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                // Fetch demande details and create Demande objects
                int numero = rs.getInt("numero");
                LocalDate dateReserv = rs.getObject("datereserv", LocalDate.class);
                LocalDate dateDebut = rs.getObject("datedebut", LocalDate.class);
                String matricule = rs.getString("matricule");
                int notype = rs.getInt("notype");
                String immat = rs.getString("immat");
                int duree = rs.getInt("duree");
                String etat = rs.getString("etat");

                // Fetch linked objects required by Demande constructor
                Personne personne = getPersonneByMatricule(matricule);
                Type type = getTypeByNumero(notype);
                // Vehicule retrieval is optional; return null if not available
                Vehicule vehicule = (immat != null && !immat.isEmpty()) ? getVehiculeByImmat(immat) : null;

                // If Demande stores a computed end date, compute it (assumes end = start + duree days)
                LocalDate dateFin = (dateDebut != null) ? dateDebut.plusDays(duree) : null;

                Demande demande = new Demande(dateReserv, numero, dateDebut, personne, type, vehicule, duree, dateFin, etat);
                demandes.add(demande);
            }
        } catch (SQLException e) {
            System.out.println("Error fetching demandes: \n" + e.getMessage());
        }
        return demandes;
    }

    // Helper to retrieve a Personne by matricule
    public Personne getPersonneByMatricule(String matricule) {
        String sql = "SELECT matricule,nom,telephone,noservice,password FROM personne WHERE matricule = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, matricule);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new Personne(
                        rs.getString("matricule"),
                        rs.getString("nom"),
                        rs.getString("telephone"),
                        getServiceByNumero(rs.getInt("noservice")),
                        rs.getString("password")
                );
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération de la personne : " + e.getMessage());
        }
        return null;
    }

    // Helper to retrieve a Type by its id/numero
    public Type getTypeByNumero(int numero) {
        String sql = "SELECT numero, libelle FROM type WHERE numero = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, numero);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new Type(rs.getInt("numero"), rs.getString("libelle"));
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération du type : " + e.getMessage());
        }
        return null;
    }

    // Helper to retrieve a Vehicule by immatriculation; return null if not found or not applicable
    public Vehicule getVehiculeByImmat(String immat) {
        String sql = "SELECT immat, marque, modele, annee, notype FROM vehicule WHERE immat = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, immat);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                Type type = getTypeByNumero(rs.getInt("notype"));
                return new Vehicule(
                        rs.getString("immat"),
                        rs.getString("marque"),
                        rs.getString("modele"),
                        type
                );
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération du véhicule : " + e.getMessage());
        }
        return null;
    }

    //get Demande by numero
    public Demande getDemandeByNumero(int numero) {
        String sql = "SELECT * FROM demande WHERE numero = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, numero);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                // Fetch demande details and create Demande object
                LocalDate dateReserv = rs.getObject("datereserv", LocalDate.class);
                LocalDate dateDebut = rs.getObject("datedebut", LocalDate.class);
                String matricule = rs.getString("matricule");
                int notype = rs.getInt("notype");
                String immat = rs.getString("immat");
                int duree = rs.getInt("duree");
                String etat = rs.getString("etat");

                // Fetch linked objects required by Demande constructor
                Personne personne = getPersonneByMatricule(matricule);
                Type type = getTypeByNumero(notype);
                Vehicule vehicule = (immat != null && !immat.isEmpty()) ? getVehiculeByImmat(immat) : null;

                // If Demande stores a computed end date, compute it (assumes end = start + duree days)
                LocalDate dateFin = (dateDebut != null) ? dateDebut.plusDays(duree) : null;

                return new Demande(dateReserv, numero, dateDebut, personne, type, vehicule, duree, dateFin, etat);
            }
        } catch (SQLException e) {
            System.out.println("Error fetching demande: \n" + e.getMessage());
        }
        return null;
    }

    // Update a Demande
    public boolean updateDemande(Demande demande) {
        String sql = "UPDATE demande SET datereserv = ?, datedebut = ?, matricule = ?, notype = ?, immat = ?, duree = ?, etat = ? WHERE numero = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setObject(1, demande.getDateReserv());
            stmt.setObject(2, demande.getDateDebut());
            stmt.setString(3, demande.getPersonne().getMatricule());
            stmt.setInt(4, demande.getType().getNumero());
            if (demande.getVehicule() != null) {
                stmt.setString(5, demande.getVehicule().getImmatriculation());
            } else {
                stmt.setNull(5, java.sql.Types.VARCHAR);
            }
            stmt.setInt(6, demande.getDuree());
            stmt.setString(7, demande.getEtat());
            stmt.setInt(8, demande.getNumero());

            int lignes = stmt.executeUpdate();
            return lignes > 0; // true si au moins une ligne mise à jour
        } catch (SQLException e) {
            System.err.println("Erreur mise à jour demande : " + e.getMessage());
            return false;
        }
    }
}
