package Buffers;

import Agents.ClientInServer;

import java.io.IOException;
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
                System.out.println("New connection: " + clientSocket);
                new Thread(new ClientInServer("Client" + (i + 1), clientSocket, counter)).start();
                i++;
            }
            System.out.println("Server has finished");
            serverSocket.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}