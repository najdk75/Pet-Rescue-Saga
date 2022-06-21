package Jeu.Vue;

import Jeu.Modele.*;
import Jeu.Controleur.*;



import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class NiveauFrame extends JFrame {
    Joueur joueur;
    ImagePanel global = new ImagePanel("./Ressources/Images/bgforest.jpg");

    public NiveauFrame(Joueur j) {
        this.joueur = j;
        setContentPane(global);
        add(new NiveauPanel());
        setVisible(true);
        setResizable(false);

        pack();

    }

    public class NiveauPanel extends JPanel {

        ArrayList<BoutonNiveau> listeDeBoutons = new ArrayList<>();

        public NiveauPanel() {
            setBounds(290, 390, 100, 260);
            setLayout(new GridLayout(joueur.nombreDeNiveauxTotal,0,30,10));
            setOpaque(false);
            chargerNiveaux();
            rajouterActionListeners();
        }




        public void rajouterActionListeners() {
            for (BoutonNiveau bouton : listeDeBoutons) {
                bouton.addActionListener((event) -> {
                    int numeroDuNiveau = Character.getNumericValue(bouton.getText().charAt((bouton.getText().length()-1)));
                    String cheminDuNiveau = "./Ressources/Niveaux/" + bouton.getText().charAt(bouton.getText().length() - 1);
                    try {
                        joueur.chargerUnNiveau(numeroDuNiveau);
                        EventQueue.invokeLater(() -> {
                            try {
                                new GameFrame(joueur);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        });

                        dispose();
                    } catch (IOException e) {
                        System.out.println("ce fichier n'existe pas !!");
                    }
                });
            }
        }


        public void chargerNiveaux() {
            if (joueur.nombreDeNiveauxTotal > 0) {

                for (int i = 1; i <= joueur.nombreDeNiveauxTotal; i++) {
                    String numeroDuNiveau = Integer.toString(i);
                    var bouton = new BoutonNiveau(numeroDuNiveau);
                    if (!joueur.getAvancement().get(i)[0]){
                        bouton.setEnabled(false);
                    }
                    listeDeBoutons.add(bouton);
                    add(bouton);
                }

            }
        }
    }

    public class BoutonNiveau extends JButton {
        Font newFont;

        {
            try {

                newFont = Font.createFont(Font.TRUETYPE_FONT, new File("./Ressources/Font/DauberHandNumerals-G1eP.ttf")).deriveFont(50f);
                GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
                ge.registerFont(Font.createFont(Font.TRUETYPE_FONT, new File("./Ressources/Font/DauberHandNumerals-G1eP.ttf")));
            } catch (FontFormatException | IOException e) {
                e.printStackTrace();
            }


        }

        public BoutonNiveau(String s) {
            setText(s);
            setOpaque(false);
            setForeground(Color.black);
            setContentAreaFilled(false);
            setBorderPainted(false);
            setFocusPainted(false);
            setFont(newFont);


        }


    }


}
