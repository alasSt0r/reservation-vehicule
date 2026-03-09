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

    public Demande getDemandeByNumero(int numero) {
        return gateway.getDemandeByNumero(numero);
    }

    public Boolean mettreAJourDemande(Demande demande, LocalDate ancienneDateReserv) {
        return gateway.updateDemande(demande, ancienneDateReserv);
    }

    public boolean accepterDemande(int numero) {
        Demande demande = gateway.getDemandeByNumero(numero);
        if (demande == null) {
            System.out.println("Demande introuvable.");
            return false;
        }
        if (!demande.getEtat().equals("demandée")) {
            System.out.println("La demande ne peut pas être acceptée car elle est dans l'état : " + demande.getEtat());
            return false;
        }
        LocalDate ancienneDateReserv = demande.getDateReserv();
        demande.setEtat("acceptée");
        return gateway.updateDemande(demande, ancienneDateReserv);
    }
    public boolean accepterDemande(int numeroDemande, String immatriculation) {
        return gateway.accepterDemande(numeroDemande, immatriculation);
    }
}
