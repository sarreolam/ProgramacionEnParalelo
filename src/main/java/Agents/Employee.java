package Agents;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.Socket;
import java.util.Objects;
import java.util.Random;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import Buffers.Counter;
import Buffers.Kitchen;

import javax.imageio.ImageIO;

public class Employee extends Thread {
    public int port = 5000;
    private final String name;
    private boolean running = true;

    public enum EmployeeState { NACIENDO, CAMINANDO_AL_MOSTRADOR, CAMINANDO_A_MAQUINA, ESPERANDO, ATENDIENDO, SALIENDO, FUERA, PREPARANDO }
    private EmployeeState state;

    private final EmployeeMovement movement;
    private int assignedCounterIndex = -1;
    private int assignedMachineIndex = -1;

    private BufferedImage[] walkAnim;
    private BufferedImage[] workAnim;

    private BufferedImage[] currentAnim;
    private int frameIndex = 0;
    private long lastTime = 0;
    private int frameDelay = 120;

    public Employee(String name) {
        this.name = name;
        this.state = EmployeeState.NACIENDO;
        this.movement = new EmployeeMovement(400, 300);
        LoadSprites();
        UpdateAnimationArray();
    }

    private void LoadSprites(){
        try{
            walkAnim = LoadAnim("src/main/java/Images/walkingClient_", 11);
            workAnim = LoadAnim("src/main/java/Images/plinkIdle_",20);
        } catch (RuntimeException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    private BufferedImage[] LoadAnim(String base, int count) throws IOException {
        BufferedImage[] anim = new BufferedImage[count];
        for(int i = 0; i < count; i++){
            anim[i] = ImageIO.read(new File(base + i + ".gif"));
        }
        return anim;
    }

    public void UpdateAnimationArray() {
        BufferedImage[] previousAnim = currentAnim;

        switch (state) {
            case NACIENDO:
                break;
            case CAMINANDO_AL_MOSTRADOR:
                currentAnim = walkAnim;
                break;
            case ESPERANDO:
                currentAnim = workAnim;
                break;
            case CAMINANDO_A_MAQUINA:
                currentAnim = walkAnim;
                break;
            case ATENDIENDO:
                currentAnim = workAnim;
                break;
            case PREPARANDO:
                currentAnim = workAnim;
                break;
            case FUERA:
                currentAnim = workAnim;
                break;
            case SALIENDO:
                currentAnim = walkAnim;
                break;
            default:
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

    public EmployeeState getEmployeeState() {
        return state;
    }

    public double getX() {
        return movement.getPositionX();
    }

    public double getY() {
        return movement.getPositionY();
    }

    public void UpdateAnimation() {
        movement.moveTowardsTarget();

        if (movement.hasReachedTarget()) {
            switch (state) {
                case NACIENDO:
                    movement.setTargetToWaitingArea();
                    break;
                case CAMINANDO_AL_MOSTRADOR:
                    state = EmployeeState.ATENDIENDO;
                    movement.setTargetToCounter(assignedCounterIndex);
                    UpdateAnimationArray();
                    break;
                case CAMINANDO_A_MAQUINA:
                    state = EmployeeState.PREPARANDO;
                    movement.setTargetToMachine(assignedMachineIndex);
                    UpdateAnimationArray();
                    break;
                case SALIENDO:
                    state = EmployeeState.FUERA;
                    UpdateAnimationArray();
                    break;
                case ESPERANDO:
                case ATENDIENDO:
                case PREPARANDO:
                case FUERA:
                    break;
            }
        }
    }

    @Override
    public void run() {
        System.out.println(name + " ha comenzado su turno.");
        try (Socket socket = new Socket("localhost", port);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            System.out.println(name + " conectado al servidor.");
            state = EmployeeState.NACIENDO;
            UpdateAnimationArray();

            this.assignedCounterIndex = (new Random().nextInt(5));

            while (running) {
                out.println("employeeCheckClients " + name);
                String response = in.readLine();
                boolean hasClient = Boolean.parseBoolean(response);

                if (hasClient) {
                    state = EmployeeState.CAMINANDO_AL_MOSTRADOR;
                    UpdateAnimationArray();
                    movement.setTargetToCounter(assignedCounterIndex);

                    // Esperar a llegar
                    while (!movement.hasReachedTarget()) {
                        Thread.sleep(50);
                    }

                    out.println("employeeArrive " + name);
                    String pedidoResponse = in.readLine();
                    int pedidoId = Integer.parseInt(pedidoResponse);

                    if (pedidoId != -1) {
                        System.out.println(name + " obtuvo pedido #" + pedidoId);

                        out.println("employeeAddOrder " + name + " " + pedidoId);
                        in.readLine();

                        out.println("employeeTakeOrder " + name);
                        String orderResponse = in.readLine();
                        int takenOrder = Integer.parseInt(orderResponse);

                        if (takenOrder != -1) {
                            out.println("employeeGetMachine " + name);
                            String machineName = in.readLine();

                            if (!machineName.equals("none")) {
                                if (machineName.startsWith("Machine")) {
                                    assignedMachineIndex = Integer.parseInt(machineName.substring(7));
                                }

                                state = EmployeeState.CAMINANDO_A_MAQUINA;
                                UpdateAnimationArray();
                                movement.setTargetToMachine(assignedMachineIndex);

                                while (!movement.hasReachedTarget()) {
                                    Thread.sleep(50);
                                }

                                System.out.println(name + " terminó de preparar pedido #" + takenOrder);

                                out.println("employeeDeliverOrder " + name + " " + takenOrder);
                                in.readLine();

                                state = EmployeeState.ESPERANDO;
                                UpdateAnimationArray();
                                movement.setTargetToWaitingArea();

                                while (!movement.hasReachedTarget()) {
                                    Thread.sleep(50);
                                }

                                state = EmployeeState.ESPERANDO;
                                UpdateAnimationArray();
                            } else {
                                System.out.println(name + " no encontró máquina disponible");
                                Thread.sleep(1000);
                            }
                        }
                    }
                } else {
                    out.println("employeeCheckOrders " + name);
                    response = in.readLine();
                    boolean hasOrders = Boolean.parseBoolean(response);

                    if (hasOrders) {
                        out.println("employeeTakeOrder " + name);
                        String orderResponse = in.readLine();
                        int pedidoId = Integer.parseInt(orderResponse);

                        if (pedidoId != -1) {
                            out.println("employeeGetMachine " + name);
                            String machineName = in.readLine();

                            if (!machineName.equals("none")) {
                                if (machineName.startsWith("Machine")) {
                                    assignedMachineIndex = Integer.parseInt(machineName.substring(7));
                                }

                                state = EmployeeState.CAMINANDO_A_MAQUINA;
                                UpdateAnimationArray();
                                movement.setTargetToMachine(assignedMachineIndex);

                                while (!movement.hasReachedTarget()) {
                                    Thread.sleep(50);
                                }

                                System.out.println(name + " terminó de preparar pedido #" + pedidoId);

                                out.println("employeeDeliverOrder " + name + " " + pedidoId);
                                in.readLine();

                                state = EmployeeState.ESPERANDO;
                                UpdateAnimationArray();
                                movement.setTargetToWaitingArea();

                                while (!movement.hasReachedTarget()) {
                                    Thread.sleep(50);
                                }

                                state = EmployeeState.ESPERANDO;
                                UpdateAnimationArray();
                            }
                        }
                    } else {
                        if (state != EmployeeState.ESPERANDO) {
                            state = EmployeeState.ESPERANDO;
                            UpdateAnimationArray();
                            movement.setTargetToWaitingArea();
                        }
                        Thread.sleep(1000);
                    }
                }
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            running = false;
        }
    }
}