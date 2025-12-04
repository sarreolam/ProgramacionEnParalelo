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
    private int assignedCounterIndex = -1;
    private int assignedMachineIndex = -1;

    private BufferedImage[] walkAnim;
    private BufferedImage[] workAnim;
    private BufferedImage[] currentAnim;
    private int frameIndex = 0;
    private long lastTime = 0;
    private final int frameDelay = 120;

    private final int tiempoAtender;
    private final double velocidadMovimiento;

    public Employee(String name, Counter counter, Window window, Kitchen kitchen, int tiempoAtender, int velocidadMovimiento) {
        this.name = name;
        this.counter = counter;
        this.kitchen = kitchen;
        this.window = window;
        this.tiempoAtender = tiempoAtender;
        this.velocidadMovimiento = velocidadMovimiento;
        this.state = EmployeeState.FUERA;
        this.movement = new EmployeeMovement(400, 300, velocidadMovimiento);
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
            case ESPERANDO:
            case ATENDIENDO:
            case PREPARANDO:
            case FUERA:
                currentAnim = workAnim;
                break;
            case CAMINANDO_AL_MOSTRADOR:
            case CAMINANDO_A_VENTANILLA:
            case CAMINANDO_A_MAQUINA:
            case SALIENDO:
                currentAnim = walkAnim;
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

    public String getEmployeeName() {
        return name;
    }

    public EmployeeMovement getMovement() {
        return movement;
    }

    public void UpdateAnimation() {
        movement.moveTowardsTarget();
    }

    @Override
    public void run() {
        System.out.println(name + " ha comenzado su turno.");
        state = EmployeeState.FUERA;
        kitchen.entrarCocina(name);

        while (isRunning) {
            try {
                if (window.hayCarrosEsperando() && window.intentarAtender()) {
                    try {
                        state = EmployeeState.CAMINANDO_A_VENTANILLA;
                        UpdateAnimationArray();
                        movement.setTargetToWindow();
                        System.out.println(name + " va a la ventanilla a atender.");

                        esperarLlegada();

                        state = EmployeeState.ATENDIENDO;
                        UpdateAnimationArray();
                        Thread.sleep(tiempoAtender * 1000L);

                        int pedidoId = window.empleadoLlega(name);
                        if (pedidoId != -1) {
                            kitchen.agregarPedido(pedidoId, "window");
                        }
                    } finally {
                        window.liberarAtencion();
                    }
                }
                else if (counter.hayClientesEsperando()) {
                    assignedCounterIndex = counter.obtenerCounterLibre();

                    if (assignedCounterIndex != -1) {
                        try {
                            state = EmployeeState.CAMINANDO_AL_MOSTRADOR;
                            UpdateAnimationArray();
                            movement.setTargetToCounter(assignedCounterIndex);
                            System.out.println(name + " va al counter " + assignedCounterIndex + " a atender.");

                            esperarLlegada();



                            int pedidoId = counter.empleadoLlega(name, this);
                            if (pedidoId != -1) {
                                kitchen.agregarPedido(pedidoId, "counter");
                            }
                        } finally {
                            counter.liberarCounter(assignedCounterIndex);
                            assignedCounterIndex = -1;
                        }
                    } else {
                        state = EmployeeState.ESPERANDO;
                        UpdateAnimationArray();
                        movement.setTargetToWaitingArea();
                        System.out.println(name + " - Counters ocupados, esperando...");
                        Thread.sleep(500);
                    }
                }
                else if (kitchen.hayPedidosEnEspera()) {
                    if (!kitchen.hayMaquinasDisponibles()) {
                        state = EmployeeState.ESPERANDO;
                        UpdateAnimationArray();
                        movement.setTargetToWaitingArea();
                        System.out.println(name + " - Máquinas ocupadas, esperando...");
                        Thread.sleep(500);
                        continue;
                    }

                    Pedido pedido = kitchen.tomarPedido();
                    if (pedido != null) {
                        Machine maquina = kitchen.obtenerMaquinaLibre();

                        if (maquina != null) {
                            assignedMachineIndex = kitchen.getIndexOfMachine(maquina);

                            state = EmployeeState.CAMINANDO_A_MAQUINA;
                            UpdateAnimationArray();
                            movement.setTargetToMachine(assignedMachineIndex);
                            System.out.println(name + " va a la máquina " + assignedMachineIndex);

                            esperarLlegada();

                            state = EmployeeState.PREPARANDO;
                            UpdateAnimationArray();
                            System.out.println(name + " prepara pedido #" + pedido.getId() + " usando " + maquina.getNombre());
                            maquina.preparar(name);

                            if (pedido.getSource().equals("counter")) {
                                assignedCounterIndex = counter.obtenerCounterParaEntrega();

                                if (assignedCounterIndex != -1) {
                                    try {
                                        state = EmployeeState.CAMINANDO_AL_MOSTRADOR;
                                        UpdateAnimationArray();
                                        movement.setTargetToCounter(assignedCounterIndex);
                                        System.out.println(name + " va al counter " + assignedCounterIndex + " a entregar");

                                        esperarLlegada();

                                        state = EmployeeState.ATENDIENDO;
                                        UpdateAnimationArray();
                                        Thread.sleep(tiempoAtender * 1000L);
                                        counter.entregarPedido(pedido, name);
                                    } finally {
                                        counter.liberarCounter(assignedCounterIndex);
                                    }
                                } else {
                                    state = EmployeeState.ESPERANDO;
                                    UpdateAnimationArray();
                                    movement.setTargetToWaitingArea();
                                    Thread.sleep(500);
                                }
                            } else {
                                if (window.intentarAtender()) {
                                    try {
                                        state = EmployeeState.CAMINANDO_A_VENTANILLA;
                                        UpdateAnimationArray();
                                        movement.setTargetToWindow();
                                        System.out.println(name + " va a ventanilla a entregar");

                                        esperarLlegada();

                                        state = EmployeeState.ATENDIENDO;
                                        UpdateAnimationArray();
                                        Thread.sleep(tiempoAtender * 1000L);
                                        window.entregarPedido(pedido, name);
                                    } finally {
                                        window.liberarAtencion();
                                    }
                                } else {
                                    state = EmployeeState.ESPERANDO;
                                    UpdateAnimationArray();
                                    movement.setTargetToWaitingArea();
                                    Thread.sleep(500);
                                }
                            }
                        } else {
                            System.out.println(name + " no encontró máquina libre.");
                            state = EmployeeState.ESPERANDO;
                            UpdateAnimationArray();
                            movement.setTargetToWaitingArea();
                            Thread.sleep(1000);
                        }
                    }
                }
                else {
                    state = EmployeeState.ESPERANDO;
                    UpdateAnimationArray();
                    movement.setTargetToWaitingArea();
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
    private void esperarLlegada() throws InterruptedException {
        // Dar tiempo para que la animación de movimiento sea visible
        Thread.sleep(1000);

        while (!movement.hasReachedTarget()) {
             Thread.sleep(100);
         }
    }
    public void setState(EmployeeState s){
        state = s;
    }
    public int getTiempoAtender(){
        return tiempoAtender;
    }

    public enum EmployeeState {
        NACIENDO, CAMINANDO_AL_MOSTRADOR, CAMINANDO_A_VENTANILLA,
        CAMINANDO_A_MAQUINA, ESPERANDO, ATENDIENDO, SALIENDO, FUERA, PREPARANDO
    }
}