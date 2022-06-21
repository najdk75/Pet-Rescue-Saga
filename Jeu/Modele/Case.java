package Jeu.Modele;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.io.Serializable;

public abstract class Case extends JButton implements Serializable {

     private int x;
     private int y;


    public Case(int x, int y) {
        this.x = x;
        this.y = y;


    }

    public void setCoordonnees(int x, int y) {
        this.x = x;
        this.y = y;


    }

    public int getCoordX(){
        return this.x;
    }
    public int getCoordY(){
        return this.y;
    }

}
