package Agents;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Objects;
import java.util.Random;
import Buffers.Counter;


public class Client extends Thread {
    private final String name;
    private int port = 5000;
    private boolean running = true;

    public enum ClientState { NACIENDO, CAMINANDO, ESPERANDO, PIDIENDO, SALIENDO, FUERA }
    private ClientState state;

    public Client(String name) {
        this.name = name;
        this.state = ClientState.NACIENDO;
    }

    public Client(String name, String port) {
        this.name = name;
        this.port = Integer.parseInt(port);
        this.state = ClientState.NACIENDO;
    }

    public ClientState getClientState() {
        return state;
    }


    @Override
    public void run() {
        System.out.println("Client " + name + " thread started");

        try {
            Socket socket = new Socket("localhost", port);

            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            System.out.println("Client " + name + " input stream created");

            state = ClientState.CAMINANDO;
            esperar(1000);

            state = ClientState.PIDIENDO;
            String message = "clientArrive " + name;
            out.println(message);
            out.flush();
            System.out.println("Client " + name + " waiting for response...");

            String resp = in.readLine();

            if(resp != null && resp.equals("OK")){
                System.out.println("Client " + name + " got OK, leaving");
                state = ClientState.SALIENDO;
                esperar(1000);
                state = ClientState.FUERA;
                running = false;
            } else {
                System.out.println("Client " + name + " did NOT get OK");
            }

            System.out.println("Client " + name + " closing socket");
            socket.close();

        } catch (IOException e){
            System.out.println("Client " + name + " IOException: " + e.getMessage());
            e.printStackTrace();
        }

        /*System.out.println("Cliente " + name + " ha iniciado su turno");
        state = ClientState.CAMINANDO;
        esperar(1000);
        state = ClientState.PIDIENDO;
        counter.clienteLlega(name);
        state = ClientState.SALIENDO;
        esperar(1000);
        state = ClientState.FUERA;
        System.out.println(name + " salió del restaurante.");*/

    }

    private void esperar(int t){
		try{
			Thread.sleep(t);
		}catch(InterruptedException e){
			System.out.println(e);		
		}
	}
}
