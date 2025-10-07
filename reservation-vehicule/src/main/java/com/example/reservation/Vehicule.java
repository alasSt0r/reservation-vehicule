package com.example.reservation;

public class  Vehicule {
    private String immatriculation;
    private String marque;
    private String modele;
    private Type type;

    public Vehicule(String immatriculation, String marque, String modele, Type type) {
        this.immatriculation = immatriculation;
        this.marque = marque;
        this.modele = modele;
        this.type = type;
    }

    public String getImmatriculation() {
        return immatriculation;
    }

    public String getMarque() {
        return marque;
    }

    public String getModele() {
        return modele;
    }

    public Type getType() {
        return type;
    }
}
