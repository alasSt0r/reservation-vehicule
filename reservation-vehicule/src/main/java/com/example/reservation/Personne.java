package com.example.reservation;

public class Personne {
    private String telephone;
    private Service service;
    private String password;
    private String matricule;
    private String nom;

    public Personne(String matricule, String nom, String telephone, Service service, String password) {
        this.matricule = matricule;
        this.nom = nom;
        this.telephone = telephone;
        this.service = service;
        this.password = password;
    }

    public String getMatricule() {
        return matricule;
    }

    public String getNom() {
        return nom;
    }

    public String getTelephone() {
        return telephone;
    }

    public Service getService() {
        return service;
    }

    public String getPassword() {
        return password;
    }
}
