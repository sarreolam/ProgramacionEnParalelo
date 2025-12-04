package Buffers;

import Agents.DriveThru;
import Utils.Pedido;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicBoolean;

public class Window {
    private final Semaphore espacioVentanilla;   // SOLO 1 carro
    private final Semaphore carrosEsperando;     // pedidos listos para ser atendidos

    // control de empleado atendiendo
    private final AtomicBoolean empleadoAtendiendo = new AtomicBoolean(false);

    private final Object mutex = new Object();
    private final Queue<String> colaCarros = new LinkedList<>();

    private final Map<Integer, String> pedidosEnProceso = new HashMap<>();
    private final Map<String, Boolean> pedidosListos = new HashMap<>();
    private int idPedido = 10000;

    public Window() {
        this.espacioVentanilla = new Semaphore(1, true);
        this.carrosEsperando = new Semaphore(0, true);
    }

    public void carroLlega(String nombreCarro, DriveThru driveThru) {
        System.out.println(nombreCarro + " esta haciendo fila");

        try {
            espacioVentanilla.acquire(); // SOLO 1 cliente puede estar
            driveThru.setState(DriveThru.DriveThruState.EN_VENTANILLA);
            driveThru.getMovement().setTargetToVentanilla();
            esperar(200);

            System.out.println(nombreCarro + " llega a la ventanilla...");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        synchronized (mutex) {
            colaCarros.add(nombreCarro);
            System.out.println(nombreCarro + " está en ventanilla (fila: " + colaCarros.size() + ")");
        }
        carrosEsperando.release();

        driveThru.setState(DriveThru.DriveThruState.ESPERANDO_ORDEN);
        boolean atendido = false;
        while (!atendido) {
            synchronized (mutex) {
                if (pedidosListos.getOrDefault(nombreCarro, false)) {
                    atendido = true;
                }
            }
            esperar(200);
        }
        System.out.println(nombreCarro + " recibió su pedido y deja ventanilla.");
        espacioVentanilla.release();
    }
    public boolean intentarAtender() {
        return empleadoAtendiendo.compareAndSet(false, true);
    }
    public void liberarAtencion() {
        empleadoAtendiendo.set(false);
    }

    public int empleadoLlega(String name) {
        if (!carrosEsperando.tryAcquire()) {
            return -1;
        }

        String carro;
        synchronized (mutex) {
            carro = colaCarros.poll();
        }

        int pedidoId = -1;

        if (carro != null) {
            System.out.println(name + " atiende en drive-thru a " + carro);
            esperar(2000);
            pedidoId = registrarPedido(carro);
            System.out.println(name + " terminó drive-thru para " + carro);
        }

        return pedidoId;
    }

    private int registrarPedido(String carro) {
        synchronized (mutex) {
            int pedidoIdDT = idPedido++;
            pedidosEnProceso.put(pedidoIdDT, carro);
            pedidosListos.put(carro, false);
            System.out.println("Se registró pedido #" + pedidoIdDT + " para " + carro + " (DriveThru)");
            return pedidoIdDT;
        }
    }

    public void entregarPedido(Pedido pedido, String name) {
        synchronized (mutex) {
            String carro = pedidosEnProceso.remove(pedido.getId());
            if (carro != null) {
                pedidosListos.put(carro, true);
                System.out.println(name + " entregó pedido #" + pedido.getId() + " a " + carro + " (DriveThru)");
            }
        }
    }

    public boolean hayCarrosEsperando() {
        return carrosEsperando.availablePermits() > 0;
    }

    private void esperar(int t) {
        try {
            Thread.sleep(t);
        } catch (InterruptedException e) {
            System.out.println(e);
        }
    }

    public int getCarrosEnFila() {
        synchronized (mutex) {
            return colaCarros.size();
        }
    }
    public int ventanillaOcupada() {
        if (espacioVentanilla.availablePermits()==0){
            return 1;
        }
        return 0;
    }
}