package Agents;

public class DriveThruMovement {

    private double positionX, positionY;
    private double targetX, targetY;
    private double speed;
    private double entradaX = 10;
    private double entradaY = 80;

    private double filaX = -80;
    private double filaY = 100;

    private double ventanillaX = 400;
    private double ventanillaY = 100;

    private double salidaX = 1000;
    private double salidaY = 80;

    public DriveThruMovement(double initialX, double initialY, double speed) {
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


    public void setPuntoEntrada(double x, double y) {
        this.entradaX = x;
        this.entradaY = y;
    }

    public void setPuntoFila(double x, double y) {
        this.filaX = x;
        this.filaY = y;
    }

    public void setVentanillaPosition(double x, double y) {
        this.ventanillaX = x;
        this.ventanillaY = y;
    }

    public void setPuntoSalida(double x, double y) {
        this.salidaX = x;
        this.salidaY = y;
    }

    public void setTargetToEntrada() {
        setTarget(entradaX, entradaY);
    }

    public void setTargetToVentanilla() {
        setTarget(ventanillaX, ventanillaY);
    }

    public void setTargetToSalida() {
        setTarget(salidaX, salidaY);
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
        double dist = Math.sqrt(dx * dx + dy * dy);

        if (dist > 0) {
            positionX += (dx / dist) * speed;
            positionY += (dy / dist) * speed;
        }
    }
}
