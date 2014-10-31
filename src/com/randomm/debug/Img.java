package com.randomm.debug;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Panel;
import java.awt.image.BufferedImage;
import java.io.*;

import javax.imageio.ImageIO;
import javax.swing.JOptionPane;


public class Img extends Panel {

	private static final long serialVersionUID = 4681974049945906314L;
	
	private BufferedImage originalImage = null;
    private Image image = null;
    public Img() {
        initComponents();
    }
    
    private void initComponents() {
        setLayout(null);
        addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentResized(java.awt.event.ComponentEvent evt) {
                formComponentResized();
            }
        });
    }
    
    private void formComponentResized() {
        int w = this.getWidth();
        int h = this.getHeight();
        if ((originalImage != null) && (w > 0) && (h > 0)) {
            image = originalImage.getScaledInstance(w, h, Image.SCALE_DEFAULT);
            this.repaint();
        }
    }
    
    public void paint(Graphics g) {
        if (image != null) {
            g.drawImage(image, 0, 0, null);
        }
    }
    
    public BufferedImage getImage() {
        return originalImage;
    }
    
    public void setImage(BufferedImage image) {
        this.image = image;
        originalImage = image; 
        formComponentResized();
        this.repaint();
    }
    public void setImageFile(File imageFile) {
        try {
            if (imageFile == null) {
                originalImage = null;
            }
            BufferedImage bi = ImageIO.read(imageFile);
            originalImage = bi;
            formComponentResized();
        } catch (IOException ex) {
            System.err.println("Error, can't load image!");
            JOptionPane.showMessageDialog(new Frame(), "Can't load image!","Error", JOptionPane.ERROR_MESSAGE, null);
        }
        repaint();
    }

}
