package com.example.reservation;

import java.util.Scanner;
import java.time.LocalDate;

public class App {

    public static void creerDemande(Personne utilisateur, Scanner sc) {
        // Création du service de demande
        Gateway gateway = new Gateway();
        DemandeService demandeService = new DemandeService(gateway);

        // Sélection du type de véhicule
        System.out.println("\nTypes de véhicules disponibles :");
        System.out.println("1 - Citadine");
        System.out.println("2 - Berline");
        System.out.println("3 - Utilitaire");
        System.out.print("Choisissez le type de véhicule : ");
        int typeChoice = lireChoix(sc);
        Type typeVehicule;
        switch (typeChoice) {
            case 1:
                typeVehicule = new Type(1, "Citadine");
                break;
            case 2:
                typeVehicule = new Type(2, "Berline");
                break;
            case 3:
                typeVehicule = new Type(3, "Utilitaire");
                break;
            default:
                System.out.println("Type de véhicule invalide.");
                return;
        }

        // Saisie de la date de début
        System.out.print("\nDate de début (format AAAA-MM-JJ) : ");
        String dateDebutStr = sc.nextLine();
        LocalDate dateDebut;
        try {
            dateDebut = LocalDate.parse(dateDebutStr);
            if (dateDebut.isBefore(LocalDate.now())) {
                System.out.println("La date de début ne peut pas être dans le passé.");
                return;
            }
        } catch (Exception e) {
            System.out.println("Format de date invalide.");
            return;
        }

        // Saisie de la durée
        System.out.print("Durée de réservation en jours : ");
        int duree;
        try {
            duree = Integer.parseInt(sc.nextLine());
            if (duree <= 0) {
                System.out.println("La durée doit être positive.");
                return;
            }
        } catch (NumberFormatException e) {
            System.out.println("Durée invalide.");
            return;
        }

        // Création de la demande
        boolean success = demandeService.creerDemande(utilisateur, typeVehicule, dateDebut, duree);
        if (success) {
            System.out.println("Demande créée avec succès !");
        } else {
            System.out.println("Erreur lors de la création de la demande.");
        }
    }

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

    public static void menuUtilisateur(Scanner sc, Personne user) {
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
                    System.out.println("→ Réservation d'un véhicule...");
                    creerDemande(user, sc);
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
            menuUtilisateur(sc, user);
        } else if (service.equalsIgnoreCase("gestionVehicule")) {
            menuPersonnel(sc);
        } else {
            System.out.println("Service inconnu. Accès refusé.");
        }

        System.out.println("Merci d’avoir utilisé le système !");
        sc.close();
    }
}
