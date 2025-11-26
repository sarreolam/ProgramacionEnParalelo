package Agents;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.Random;

public class Machine extends Thread {

    public enum EstadoMaquina { OPERATIVA, EN_USO, AVERIADA, REPARANDO }

    private final String nombre;
    private EstadoMaquina estado;
    private final Lock lock = new ReentrantLock(true);
    private final Random random = new Random();
    private boolean isRunning = true;

    public Machine(String nombre) {
        this.nombre = nombre;
        this.estado = EstadoMaquina.OPERATIVA;
    }

    public boolean tryUse() {
        if (estado == EstadoMaquina.OPERATIVA && lock.tryLock()) {
            estado = EstadoMaquina.EN_USO;
            return true;
        }
        return false;
    }

    public void preparar(String empleado){

        try {
            // Simular trabajo de preparación
            runFor(2000);

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
                            runFor(3000);
                            estado = EstadoMaquina.OPERATIVA;
                            System.out.println( nombre + " reparada - vuelve a OPERATIVA");
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

    private void esperar(int t){
        try{
            Thread.sleep(t);
        }catch(InterruptedException e){
            System.out.println(e);
        }
    }

    private void runFor(long millis) {
        long start = System.currentTimeMillis();
        while (System.currentTimeMillis() - start < millis) {
            // Mantente ocupado (simulando trabajo)
            double x = Math.sin(System.nanoTime());
        }
    }

    @Override
    public String toString() {
        return nombre + " - " + estado;
    }
}