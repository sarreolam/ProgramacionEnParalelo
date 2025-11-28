import Agents.Client;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.concurrent.Semaphore;

public class StoreVisual extends VisualWindow {
    private Image background;
    public StoreVisual(JTextArea tArea, Semaphore semaphore, ArrayList<Client> clients){
        super(tArea, semaphore, clients);
        try {
            background = ImageIO.read(new File("src/main/java/Images/restaurant.jpg"));
        } catch (IOException e) {
            System.out.println("No se pudo cargar la imagen: " + e.getMessage());
        }
        repaint();
        activatePaintingAgents();
    }

    void activatePaintingAgents(){
        PaintingAgents paintingAgents = new PaintingAgents(clients, this);
        Thread th = new Thread(paintingAgents);
        th.start();
    }

    @Override
    public void update(Graphics g) {
        g.clearRect(0, 0, getWidth(), getHeight());
        paint(g);
    }
    @Override
    public void paint(Graphics g) {
        if (background != null) {
            System.out.println("Background");
            g.drawImage(background, 0, 0, getWidth(), getHeight(), this);
        }
        for(Client client : clients){
            int px = (int) client.getX();
            int py = (int) client.getY();
            BufferedImage sprite = client.getCurrentSprite();
            if (sprite != null) {
                g.drawImage(sprite, px, py, 40, 40, null);
            } else {
                g.setColor(Color.RED);
                g.fillOval(px, py, 20, 20);
            }
        }
    }
}
