package Buffers;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Chair {

    private final String nombre;
    private final int index;
    private final Lock lock = new ReentrantLock(true);

    public Chair(String nombre, int index) {
        this.nombre = nombre;
        this.index = index;
    }

    public boolean tryUse() {
        return lock.tryLock();
    }

    public void liberar() {
        lock.unlock();
    }

    public String getNombre() {
        return nombre;
    }

    public int getIndex() {
        return index;
    }
}
