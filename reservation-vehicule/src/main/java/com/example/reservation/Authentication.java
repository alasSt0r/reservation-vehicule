package com.example.reservation;

import java.util.Scanner;

public class Authentication {
    private boolean isAuthenticated = false;
    private String matricule;
    private String password;
    private Personne user;

    public Authentication(Scanner stdin, Gateway gateway) {
        user = null;
        isAuthenticated = false;

        while (!isAuthenticated) {
            System.out.print(Colors.bold("Enter Matricule: "));
            matricule = stdin.nextLine();
            System.out.print(Colors.bold("Enter Password: "));
            password = stdin.nextLine();
            user = gateway.login(matricule, password);
            if (user != null) {
                isAuthenticated = true;
                System.out.println(Colors.boldGreen("Authentication successful. Welcome " + user.getNom() + "!"));
            } else {
                System.out.println(Colors.boldRed("Authentication failed. Invalid matricule or password. Please try again."));
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
