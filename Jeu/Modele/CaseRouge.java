package Jeu.Modele;

import java.awt.*;
import java.awt.event.ActionEvent;

public class CaseRouge extends CaseJouable{

    public CaseRouge(int x, int y) {
        super(x, y);
        setBackground(new Color(147,0,0));
    }
    public String toString(){
        return "R";
    }


}
