package com.example.reservation;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Scanner;

public class App {

    public static void creerDemande(Personne utilisateur, Scanner sc, DemandeService demandeService) {
        ArrayList<Type> typesDisponibles = demandeService.getTypesDisponibles();
        if (typesDisponibles.isEmpty()) {
            System.out.println("Aucun type de véhicule disponible.");
            return;
        }

        System.out.println("\nTypes de véhicules disponibles :");
        for (int i = 0; i < typesDisponibles.size(); i++) {
            Type type = typesDisponibles.get(i);
            System.out.println((i + 1) + " - " + type.getLibelle());
        }
        System.out.print("Choisissez le type de véhicule : ");
        int typeChoice = lireChoix(sc);
        if (typeChoice < 1 || typeChoice > typesDisponibles.size()) {
            System.out.println("Type de véhicule invalide.");
            return;
        }
        Type typeVehicule = typesDisponibles.get(typeChoice - 1);

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

    public static void voirMesReservations(Personne user, DemandeService demandeService) {
        ArrayList<Demande> demandes = demandeService.getDemandesUtilisateur(user.getMatricule());
        if (demandes.isEmpty()) {
            System.out.println("Aucune réservation trouvée.");
            return;
        }

        System.out.println("\nMes réservations :");
        for (Demande demande : demandes) {
            System.out.println("N°" + demande.getNumero()
                    + " | Réservée le: " + demande.getDateReserv()
                    + " | Début: " + demande.getDateDebut()
                    + " | Type: " + demande.getType().getLibelle()
                    + " | Durée: " + demande.getDuree() + " jour(s)"
                    + " | État: " + demande.getEtat());
        }
    }

    public static void menuUtilisateur(Scanner sc, Personne user, DemandeService demandeService) {
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
                    creerDemande(user, sc, demandeService);
                    break;
                case 2:
                    System.out.println("→ Affichage de vos réservations...");
                    voirMesReservations(user, demandeService);
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
        Gateway gateway = new Gateway();

        Authentication auth = new Authentication(sc, gateway);
        Personne user = auth.getUser();
        DemandeService demandeService = new DemandeService(gateway);

        String service = user.getService().getLibelle();

        System.out.println("Utilisateur connecté : " + user.getNom() + " (" + service + ")");

        if (service.equalsIgnoreCase("dev")) {
            menuUtilisateur(sc, user, demandeService);
        } else if (service.equalsIgnoreCase("gestionVehicule")) {
            menuPersonnel(sc);
        } else {
            System.out.println("Service inconnu. Accès refusé.");
        }

        System.out.println("Merci d’avoir utilisé le système !");
        sc.close();
    }
}
