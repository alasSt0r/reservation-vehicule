package com.example.reservation;

import java.util.Scanner;

public class App {

    public static int lireChoix(Scanner sc) {
        int choix;
        while (true) {
            try {
                choix = Integer.parseInt(sc.nextLine());
                break;
            } catch (NumberFormatException e) {
                System.out.print("Entrée invalide. Veuillez saisir un nombre : ");
            }
        }
        return choix;
    }


    public static void menuUtilisateur(Scanner sc) {
        int choixU;
        do {
            System.out.println(" MENU UTILISATEUR ");
            System.out.println("1 - Réserver un véhicule");
            System.out.println("2 - Voir mes réservations");
            System.out.println("0 - Quitter");
            System.out.print("Votre choix : ");
            choixU = lireChoix(sc);

            switch (choixU) {
                case 1:
                    System.out.println("→ Réservation d’un véhicule...");
                    break;
                case 2:
                    System.out.println("→ Affichage de vos réservations...");
                    break;
                case 0:
                    System.out.println("→ Déconnexion...");
                    break;
                default:
                    System.out.println("Choix invalide, veuillez réessayer.");
            }
        } while (choixU != 0);
    }


    public static void menuPersonnel(Scanner sc) {
        int choixP;
        do {
            System.out.println("MENU PERSONNEL ");
            System.out.println("1 - Voir les demandes en cours");
            System.out.println("2 - Modifier une demande");
            System.out.println("3 - Voir toutes les demandes");
            System.out.println("0 - Quitter");
            System.out.print("Votre choix : ");
            choixP = lireChoix(sc);

            switch (choixP) {
                case 1:
                case 2:
                case 3:
                    System.out.println("→ Cette fonctionnalité sera implémentée plus tard.");
                    break;
                case 0:
                    System.out.println("→ Déconnexion...");
                    break;
                default:
                    System.out.println("Choix invalide, veuillez réessayer.");
            }
        } while (choixP != 0);
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        Authentication auth = new Authentication();
        Personne user = auth.getUser();
        String service = user.getService().getLibelle();

        System.out.println("Utilisateur connecté : " + user.getNom() + " (" + service + ")");

        // Redirection selon le service
        if (service.equalsIgnoreCase("dev")) {
            menuUtilisateur(sc);
        } else if (service.equalsIgnoreCase("gestionVehicule")) {
            menuPersonnel(sc);
        } else {
            System.out.println("Service inconnu. Accès refusé.");
        }

        System.out.println("Merci d’avoir utilisé le système !");
        sc.close();
    }
}
