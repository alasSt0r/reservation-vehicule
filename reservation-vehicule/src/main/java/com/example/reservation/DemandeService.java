package com.example.reservation;

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
        return gateway.insertDemande(demande);
    }
}
