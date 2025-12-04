package Buffers;

import Agents.Client;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;

public class CounterClient {
    private final int capacidadMaxima;
    private final Semaphore espaciosDisponibles;
    private final Object mutex = new Object();

    public final int numberOfCounters;
    private final AtomicInteger nextCounter = new AtomicInteger(0);
    private final Map<String, Integer> clientToCounterMap = new HashMap<>();

    public CounterClient(int numberOfCounters) {
        this.capacidadMaxima = numberOfCounters;
        this.espaciosDisponibles = new Semaphore(capacidadMaxima, true);
        this.numberOfCounters = numberOfCounters;
    }

    public int assignCounter(String nombreCliente) {
        synchronized (mutex) {
            int assignedCounter = nextCounter.getAndUpdate(n -> (n + 1) % numberOfCounters);
            clientToCounterMap.put(nombreCliente, assignedCounter);
            System.out.println(nombreCliente + " asignado al counter " + assignedCounter);
            return assignedCounter;
        }
    }

    public void clienteLlega(String nombreCliente, Client client) {

        try {
            espaciosDisponibles.acquire();
            client.setState(Client.ClientState.PIDIENDO);
            client.UpdateAnimationArray();
            client.UpdateTargetByState();
            System.out.println(client.getClientName() + " esta pidiendo en counter " + client.getAssignedCounterIndex());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        try (Socket socket = new Socket("localhost", 5000);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            out.println(nombreCliente);
            in.readLine();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            espaciosDisponibles.release();
            synchronized (mutex) {
                clientToCounterMap.remove(nombreCliente);
            }
        }
    }

    public Integer getClientCounter(String nombreCliente) {
        synchronized (mutex) {
            return clientToCounterMap.get(nombreCliente);
        }
    }

    public int getClientsAtCounter(int counterIndex) {
        synchronized (mutex) {
            return (int) clientToCounterMap.values().stream()
                    .filter(idx -> idx == counterIndex)
                    .count();
        }
    }
}