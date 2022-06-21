package Jeu.Modele;

public abstract class CaseNonJouable extends Case {

    public CaseNonJouable(int x, int y) {
        super(x, y);
        setEnabled(false);
    }
}
