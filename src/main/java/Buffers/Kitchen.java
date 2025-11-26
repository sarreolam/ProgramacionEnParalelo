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

    public Kitchen(Machine[] machines) {
        this.empleadosDentro = new Semaphore(5, true);
        this.machines = machines;
    }

    public synchronized void agregarPedido(int pedidoId) {
        pedidosPendientes.add(pedidoId);
        System.out.println("📦 Pedido #" + pedidoId + " agregado a la cocina. Total pendientes: " + pedidosPendientes.size());
    }

    public synchronized boolean hayPedidosEnEspera() {
        return !pedidosPendientes.isEmpty();
    }

    public synchronized Integer tomarPedido() {
        Integer pedido = pedidosPendientes.poll();
        if (pedido != null) {
            System.out.println("Pedido #" + pedido + " tomado de la cocina. Quedan: " + pedidosPendientes.size());
        }
        return pedido;
    }

    public void entrarCocina(String empleado){
        try {
            empleadosDentro.acquire();
            System.out.println(empleado + " entra a la cocina.");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void salirCocina(String empleado) {
        System.out.println(empleado + " sale de la cocina.");
        empleadosDentro.release();
    }

    public Machine obtenerMaquinaLibre() {
        for (Machine m : machines) {
            if (m.tryUse()) {
                System.out.println(m.getNombre() + " asignada!");
                return m;
            }
        }
        System.out.println("No hay maquinas disponibles");
        return null;
    }

    //cambio el tryUse() hacia que se bloquearan todas la maquinas
    public int getUsedMachines() {
        int usedMachines = 0;
        for(Machine m : machines) {
            if(m.getEstado() == Machine.EstadoMaquina.EN_USO) {
                usedMachines++;
            }
        }
        return usedMachines;
    }

    //lo mismo
    public int getUnusedMachines() {
        int unusedMachines = 0;
        for(Machine m : machines) {
            if(m.getEstado() == Machine.EstadoMaquina.OPERATIVA) {
                unusedMachines++;
            }
        }
        return unusedMachines;
    }

    public int getMachinesLength(){
        return machines.length;
    }

    public synchronized int getPedidosPendientes() {
        return pedidosPendientes.size();
    }
}