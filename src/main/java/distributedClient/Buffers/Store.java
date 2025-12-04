package Buffers;

import Agents.Client;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.Semaphore;

public class Store {

    private final Semaphore empleadosDentro;
    private final int nextOrderId = 0;
    private final Chair[] chairs;
    private final int capacidadMaxima;

    public Store(int numClients, Chair[] chairs) {
        this.capacidadMaxima = numClients;
        this.empleadosDentro = new Semaphore(numClients, true);
        this.chairs = chairs;
    }

    public void entrarTienda(String cliente) {
        try {
            empleadosDentro.acquire();
            System.out.println(cliente + " entra a la tienda.");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void salirTienda(String cliente) {
        System.out.println(cliente + " sale de la tienda.");
        empleadosDentro.release();
    }

    public Chair obtenerSillaLibre() {
        for (Chair m : chairs) {
            if (m.tryUse()) {
                System.out.println(m.getNombre() + " asignada!");
                return m;
            }
        }
        System.out.println("No hay maquinas disponibles");
        return null;
    }
    public int getChairsNum(){
        return chairs.length;
    }
    public int getClientesDentro() {
        return capacidadMaxima - empleadosDentro.availablePermits();
    }
}
