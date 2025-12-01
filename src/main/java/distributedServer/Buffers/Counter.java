package Buffers;

import Utils.Pedido;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.Semaphore;

public class Counter {
    private final int capMaxCounter;
    private final Semaphore espaciosDisponibles;
    private final Semaphore clientesEsperando;
    private final Object mutex = new Object();
    private final Queue<String> colaClientes = new LinkedList<>();
    private final Map<Integer, String> pedidosEnProceso = new HashMap<>();
    private final Map<String, Boolean> pedidosListos = new HashMap<>();
    private int idCounter = 0;

    public Counter(int capacidadMaxima) {
        this.capMaxCounter = capacidadMaxima;
        this.espaciosDisponibles = new Semaphore(capacidadMaxima, true);
        this.clientesEsperando = new Semaphore(0, true);
    }

    public void clienteLlega(String nombreCliente) {
        System.out.println(nombreCliente + " camina hacia el counter...");

        try {
            espaciosDisponibles.acquire();
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
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

    public int empleadoLlega(String nombreEmpleado) {
        if (!clientesEsperando.tryAcquire()) {
            return -1;
        }
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


}
