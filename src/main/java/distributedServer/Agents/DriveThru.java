package Agents;

import Buffers.Counter;
import Buffers.Kitchen;
import Buffers.Window;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class DriveThru extends Thread {
    private final String name;
    private final Window window;
    private DriveThruState state;
    private final DriveThruMovement movement;

    private BufferedImage[] drivingAnim;
    private BufferedImage[] waitingAnim;
    private BufferedImage[] currentAnim;

    private int frameIndex = 0;
    private long lastTime = 0;
    private final int frameDelay = 120;

    private boolean isRunning = true;

    public enum DriveThruState {
        LLEGANDO,
        ESPERANDO_VENTANILLA,
        EN_VENTANILLA,
        ESPERANDO_ORDEN,
        SALIENDO,
        FUERA
    }

    public DriveThru(String name, Window window) {
        this.name = name;
        this.window = window;
        this.state = DriveThruState.LLEGANDO;
        this.movement = new DriveThruMovement(-200, 50, 3);
        LoadSprites();
        UpdateAnimationArray();
    }

    private void LoadSprites() {
        try {
            drivingAnim = LoadAnim("Images/driving_", 11);
            waitingAnim = LoadAnim("Images/waiting_", 23);
        } catch (RuntimeException | IOException e) {
            System.out.println("No se pudieron cargar sprites del carro: " + e.getMessage());
            drivingAnim = new BufferedImage[0];
            waitingAnim = new BufferedImage[0];
        }
    }

    private BufferedImage[] LoadAnim(String base, int count) throws IOException {
        BufferedImage[] anim = new BufferedImage[count];
        for (int i = 0; i < count; i++) {
            anim[i] = ImageIO.read(new File(base + i + ".png"));
        }
        return anim;
    }

    public void UpdateAnimationArray() {
        BufferedImage[] previousAnim = currentAnim;

        switch (state) {
            case LLEGANDO:
            case SALIENDO:
                currentAnim = drivingAnim;
                break;
            case ESPERANDO_VENTANILLA:
            case EN_VENTANILLA:
            case ESPERANDO_ORDEN:
                currentAnim = waitingAnim;
                break;
            case FUERA:
                currentAnim = null;
                break;
        }

        if (previousAnim != currentAnim) {
            frameIndex = 0;
        }
    }

    public BufferedImage getCurrentSprite() {
        if (currentAnim == null || currentAnim.length == 0) return null;

        long now = System.currentTimeMillis();
        if (now - lastTime > frameDelay) {
            frameIndex = (frameIndex + 1) % currentAnim.length;
            lastTime = now;
        }

        if (frameIndex >= currentAnim.length) {
            frameIndex = 0;
        }

        return currentAnim[frameIndex];
    }



    public double getX() {
        return movement.getPositionX();
    }

    public double getY() {
        return movement.getPositionY();
    }

    public String getCarName() {
        return name;
    }

    public DriveThruMovement getMovement() {
        return movement;
    }

    public void UpdateAnimation() {
        movement.moveTowardsTarget();
    }

    @Override
    public void run() {
        try {

            //en coordenadas de entrada
            state = DriveThruState.LLEGANDO;
            movement.setTargetToEntrada();
            while (!movement.hasReachedTarget()) {
                movement.moveTowardsTarget();
                esperar(25);
            }

            state = DriveThruState.ESPERANDO_VENTANILLA;
            System.out.println(name + " esperando turno...");

            window.carroLlega(name, this);;

            while (!movement.hasReachedTarget()) {
                movement.moveTowardsTarget();
                esperar(25);
            }

            state = DriveThruState.SALIENDO;
            movement.setTargetToSalida();
            while (!movement.hasReachedTarget()) {
                movement.moveTowardsTarget();
                esperar(25);
            }

            state = DriveThruState.FUERA;
            System.out.println(name + " terminó drive-thru y salió.");

        } catch (Exception e) {
            System.out.println("Error en drive-thru: " + e);
        }
    }

    public void setState(DriveThruState s){
        state = s;

    }


    private void esperar(int t) {
        try {
            Thread.sleep(t);
        } catch (InterruptedException e) {
            System.out.println(e);
        }
    }
    public DriveThruState getDTState() {
        return state;
    }
}
