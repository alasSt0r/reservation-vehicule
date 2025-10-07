package com.example.reservation;

public class Type {
    private int numero;
    private String libelle;

    public Type(int numero, String libelle) {
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
