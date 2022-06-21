package Jeu.Vue;

import Jeu.Modele.*;
import Jeu.Controleur.*;



import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.event.AncestorListener;
import javax.swing.undo.UndoManager;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.*;


public class MenuFrame extends JFrame {
    private ImagePanel global = new ImagePanel("./Ressources/Images/petrescue.png");
    private MenuPanel menu = new MenuPanel();
    public MenuFrame() throws IOException {

        setContentPane(global);
        setResizable(false);
        setVisible(true);
        add(menu);
        pack();

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
    public class MenuPanel extends JPanel {
        BoutonMenu jouer, charger, quitter;

        public MenuPanel() {

            setOpaque(false);
            setBounds(355, 535, 490, 83);
            setLayout(new GridLayout(0, 3, 5, 5));

            jouer = new BoutonMenu("Jouer");
            charger = new BoutonMenu("Charger");
            quitter = new BoutonMenu("Quitter");

            jouer.addActionListener(event -> {
                try {
                    String input = JOptionPane.showInputDialog("Entrez votre pseudo!");
                    if (input != null) {
                        new NiveauFrame(new Joueur(input));
                        dispose();
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }
            });

            charger.addActionListener(event -> {
                String input = JOptionPane.showInputDialog("Entrez le nom avec lequel vous vous êtes enregistré!");
                Joueur joueur = Jeu.chargerJoueur(input);
                new NiveauFrame(joueur);
                dispose();
            });

            quitter.addActionListener(event -> dispose());
            add(jouer);
            add(charger);
            add(quitter);
        }

    }

    public class BoutonMenu extends JButton {
        Font newFont;

        {
            try {
                newFont = Font.createFont(Font.TRUETYPE_FONT, new File("./Ressources/Font/VacationsInParadisePersonalUse-qwml.ttf")).deriveFont(Font.BOLD, 30f);
                GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
                ge.registerFont(Font.createFont(Font.TRUETYPE_FONT, new File("./Ressources/Font/VacationsInParadisePersonalUse-qwml.ttf")));
            } catch (FontFormatException | IOException e) {
                e.printStackTrace();
            }

        }

        public BoutonMenu(String s) {
            setText(s);
            setOpaque(false);
            setForeground(Color.white);
            setContentAreaFilled(false);
            setBorderPainted(false);
            setFocusPainted(false);
            setFont(newFont);
        }
    }

}