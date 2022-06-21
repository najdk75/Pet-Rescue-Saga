package Jeu.Modele;

import java.awt.*;
import java.awt.event.ActionEvent;

public class CaseBleu extends CaseJouable {

    public CaseBleu(int x, int y) {
        super(x, y);
        setBackground(new Color(29, 56, 251));
    }

    public String toString() {
        return "B";
    }


}
