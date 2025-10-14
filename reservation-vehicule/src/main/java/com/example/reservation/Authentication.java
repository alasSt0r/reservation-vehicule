package com.example.reservation;

public class Authentication {
    private boolean isAuthenticated = false;
    private String matricule;
    private String password;
    private Personne user;
    private java.util.Scanner stdin;

    public Authentication() {
        this.stdin = new java.util.Scanner(System.in);
        user = null;
        isAuthenticated = false;
        Gateway gateway = new Gateway();
        while (!isAuthenticated) {
            System.out.print("Enter Matricule: ");
            matricule = stdin.nextLine();
            System.out.print("Enter Password: ");
            password = stdin.nextLine();
            user = gateway.login(matricule, password);
            if (user != null) {
                isAuthenticated = true;
                System.out.println("Authentication successful. Welcome " + user.getNom());
            } else {
                System.out.println("Authentication failed. Invalid matricule or password. Please try again.");
            }
        }
    }

    public String getMatricule() {
        return matricule;
    }
    public String getPassword() {
        return password;
    }
    public boolean isAuthenticated() {
        return isAuthenticated;
    }
    public Personne getUser() {
        return user;
    }
}