package Agents;
import java.util.Random;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import Buffers.Counter;
import Buffers.Kitchen;


public class Employee extends Thread {
    private final String name;
    private final Counter counter;
    private final Kitchen kitchen;
    private boolean isRunning = true;

    public enum EstadoEmpleado { NACIENDO, CAMINANDO_AL_MOSTRADOR, CAMINANDO_A_MAQUINA, ESPERANDO, ATENDIENDO, SALIENDO, FUERA, PREPARANDO }
    private EstadoEmpleado state;

    public Employee(String name, Counter counter, Kitchen kitchen) {
        this.name = name;
        this.counter = counter;
        this.kitchen = kitchen;
        this.state = EstadoEmpleado.NACIENDO;
    }

    @Override
    public void run() {
        
        System.out.println(name + " ha comenzado su turno.");
        state = EstadoEmpleado.FUERA;

        kitchen.entrarCocina(name);
        while (isRunning) {
            try {
                if (counter.hayClientesEsperando()) {
                    state = EstadoEmpleado.CAMINANDO_AL_MOSTRADOR;
                    System.out.println(name + " va al counter a atender.");
                    Thread.sleep(1000);
                    state = EstadoEmpleado.ATENDIENDO;
                    int pedidoId = counter.empleadoLlega(name);
                    if (pedidoId != -1) {
                        kitchen.agregarPedido(pedidoId);
                    }
                } else if (kitchen.hayPedidosEnEspera()) {
                    Integer pedido = kitchen.tomarPedido();
                    if (pedido != null) {
                        state = EstadoEmpleado.CAMINANDO_A_MAQUINA;
                        Machine maquina = kitchen.obtenerMaquinaLibre();
                        if (maquina != null) {
                            System.out.println(name + " prepara pedido #" + pedido + " usando " + maquina.getNombre());
                            state = EstadoEmpleado.PREPARANDO;
                            maquina.preparar(name);
                            System.out.println(name + " terminó pedido #" + pedido);
                            counter.entregarPedido(pedido, name);
                        } else {
                            System.out.println(name + " no encontró máquina libre.");
                            Thread.sleep(1000);
                        }

                    }
                } else {
                    state = EstadoEmpleado.ESPERANDO;
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
    public EstadoEmpleado getEstado() {
        return state;
    }

}
