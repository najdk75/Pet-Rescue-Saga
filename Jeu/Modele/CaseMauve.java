package Jeu.Modele;

import java.awt.*;
import java.awt.event.ActionEvent;

public class CaseMauve extends CaseJouable{

    public CaseMauve(int x, int y) {
        super(x, y);
        setBackground(new Color(238, 130, 238));
    }
    public String toString(){
        return "M";
    }


}
