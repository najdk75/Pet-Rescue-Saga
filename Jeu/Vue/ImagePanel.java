package Jeu.Vue;

import Jeu.Modele.*;
import Jeu.Controleur.*;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class ImagePanel extends JPanel {
    public BufferedImage image;



    public ImagePanel(String s) {
        {
            try {
                image = ImageIO.read(new File(s));

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        setPreferredSize(new Dimension(image.getWidth(), image.getHeight()));
        setLayout(null);
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(image, 0, 0, this);

    }

}
