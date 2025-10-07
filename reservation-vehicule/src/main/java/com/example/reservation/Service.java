package com.example.reservation;

public class Service {
    private int numero;
    private String libelle;

    public Service(int numero, String libelle) {
        this.numero = numero;
        this.libelle = libelle;
    }

    public int getNumero() {
        return numero;
    }

    public String getLibelle() {
        return libelle;
    }
}
