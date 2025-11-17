package Buffers;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.Semaphore;

import Agents.Machine;

public class Kitchen {

    private final Semaphore empleadosDentro;
    private final Queue<Integer> pedidosPendientes = new LinkedList<>();
    private final Machine[] machines;
    private int nextOrderId = 0;

    public Kitchen( Machine[] machines) {
        this.empleadosDentro = new Semaphore(5, true);
        this.machines = machines;
    }

    public synchronized void agregarPedido(int pedidoId) {
        pedidosPendientes.add(pedidoId);
        System.out.println("📦 Pedido #" + pedidoId + " agregado a la cocina.");
    }

    public synchronized boolean hayPedidosEnEspera() {
        return !pedidosPendientes.isEmpty();
    }

    public synchronized Integer tomarPedido() {
        return pedidosPendientes.poll();
    }


    public void entrarCocina(String empleado){
        try {
          empleadosDentro.acquire();
        } catch (InterruptedException e) {
          // TODO Auto-generated catch block
        }
        System.out.println(empleado + " entra a la cocina.");
    }

    public void salirCocina(String empleado) {
        System.out.println(empleado + " sale de la cocina.");
        empleadosDentro.release();
    }

    public Machine obtenerMaquinaLibre() {
        for (Machine m : machines) {
            if (m.tryUse()) return m;
        }
        return null;
    }
    public synchronized int getPedidosPendientes() {
        return pedidosPendientes.size();
    }
}

