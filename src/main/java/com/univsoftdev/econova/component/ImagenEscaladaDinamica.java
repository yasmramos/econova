package com.univsoftdev.econova.component;

import java.awt.Graphics;
import java.awt.Image;
import javax.swing.ImageIcon;
import javax.swing.JPanel;

public class ImagenEscaladaDinamica extends JPanel {

    private static final long serialVersionUID = 1L;

    private Image image;

    public ImagenEscaladaDinamica(String imagePath) {
        image = new ImageIcon(imagePath).getImage();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        // Escalar la imagen al tamaño del panel
        g.drawImage(image, 0, 0, getWidth(), getHeight(), this);
    }
    
    
}
