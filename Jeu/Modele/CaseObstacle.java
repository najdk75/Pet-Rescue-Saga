package Jeu.Modele;

import java.awt.*;
import java.awt.event.ActionEvent;

public class CaseObstacle extends CaseNonJouable {


    public CaseObstacle(int x, int y) {
        super(x, y);
        setBackground(Color.black);

    }

    public String toString() {
        return "/";
    }



}
