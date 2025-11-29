import Agents.Client;
import Agents.Employee;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.concurrent.Semaphore;

//store visual de empleados
public class StoreVisual extends MyCanvasE {
    private Image background;

    //coordenadas de los counters hardcodeado
    private static final int COUNTER_X = 650;
    private static final int COUNTER_Y_START = 100;
    private static final int COUNTER_SPACING = 100;
    private static final int COUNTER_COUNT = 5;

    //coordenandas de las machines hardcodeado
    private static final int MACHINE_X = 150;
    private static final int MACHINE_Y_START = 100;
    private static final int MACHINE_SPACING = 80;
    private static final int MACHINE_COUNT = 5;

    public StoreVisual(JTextArea tArea, Semaphore semaphore, ArrayList<Employee> employees){
        super(tArea, semaphore, employees);
        try {
            background = ImageIO.read(new File("src/main/java/Images/restaurant.jpg"));
        } catch (IOException e) {
            System.out.println("No se pudo cargar la imagen: " + e.getMessage());
        }
        activatePaintingAgents();
    }

    void activatePaintingAgents(){
        PaintingAgentsE paintingAgents = new PaintingAgentsE(employees, this);
        Thread th = new Thread(paintingAgents);
        th.start();
    }

    @Override
    public void update(Graphics g) {
        paint(g);
    }

    @Override
    public void paint(Graphics g) {
        if (background != null) {
            g.drawImage(background, 0, 0, getWidth(), getHeight(), this);
        } else {
            g.setColor(Color.WHITE);
            g.fillRect(0, 0, getWidth(), getHeight());
        }
        //dibuja los counters
        g.setColor(new Color(150, 75, 0));
        for (int i = 0; i < COUNTER_COUNT; i++) {
            int y = COUNTER_Y_START + i * COUNTER_SPACING;
            g.fillRect(COUNTER_X, y, 100, 40);
            g.setColor(Color.BLACK);
            g.drawString("Counter " + (i + 1), COUNTER_X + 10, y - 5);
            g.setColor(new Color(150, 75, 0));
        }

        //dibuja las maquinas mejor poner una imagen
        g.setColor(Color.DARK_GRAY);
        for (int i = 0; i < MACHINE_COUNT; i++) {
            int y = MACHINE_Y_START + i * MACHINE_SPACING;
            g.fillRect(MACHINE_X, y, 70, 70);
            g.setColor(Color.WHITE);
            g.drawString("M. " + (i + 1), MACHINE_X + 20, y + 35);
            g.setColor(Color.DARK_GRAY);
        }

        // dibuja los empleados
        for(Employee employee : employees){
            int px = (int) employee.getX();
            int py = (int) employee.getY();
            BufferedImage sprite = employee.getCurrentSprite();

            // estado debug
            g.setColor(Color.BLUE);
            g.drawString(employee.getEmployeeState().toString(), px - 10, py - 5);

            if (sprite != null) {
                g.drawImage(sprite, px, py, 40, 40, null);
            } else {
                g.setColor(Color.RED);
                g.fillOval(px, py, 20, 20);
            }
        }
    }
}