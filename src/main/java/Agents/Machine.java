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
    private boolean action = false;

    public Machine(String nombre) {
        this.nombre = nombre;
        this.estado = EstadoMaquina.OPERATIVA;
    }

    public boolean tryUse() {
        if (estado == EstadoMaquina.OPERATIVA && lock.tryLock()) {
            estado = EstadoMaquina.EN_USO;
            return true;
        }else if(estado == EstadoMaquina.AVERIADA && lock.tryLock()){
            estado = EstadoMaquina.REPARANDO;
            return true;
        }
        return false;
    }

    public void preparar(String empleado){
        action = true;
        while (action) {
          runFor(200);
        }
        if (random.nextInt(10) < 2) {
          estado = EstadoMaquina.AVERIADA;
        }else{
          estado = EstadoMaquina.OPERATIVA;
        }
        liberar();

    }

    public void liberar() {
        lock.unlock();
    }

    @Override
    public void run() {
        while (isRunning) {
          switch (estado) {
              case OPERATIVA:

                  break;
              case AVERIADA:


                  break;
                  
              case EN_USO:
                  if(action){
                    runFor(1000);
                  }
                  action = false;
                  break;
              case REPARANDO:
                  if(action){
                    runFor(3000);
                    estado = EstadoMaquina.EN_USO;
                  }
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
        // Mantente ocupado (RUNNING)
        double x = Math.sin(System.nanoTime());
    }
  }
}
