package Buffers;

import Agents.Employee;
import Utils.Pedido;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;

public class Counter {
    private final int capMaxCounter;
    private final Semaphore espaciosDisponibles;
    private final Semaphore clientesEsperando;
    private final Object mutex = new Object();
    private final Queue<String> colaClientes = new LinkedList<>();
    private final Map<Integer, String> pedidosEnProceso = new HashMap<>();
    private final Map<String, Boolean> pedidosListos = new HashMap<>();
    private int idCounter = 0;

    // Para distribuir empleados entre counters
    private final AtomicInteger nextCounterIndex = new AtomicInteger(0);

    // Para rastrear qué counters están ocupados
    private final Map<Integer, Boolean> countersOcupados = new HashMap<>();

    public Counter(int capacidadMaxima) {
        this.capMaxCounter = capacidadMaxima;
        this.espaciosDisponibles = new Semaphore(capacidadMaxima, true);
        this.clientesEsperando = new Semaphore(0, true);

        for (int i = 0; i < capacidadMaxima; i++) {
            countersOcupados.put(i, false);
        }
    }

    public void clienteLlega(String nombreCliente) {
        System.out.println(nombreCliente + " camina hacia el counter...");

        try {
            espaciosDisponibles.acquire();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        synchronized (mutex) {
            colaClientes.add(nombreCliente);
            System.out.println(nombreCliente + " está esperando en el counter. [Clientes en fila: " + colaClientes.size() + "]");
        }

        clientesEsperando.release();

        boolean atendido = false;
        while (!atendido) {
            synchronized (mutex) {
                if (pedidosListos.getOrDefault(nombreCliente, false)) {
                    atendido = true;
                }
            }
            esperar(200);
        }

        System.out.println(nombreCliente + " fue atendido y deja el counter.");
        espaciosDisponibles.release();
    }

    public int empleadoLlega(String nombreEmpleado, Employee employee) {
        if (!clientesEsperando.tryAcquire()) {
            return -1;
        }
        employee.setState(Employee.EmployeeState.ATENDIENDO);
        employee.UpdateAnimationArray();
        esperar((int) (employee.getTiempoAtender() * 2000L));
        String cliente;
        synchronized (mutex) {
            cliente = colaClientes.poll();
        }
        int pedidoId = -1;

        if (cliente != null) {
            System.out.println(nombreEmpleado + " atiende a " + cliente + ".");
            esperar(2000);
            pedidoId = registrarPedido(cliente);
            System.out.println(nombreEmpleado + " terminó de atender a " + cliente + ".");
        }
        return pedidoId;
    }

    public int registrarPedido(String cliente) {
        synchronized (mutex) {
            int pedidoId = idCounter++;
            pedidosEnProceso.put(pedidoId, cliente);
            pedidosListos.put(cliente, false);
            System.out.println("Se registró pedido #" + pedidoId + " para " + cliente);
            return pedidoId;
        }
    }

    public void entregarPedido(Pedido pedido, String empleado) {
        synchronized (mutex) {
            String cliente = pedidosEnProceso.remove(pedido.getId());
            if (cliente != null) {
                pedidosListos.put(cliente, true);
                System.out.println(empleado + " entregó pedido #" + pedido.getId() + " a " + cliente);
            }
        }
    }

    public boolean hayClientesEsperando() {
        return clientesEsperando.availablePermits() > 0;
    }

    public int obtenerCounterLibre() {
        if (capMaxCounter <= 0) {
            return 0;
        }

        int index = nextCounterIndex.getAndIncrement() % capMaxCounter;
        return index;
    }

    public int obtenerCounterParaEntrega() {
        if (capMaxCounter <= 0) {
            return 0;
        }
        int index = nextCounterIndex.getAndIncrement() % capMaxCounter;
        return index;
    }

    private void esperar(int t) {
        try {
            Thread.sleep(t);
        } catch (InterruptedException e) {
            System.out.println(e);
        }
    }

    public int getClientesEnFila() {
        synchronized (mutex) {
            return colaClientes.size();
        }
    }

    public void liberarCounter(int index) {
        if (index >= 0 && index < capMaxCounter) {
            synchronized (mutex) {
                countersOcupados.put(index, false);
                System.out.println("✓ Counter " + index + " liberado");
            }
        }
    }

    public int getCapMaxCounter(){
        return capMaxCounter;
    }
}