package com.example.reservation;

import java.time.LocalDate;
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
                    faireDemande();
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
    public static void faireDemande(){
         Authentication auth = new Authentication();
        Personne user = auth.getUser();
        String service = user.getService().getLibelle();
        Gateway gateway = new Gateway();          // Assure-toi que la connexion est initialisée
        DemandeService demandeService = new DemandeService(gateway);

        // Récupérer les informations de la demande via Scanner
        Scanner sc = new Scanner(System.in);
        System.out.println("Entrez la date de début (AAAA-MM-JJ) :");
        LocalDate dateDebut = LocalDate.parse(sc.nextLine());

        System.out.println("Entrez le numéro du type de véhicule :");
        int typeNumero = Integer.parseInt(sc.nextLine());
        Type type = new Type(typeNumero, ""); // tu peux récupérer le libellé si tu veux

        System.out.println("Entrez la durée (en heures) :");
        int duree = Integer.parseInt(sc.nextLine());

        // Créer la demande
        boolean success = demandeService.creerDemande(user, type, dateDebut, duree);

        if (success) {
            System.out.println("La demande a été ajoutée avec succès !");
        } else {
            System.out.println("Erreur lors de l'ajout de la demande.");
        }
        
        sc.close();
    }
    public static void modifierDemande() {
    Gateway gateway = new Gateway();
    Scanner sc = new Scanner(System.in);

    System.out.print("Entrez le numéro de la demande à modifier : ");
    int numero = Integer.parseInt(sc.nextLine());

    Demande demande = gateway.getDemandeByNumero(numero);

    if (demande == null) {
        System.out.println("Aucune demande trouvée avec ce numéro.");
        return;
    }

    System.out.println("\n Demande actuelle ");
    System.out.println("Date de début : " + demande.getDateDebut());
    System.out.println("Durée : " + demande.getDuree());
    System.out.println("État : " + demande.getEtat());
    System.out.println("Type : " + (demande.getType() != null ? demande.getType().getLibelle() : "aucun"));
    System.out.println("Véhicule : " + (demande.getVehicule() != null ? demande.getVehicule().getImmatriculation() : "aucun"));
    System.out.println("Personne : " + (demande.getPersonne() != null ? demande.getPersonne().getNom() : "aucune"));
    System.out.println("Date retour effectif : " + demande.getDateretoureffectif());

    System.out.println("\n Modification");


    System.out.print("Nouvelle date de début (AAAA-MM-JJ) [laisser vide pour ne pas changer] : ");
    String dateDebutInput = sc.nextLine();
    LocalDate newDateDebut = dateDebutInput.isEmpty() ? demande.getDateDebut() : LocalDate.parse(dateDebutInput);


    System.out.print("Nouvelle durée (en heures) [laisser vide pour ne pas changer] : ");
    String dureeInput = sc.nextLine();
    int newDuree = dureeInput.isEmpty() ? demande.getDuree() : Integer.parseInt(dureeInput);


    System.out.print("Nouvelle date de retour effective (AAAA-MM-JJ ou vide) : ");
    String retourInput = sc.nextLine();
    LocalDate newDateRetour = retourInput.isEmpty() ? demande.getDateretoureffectif() : LocalDate.parse(retourInput);


    System.out.print("Nouvel état (demandée, validée, refusée...) [laisser vide pour ne pas changer] : ");
    String etatInput = sc.nextLine();
    String newEtat = etatInput.isEmpty() ? demande.getEtat() : etatInput;


    System.out.print("Nouvel ID du type de véhicule [laisser vide pour ne pas changer] : ");
    String typeInput = sc.nextLine();
    Type newType;
    if (typeInput.isEmpty()) {
        newType = demande.getType();
    } else {
        int idType = Integer.parseInt(typeInput);
        newType = gateway.getTypeById(idType);
    }

    
    System.out.print("Nouvelle immatriculation du véhicule [laisser vide pour ne pas changer] : ");
    String immatInput = sc.nextLine();
    Vehicule newVehicule;
    if (immatInput.isEmpty()) {
        newVehicule = demande.getVehicule();
    } else {
        newVehicule = gateway.getVehiculeByImmatriculation(immatInput);
    }


    System.out.print("Nouveau matricule de la personne [laisser vide pour ne pas changer] : ");
    String matriculeInput = sc.nextLine();
    Personne newPersonne;
    if (matriculeInput.isEmpty()) {
        newPersonne = demande.getPersonne();
    } else {
        newPersonne = gateway.getPersonneByMatricule(matriculeInput);
    }


    Demande updated = new Demande(
        demande.getDateReserv(),
        demande.getNumero(),
        newDateDebut,
        newPersonne,
        newType,
        newVehicule,
        newDuree,
        newDateRetour,
        newEtat
    );


    gateway.updateDemande(updated);
    System.out.println("La demande a été mise à jour avec succès !");
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
                case 2:modifierDemande(); 
                    break;
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
