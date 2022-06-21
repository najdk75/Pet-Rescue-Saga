
package Jeu.Controleur;

import Jeu.Modele.*;
import Jeu.Vue.*;

import java.awt.*;
import java.io.*;
import java.util.Random;
import java.util.Scanner;


public class Jeu {


    private static Scanner sc = new Scanner(System.in);
    private static File[] listeDesNiveaux = (new File("./Ressources/Niveaux")).listFiles();

    public static void main(String[] args) throws IOException {
        PetRescueSaga();
    }
    //fonction fondamentale permettant de sauvegarder et donc de serialiser un joueur.
    public static void sauvegarder(Joueur j) {
        try {
            String chemin = "./Ressources/Sauvegardes/" + j.nom + ".ser";
            File file = new File(chemin);
            file.createNewFile();
            FileOutputStream fos = new FileOutputStream(file);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(j);
            oos.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    //Fonction fondamentale permettant de désérialiser un joueur
    public static Joueur chargerJoueur(String name) {
        try {
            String s = "./Ressources/Sauvegardes/" + name + ".ser";
            FileInputStream fis = new FileInputStream(s);
            ObjectInputStream ois = new ObjectInputStream(fis);
            return (Joueur) ois.readObject();

        } catch (FileNotFoundException e) {
            System.out.println("Il n'existe pas de sauvegarde avec votre nom!!");
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static int choixTerminalOuGraphiqueOuRobot() {
        int choix = 0;
        do {
            System.out.println("Voulez-vous jouer sur : \n - Terminal (0)\n - Interface graphique (1) \n - Laisser le robot jouer (2)");
            choix = sc.nextInt();
            if (choix != 0 && choix != 1 && choix != 2) {
                System.out.println("Choisissez 0 ou 1 ou 2");
            }

        } while (choix != 0 && choix != 1 && choix != 2);
        return choix;
    }


    public static void PetRescueSaga() throws IOException {
        int choix = choixTerminalOuGraphiqueOuRobot();
        if (choix == 0) jeuTerminal();
        else if (choix == 1) jeuGraphique();
        else jeuRobot();

    }

    private static void jeuGraphique() {
        EventQueue.invokeLater(() -> {
            try {
                new MenuFrame();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }
    // on crée un plateau correspondant au niveau que l'utilisateur aura choisi.
    // le robot choisira une combinaison (x,y) et si celle si est correcte, alors on supprimera dans le plateau et on l'affichera.
    private static void jeuRobot() throws IOException {
        String cheminDuNiveau = "./Ressources/Niveaux/" + demanderUnNiveau();
        Plateau plateau = new Plateau(new File(cheminDuNiveau));
        while (plateau.peutEncoreJouer()) {
            plateau.afficher();
            var choix = choixAleatoire(plateau);
            System.out.println("Le robot a choisi les coordonnées : (" + choix[0]+","+ choix[1]+ ")");
            plateau.supprimer(choix[0],choix[1]);
        }
        plateau.afficher();
    }

    // ici on demande au joueur le numéro du niveau auquel il veut jouer.
    private static String demanderUnNiveau() {
        int size = listeDesNiveaux.length;
        int choix = 0;
        do {
            System.out.println("Choisissez un niveau entre 1 et " + size);
            choix = sc.nextInt();
            if (choix > size || choix < 1) {
                System.out.println("Respectez les conditions!!");
            }
        } while (choix > size || choix < 1);
        return Integer.toString(choix);
    }

    private static int[] choixAleatoire(Plateau plateau) {

        Random rd = new Random();
        int x;
        int y;

        do {
            x = rd.nextInt(plateau.hauteur);
            y = rd.nextInt(plateau.largeur);
        } while (!plateau.possible(x, y));

        return new int[]{x, y};
    }

    private static void jeuTerminal() throws IOException {
        if (demanderPourChargerUnProfil()) {
            chargerProfil();
        } else {
            jouer(initialiserJoueur());
        }

    }

    private static void chargerProfil() throws IOException {

        System.out.println("Quel était votre pseudo ?");
        String reponse = sc.nextLine();
        Joueur joueur = chargerJoueur(reponse);

        if (joueur == null) {

            System.out.println("Vous n'avez pas de sauvegarde sous ce nom, voulez vous ressayer?");
            jouer(initialiserJoueur());

        } else {
            System.out.println("Welcome Back " + joueur.nom + ", tu es au niveau " + joueur.getP().numeroDuNiveau + ". C'est parti!!!");
            jouer(joueur);

        }


    }


    private static void jouer(Joueur joueur) throws IOException {
        var plateau = joueur.getP();

        while (plateau.peutEncoreJouer()) {
            plateau.afficher();
            if (plateau.score >= 100) {
                if (demanderFusee()) {
                    System.out.println(plateau.score);
                    int colonneASupprimer = demanderColonneASupprimer(plateau);
                    plateau.fusee(colonneASupprimer);
                    System.out.println(plateau.score);
                    plateau.afficher();
                }
            }
            System.out.println("Choisissez des coordonnées");
            plateau.supprimer(sc.nextInt(), sc.nextInt());

        }

        if (plateau.gagner()) {
            System.out.println("Bravo vous avez sauvé tous les animaux");
            joueur.niveauGagne();

            if (!joueur.isJeuFini()) {
                joueur.accesAuNiveauSuivant();
            }

        } else System.out.println("Vous avez perdu!");

        joueur.resetLevel();
        choixFinDeJeu(joueur);

    }

    private static void choixFinDeJeu(Joueur joueur) throws IOException {
        if (!joueur.isJeuFini()) {
            if (joueur.peutAccederAuProchainNiveau()) {
                if (demanderProchainNiveau()) {
                    joueur.miseAJourPlateauJoueur();
                    jouer(joueur);

                } else if (demanderSauvegarder()) {
                    sauvegarder(joueur);
                    boolean choix = demanderProchainNiveau();
                    if (choix) {
                        joueur.miseAJourPlateauJoueur();
                        jouer(joueur);
                    } else System.out.println("Fin du jeu! byebye");

                } else System.out.println("Fin du jeu!");
            }

        } else if (demanderRecommencer()) {
            if (demanderCharger()) {
                chargerUnNiveau(joueur);
                jouer(joueur);
            } else {
                joueur.resetAll();
                jouer(joueur);
            }


        } else System.out.println("Fin du jeu!");

    }

    private static boolean demanderCharger() {
        int choix = 0;
        do {
            System.out.println("Voulez-vous charger un niveau? 1/0 ");
            choix = sc.nextInt();
            if (choix != 0 && choix != 1) {
                System.out.println("Choisissez 1 ou 0");
            }

        } while (choix != 0 && choix != 1);
        return choix == 1;
    }

    private static boolean demanderFusee() {
        int choix = 0;
        do {
            System.out.println("Voulez vous utiliser une fusée? 1/0 ");
            choix = sc.nextInt();
            if (choix != 0 && choix != 1) {
                System.out.println("Choisissez 1 ou 0");
            }

        } while (choix != 0 && choix != 1);
        return choix == 1;

    }

    private static int demanderColonneASupprimer(Plateau p) {
        int choix = 0;
        do {
            System.out.println("Choisissez une colonne entre 0 et " + (p.largeur - 1));
            choix = sc.nextInt();
            if (!p.dansLeTableau(0, choix) || p.colonneObstacleOuVide(choix)) {
                System.out.println("Respectez les conditions et ne choisissez pas une colonne ne contenant aucune case jouable!");
            }

        } while (!p.dansLeTableau(0, choix) || p.colonneObstacleOuVide(choix));
        return choix;

    }

    private static void chargerUnNiveau(Joueur j) throws IOException {
        int choix = 0;

        do {
            System.out.println("Choisissez un niveau entre 1 et " + j.nombreDeNiveauxTotal);
            choix = sc.nextInt();
            if (choix < 1 || choix > j.nombreDeNiveauxTotal) {
                System.out.println("Choisissez bien svp");
            }

        } while (choix < 1 || choix > j.nombreDeNiveauxTotal);
        j.chargerUnNiveau(choix);

    }


    private static boolean demanderRecommencer() {
        int choix = 0;
        do {
            System.out.println("Voulez-vous recommencer? 1/0 ");
            choix = sc.nextInt();
            if (choix != 0 && choix != 1) {
                System.out.println("Choisissez 1 ou 0");
            }

        } while (choix != 0 && choix != 1);
        return choix == 1;
    }

    private static boolean demanderProchainNiveau() {
        int choix = 0;
        do {
            System.out.println("Voulez-vous passer au niveau supérieur? 1/0 ");
            choix = sc.nextInt();
            if (choix != 0 && choix != 1) {
                System.out.println("Choisissez 1 ou 0");
            }

        } while (choix != 0 && choix != 1);
        return choix == 1;
    }

    private static boolean demanderSauvegarder() {
        int choix = 0;
        do {
            System.out.println("Voulez-vous sauvegarder? 1/0 ");
            choix = sc.nextInt();
            if (choix != 0 && choix != 1) {
                System.out.println("Choisissez 1 ou 0");
            }

        } while (choix != 0 && choix != 1);
        return choix == 1;
    }

    private static Joueur initialiserJoueur() throws IOException {

        System.out.println("Tapez votre nom");
        String name = sc.next();

        return new Joueur(name);
    }

    private static boolean demanderPourChargerUnProfil() {
        int choix = 0;
        do {
            System.out.println("Souhaitez-vous charger un profil existant? Tapez 1 pour oui, tapez 0 pour non");
            choix = sc.nextInt();
            if (choix != 0 && choix != 1) {
                System.out.println("Choisissez 0 ou 1");
            }

        } while (choix != 1 && choix != 0);

        return (choix == 1);

    }


}

