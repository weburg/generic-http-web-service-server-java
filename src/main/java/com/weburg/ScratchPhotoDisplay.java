package com.weburg;

import example.domain.Photo;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class ScratchPhotoDisplay {
    public static void main(String args[]) throws IOException
    {
        Photo photo = new Photo();
        photo.setPhotoFile(new File(args[0]));
        scratchPhotoDisplay(photo);
    }

    public static void scratchPhotoDisplay(Photo photo) throws IOException
    {
        BufferedImage img = ImageIO.read(photo.getPhotoFile());
        ImageIcon icon = new ImageIcon(img);
        JFrame frame = new JFrame();
        frame.setLayout(new FlowLayout());
        frame.setSize(800,600);
        frame.setTitle(photo.getCaption());
        frame.getContentPane().setBackground(new Color(0,0,0));
        frame.setLocationRelativeTo(null);
        JLabel lbl = new JLabel();
        lbl.setIcon(icon);
        frame.add(lbl);
        frame.setVisible(true);
        frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
    }
}
