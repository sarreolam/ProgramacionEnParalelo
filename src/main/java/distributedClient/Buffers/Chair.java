package Buffers;

import Agents.Client;

import java.util.Random;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Chair {
    private final String nombre;
    private final Lock lock = new ReentrantLock(true);

    public Chair(String nombre) {
        this.nombre = nombre;
    }

    public boolean tryUse() {
        return lock.tryLock();
    }
    public String getNombre() {
        return nombre;
    }
    public void liberar() {
        lock.unlock();
    }
}
