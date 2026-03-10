package com.example.reservation;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.time.LocalDate;
import java.util.ArrayList;

public class Gateway {
    private Connection connection;
    private static final String DB_URL = "jdbc:postgresql://192.168.1.245:5432/slam2026_AP_mariuswassimyasmine";
    private static final String DB_USER = "wartel";
    private static final String DB_PASSWORD = "wartel";
    // private static final String DB_URL =
    // "jdbc:postgresql://localhost:5432/reservationVehicule";
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
                int numero = rs.getInt("numero");
                String libelle = rs.getString("libelle");
                types.add(new Type(numero, libelle));
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
        String sql = "SELECT public.creer_demande_fn(?, ?, ?, ?) AS numero_cree";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, demande.getPersonne().getMatricule());
            stmt.setInt(2, demande.getType().getNumero());
            stmt.setObject(3, demande.getDateDebut());
            stmt.setInt(4, demande.getDuree());

            ResultSet rs = stmt.executeQuery();
            return rs.next() && rs.getInt("numero_cree") > 0;
        } catch (SQLException e) {
            System.err.println("Erreur appel fonction creer_demande_fn : " + e.getMessage());
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

    public Type getTypeById(int id) {
        try {
            PreparedStatement ps = connection.prepareStatement("SELECT numero, libelle FROM type WHERE numero = ?");
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new Type(
                        rs.getInt("numero"),
                        rs.getString("libelle"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // Get all demandes
    public ArrayList<Demande> getAllDemandes() {
        ArrayList<Demande> demandes = new ArrayList<>();
        String query = "SELECT * FROM demande";

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
                Vehicule vehicule = (immat != null && !immat.isEmpty()) ? getVehiculeByImmatriculation(immat) : null;

                // If Demande stores a computed end date, compute it (assumes end = start +
                // duree days)
                LocalDate dateFin = (dateDebut != null) ? dateDebut.plusDays(duree) : null;

                Demande demande = new Demande(dateReserv, numero, dateDebut, personne, type, vehicule, duree, dateFin,
                        etat);
                demandes.add(demande);
            }
        } catch (SQLException e) {
            System.out.println("Error fetching demandes: \n" + e.getMessage());
        }
        return demandes;
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
                Vehicule vehicule = (immat != null && !immat.isEmpty()) ? getVehiculeByImmatriculation(immat) : null;

                // If Demande stores a computed end date, compute it (assumes end = start +
                // duree days)
                LocalDate dateFin = (dateDebut != null) ? dateDebut.plusDays(duree) : null;

                Demande demande = new Demande(dateReserv, numero, dateDebut, personne, type, vehicule, duree, dateFin,
                        etat);
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
                        rs.getString("password"));
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

    // Helper to retrieve a Vehicule by immatriculation from the vehicule table
    public Vehicule getVehiculeByImmatriculation(String immat) {
        String sql = "SELECT immat, marque, modele, notype FROM vehicule WHERE immat = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, immat);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                Type type = getTypeByNumero(rs.getInt("notype"));
                return new Vehicule(
                        rs.getString("immat"),
                        rs.getString("marque"),
                        rs.getString("modele"),
                        type);
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération du véhicule : " + e.getMessage());
        }
        return null;
    }

    // get Demande by numero
    public Demande getDemandeByNumero(int numero) {
        String sql = "SELECT * FROM demande WHERE numero = ? ORDER BY datereserv DESC LIMIT 1";
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
                Vehicule vehicule = (immat != null && !immat.isEmpty()) ? getVehiculeByImmatriculation(immat) : null;

                // If Demande stores a computed end date, compute it (assumes end = start +
                // duree days)
                LocalDate dateFin = (dateDebut != null) ? dateDebut.plusDays(duree) : null;

                return new Demande(dateReserv, numero, dateDebut, personne, type, vehicule, duree, dateFin, etat);
            }
        } catch (SQLException e) {
            System.out.println("Error fetching demande: \n" + e.getMessage());
        }
        return null;
    }

    // Get all vehicules from the database
    public ArrayList<Vehicule> getAllVehicules() {
        ArrayList<Vehicule> vehicules = new ArrayList<>();
        String sql = "SELECT immat, marque, modele, notype FROM vehicule";

        try (Statement stmt = connection.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Type type = getTypeByNumero(rs.getInt("notype"));
                Vehicule vehicule = new Vehicule(
                        rs.getString("immat"),
                        rs.getString("marque"),
                        rs.getString("modele"),
                        type);
                vehicules.add(vehicule);
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération des véhicules : " + e.getMessage());
        }

        return vehicules;
    }

    // Update a Demande via la clé primaire (numero, datereserv)
    // Note: datereserv ne peut pas être modifiée car elle fait partie de la clé
    // primaire
    public boolean updateDemande(Demande demande, LocalDate ancienneDateReserv) {
        String sql = "UPDATE demande SET datedebut = ?, matricule = ?, notype = ?, immat = ?, duree = ?, etat = ? WHERE numero = ? AND datereserv = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setObject(1, demande.getDateDebut());
            stmt.setString(2, demande.getPersonne().getMatricule());
            stmt.setInt(3, demande.getType().getNumero());
            if (demande.getVehicule() != null) {
                stmt.setString(4, demande.getVehicule().getImmatriculation());
            } else {
                stmt.setNull(4, Types.VARCHAR);
            }
            stmt.setInt(5, demande.getDuree());
            stmt.setString(6, demande.getEtat());
            stmt.setInt(7, demande.getNumero());
            stmt.setObject(8, ancienneDateReserv);

            int lignes = stmt.executeUpdate();
            return lignes > 0; // true si au moins une ligne mise à jour
        } catch (SQLException e) {
            System.err.println("Erreur mise à jour demande : " + e.getMessage());
            return false;
        }
    }

    // Méthode pour récupérer une demande par clé primaire (numéro + date de
    // réservation)
    public Demande getDemandeByNumeroAndDateReserv(int numero, LocalDate dateReserv) {
        String sql = "SELECT * FROM demande WHERE numero = ? AND datereserv = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, numero);
            stmt.setObject(2, dateReserv);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                // Fetch demande details and create Demande object
                LocalDate dateDebut = rs.getObject("datedebut", LocalDate.class);
                String matricule = rs.getString("matricule");
                int notype = rs.getInt("notype");
                String immat = rs.getString("immat");
                int duree = rs.getInt("duree");
                String etat = rs.getString("etat");

                // Fetch linked objects required by Demande constructor
                Personne personne = getPersonneByMatricule(matricule);
                Type type = getTypeByNumero(notype);
                Vehicule vehicule = (immat != null && !immat.isEmpty()) ? getVehiculeByImmatriculation(immat) : null;

                // If Demande stores a computed end date, compute it (assumes end = start +
                // duree days)
                LocalDate dateFin = (dateDebut != null) ? dateDebut.plusDays(duree) : null;

                return new Demande(dateReserv, numero, dateDebut, personne, type, vehicule, duree, dateFin, etat);
            }
        } catch (SQLException e) {
            System.out.println("Error fetching demande: \n" + e.getMessage());
        }
        return null;
    }

    public ArrayList<Vehicule> getVehiculesByType(Type type) {
        ArrayList<Vehicule> vehicules = new ArrayList<>();
        String query = "SELECT * FROM vehicule WHERE notype = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, type.getNumero());
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Vehicule vehicule = new Vehicule(
                        rs.getString("immat"),
                        rs.getString("marque"),
                        rs.getString("modele"),
                        type);
                vehicules.add(vehicule);
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération des véhicules : " + e.getMessage());
        }
        return vehicules;
    }

    public boolean isConnected() {
        return connection != null;
    }

    public ArrayList<Demande> getDemandesByMatricule(String matricule) {
        ArrayList<Demande> demandes = new ArrayList<>();
        if (connection == null) {
            System.err.println("Connexion BDD indisponible.");
            return demandes;
        }
        String sql = "SELECT d.numero, d.datereserv, d.datedebut, d.matricule, d.notype, d.duree, d.etat, t.libelle AS type_libelle "
                +
                "FROM demande d JOIN type t ON t.numero = d.notype WHERE d.matricule = ? ORDER BY d.datereserv DESC, d.numero DESC";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, matricule);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Type type = new Type(rs.getInt("notype"), rs.getString("type_libelle"));
                Personne personne = new Personne(rs.getString("matricule"), "", "", null, "");
                Demande demande = new Demande(
                        rs.getDate("datereserv").toLocalDate(),
                        rs.getInt("numero"),
                        rs.getDate("datedebut").toLocalDate(),
                        personne,
                        type,
                        null,
                        rs.getInt("duree"),
                        null,
                        rs.getString("etat"));
                demandes.add(demande);
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération des réservations : " + e.getMessage());
        }
        return demandes;
    }

    public boolean accepterDemande(int numeroDemande, String immatriculation) {
        String query = "UPDATE demande SET etat = 'acceptée', immat = ? WHERE numero = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, immatriculation);
            stmt.setInt(2, numeroDemande);
            int rowsUpdated = stmt.executeUpdate();
            return rowsUpdated > 0;
        } catch (SQLException e) {
            System.err.println(Colors.boldRed("Erreur lors de l'acceptation de la demande : ") + e.getMessage());
        }
        return false;
    }
}
