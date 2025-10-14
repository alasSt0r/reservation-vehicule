package com.example.reservation;

import java.time.LocalDate;

public class DemandeService {
    private Gateway gateway;

    public DemandeService(Gateway gateway) {
        this.gateway = gateway;
    }

    public boolean creerDemande(Personne personne, Type type, LocalDate dateDebut, int duree) {
        // Crée un objet demande
        Demande demande = new Demande(LocalDate.now(), numero:null, dateDebut, personne, type, vehicule:null, duree, dateretoureffectif:null, "demandé")
        // Appelle la Gateway pour insérer dans la BDD
        return gateway.insertDemande(demande);
    }
}
