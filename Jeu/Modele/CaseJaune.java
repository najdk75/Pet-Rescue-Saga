package Jeu.Modele;

import java.awt.*;
import java.awt.event.ActionEvent;

public class CaseJaune extends CaseJouable {


    public CaseJaune(int x, int y) {
        super(x, y);
        setBackground(new Color(234, 251, 29));
    }

    public String toString() {
        return "J";
    }



}
