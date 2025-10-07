package com.example.reservation;
import java.util.Date;

public class Demande {
    private Date dateReserv;
    private int numero;
    private Date dateDebut;
    private Personne personne;
    private Type type;
    private Vehicule vehicule;
    private int duree;
    private Date dateretoureffectif;
    private String etat;

    public Demande(Date dateReserv, int numero, Date dateDebut, Personne personne, Type type, Vehicule vehicule, int duree, Date dateretoureffectif, String etat) {
        this.dateReserv = dateReserv;
        this.numero = numero;
        this.dateDebut = dateDebut;
        this.personne = personne;
        this.type = type;
        this.vehicule = vehicule;
        this.duree = duree;
        this.dateretoureffectif = dateretoureffectif;
        this.etat = etat;
    }

    public Date getDateReserv() {
        return dateReserv;
    }

    public int getNumero() {
        return numero;
    }

    public Date getDateDebut() {
        return dateDebut;
    }

    public Personne getPersonne() {
        return personne;
    }

    public Type getType() {
        return type;
    }

    public Vehicule getVehicule() {
        return vehicule;
    }

    public int getDuree() {
        return duree;
    }

    public Date getDateretoureffectif() {
        return dateretoureffectif;
    }

    public String getEtat() {
        return etat;
    }
}
