package com.example.reservation;

import java.sql.SQLException;
import java.time.LocalDate;

public class DemandeService {

    private Gateway gateway;

    public DemandeService(Gateway gateway) {
        this.gateway = gateway;
    }

    public boolean creerDemande(Personne personne, Type type, LocalDate dateDebut, int duree) {
        LocalDate dateReserv = LocalDate.now();
        int numero = gateway.getNextNumero(dateReserv);
        Demande demande = new Demande(dateReserv, numero, dateDebut, personne, type, null, duree, null, "demandée");

        // Ajout des logs
        System.out.println("Création demande : ");
        System.out.println("Numéro : " + numero);
        System.out.println("Date réservation : " + dateReserv);
        System.out.println("Date début : " + dateDebut);
        System.out.println("Durée : " + duree);
        System.out.println("Personne : " + personne.getNom());
        System.out.println("Type : " + type.getLibelle());

        boolean result = gateway.insertDemande(demande);
        System.out.println("Résultat insertion : " + result);
        return result;
    }
}
