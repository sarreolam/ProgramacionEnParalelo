package Agents;

import Buffers.Chair;
import Buffers.CounterClient;
import Buffers.Store;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Random;

public class Client extends Thread {

    private final String name;
    private final CounterClient counterClient;
    private final Store store;
    private ClientState state;

    private boolean isRunning = true;
    private final ClientMovement movement;
    private final int tiempoComer;
    private final double velocidadMovimiento;

    private BufferedImage[] walkAnim;
    private BufferedImage[] orderAnim;
    private BufferedImage[] eatAnim;
    private BufferedImage[] idleAnim;
    private BufferedImage[] currentAnim;

    private int frameIndex = 0;
    private long lastTime = 0;
    private final int frameDelay = 120;
    private long counterArrivalTime = -1;
    private long chairArrivalTime = -1;

    private int queuePositionAtCounter = 0;
    private int queuePositionAtChair = 0;;
    private int assignedCounterIndex = -1;
    private int assignedChairIndex = -1;


    public Client(String name, CounterClient counterClient, Store store, int tiempoComer, double velocidadMovimiento) {
        this.name = name;
        this.counterClient = counterClient;
        this.store = store;
        this.state = ClientState.CAMINANDO_A_LA_TIENDA;
        this.tiempoComer = tiempoComer;
        this.velocidadMovimiento = velocidadMovimiento;
        this.movement = new ClientMovement(400, 300, velocidadMovimiento);

        LoadSprites();
        UpdateAnimationArray();
    }

    public void run() {
        store.entrarTienda(name);
        while(isRunning){
            Random r = new Random();
            int x = r.nextInt(100);
            if (x < 40) {
                // Assign counter and record arrival time
                assignedCounterIndex = counterClient.assignCounter(name);
                counterArrivalTime = System.currentTimeMillis(); // Record when joined queue

                state = ClientState.CAMINANDO_AL_MOSTRADOR;
                UpdateAnimationArray();
                UpdateTargetByState();
                System.out.println(name + " camina al counter " + assignedCounterIndex);
                esperar(2000);



                counterClient.clienteLlega(name, this);

                System.out.println(name + " se aleja del counter con su comida");
                assignedCounterIndex = -1;
                counterArrivalTime = -1; // Reset

            } else if (x < 60) {
                Chair chair = store.obtenerSillaLibre();
                if (chair != null){
                    System.out.println(name + " se fue a sentar en "+ chair.getNombre());
                    assignedChairIndex = chair.getIndex();
                    chairArrivalTime = System.currentTimeMillis(); // Record when joined queue

                    state = ClientState.CAMINANDO_A_ASIENTOS;
                    UpdateAnimationArray();
                    UpdateTargetByState();
                    esperar(1000);

                    state = ClientState.SENTADO;
                    UpdateAnimationArray();
                    UpdateTargetByState();
                    esperar(10000);

                    chair.liberar();
                    assignedChairIndex = -1;
                    chairArrivalTime = -1; // Reset
                }
            } else if (x < 70) {
                System.out.println(name + " camina porque quizo");
                state = ClientState.CAMINANDO;
                UpdateAnimationArray();
                UpdateTargetByState();
                esperar(2000);
            } else if (x < 85){
                state = ClientState.ESPERANDO;
                UpdateAnimationArray();
                UpdateTargetByState();
                System.out.println(name + " No hace nada");
                esperar(2000);
            } else {
                state = ClientState.SALIENDO;
                UpdateAnimationArray();
                UpdateTargetByState();
                isRunning = false;
                esperar(1000);
                store.salirTienda(name);
            }
            state = ClientState.ESPERANDO;
            UpdateAnimationArray();
            UpdateTargetByState();
        }
        state = ClientState.FUERA;
        UpdateAnimationArray();
        UpdateTargetByState();
    }

    public long getCounterArrivalTime() {
        return counterArrivalTime;
    }

    public long getChairArrivalTime() {
        return chairArrivalTime;
    }

    public String GetName() {
        return name;
    }

    private void esperar(int t) {
        try {
            Thread.sleep(t);
        } catch (InterruptedException e) {
            System.out.println(e);
        }
    }

    private void LoadSprites() {
        try {
            walkAnim = LoadAnim("Images/walkingClient_", 11);
            orderAnim = LoadAnim("Images/orderClient_", 13);
            eatAnim = LoadAnimPng("Images/clientMonch_", 20);
            idleAnim = LoadAnim("Images/plinkIdle_", 20);
        } catch (RuntimeException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    private BufferedImage[] LoadAnimPng(String base, int count) throws IOException {
        BufferedImage[] anim = new BufferedImage[count];
        for (int i = 0; i < count; i++) {
            anim[i] = ImageIO.read(new File(base + i + ".png"));
        }
        return anim;
    }

    private BufferedImage[] LoadAnim(String base, int count) throws IOException {
        BufferedImage[] anim = new BufferedImage[count];
        for (int i = 0; i < count; i++) {
            anim[i] = ImageIO.read(new File(base + i + ".gif"));
        }
        return anim;
    }

    public void UpdateAnimationArray() {
        BufferedImage[] previousAnim = currentAnim;

        switch (state) {
            case CAMINANDO_A_LA_TIENDA:
            case CAMINANDO_A_ASIENTOS, CAMINANDO_AL_MOSTRADOR, FUERA, SALIENDO:
                currentAnim = walkAnim;
                break;
            case ESPERANDO:
                currentAnim = idleAnim;
                break;
            case PIDIENDO:
                currentAnim = orderAnim;
                break;
            case SENTADO:
                currentAnim = eatAnim;
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

    public ClientState getClientState() {
        return state;
    }

    public ClientMovement getMovement() {
        return movement;
    }

    public enum ClientState {CAMINANDO_A_LA_TIENDA, CAMINANDO_AL_MOSTRADOR, CAMINANDO_A_ASIENTOS, CAMINANDO, ESPERANDO, PIDIENDO, SALIENDO, FUERA, SENTADO}

    public void UpdateAnimation() {
        movement.moveTowardsTarget();
        if (state == ClientState.CAMINANDO_AL_MOSTRADOR || state == ClientState.PIDIENDO) {
            UpdateTargetByState();
        } else if (state == ClientState.CAMINANDO_A_ASIENTOS) {
            UpdateTargetByState();
        }
    }

    public void UpdateTargetByState() {
        switch (state) {

            case CAMINANDO_A_LA_TIENDA:
                movement.setTarget(100, 300);
                break;

            case CAMINANDO_AL_MOSTRADOR, PIDIENDO:
                movement.setTargetToCounter(assignedCounterIndex, queuePositionAtCounter);
                break;

            case CAMINANDO_A_ASIENTOS:
                movement.setTargetToChair(assignedChairIndex, queuePositionAtChair);
                break;

            case SENTADO:
                break;

            case CAMINANDO:
                double rx = 100 + Math.random() * 600;
                double ry = 100 + Math.random() * 400;
                movement.setTarget(rx, ry);
                break;

            case ESPERANDO:
                movement.setTargetToParkArea();
                break;

            case SALIENDO, FUERA:
                movement.setTarget(900, 300);
                break;
        }
    }

    public int getAssignedCounterIndex() {
        return assignedCounterIndex;
    }

    public int getQueuePositionAtCounter() {
        return queuePositionAtCounter;
    }

    public void setQueuePositionAtCounter(int position) {
        this.queuePositionAtCounter = position;
    }

    public int getAssignedChairIndex() {
        return assignedChairIndex;
    }

    public int getQueuePositionAtChair() {
        return queuePositionAtChair;
    }

    public void setQueuePositionAtChair(int position) {
        this.queuePositionAtChair = position;
    }

    public void setState(ClientState s){state = s; }
    public String getClientName(){return name;}
}