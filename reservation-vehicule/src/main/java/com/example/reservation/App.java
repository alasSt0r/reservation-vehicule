package com.example.reservation;

import java.util.ArrayList;
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
            System.out.println("4 - Accepter une demande");
            System.out.println("0 - Quitter");
            System.out.print("Votre choix : ");
            choixP = lireChoix(sc);

            switch (choixP) {
                case 1:
                case 2:
                case 3:
                    afficherDemandes(sc);
                    break;
                case 4:
                    accepterDemande(sc);
                    break;
                case 0:
                    System.out.println(Colors.bold("→ Déconnexion..."));
                    break;
                default:
                    System.out.println(Colors.boldRed("Choix invalide, veuillez réessayer."));
            }
        } while (choixP != 0);
    }

    public static void accepterDemande(Scanner sc) {

        Gateway gateway = new Gateway();
        DemandeService demandeService = new DemandeService(gateway);
        ArrayList<Demande> demandes = gateway.getAllDemandesWaiting();
        System.out.println(Colors.bold("Demandes en cours :"));
        for (Demande d : demandes) {
            System.out.println(d.toString());
        }

        System.out.print(Colors.bold("Entrez le numéro de la demande à accepter : "));
        int numeroDemande = lireChoix(sc);
        if (demandeService.accepterDemande(numeroDemande)) {
            System.out.println(Colors.boldGreen("Demande numéro " + numeroDemande + " acceptée avec succès."));
        } else {
            System.out.println(Colors.boldRed("Erreur lors de l'acceptation de la demande numéro " + numeroDemande + "."));
        }
    }

    public static void afficherDemandes(Scanner sc) {
        Gateway gateway = new Gateway();
        ArrayList<Demande> demandes = gateway.getAllDemandes();
        System.out.println(Colors.bold("Toutes les demandes :"));
        for (Demande d : demandes) {
            System.out.println(d.toString());
        }
        System.out.println(Colors.bold("Fin de la liste des demandes. Appuyez sur Entrée pour continuer..."));
        sc.nextLine();
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        Authentication auth = new Authentication();
        Personne user = auth.getUser();
        String service = user.getService().getLibelle();

        System.out.println(Colors.bold("Utilisateur connecté : " + user.getNom() + " (" + service + ")"));

        // Redirection selon le service
        if (service.equalsIgnoreCase("dev")) {
            menuUtilisateur(sc);
        } else if (service.equalsIgnoreCase("gestionVehicule")) {
            menuPersonnel(sc);
        } else {
            System.out.println(Colors.boldRed("Service inconnu. Accès refusé."));
        }

        System.out.println("Merci d’avoir utilisé le système !");
        sc.close();
    }
}
