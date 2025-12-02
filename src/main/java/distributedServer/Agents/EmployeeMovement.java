package Agents;

import java.awt.*;
import java.util.ArrayList;

public class EmployeeMovement {

    private double positionX, positionY;
    private double targetX, targetY;
    private double speed;

    // Posiciones dinámicas
    private double windowX = 600, windowY = 800;
    private ArrayList<Point> counterPoints = new ArrayList<>();
    private ArrayList<Point> machinePoints = new ArrayList<>();

    public EmployeeMovement(int initialX, int initialY, double speed) {
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

    public void setMachinePoints(ArrayList<Point> list) {
        if (list != null) {
            this.machinePoints = new ArrayList<>(list);
        }
    }

    public void setTargetToWindow() {
        setTarget(windowX, windowY);
    }

    public void setTargetToCounter(int index) {
        if (counterPoints.isEmpty()) {
            System.out.println("counterPoints está vacío");
            return;
        }

        if (index < 0 || index >= counterPoints.size()) {
            index = 0;
        }

        Point p = counterPoints.get(index);
        setTarget(p.x, p.y);
        System.out.println(" Target counter " + index + ": (" + p.x + ", " + p.y + ")");
    }

    public void setTargetToMachine(int index) {
        if (machinePoints.isEmpty()) {
            System.out.println(" machinePoints está vacío");
            return;
        }

        if (index < 0 || index >= machinePoints.size()) {
            index = 0;
        }

        Point p = machinePoints.get(index);
        setTarget(p.x, p.y);
        System.out.println("Target machine " + index + ": (" + p.x + ", " + p.y + ")");
    }

    public void setTargetToWaitingArea() {
        // Área de espera en el centro
        setTarget(400, 300);
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