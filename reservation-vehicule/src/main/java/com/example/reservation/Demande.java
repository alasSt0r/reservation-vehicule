package com.example.reservation;
import java.time.LocalDate;

public class Demande {
    private LocalDate dateReserv;
    private int numero;
    private LocalDate dateDebut;
    private Personne personne;
    private Type type;
    private Vehicule vehicule;
    private int duree;
    private LocalDate dateretoureffectif;
    private String etat;

    public Demande(LocalDate dateReserv, int numero, LocalDate dateDebut, Personne personne, Type type, Vehicule vehicule, int duree, LocalDate dateretoureffectif, String etat) {
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
<<<<<<< HEAD
   
=======
    
    public void setNumero(int newNumero){
        this.numero=newNumero;
    }
>>>>>>> 2f27357f0143ce89d232d4d3cafa8334798634de

    public LocalDate getDateReserv() {
        return dateReserv;
    }

    public int getNumero() {
        return numero;
    }

    public LocalDate getDateDebut() {
<<<<<<< HEAD
       return dateDebut;
=======
        return dateDebut;   
>>>>>>> 2f27357f0143ce89d232d4d3cafa8334798634de
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

    public LocalDate getDateretoureffectif() {
        return dateretoureffectif;
    }

    public String getEtat() {
        return etat;
    }
}
