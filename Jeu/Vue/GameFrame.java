package Jeu.Vue;

import Jeu.Modele.*;
import Jeu.Controleur.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

public class GameFrame extends JFrame {

    public ImagePanel global = new ImagePanel("./Ressources/Images/game.jpg");
    public JPanel informationPanel = new JPanel();
    public JLabel score = new JLabel();
    public JLabel nom;
    public JLabel niveau;
    public PlateauPanel p;
    public FuseeBouton fuseeBouton;
    public Joueur joueur;
    public Font newFont;

    {
        try {
            newFont = Font.createFont(Font.TRUETYPE_FONT, new File("./Ressources/Font/Think Smart .ttf")).deriveFont(75f);
            GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
            ge.registerFont(Font.createFont(Font.TRUETYPE_FONT, new File("./Ressources/Font/Think Smart .ttf")));
        } catch (FontFormatException | IOException e) {
            e.printStackTrace();
        }
    }

    public GameFrame(Joueur j) throws IOException {
        this.joueur = j;
        fuseeBouton = new FuseeBouton();
        this.p = new PlateauPanel(j.getP());
        p.setBounds(177, 224, 420, 380);
        setContentPane(global);
        add(p);

        JLabel niveau = new JLabel("Niveau " + p.plateau.numeroDuNiveau);
        JLabel nom = new JLabel(joueur.nom);
        score.setText("Score " + p.plateau.score);

        informationPanel.setLayout(new GridLayout(3, 0));
        informationPanel.setBounds(175, 44, 300, 130);
        informationPanel.setOpaque(false);
        nom.setFont(newFont);
        nom.setForeground(Color.black);

        niveau.setForeground(Color.black);
        niveau.setFont(newFont);
        score.setForeground(Color.black);
        score.setFont(newFont);

        informationPanel.add(nom);
        informationPanel.add(niveau);
        informationPanel.add(score);

        add(informationPanel);

        JPanel fuseePanel = new JPanel();

        fuseePanel.setBounds(650, 240, fuseeBouton.getWidth(), fuseeBouton.getHeight());
        fuseePanel.add(fuseeBouton);
        fuseePanel.setOpaque(false);
        add(fuseePanel);

        setVisible(true);
        setResizable(false);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        pack();
    }

    private void updateScore() {
        this.score.setText("Score " + p.plateau.score);
    }


    public class PlateauPanel extends JPanel {
        Plateau plateau;
        boolean fusee = false;

        public PlateauPanel(Plateau p) throws IOException {
            this.plateau = p;
            setLayout(new GridLayout(plateau.hauteur, plateau.largeur));
            setOpaque(false);
            remplirPanel();

        }

        private void remplirPanel() {
            for (int i = 0; i < plateau.hauteur; i++) {
                for (int j = 0; j < plateau.largeur; j++) {
                    if (plateau.plateau[i][j] == null) {
                        var tmp = new CaseG(i, j, Color.lightGray);
                        tmp.setEnabled(false);
                        tmp.setOpaque(false);
                        add(tmp);
                    } else if (plateau.plateau[i][j] instanceof CaseAnimaux) {
                        var tmp = new JButton();
                        tmp.setIcon(new ImageIcon(new ImageIcon("./Ressources/Images/foxie.png").getImage().getScaledInstance(50, 50, Image.SCALE_DEFAULT)));
                        tmp.setDisabledIcon(new ImageIcon(new ImageIcon("./Ressources/Images/foxie.png").getImage().getScaledInstance(50, 50, Image.SCALE_DEFAULT)));
                        tmp.setEnabled(false);
                        add(tmp);
                    } else if (plateau.plateau[i][j] instanceof CaseObstacle) {
                        var tmp = new CaseG(i, j, plateau.plateau[i][j].getBackground());
                        tmp.setEnabled(false);
                        add(tmp);

                    } else add(new CaseG(i, j, plateau.plateau[i][j].getBackground()));
                }

            }
        }


        public void updatePanel() {
            removeAll();
            remplirPanel();
            updateScore();
            updateFuseeBouton();
            revalidate();
            repaint();
        }


        public void fuseeChoisie() {
            fusee = true;
        }

        private void updateFuseeBouton() {
            fuseeBouton.setEnabled(plateau.score >= 100);
        }


        public class CaseG extends JButton implements ActionListener {
            int xCoord, yCoord;

            public CaseG(int x, int y, Color couleur) {

                setFocusPainted(false);
                setBackground(couleur);
                addActionListener(this);
                this.xCoord = x;
                this.yCoord = y;

            }

            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                CaseG event = (CaseG) actionEvent.getSource();
                int xC = event.xCoord;
                int yC = event.yCoord;
                // si fusee est true alors ça veut dire que le joueur s'apprête à lancer une fusée
                if (fusee) {
                    plateau.fusee(yC);
                    fusee = false;
                } else { // sinon on joue un coup normal
                    plateau.supprimer(xC, yC);
                }
                updatePanel();

                if (plateau.gagner()) {
                    afficherVictoireEtDemanderSuite();
                } else if (plateau.perdu() && !fuseeBouton.isEnabled()) {
                    afficherDefaiteEtDemanderSuite();
                }

            }

            private void afficherDefaiteEtDemanderSuite() {
                JOptionPane.showMessageDialog(null, "Vous avez perdu!");
                demanderSauvegarder();
                try {
                    demanderRessayer();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }


            private void afficherVictoireEtDemanderSuite() {

                JOptionPane.showMessageDialog(null, "Vous avez gagné!");
                joueur.niveauGagne();
                if (joueur.peutAccederAuProchainNiveau()) {
                    try {
                        joueur.miseAJourPlateauJoueur();
                        demanderAccederNiveauSuivant();
                        dispose();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else if (joueur.isJeuFini()) {
                    demanderSauvegarder();
                    JOptionPane.showMessageDialog(null, "Vous avez fini le jeu!");
                    dispose();

                }
            }

            private void demanderRessayer() throws IOException {
                int reponse = JOptionPane.showConfirmDialog(null,
                        "Voulez-vous ressayer?", "PetRescueSaga", JOptionPane.YES_NO_OPTION);

                if (reponse == 0) {
                    joueur.resetLevel();
                    EventQueue.invokeLater(() -> {
                        try {
                            new GameFrame(joueur);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    });
                    dispose();
                }
            }


            private void demanderSauvegarder() {
                int reponse = JOptionPane.showConfirmDialog(null,
                        "Voulez-vous sauvegarder?", "PetRescueSaga", JOptionPane.YES_NO_OPTION);
                ;
                if (reponse == 0) {
                    Jeu.sauvegarder(joueur);
                    dispose();
                } else dispose();
            }

            private void demanderAccederNiveauSuivant() throws IOException {
                int input = JOptionPane.showConfirmDialog(null,
                        "Voulez-vous passer au niveau supérieur", "PetRescueSaga", JOptionPane.YES_NO_OPTION);
                ;
                if (input == 0) {
                    new GameFrame(joueur);
                    dispose();

                } else {
                    demanderSauvegarder();
                }

            }
        }

    }

    public class FuseeBouton extends JButton implements ActionListener {
        public FuseeBouton() {
            ImageIcon ic = new ImageIcon("./Ressources/Images/startup.png");
            setFocusPainted(false);
            setIcon(ic);
            setSize(ic.getIconWidth(), ic.getIconHeight());
            setEnabled(false);
            addActionListener(this);
        }

        @Override
        public void actionPerformed(ActionEvent actionEvent) {
            p.fuseeChoisie();
        }
    }

}
