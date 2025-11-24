import Agents.ClientHandler;
import Agents.Machine;
import Buffers.Counter;
import Buffers.Kitchen;
import Buffers.Store;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

public class CounterServer {
    private ServerSocket serverSocket;
    private final Machine[] machines;
    private final Counter counter;
    private final Kitchen kitchen;
    private final Store store;

    public CounterServer(int port, Machine[] machines, Counter counter, Store store) throws IOException {
        serverSocket = new ServerSocket(port);
        this.machines = machines;
        this.kitchen = new Kitchen(machines);
        this.counter = counter;
        this.store = store;
    }

    public void startServer() throws IOException {
        System.out.println("Server initialized");
        while (true) {
            Socket socket = serverSocket.accept();
            System.out.println("New connection: " + socket);
            new Thread(new ClientHandler(socket, counter, kitchen, store)).start();
        }
    }

    public static void main(String[] args) throws IOException {
        //Scanner scanner = new Scanner(System.in);
        //System.out.print("Enter number of machines: ");
        //int numMachines = scanner.nextInt();
        Machine[] machines = new Machine[5];

        for (int i = 0; i < 5; i++) {
            machines[i] = new Machine("Machine" + i);
            machines[i].start();
        }

        Counter counter = new Counter();
        Store store = new Store(10);
        CounterServer counterServer = new CounterServer(5000, machines, counter, store);
        counterServer.startServer();
    }
}
