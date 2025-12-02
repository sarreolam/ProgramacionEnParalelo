/**
 * Verifica si hay máquinas disponibles sin bloquear
 * @return true si al menos una máquina está libre
 */
package Buffers;

import Agents.Machine;
import Utils.Pedido;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.Semaphore;

public class Kitchen {

    private final Semaphore empleadosDentro;
    private final Queue<Pedido> pedidosPendientes = new LinkedList<>();
    private final Machine[] machines;
    private final int nextOrderId = 0;

    public Kitchen(Machine[] machines) {
        this.empleadosDentro = new Semaphore(5, true);
        this.machines = machines;
    }
    public boolean hayMaquinasDisponibles() {
        for (Machine m : machines) {
            if (m.getEstado() == Machine.EstadoMaquina.OPERATIVA) {
                return true;
            }
        }
        return false;
    }
    public synchronized void agregarPedido(int pedidoId, String source) {
        pedidosPendientes.add(new Pedido(pedidoId, source));
        System.out.println("Pedido #" + pedidoId + " agregado a la cocina. Total pendientes: " + pedidosPendientes.size());
    }

    public synchronized boolean hayPedidosEnEspera() {
        return !pedidosPendientes.isEmpty();
    }

    public synchronized Pedido tomarPedido() {
        Pedido pedido = pedidosPendientes.poll();
        if (pedido != null) {
            System.out.println("Pedido #" + pedido.getId() + " tomado de la cocina. Quedan: " + pedidosPendientes.size());
        }
        return pedido;
    }

    public void entrarCocina(String empleado) {
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

    public int getIndexOfMachine(Machine machine) {
        if (machine == null) {
            return 0;
        }

        for (int i = 0; i < machines.length; i++) {
            if (machines[i] == machine) {
                return i;
            }
        }

        System.out.println("⚠️ Máquina no encontrada en el array, retornando índice 0");
        return 0;
    }

    public int getUsedMachines() {
        int usedMachines = 0;
        for (Machine m : machines) {
            if (m.getEstado() == Machine.EstadoMaquina.EN_USO) {
                usedMachines++;
            }
        }
        return usedMachines;
    }

    public int getUnusedMachines() {
        int unusedMachines = 0;
        for (Machine m : machines) {
            if (m.getEstado() == Machine.EstadoMaquina.OPERATIVA) {
                unusedMachines++;
            }
        }
        return unusedMachines;
    }

    public int getMachinesLength() {
        return machines.length;
    }

    public synchronized int getPedidosPendientes() {
        return pedidosPendientes.size();
    }
}