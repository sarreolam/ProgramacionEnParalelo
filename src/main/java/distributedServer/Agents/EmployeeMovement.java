// Archivo: Agents/EmployeeMovement.java
package Agents;

//por ahora no verfica si la maquina o el counters ya estan siendo ocupados por eso se enciman
public class EmployeeMovement {

    private static final int WIDTH = 800;
    private static final int HEIGHT = 600;

    //coordenadas de los counters
    private static final int COUNTER_X = 650;
    private static final int COUNTER_Y_START = 100;
    private static final int COUNTER_SPACING = 100;

    //coordenandas de las maquinas
    private static final int MACHINE_X = 150;
    private static final int MACHINE_Y_START = 100;
    private static final int MACHINE_SPACING = 80;

    // coordenadas de espera
    private static final int WAITING_X = 400;
    private static final int WAITING_Y = 300;
    private final double speed = 3.0; // Velocidad de movimiento en píxeles/frame
    private double positionX, positionY;
    private double targetX, targetY;

    public EmployeeMovement(int initialX, int initialY) {
        this.positionX = initialX;
        this.positionY = initialY;
        this.targetX = initialX;
        this.targetY = initialY;
    }

    public double getPositionX() {
        return positionX;
    }

    public double getPositionY() {
        return positionY;
    }

    public void setPosition(double x, double y) {
        this.positionX = x;
        this.positionY = y;
    }

    public void setTarget(double x, double y) {
        this.targetX = x;
        this.targetY = y;
    }


    public boolean hasReachedTarget() {
        return Math.abs(targetX - positionX) < speed && Math.abs(targetY - positionY) < speed;
    }


    public boolean moveTowardsTarget() {
        if (hasReachedTarget()) {
            positionX = targetX;
            positionY = targetY;
            return true;
        }

        double dx = targetX - positionX;
        double dy = targetY - positionY;
        double distance = Math.sqrt(dx * dx + dy * dy);

        positionX += (dx / distance) * speed;
        positionY += (dy / distance) * speed;
        return false;
    }


    public void setTargetToWaitingArea() {
        setTarget(WAITING_X, WAITING_Y);
    }


    public void setTargetToCounter(int counterIndex) {
        if (counterIndex < 0 || counterIndex > 4) counterIndex = 0;
        double targetY = COUNTER_Y_START + counterIndex * COUNTER_SPACING;
        setTarget(COUNTER_X, targetY);
    }

    public void setTargetToMachine(int machineIndex) {
        if (machineIndex < 0 || machineIndex > 4) machineIndex = 0;
        double targetY = MACHINE_Y_START + machineIndex * MACHINE_SPACING;
        setTarget(MACHINE_X, targetY);
    }
}