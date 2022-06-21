package Jeu.Modele;

import java.awt.*;
import java.awt.event.ActionEvent;

public class CaseAnimaux extends CaseNonJouable{

    public CaseAnimaux(int x, int y) {
        super(x, y);
        setBackground(Color.pink);

    }

    public String toString(){
        return "*";
    }


}
