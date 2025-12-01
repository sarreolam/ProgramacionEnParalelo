package Buffers;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.Semaphore;

public class CounterClient {
    private final int capacidadMaxima;
    private final Semaphore espaciosDisponibles;
    private final Object mutex = new Object();
    private final Queue<String> colaClientes = new LinkedList<>();

    private final Map<Integer, String> pedidosEnProceso = new HashMap<>();
    private final Map<String, Boolean> pedidosListos = new HashMap<>();


    public CounterClient() {
        this.capacidadMaxima = 5;
        this.espaciosDisponibles = new Semaphore(capacidadMaxima, true);
    }

    public void clienteLlega(String nombreCliente) {

        try {
            espaciosDisponibles.acquire();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        try (Socket socket = new Socket("localhost", 5000);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            in.readLine();


            in.close();
            out.close();
            socket.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            espaciosDisponibles.release();
        }
    }

}