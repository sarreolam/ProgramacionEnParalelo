import Agents.Employee;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.Semaphore;

public class KitchenVisual extends MyCanvasE {

    private final int COUNTER_COUNT;
    private final int MACHINE_COUNT;

    private Image background;

    public int ventX, ventY;
    public ArrayList<Point> counterPoints = new ArrayList<>();
    public ArrayList<Point> machinePoints = new ArrayList<>();

    private Image ventImg;
    public Image counterImg;
    private Image machineImg;

    public KitchenVisual(JTextArea tArea, Semaphore semaphore, ArrayList<Employee> employees, int COUNTER_COUNT, int MACHINE_COUNT) {

        super(tArea, semaphore, employees);
        this.COUNTER_COUNT = COUNTER_COUNT;
        this.MACHINE_COUNT = MACHINE_COUNT;

        try {
            background = ImageIO.read(new File("Images/fondo.jpg"));
            //ventImg = ImageIO.read(new File("Images/ventanilla.png"));
            counterImg = ImageIO.read(new File("Images/counter.png"));
            machineImg = ImageIO.read(new File("Images/machine.png"));
        } catch (IOException e) {
            System.out.println("No se pudo cargar la imagen: " + e.getMessage());
        }
        activatePaintingAgents();
    }

    void activatePaintingAgents() {
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

        int w = getWidth();
        int h = getHeight();
        if (background != null) {
            g.drawImage(background, 0, 0, getWidth(), getHeight(), this);
        } else {
            g.setColor(Color.WHITE);
            g.fillRect(0, 0, w, h);
        }

        counterPoints.clear();
        machinePoints.clear();

        int ventWidth = 120;
        int ventHeight = 45;

        ventX = (w / 2) - (ventWidth / 2);
        ventY = 20;

        g.setColor(new Color(160, 160, 160));
        g.fillRect(ventX, ventY, ventWidth, ventHeight);

        g.setColor(Color.BLACK);
        g.drawString("VENTANILLA", ventX + 15, ventY - 5);

        int counterWidth = 100;
        int counterHeight = 80;

        int espacioCounter = h / (COUNTER_COUNT + 1);
        int counterX = (int)(w * 0.75);

        for (int i = 0; i < COUNTER_COUNT; i++) {
            int y = espacioCounter * (i + 1);

            if (counterImg != null) {
                g.drawImage(counterImg, counterX, y, counterWidth, counterHeight, this);

            } else {
                g.setColor(new Color(255, 75, 0));
                g.fillRect(counterX, y, counterWidth, counterHeight);
            }


            g.setColor(Color.WHITE);
            g.drawString("Counter " + (i + 1), counterX + 10, y - 5);

            counterPoints.add(new Point(counterX - 50, y + counterHeight / 2));

            g.setColor(new Color(150, 75, 0));
        }

        int machineSize = 70;

        int espacioMachine = h / (MACHINE_COUNT + 1);
        int machineX = (int)(w * 0.20);

        g.setColor(Color.DARK_GRAY);

        for (int i = 0; i < MACHINE_COUNT; i++) {
            int y = espacioMachine * (i + 1);

            if (machineImg != null) {
                g.drawImage(machineImg, machineX, y, machineSize, machineSize, this);

            } else {
                g.setColor(new Color(255, 75, 0));
                g.fillRect(machineX, y, machineSize, machineSize);
            }

            g.setColor(Color.WHITE);
            g.drawString("M. " + (i + 1), machineX + 20, y + 35);

            machinePoints.add(new Point(machineX + machineSize + 10, y + machineSize / 2));

            g.setColor(Color.DARK_GRAY);
        }

        for (Employee employee : employees) {
            employee.getMovement().setWindowPosition(ventX + ventWidth / 2, ventY + ventHeight + 20);
            employee.getMovement().setCounterPoints(counterPoints);
            employee.getMovement().setMachinePoints(machinePoints);
        }

        for (Employee employee : employees) {
            int px = (int) employee.getX();
            int py = (int) employee.getY();
            BufferedImage sprite = employee.getCurrentSprite();

            g.setColor(Color.WHITE);
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