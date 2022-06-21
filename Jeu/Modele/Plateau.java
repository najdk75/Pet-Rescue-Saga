package Jeu.Modele;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.sql.SQLOutput;
import java.util.ArrayList;
import java.util.Scanner;


public class Plateau implements Serializable {
    public int numeroDuNiveau;
    public int hauteur;
    public int largeur;
    public int score = 0;
    public int nbAnimaux;
    public Case[][] plateau;

    public Plateau(File file) throws IOException {
        plateau = calculer(file);
        numeroDuNiveau = Integer.parseInt(file.getName());
    }



    public boolean peutEncoreJouer(){
        return nbAnimaux > 0 && !plusDeCaseAdjacente();
    }
    public boolean gagner(){
        return nbAnimaux == 0;

    }

    public boolean perdu(){
        return plusDeCaseAdjacente() && nbAnimaux > 0;
    }

    private boolean plusDeCaseAdjacente(){

        for (Case[] p : plateau){
            for (Case c : p){
                if (c!= null && !(c instanceof CaseObstacle)){
                    if (adjacent(c)) return false;
                }

            }
        }
        return true;
    }

    private void animauxSauve() {
        for (int j = 0; j < largeur; j++) {
            if (plateau[hauteur - 1][j] instanceof CaseAnimaux) {
                plateau[hauteur - 1][j] = null;
                nbAnimaux--;
                reorganiser();
            }
        }
    }


    private void reorganiser() {
        while (peutEncoreOrganiser() || peutEncoreDescendre()) {
            descendre();
            deplacer();

        }
        animauxSauve();
    }

    private boolean peutEncoreDescendre() {
        for (int j = 0; j < largeur; j++) {
            for (int i = 0; i < hauteur; i++) {
                if (sousColonneATrou(i, j)) return true;
            }
        }
        return false;
    }

    private void ajouter(int x, int y, Case c) {
        if (dansLeTableau(x, y)) {
            plateau[x][y] = c;
        }

    }

    public void afficher() {
        System.out.print("  ");

        for (int i = 0; i < largeur; i++) {
            System.out.print(" " + i);
        }
        System.out.println();
        for (int i = 0; i < largeur * 2 + 2; i++) {
            System.out.print("-");
        }
        System.out.println();
        for (int i = 0; i < hauteur; i++) {
            System.out.print(i + "| ");
            for (int j = 0; j < largeur; j++) {
                if (plateau[i][j] == null) {
                    System.out.print(". ");
                } else {
                    System.out.print(plateau[i][j] + " ");
                }
            }
            System.out.println();
        }
    }

    public void fusee(int y){
        if (!colonneObstacleOuVide(y)) {
            for (int i = 0; i < hauteur; i++) {
                if (plateau[i][y] instanceof CaseJouable) {
                    plateau[i][y] = null;
                }
            }
            reorganiser();
            score = 0;
        }


    }




    public void supprimer(int x, int y)  {

        if (possible(x, y)) {
            supprimerFact(x, y);

            reorganiser();

        }

    }

    private void supprimerFact(int x, int y) {

        var tmp = plateau[x][y];
        plateau[x][y] = null;
        score += 10;

        if (dansLeTableau(x - 1, y) && isAdja(x - 1, y, tmp)) {
            supprimerFact(x - 1, y);

        }
        if (dansLeTableau(x + 1, y) && isAdja(x + 1, y, tmp)) {
            supprimerFact(x + 1, y);

        }
        if (dansLeTableau(x, y - 1) && isAdja(x, y - 1, tmp)) {
            supprimerFact(x, y - 1);

        }
        if (dansLeTableau(x, y + 1) && isAdja(x, y + 1, tmp)) {
            supprimerFact(x, y + 1);

        }

    }

    /**
     * METHODES SERVANT À CONTRÔLER LES POSSIBILITÉS DE DÉPLACEMENT DU CHOIX DU JOUEUR
     **/
    public boolean possible(int x, int y)  {

        if (!dansLeTableau(x, y)) {
            System.out.println(("Les valeurs choisies sont hors du tableau."));
            return false;
        }

        if (!caseJouable(x, y)) {
            System.out.println("Cette case n'est pas jouable!");
            return false;
        }

        return adjacent(plateau[x][y]);
    }

    private boolean isAdja(int x, int y, Case c) {
        return plateau[x][y] != null && plateau[x][y].getClass().equals(c.getClass());
    }

    private boolean adjacent(Case c) {

        int x = c.getCoordX();
        int y = c.getCoordY();

        if (dansLeTableau(x - 1, y) && plateau[x - 1][y] != null) {
            if (plateau[x - 1][y].getClass().equals(c.getClass())) return true;
        }
        if (dansLeTableau(x + 1, y) && plateau[x + 1][y] != null) {
            if (plateau[x + 1][y].getClass().equals(c.getClass())) return true;
        }
        if (dansLeTableau(x, y - 1) && plateau[x][y - 1] != null) {
            if (plateau[x][y - 1].getClass().equals(c.getClass())) return true;
        }
        if (dansLeTableau(x, y + 1) && plateau[x][y + 1] != null) {
            return plateau[x][y + 1].getClass().equals(c.getClass());
        }

        return false;
    }

    public boolean dansLeTableau(int x, int y) {
        return x >= 0 && x < hauteur && y >= 0 && y < largeur;

    }

    private boolean caseJouable(int x, int y) {
        return plateau[x][y] instanceof CaseJouable;
    }


    private void descendre() {
        for (int j = 0; j < largeur; j++) {
            descendreCases(j);
        }
    }

    private void descendreCases(int y) {
        int i = 0;
        while (colonneATrou(y)) {

            if (!(plateau[i][y] instanceof CaseObstacle) && plateau[i][y] != null) {
                modifierCases(trouverPositionDeLaPremiereCaseADescendre(y), y);

            }
            i++;
        }
    }

    private void modifierCases(int x, int y) {

        var tmp = plateau[x][y];
        plateau[x][y] = null;
        int newX = trouverPositionDeProchaineCaseLibre(x, y);

        plateau[newX][y] = tmp;

        plateau[newX][y].setCoordonnees(newX, y);

    }


    private int trouverPositionDeLaPremiereCaseADescendre(int y) {
        for (int i = 0; i < hauteur - 1; i++) {
            if ((plateau[i][y] != null && !(plateau[i][y] instanceof CaseObstacle) && plateau[i + 1][y] == null))
                return i;
        }
        return hauteur - 1;
    }

    private int trouverPositionDeProchaineCaseLibre(int x, int y) {

        for (int i = x; i < hauteur - 1; i++) {
            if (plateau[i + 1][y] != null) return i;
        }
        return hauteur - 1;
    }

    private boolean colonneATrou(int y) {
        for (int i = 0; i < hauteur - 1; i++) {
            if ((plateau[i][y] != null && !(plateau[i][y] instanceof CaseObstacle)) && plateau[i + 1][y] == null) {
                return true;
            }
        }
        return false;
    }

    private boolean sousColonneATrou(int x, int y) {
        for (int i = x; i < hauteur - 1; i++) {
            if ((plateau[i][y] != null && !(plateau[i][y] instanceof CaseObstacle)) && plateau[i + 1][y] == null) {
                return true;
            }
        }
        return false;
    }

    /**
     * CI-DESSOUS LES MÉTHODES QUI SERVENT À DÉPLACER LES COLONNES SI ELLES EN ONT LA POSSIBILITÉ.
     */

    private void deplacer() {

        int y = 1;
        while (y < largeur) {
            deplacerColonne(y);
            descendre();
            y++;
        }
    }

    private boolean nonObstacleEtnonNull(int x, int y) {
        return (plateau[x][y] != null && !(plateau[x][y] instanceof CaseObstacle));
    }


    private void deplacerColonne(int y) {
        if (!colonneObstacleOuVide(y)) {
            var positionDebutSousColonne = positionPremiereCaseNonObstacle(y);

            for (int i = positionDebutSousColonne; i < hauteur; i++) {
                if (plateau[i][y] != null && peutDeplacerVersLaGauche(i, y)) {
                    deplacerVersLaGauche(i, y);
                    i = positionDeLaProchaineCaseObstacle(i, y);
                }
            }
        }
    }

    private void deplacerVersLaGauche(int x, int y) {

        for (int i = x; i < positionDeLaProchaineCaseObstacle(x, y); i++) {
            plateau[i][y - 1] = plateau[i][y];
            plateau[i][y] = null;
            plateau[i][y - 1].setCoordonnees(i, y - 1);


        }
    }

    private boolean peutDeplacerVersLaGauche(int x, int y) {
        if (dansLeTableau(x, y - 1)) {
            var positionCaseObstacle = positionDeLaProchaineCaseObstacle(x, y);
            var positionCaseObstacleGauche = positionDeLaProchaineCaseObstacle(x, y - 1);
            if (positionCaseObstacleGauche >= positionCaseObstacle) {
                return colonneVide(x, y - 1);
            }
        }

        return false;
    }

    public boolean colonneObstacleOuVide(int y) {
        for (int i = 0; i < hauteur; i++) {
            if (nonObstacleEtnonNull(i, y)) {
                return false;
            }
        }
        return true;
    }

    private int positionPremiereCaseNonObstacle(int y) {

        for (int i = 0; i < hauteur; i++) {
            if (nonObstacleEtnonNull(i, y)) {
                return i;
            }
        }
        return -1;
    }

    // vérifie si une sous-colonne est vide (peut être la colonne entière)
    private boolean colonneVide(int x, int y) {
        for (int i = x; i < positionDeLaProchaineCaseObstacle(x, y); i++) {
            if (plateau[i][y] != null) {
                return false;
            }
        }
        return true;
    }

    private int positionDeLaProchaineCaseObstacle(int x, int y) {
        for (int i = x; i < hauteur; i++) {
            if (plateau[i][y] instanceof CaseObstacle) {
                return i;
            }
        }
        return hauteur;
    }

    private boolean peutEncoreOrganiser() {
        for (int j = 0; j < largeur; j++) {
            if (!colonneObstacleOuVide(j)) {
                for (int i = 0; i < hauteur; i++) {
                    if (nonObstacleEtnonNull(i, j) && peutDeplacerVersLaGauche(i, j)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }


    private Case[][] calculer(File file) throws IOException {//calcule la hauteur et largeur du plateau selon le niveau

        FileInputStream fileStream = new FileInputStream(file);
        InputStreamReader input = new InputStreamReader(fileStream);
        BufferedReader reader = new BufferedReader(input);

        var line = reader.readLine();
        hauteur = Character.getNumericValue(line.charAt(0));

        largeur = Character.getNumericValue(line.charAt(1));
        plateau = new Case[hauteur][largeur];


        var caracteristiques = parse(file);
        return loadPlateau(caracteristiques) ;
    }

    private ArrayList<String> parse(File file) throws FileNotFoundException { //chaque string de la liste contient les informations d'une case
        Scanner input = new Scanner(file);
        String line;
        ArrayList<String> data = new ArrayList<>();
        line = input.nextLine();
        while (input.hasNext()) {

            line = input.nextLine();

            line = line.replace("|", "");
            data.add(line);
        }
        input.close();
        return data;
    }

    private Case[][] loadPlateau(ArrayList<String> liste) throws FileNotFoundException { //rempli le plateau selon le niveau
        int x, y;
        char type, nature;

        for (String s : liste) {
            y = Character.getNumericValue(s.charAt(0));
            x = Character.getNumericValue(s.charAt(1));
            type = s.charAt(2);
            nature = s.charAt(3);
            // switch nature p.ajouter(x,y, new CaseNature(x,y)
            remplirPlateau(x, y, type, nature);
            //System.out.println(l.get(i));
        }
        return plateau;
    }

    private void remplirPlateau(int x, int y, char type, char nature) {

        if (type == 'j') { //case jouable
            switch (nature) {
                case 'v':
                    this.ajouter(x, y, new CaseVerte(x, y));
                    break;
                case 'r':
                    this.ajouter(x, y, new CaseRouge(x, y));
                    break;
                case 'j':
                    this.ajouter(x, y, new CaseJaune(x, y));
                    break;
                case 'b':
                    this.ajouter(x, y, new CaseBleu(x, y));
                    break;
                case 'm':
                    this.ajouter(x, y, new CaseMauve(x, y));
                    break;
                default:
                    break;
            }
        } else { // (type == 'n') case non jouable

            switch (nature) {
                case 'o':
                    this.ajouter(x, y, new CaseObstacle(x, y));
                    break;
                case 'v':
                    this.ajouter(x, y, null);
                    break;
                case 'a':
                    this.ajouter(x, y, new CaseAnimaux(x, y));
                    nbAnimaux++;
                    break;

                default:
                    break;
            }
        }

    }
}