package Agents;

import java.util.Random;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Machine extends Thread {

    private final String nombre;
    private final Lock lock = new ReentrantLock(true);
    private final Random random = new Random();
    private EstadoMaquina estado;
    private final boolean isRunning = true;
    private final int tiempoPreparar;
    private final int tiempoReparacion;


    public Machine(String nombre, int tiempoPreparar, int tiempoReparacion) {
        this.nombre = nombre;
        this.estado = EstadoMaquina.OPERATIVA;
        this.tiempoPreparar = tiempoPreparar;
        this.tiempoReparacion = tiempoReparacion;
    }

    public boolean tryUse() {
        if (estado == EstadoMaquina.OPERATIVA && lock.tryLock()) {
            estado = EstadoMaquina.EN_USO;
            return true;
        }
        return false;
    }

    public void preparar(String empleado) {

        try {
            // Simular trabajo de preparación
            runFor(tiempoPreparar);

            // Probabilidad de avería (20%)
            if (random.nextInt(10) < 2) {
                estado = EstadoMaquina.AVERIADA;
            } else {
                estado = EstadoMaquina.OPERATIVA;
            }

        } finally {
            liberar();
        }
    }

    public void liberar() {
        lock.unlock();

    }

    @Override
    public void run() {
        while (isRunning) {
            switch (estado) {
                case OPERATIVA:
                    // Esperando a ser usada
                    break;

                case AVERIADA:
                    // Intentar auto-reparación
                    if (lock.tryLock()) {
                        try {
                            estado = EstadoMaquina.REPARANDO;
                            runFor(tiempoReparacion);
                            estado = EstadoMaquina.OPERATIVA;
                            System.out.println(nombre + " reparada - vuelve a OPERATIVA");
                        } finally {
                            lock.unlock();
                        }
                    }
                    break;

                case EN_USO:
                    // Siendo usada por un empleado
                    break;

                case REPARANDO:
                    // En proceso de reparación
                    break;
            }
            esperar(1000);
        }
    }

    public EstadoMaquina getEstado() {
        return estado;
    }

    public String getNombre() {
        return nombre;
    }

    private void esperar(int t) {
        try {
            Thread.sleep(t);
        } catch (InterruptedException e) {
            System.out.println(e);
        }
    }

    private void runFor(long seconds) {
        long millis = seconds * 1000;
        long start = System.currentTimeMillis();
        while (System.currentTimeMillis() - start < millis) {
            double x = Math.sin(System.nanoTime());
        }
    }


    @Override
    public String toString() {
        return nombre + " - " + estado;
    }

    public enum EstadoMaquina {OPERATIVA, EN_USO, AVERIADA, REPARANDO}
}