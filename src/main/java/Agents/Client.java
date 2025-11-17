package Agents;
import java.util.Random;
import Buffers.Counter;


public class Client extends Thread {
    private final String name;
    private final Counter counter;

    public enum EstadoCliente { NACIENDO, CAMINANDO, ESPERANDO, PIDIENDO, SALIENDO, FUERA }
    private EstadoCliente state;

    public Client(String name, Counter counter) {
        this.name = name;
        this.counter = counter;
        this.state = EstadoCliente.NACIENDO;
    }

    @Override
    public void run() {

        state = EstadoCliente.CAMINANDO;
        esperar(1000);
        state = EstadoCliente.PIDIENDO;
        counter.clienteLlega(name);
        state = EstadoCliente.SALIENDO;
        esperar(1000);
        state = EstadoCliente.FUERA;
        System.out.println(name + " salió del restaurante.");

    }

    private void esperar(int t){
		try{
			Thread.sleep(t);
		}catch(InterruptedException e){
			System.out.println(e);		
		}
	}
}
