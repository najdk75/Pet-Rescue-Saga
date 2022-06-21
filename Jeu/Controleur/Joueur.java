package Jeu.Controleur;

import Jeu.Modele.*;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class Joueur implements Serializable {
    private Map<Integer, Boolean[]> avancement = new HashMap<>();
    /**
     * on relie le numéro du niveau à un tableau de boolean de taille 2, la premiere case montrera si le niveau est débloqué
     * la deuxieme case montrera si le niveau a été gagné.
     */
    private Plateau p;
    public String nom;


    public int nombreDeNiveauxTotal;

    private boolean jeuFini = false;

    public Joueur(String nom) throws IOException {
        initialisation();
        p = new Plateau(new File("./Ressources/Niveaux/1"));
        this.nom = nom;


    }

    public Joueur() throws IOException {
        initialisation();
        p = new Plateau(new File("./Ressources/Niveaux/1"));

    }

    public void resetAll() throws IOException {
        p = new Plateau(new File("./Ressources/Niveaux/1"));
    }

    public void resetLevel() throws IOException {
        p = new Plateau(new File("./Ressources/Niveaux/" + p.numeroDuNiveau));
    }

    public void chargerUnNiveau(int n) throws IOException {
        p = new Plateau(new File("./Ressources/Niveaux/" + n));
    }

    private void initialisation() {
        File[] f = (new File("./Ressources/Niveaux")).listFiles();

        assert f != null;
        Arrays.sort(f);
        nombreDeNiveauxTotal = f.length;

        var tableauPremierNiveau = new Boolean[2];
        tableauPremierNiveau[0] = true;
        tableauPremierNiveau[1] = false;
        avancement.put(Integer.parseInt(f[0].getName()), tableauPremierNiveau);

        for (int i = 1; i < f.length; i++) {
            var tableau = new Boolean[2];
            tableau[0] = false;
            tableau[1] = false;
            avancement.put(Integer.parseInt(f[i].getName()), tableau);
        }
    }


    public boolean peutAccederAuProchainNiveau() {

        return (p.numeroDuNiveau + 1 <= nombreDeNiveauxTotal && avancement.get(p.numeroDuNiveau + 1)[0]);
        // on verifie si on est pas déjà dans le dernier niveau, et que le prochain niveau a été débloqué.
    }

    // on debloque le prochain niveau.
    public void accesAuNiveauSuivant() {
        if (p.numeroDuNiveau + 1 <= nombreDeNiveauxTotal) {
            if (!avancement.get(p.numeroDuNiveau + 1)[0]) {
                avancement.get(p.numeroDuNiveau + 1)[0] = true;

            }
        }
    }

    public void niveauGagne() {
        if (!avancement.get(p.numeroDuNiveau)[1]) {
            avancement.get(p.numeroDuNiveau)[1] = true;
        }
        accesAuNiveauSuivant();
        jeuFini();
    }

    // on passe au niveau suivant en changeant de plateau
    public void miseAJourPlateauJoueur() throws IOException {

        if (!jeuFini) {
            int x = p.numeroDuNiveau + 1;
            if (x <= nombreDeNiveauxTotal) {
                String nouveauChemin = "./Ressources/Niveaux/" + x;
                File prochainNiveau = new File(nouveauChemin);

                this.p = new Plateau(prochainNiveau);

            }
        } else {
            System.out.println("Vous avez fini le jeu!");
        }

    }

    private boolean jeuTermine() {
        return avancement.get(nombreDeNiveauxTotal)[1];
    }

    private void jeuFini() {
        if (jeuTermine()) {
            jeuFini = true;
        }
    }

    public Plateau getP() {
        return p;
    }

    public void setP(Plateau p) {
        this.p = p;
    }


    public Map<Integer, Boolean[]> getAvancement() {
        return avancement;
    }


    public boolean isJeuFini() {
        return jeuFini;
    }
}
