package example;

import example.domain.Image;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class ScratchImageDisplay {
    public static void main(String args[]) throws IOException
    {
        Image Image = new Image();
        Image.setImageFile(new File(args[0]));
        scratchImageDisplay(Image);
    }

    public static void scratchImageDisplay(Image Image) throws IOException
    {
        BufferedImage img = ImageIO.read(Image.getImageFile());
        ImageIcon icon = new ImageIcon(img);
        JFrame frame = new JFrame();
        frame.setLayout(new FlowLayout());
        frame.setSize(800,600);
        frame.setTitle(Image.getCaption());
        frame.getContentPane().setBackground(new Color(0,0,0));
        frame.setLocationRelativeTo(null);
        JLabel lbl = new JLabel();
        lbl.setIcon(icon);
        frame.add(lbl);
        frame.setVisible(true);
        frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
    }
}
