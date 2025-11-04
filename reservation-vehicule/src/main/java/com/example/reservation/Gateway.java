package com.example.reservation;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.sql.Types;
import java.sql.Date;


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


    public Personne getPersonneByMatricule(String matricule) {
    Personne personne = null;

    try {
        PreparedStatement ps = connection.prepareStatement(
            "SELECT p.matricule, p.nom, p.telephone, p.password, s.numero AS service_numero, s.libelle AS service_libelle " +
            "FROM personne p " +
            "JOIN service s ON p.service_id = s.numero " +
            "WHERE p.matricule = ?"
        );
        ps.setString(1, matricule);
        ResultSet rs = ps.executeQuery();

        if (rs.next()) {
        
            Service service = new Service(
                rs.getInt("service_numero"),
                rs.getString("service_libelle")
            );

            personne = new Personne(
                rs.getString("matricule"),
                rs.getString("nom"),
                rs.getString("telephone"),
                service,
                rs.getString("password")
            );
        }

    } catch (SQLException e) {
        System.err.println("Erreur lors de la récupération de la personne : " + e.getMessage());
        e.printStackTrace();
    }

    return personne;
}


      public Type getTypeById(int id) {
        try {
            PreparedStatement ps = connection.prepareStatement("SELECT * FROM type WHERE id = ?");
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new Type(
                    rs.getInt("id"),
                    rs.getString("libelle")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Vehicule getVehiculeByImmatriculation(String immatriculation) {
    try {
        PreparedStatement ps = connection.prepareStatement(
            "SELECT * FROM vehicule WHERE immat = ?"
        );
        ps.setString(1, immatriculation);
        ResultSet rs = ps.executeQuery();

        if (rs.next()) {
            String marque = rs.getString("marque");
            String modele = rs.getString("modele");
            int idType = rs.getInt("numero");

     
            Type type = getTypeById(idType);

            return new Vehicule(immatriculation, marque, modele, type);
        }

    } catch (SQLException e) {
        e.printStackTrace();
    }
    return null;
}

    public Demande getDemandeByNumero(int numero) {
    Demande demande = null;

    try {
        PreparedStatement ps = connection.prepareStatement(
            "SELECT * FROM demande WHERE numero = ?"
        );
        ps.setInt(1, numero);
        ResultSet rs = ps.executeQuery();

        if (rs.next()) {
   
            LocalDate dateReserv = rs.getDate("datereserv").toLocalDate();
            LocalDate dateDebut = rs.getDate("datedebut").toLocalDate();
            int duree = rs.getInt("duree");

            LocalDate dateRetour = null;
            if (rs.getDate("dateretoureffectif") != null) {
                dateRetour = rs.getDate("dateretoureffectif").toLocalDate();
            }

            String etat = rs.getString("etat");


            String matriculePersonne = rs.getString("matricule"); // à modif selon colonne bdd
            int idType = rs.getInt("notype");
            String immatriculationVehicule = rs.getString("immat"); 


            Personne personne = getPersonneByMatricule(matriculePersonne);
            Type type = getTypeById(idType);
            Vehicule vehicule = null;
            if (immatriculationVehicule != null && !immatriculationVehicule.isEmpty()) {
                vehicule = getVehiculeByImmatriculation(immatriculationVehicule);
            }

            demande = new Demande(
                dateReserv,
                numero,
                dateDebut,
                personne,
                type,
                vehicule,
                duree,
                dateRetour,
                etat
            );
        }

    } catch (SQLException e) {
        e.printStackTrace();
    }

    return demande;
}

public void updateDemande(Demande demande) {
    try {
        PreparedStatement ps = connection.prepareStatement(
            "UPDATE demande SET datedebut = ?, duree = ?, dateretoureffectif = ?, etat = ?, " +
            "notype = ?, immat = ?, matricule = ? WHERE numero = ?"
        );


        ps.setDate(1, Date.valueOf(demande.getDateDebut()));
        ps.setInt(2, demande.getDuree());

        // dateRetourEffectif peut être null
        if (demande.getDateretoureffectif() != null) {
            ps.setDate(3, Date.valueOf(demande.getDateretoureffectif()));
        } else {
            ps.setNull(3, Types.DATE);
        }

        ps.setString(4, demande.getEtat());
        ps.setInt(5, demande.getType().getNumero());

        // le véhicule peut être null
        if (demande.getVehicule() != null) {
            ps.setString(6, demande.getVehicule().getImmatriculation());
        } else {
            ps.setNull(6, Types.VARCHAR);
        }

        ps.setString(7, demande.getPersonne().getMatricule());
        ps.setInt(8, demande.getNumero());

       
        ps.executeUpdate();

        System.out.println("demande mise à jour avec succès (numéro : " + demande.getNumero() + ")");

    } catch (SQLException e) {
        System.err.println("erreur lors de la mise à jour de la demande : " + e.getMessage());
    }
}



}
