package Agents;

import java.awt.*;
import java.util.ArrayList;
import java.util.Random;

public class ClientMovement {

    private double positionX, positionY;
    private double targetX, targetY;
    private double speed;

    // Posiciones dinámicas
    private double windowX = 600, windowY = 800;
    private ArrayList<Point> counterPoints = new ArrayList<>();
    private ArrayList<Point> chairPoints = new ArrayList<>();

    Random randomNumbers = new Random();
    private int lastCounterQueuePos = -1;
    private int lastChairQueuePos = -1;


    public ClientMovement(int initialX, int initialY, double speed) {
        this.positionX = initialX;
        this.positionY = initialY;
        this.targetX = initialX;
        this.targetY = initialY;
        this.speed = speed;
    }

    public double getPositionX() {
        return positionX;
    }

    public double getPositionY() {
        return positionY;
    }

    public void setTarget(double x, double y) {
        this.targetX = x;
        this.targetY = y;
    }

    public void setWindowPosition(double x, double y) {
        this.windowX = x;
        this.windowY = y;
    }

    public void setCounterPoints(ArrayList<Point> list) {
        if (list != null) {
            this.counterPoints = new ArrayList<>(list);
        }
    }

    public void setChairPoints(ArrayList<Point> list) {
        if (list != null) {
            this.chairPoints = new ArrayList<>(list);
        }
    }

    public void setTargetToWindow() {
        setTarget(windowX, windowY);
    }

    public void setTargetToCounter(int counterIndex, int queuePosition) {
        if (counterPoints != null && counterIndex >= 0 && counterIndex < counterPoints.size()) {
            Point counter = counterPoints.get(counterIndex);
            double offsetX = queuePosition * -100;
            setTarget(counter.x - offsetX, counter.y);
        }
    }

    public void setTargetToChair(int chairIndex, int queuePosition) {
        if (chairPoints != null && chairIndex >= 0 && chairIndex < chairPoints.size()) {
            Point chair = chairPoints.get(chairIndex);
            double offsetY = queuePosition * -100;
            setTarget(chair.x, chair.y  - offsetY);
        }
    }

    public void setTargetToParkArea() {
        // Área de espera en el centro
        setTarget(400 + randomNumbers.nextInt(-100,100), 300 + randomNumbers.nextInt(-100,100));
    }

    public boolean hasReachedTarget() {
        return Math.abs(targetX - positionX) < speed &&
                Math.abs(targetY - positionY) < speed;
    }

    public void moveTowardsTarget() {
        if (hasReachedTarget()) {
            positionX = targetX;
            positionY = targetY;
            return;
        }

        double dx = targetX - positionX;
        double dy = targetY - positionY;

        double distance = Math.sqrt(dx * dx + dy * dy);

        if (distance > 0) {
            positionX += (dx / distance) * speed;
            positionY += (dy / distance) * speed;
        }
    }

}