package Jeu.Modele;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public abstract class CaseJouable extends Case implements ActionListener {

    public CaseJouable(int x, int y) {
        super(x, y);
        setEnabled(true);
    }
    public void actionPerformed(ActionEvent actionEvent) {

    }
}
