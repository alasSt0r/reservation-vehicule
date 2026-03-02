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

        Type typeVehicule;
        while (true) {
            System.out.println("\nTypes de véhicules disponibles :");
            for (int i = 0; i < typesDisponibles.size(); i++) {
                Type type = typesDisponibles.get(i);
                System.out.println((i + 1) + " - " + type.getLibelle());
            }
            System.out.print("Choisissez le type de véhicule : ");
            int typeChoice = lireChoix(sc);
            if (typeChoice >= 1 && typeChoice <= typesDisponibles.size()) {
                typeVehicule = typesDisponibles.get(typeChoice - 1);
                break;
            }
            System.out.println("Type de véhicule invalide. Veuillez réessayer.");
        }

        LocalDate dateDebut;
        while (true) {
            System.out.print("\nDate de début (format AAAA-MM-JJ) : ");
            String dateDebutStr = sc.nextLine();
            try {
                dateDebut = LocalDate.parse(dateDebutStr);
                if (dateDebut.isBefore(LocalDate.now())) {
                    System.out.println("La date de début ne peut pas être dans le passé.");
                    continue;
                }
                break;
            } catch (Exception e) {
                System.out.println("Format de date invalide.");
            }
        }

        int duree;
        while (true) {
            System.out.print("Durée de réservation en jours : ");
            try {
                duree = Integer.parseInt(sc.nextLine());
                if (duree <= 0) {
                    System.out.println("La durée doit être positive.");
                    continue;
                }
                break;
            } catch (NumberFormatException e) {
                System.out.println("Durée invalide.");
            }
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
            System.out.println("4 - Accepter une demande");
            System.out.println("0 - Quitter");
            System.out.print("Votre choix : ");
            choixP = lireChoix(sc);

            switch (choixP) {
                case 1:
                case 2:modifierDemande(); 
                    break;
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
    for (int i = 0; i < demandes.size(); i++) {
        System.out.println(i + " - " + demandes.get(i).toString());
    }

    System.out.print(Colors.bold("Entrez le numéro de la demande à accepter : "));
    int indexDemande = lireChoix(sc);
    if (indexDemande < 0 || indexDemande >= demandes.size()) {
        System.out.println(Colors.boldRed("Numéro de demande invalide."));
        return;
    }
    Demande demande = demandes.get(indexDemande);
    Type type = demande.getType();
    System.out.println("Véhicules disponibles pour le type : " + type.getLibelle());
    ArrayList<Vehicule> vehicules = gateway.getVehiculesByType(type);
    if (vehicules.isEmpty()) {
        System.out.println(Colors.boldRed("Aucun véhicule disponible pour ce type."));
        return;
    }
    for (int i = 0; i < vehicules.size(); i++) {
        System.out.println(i + " - " + vehicules.get(i).toString());
    }
    System.out.print(Colors.bold("Entrez le numéro du véhicule à associer : "));
    int indexVehicule = lireChoix(sc);
    if (indexVehicule < 0 || indexVehicule >= vehicules.size()) {
        System.out.println(Colors.boldRed("Numéro de véhicule invalide."));
        return;
    }
    Vehicule vehicule = vehicules.get(indexVehicule);
    if (gateway.accepterDemande(demande.getNumero(), vehicule.getImmatriculation())) {
        System.out.println(Colors.boldGreen("Demande numéro " + demande.getNumero() + " acceptée avec succès avec le véhicule " + vehicule.getImmatriculation() + "."));
    } else {
        System.out.println(Colors.boldRed("Erreur lors de l'acceptation de la demande numéro " + demande.getNumero() + "."));
    }
}

    public static void afficherDemandesEnCours(Scanner sc) {
        Gateway gateway = new Gateway();
        ArrayList<Demande> demandes = gateway.getAllDemandesWaiting();
        System.out.println(Colors.bold("Demandes en cours :"));
        for (Demande d : demandes) {
            System.out.println(d.toString());
        }
        System.out.println(Colors.bold("Fin de la liste des demandes en cours. Appuyez sur Entrée pour continuer..."));
        sc.nextLine();
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
        Gateway gateway = new Gateway();

        Authentication auth = new Authentication(sc, gateway);
        Personne user = auth.getUser();
        if (user == null) {
            System.err.println("Arrêt de l'application: utilisateur non authentifié.");
            sc.close();
            return;
        }
        DemandeService demandeService = new DemandeService(gateway);

        String service = user.getService().getLibelle();

        System.out.println(Colors.bold("Utilisateur connecté : " + user.getNom() + " (" + service + ")"));

        if (service.equalsIgnoreCase("dev")) {
            menuUtilisateur(sc, user, demandeService);
        } else if (service.equalsIgnoreCase("gestionVehicule")) {
            menuPersonnel(sc);
        } else {
            System.out.println(Colors.boldRed("Service inconnu. Accès refusé."));
        }

        System.out.println("Merci d’avoir utilisé le système !");
        sc.close();
    }
}
