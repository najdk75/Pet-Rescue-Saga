package Jeu.Modele;

import java.awt.*;
import java.awt.event.ActionEvent;

public class CaseVerte extends CaseJouable {

    public CaseVerte(int x, int y) {
        super(x, y);
        setBackground(new Color(0, 147, 42 ));
    }

    public String toString() {
        return "V";
    }


}