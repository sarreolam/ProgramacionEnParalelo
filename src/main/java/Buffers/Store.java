package Buffers;

import java.util.concurrent.Semaphore;

public class Store {
    private final Semaphore capacidad;
    private final int maxClientes;

    public Store(int maxClientes) {
        this.maxClientes = maxClientes;
        this.capacidad = new Semaphore(maxClientes, true);
    }

    public void entrar(String cliente) throws InterruptedException {
        capacidad.acquire();
        System.out.println(cliente + " entra a la tienda.");
    }

    public void salir(String cliente) {
        System.out.println(cliente + " sale de la tienda.");
        capacidad.release();
    }
    public int getClientesDentro() {
        return maxClientes - capacidad.availablePermits();
    }
}

