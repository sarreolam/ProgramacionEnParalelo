package Agents;

import Buffers.Counter;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.Socket;


public class ClientInServer extends Thread {
    private final String name;
    private final int port = 5000;
    private final boolean running = true;
    private ClientState state;
    private final Counter counter;
    private final Socket socket;
    private double positionX, positionY;
    private double targetX, targetY;
    private final double speed = 2.0f;
    private BufferedImage[] walkAnim;
    private BufferedImage[] idleAnim;
    private BufferedImage[] leavingAnim;
    private BufferedImage[] currentAnim;
    private int frameIndex = 0;
    private long lastTime = 0;
    private final int frameDelay = 120;
    public ClientInServer(String name, Socket socket, Counter counter) {
        this.name = name;
        this.counter = counter;
        this.socket = socket;

        this.state = ClientState.NACIENDO;
        this.positionX = 0;
        this.positionY = 100;

        this.targetX = 300;
        this.targetY = 100;
        LoadSprites();
        UpdateAnimationArray();
    }

    public ClientState getClientState() {
        return state;
    }

    public double getX() {
        return positionX;
    }

    public double getY() {
        return positionY;
    }

    private void LoadSprites() {
        try {
            walkAnim = LoadAnim("Images/walkingClient_", 11);
            idleAnim = LoadAnim("Images/plinkIdle_", 20);
            //leavingAnim = loadAnim(10);
        } catch (RuntimeException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    private BufferedImage[] LoadAnim(String base, int count) throws IOException {
        BufferedImage[] anim = new BufferedImage[count];
        for (int i = 0; i < count; i++) {
            anim[i] = ImageIO.read(new File(base + i + ".gif"));
        }
        return anim;
    }

    public void UpdateAnimation() {
        switch (state) {
            case NACIENDO:
                break;
            case CAMINANDO:
                moveTowardsTarget();
                break;
            case ESPERANDO:
                break;
            case PIDIENDO:
                break;
            case SALIENDO:
                moveTowardsExit();
                break;
            default:
                break;
        }
    }

    public void UpdateAnimationArray() {
        BufferedImage[] previousAnim = currentAnim;

        switch (state) {
            case CAMINANDO:
                currentAnim = walkAnim;
                break;
            case ESPERANDO:
            case PIDIENDO:
                currentAnim = idleAnim;
                break;
            case SALIENDO:
                currentAnim = walkAnim;
                break;
            default:
                currentAnim = idleAnim;
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

    private void moveTowardsTarget() {
        double dx = targetX - positionX;
        double dy = targetY - positionY;
        double distance = Math.sqrt(dx * dx + dy * dy);
        if (distance < 1) return;
        positionX += (dx / distance) * speed;
        positionY += (dy / distance) * speed;
    }

    private void moveTowardsExit() {
        targetX = -50;
        targetY = positionY;
        moveTowardsTarget();
    }

    @Override
    public void run() {
        try {
            // Leer el mensaje del cliente
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);


            state = ClientState.PIDIENDO;
            counter.clienteLlega(name);
            state = ClientState.SALIENDO;
            esperar(1000);
            state = ClientState.FUERA;

            System.out.println(name + " salió del restaurante.");


            in.close();
            out.close();
            socket.close();
            System.out.println("Conexión con el cliente cerrada.");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void esperar(int t) {
        try {
            Thread.sleep(t);
        } catch (InterruptedException e) {
            System.out.println(e);
        }
    }

    public enum ClientState {NACIENDO, CAMINANDO, ESPERANDO, PIDIENDO, SALIENDO, FUERA}
}