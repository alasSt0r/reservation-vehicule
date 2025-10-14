package com.example.reservation;

import java.sql.SQLException;
import java.time.LocalDate;

public class DemandeService {

    private Gateway gateway;

    public DemandeService(Gateway gateway) {
        this.gateway = gateway;
    }

<<<<<<< HEAD
   /* public boolean creerDemande(Personne personne, Type type, LocalDate dateDebut, int duree) {
        // Crée un objet demande
        Demande demande = new Demande(LocalDate.now(), numero:null, dateDebut, personne, type, vehicule:null, duree, dateretoureffectif:null, "demandé")
        // Appelle la Gateway pour insérer dans la BDD
=======
    public boolean creerDemande(Personne personne, Type type, LocalDate dateDebut, int duree) {
        LocalDate dateReserv = LocalDate.now();
        int numero = gateway.getNextNumero(dateReserv);
        Demande demande = new Demande(dateReserv, numero, dateDebut, personne, type, null, duree, null, "demandée");
>>>>>>> 2f27357f0143ce89d232d4d3cafa8334798634de
        return gateway.insertDemande(demande);
    }*/
}
