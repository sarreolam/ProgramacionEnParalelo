package Agents;

import Buffers.Chair;
import Buffers.CounterClient;
import Buffers.Store;

import java.util.Random;

public class Client extends Thread {

    private final String name;
    private final CounterClient counterClient;
    private final Store store;
    private ClientState state;
    private boolean isRunning = true;

    public Client(String name, CounterClient counterClient, Store store) {
        this.name = name;
        this.counterClient = counterClient;
        this.store = store;
        this.state = ClientState.CAMINANDO_A_LA_TIENDA;
    }

    public void run() {
        store.entrarTienda(name);
        while(isRunning){
            Random r = new Random();
            int x = r.nextInt(100);
            if (x < 40) {
                //pedir
                state = ClientState.CAMINANDO_AL_MOSTRADOR;
                System.out.println(name + " esta pidiendo");
                esperar(1000);
                state = ClientState.PIDIENDO;
                counterClient.clienteLlega(name);
                System.out.println(name + " se aleja del counter con su comida");
            } else if (x < 60) {
                //sentar
                Chair chair = store.obtenerSillaLibre();
                if (chair != null){
                System.out.println(name + " se fue a sentar en "+ chair.getNombre());
                    state = ClientState.CAMINANDO_A_ASIENTOS;
                    esperar(1000);
                    state = ClientState.SENTADO;
                    esperar(10000);
                    chair.liberar();
                }
            } else if (x < 70) {
                //caminar
                System.out.println(name + " camina porque quizo");
                state = ClientState.CAMINANDO;
                esperar(2000);
                //mover el personaje a un lugar random
            }else if (x < 85){
                System.out.println(name + " No hace nada");
                esperar(2000);
                //No hace nada, se queda parado o sentado
            }else{
                //salir
                state = ClientState.SALIENDO;
                isRunning = false;
                esperar(1000);
                store.salirTienda(name);
            }
            state = ClientState.ESPERANDO;
        }
        state = ClientState.FUERA;
    }
    private void esperar(int t) {
        try {
            Thread.sleep(t);
        } catch (InterruptedException e) {
            System.out.println(e);
        }
    }
    public ClientState getClientState() {
        return state;
    }

    public enum ClientState {CAMINANDO_A_LA_TIENDA, CAMINANDO_AL_MOSTRADOR, CAMINANDO_A_ASIENTOS, CAMINANDO, ESPERANDO, PIDIENDO, SALIENDO, FUERA, SENTADO}
}