package Buffers;

import Agents.ClientInServer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class CounterServer extends Thread {
    public static boolean sigue = true;
    ArrayList<ClientInServer> clients;
    private final Counter counter;
    private final int puerto = 5000;


    public CounterServer(Counter counter) {
        this.counter = counter;

    }

    public void run() {
        try (ServerSocket serverSocket = new ServerSocket(puerto)) {
            System.out.println("Servidor iniciado. Esperando conexiones en el puerto " + puerto + "...");

            clients = new ArrayList<ClientInServer>();
            int i = 0;
            while (sigue) {
                Socket clientSocket = serverSocket.accept();

                if (i == 0){
                    new Thread(new TryServerCounter(clientSocket, counter)).start();
                }else{
                    System.out.println("New connection: " + clientSocket);
                    new Thread(new ClientInServer("Client" + (i + 1), clientSocket, counter)).start();
                }
                i++;
            }
            System.out.println("Server has finished");
            serverSocket.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
class TryServerCounter implements Runnable{
    private Socket socket;
    private Counter counter;
    TryServerCounter(Socket socket, Counter counter){
        this.socket = socket;
        this.counter = counter;

    }
    public void run() {
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

            out.println(counter.getCapMaxCounter());

            in.close();
            out.close();
            socket.close();
            System.out.println("Conexión con el cliente cerrada.");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}