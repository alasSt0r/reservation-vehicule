package com.example.reservation;

import java.time.LocalDate;
import java.util.ArrayList;

public class DemandeService {

    private Gateway gateway;

    public DemandeService(Gateway gateway) {
        this.gateway = gateway;
    }

    public ArrayList<Type> getTypesDisponibles() {
        return gateway.getAllTypes();
    }

    public ArrayList<Demande> getDemandesUtilisateur(String matricule) {
        return gateway.getDemandesByMatricule(matricule);
    }

    public boolean creerDemande(Personne personne, Type type, LocalDate dateDebut, int duree) {
        LocalDate dateReserv = LocalDate.now();
        int numero = gateway.getNextNumero(dateReserv);
        Demande demande = new Demande(dateReserv, numero, dateDebut, personne, type, null, duree, null, "demandée");

        boolean result = gateway.insertDemande(demande);
        return result;
    }
}
