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

    public Demande getDemandeByNumero(int numero) {
        return gateway.getDemandeByNumero(numero);
    }


    public void mettreAJourDemande(Demande demande) {
        gateway.updateDemande(demande);
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
        demande.setEtat("acceptée");
        return gateway.updateDemande(demande);
    }
}
