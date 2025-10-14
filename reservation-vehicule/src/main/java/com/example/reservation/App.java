package com.example.reservation;

import java.util.Scanner;

public class App {

    // Affiche le menu principal
    public static void menuCNX() {
        System.out.println("MENU ");
        System.out.println("Vous êtes :");
        System.out.println("1 - Un utilisateur");
        System.out.println("2 - Un membre du personnel du service de gestion des véhicules");
        System.out.println("0 - Quitter");
        System.out.print("Votre choix : ");
    }

    // Lit le choix de l'utilisateur
    public static int lireChoix(Scanner sc) {
        int choix;
        while (true) {
            try {
                choix = Integer.parseInt(sc.nextLine());
                break;
            } catch (NumberFormatException e) {
                System.out.print(" Entrée invalide. Veuillez saisir un nombre : ");
            }
        }
        return choix;
    }

    public static void main(String[] args) {
        System.out.println( "Welcome to Vehicle Reservation System!" );
        Authentication auth = new Authentication();
        Personne user = auth.getUser();
        Scanner sc = new Scanner(System.in);
        int choixPrincipal;

        do {
            menuCNX();
            choixPrincipal = lireChoix(sc);

            switch (choixPrincipal) {
                case 1:
                    // Menu pour l'utilisateur
                    int choixU;
                    do {
                        System.out.println(" MENU");
                        System.out.println("1 - Réserver un véhicule");
                        System.out.println("2 - Voir mes réservations");
                        System.out.println("0 - Retour au menu principal");
                        System.out.print("Votre choix : ");
                        choixU = lireChoix(sc);

                        switch (choixU) {
                            case 1:
                                System.out.println(" Réservation d’un véhicule...");
                                break;
                            case 2:
                                System.out.println(" Affichage de vos réservations...");
                                break;
                            case 0:
                                System.out.println(" Retour au menu principal...");
                                break;
                            default:
                                System.out.println(" Choix invalide, veuillez réessayer.");
                        }
                    } while (choixU != 0);
                    break;

                case 2: // MENU PERSONNEL
                    int choixP;
                    do {
                        System.out.println("MENU PERSONNEL");
                        System.out.println("1 - Voir les demandes en cours");
                        System.out.println("2 - Modifier une demande");
                        System.out.println("3 - Voir toutes les demandes");
                        System.out.println("0 - Retour au menu principal");
                        System.out.print("Votre choix : ");
                        choixP = lireChoix(sc);

                        switch (choixP) {
                            case 1:
                            case 2:
                            case 3:
                                System.out.println("Cette fonctionnalité sera implémentée plus tard.");
                                break;
                            case 0:
                                System.out.println("Retour au menu principal...");
                                break;
                            default:
                                System.out.println("Choix invalide, veuillez réessayer.");
                        }
                    } while (choixP != 0);
                    break;

                case 0:
                    System.out.println("Au revoir !");
                    break;

                default:
                    System.out.println("Choix invalide, veuillez réessayer.");
            }

        } while (choixPrincipal != 0);

        sc.close();

    }
}
