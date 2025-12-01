package Agents;

import Buffers.Counter;
import Buffers.Kitchen;
import Buffers.Window;
import Utils.Pedido;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class Employee extends Thread {
    private final String name;
    private final EmployeeMovement movement;
    private boolean isRunning = true;
    private EmployeeState state;

    private Counter counter;
    private Window window;
    private Kitchen kitchen;
    private final int assignedCounterIndex = -1;
    private final int assignedMachineIndex = -1;
    private BufferedImage[] walkAnim;
    private BufferedImage[] workAnim;
    private BufferedImage[] currentAnim;
    private int frameIndex = 0;
    private long lastTime = 0;
    private final int frameDelay = 120;
    public Employee(String name) {
        this.name = name;
        this.state = EmployeeState.NACIENDO;
        this.movement = new EmployeeMovement(400, 300);
        LoadSprites();
        UpdateAnimationArray();
    }

    public Employee(String name, Counter counter, Window window, Kitchen kitchen) {
        this.name = name;
        this.counter = counter;
        this.kitchen = kitchen;
        this.window = window;
        this.state = EmployeeState.NACIENDO;
        this.movement = new EmployeeMovement(400, 300);
        LoadSprites();
        UpdateAnimationArray();
    }

    private void LoadSprites() {
        try {
            walkAnim = LoadAnim("Images/walkingClient_", 11);
            workAnim = LoadAnim("Images/plinkIdle_", 20);
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
        state = EmployeeState.FUERA;

        kitchen.entrarCocina(name);
        while (isRunning) {
            try {
                if (counter.hayClientesEsperando()) {
                    state = EmployeeState.CAMINANDO_AL_MOSTRADOR;
                    System.out.println(name + " va al counter a atender.");
                    Thread.sleep(1000);
                    state = EmployeeState.ATENDIENDO;
                    int pedidoId = counter.empleadoLlega(name);
                    if (pedidoId != -1) {
                        kitchen.agregarPedido(pedidoId, "counter");
                    }
                }else if(window.hayCarrosEsperando()) {
                    state = EmployeeState.CAMINANDO_A_VENTANILLA;
                    System.out.println(name + " va a la ventanilla a atender.");
                    Thread.sleep(1000);
                    state = EmployeeState.ATENDIENDO;
                    int pedidoId = window.empleadoLlega(name);
                    if (pedidoId != -1) {
                        kitchen.agregarPedido(pedidoId, "window");
                    }
                } else if (kitchen.hayPedidosEnEspera()) {
                    Pedido pedido = kitchen.tomarPedido();
                    if (pedido != null) {
                        state = EmployeeState.CAMINANDO_A_MAQUINA;
                        Machine maquina = kitchen.obtenerMaquinaLibre();
                        if (maquina != null) {
                            System.out.println(name + " prepara pedido #" + pedido.getId() + " usando " + maquina.getNombre());
                            state = EmployeeState.PREPARANDO;
                            maquina.preparar(name);


                            if(pedido.getSource().equals("counter")){
                                state = EmployeeState.CAMINANDO_AL_MOSTRADOR;
                                Thread.sleep(1000);
                                counter.entregarPedido(pedido, name);
                            }else{
                                state = EmployeeState.CAMINANDO_A_VENTANILLA;
                                Thread.sleep(1000);
                                window.entregarPedido(pedido, name);
                            }
                        } else {
                            System.out.println(name + " no encontró máquina libre.");
                            Thread.sleep(1000);
                        }

                    }
                } else {
                    state = EmployeeState.ESPERANDO;
                    System.out.println(name + " no ve clientes ni pedidos, esperando...");
                    Thread.sleep(1000);
                }
            } catch (InterruptedException e) {
                isRunning = false;
                kitchen.salirCocina(name);
                System.out.println(name + " terminó su turno.");
            }
        }
    }

    public enum EmployeeState {NACIENDO, CAMINANDO_AL_MOSTRADOR, CAMINANDO_A_VENTANILLA, CAMINANDO_A_MAQUINA, ESPERANDO, ATENDIENDO, SALIENDO, FUERA, PREPARANDO}
}