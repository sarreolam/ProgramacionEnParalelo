package Agents;
import java.io.*;
import java.net.Socket;
import java.util.Objects;
import java.util.Random;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import Buffers.Counter;
import Buffers.Kitchen;

/*
public class Employee extends Thread {
    private final String name;
    private final Counter counter;
    private final Kitchen kitchen;
    private boolean isRunning = true;

    public enum EstadoEmpleado { NACIENDO, CAMINANDO_AL_MOSTRADOR, CAMINANDO_A_MAQUINA, ESPERANDO, ATENDIENDO, SALIENDO, FUERA, PREPARANDO }
    private EstadoEmpleado state;

    public Employee(String name, Counter counter, Kitchen kitchen) {
        this.name = name;
        this.counter = counter;
        this.kitchen = kitchen;
        this.state = EstadoEmpleado.NACIENDO;
    }

    @Override
    public void run() {
        
        System.out.println(name + " ha comenzado su turno.");
        state = EstadoEmpleado.FUERA;

        kitchen.entrarCocina(name);
        while (isRunning) {
            try {
                if (counter.hayClientesEsperando()) {
                    state = EstadoEmpleado.CAMINANDO_AL_MOSTRADOR;
                    System.out.println(name + " va al counter a atender.");
                    Thread.sleep(1000);
                    state = EstadoEmpleado.ATENDIENDO;
                    int pedidoId = counter.empleadoLlega(name);
                    if (pedidoId != -1) {
                        kitchen.agregarPedido(pedidoId);
                    }
                } else if (kitchen.hayPedidosEnEspera()) {
                    Integer pedido = kitchen.tomarPedido();
                    if (pedido != null) {
                        state = EstadoEmpleado.CAMINANDO_A_MAQUINA;
                        Machine maquina = kitchen.obtenerMaquinaLibre();
                        if (maquina != null) {
                            System.out.println(name + " prepara pedido #" + pedido + " usando " + maquina.getNombre());
                            state = EstadoEmpleado.PREPARANDO;
                            maquina.preparar(name);
                            System.out.println(name + " terminó pedido #" + pedido);
                            counter.entregarPedido(pedido, name);
                        } else {
                            System.out.println(name + " no encontró máquina libre.");
                            Thread.sleep(1000);
                        }

                    }
                } else {
                    state = EstadoEmpleado.ESPERANDO;
                    System.out.println(name + " no ve clientes ni pedidos, esperando...");
                    Thread.sleep(1000);
                }
            } catch (InterruptedException e) {
                isRunning = false;
                kitchen.salirCocina(name);
                System.out.println(name + " terminó su turno.");
            }
        }
    }
    public EstadoEmpleado getEstado() {
        return state;
    }

}*/
public class Employee extends Thread {
    public int port = 5000;
    private final String name;
    private boolean running = true;

    public enum EmployeeState { NACIENDO, CAMINANDO_AL_MOSTRADOR, CAMINANDO_A_MAQUINA, ESPERANDO, ATENDIENDO, SALIENDO, FUERA, PREPARANDO }
    private EmployeeState state;

    public Employee(String name) {
        this.name = name;
        this.state = EmployeeState.NACIENDO;
    }

    public Employee(String name, int port){
        this.name = name;
        this.port = port;
        this.state = EmployeeState.NACIENDO;
    }

    public EmployeeState getEmployeeState() {
        return state;
    }

    @Override
    public void run() {
        System.out.println(name + " ha comenzado su turno.");

        while (running) {
            try (Socket socket = new Socket("localhost", port);
                 PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                 BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

                out.println("employeeCheckClients " + name);
                boolean hasClients = Boolean.parseBoolean(in.readLine());

                if (hasClients) {
                    state = EmployeeState.CAMINANDO_AL_MOSTRADOR;
                    System.out.println(name + " va al counter a atender.");
                    Thread.sleep(1000);

                    state = EmployeeState.ATENDIENDO;
                    out.println("employeeArrive " + name);
                    String reply = in.readLine();
                    int pedidoId = Integer.parseInt(reply);

                    if (pedidoId != -1) {
                        out.println("employeeAddOrder " + name + " " + pedidoId);
                        in.readLine();

                        out.println("employeeCheckOrders " + name);
                        boolean hasOrders = Boolean.parseBoolean(in.readLine());

                        if (hasOrders) {
                            out.println("employeeTakeOrder " + name);
                            pedidoId = Integer.parseInt(in.readLine());

                            out.println("employeeGetMachine " + name);
                            String machineName = in.readLine();

                            if (!"none".equals(machineName)) {
                                state = EmployeeState.PREPARANDO;
                                System.out.println(name + " prepara pedido #" + pedidoId + " usando " + machineName);
                                Thread.sleep(2000);

                                out.println("employeeDeliverOrder " + name + " " + pedidoId);
                                in.readLine();
                            }
                        }
                    }
                } else {

                    out.println("employeeCheckOrders " + name);
                    boolean hasOrders = Boolean.parseBoolean(in.readLine());

                    if (hasOrders) {
                        out.println("employeeTakeOrder " + name);
                        int pedidoId = Integer.parseInt(in.readLine());

                        out.println("employeeGetMachine " + name);
                        String machineName = in.readLine();

                        if (!"none".equals(machineName)) {
                            state = EmployeeState.PREPARANDO;
                            System.out.println(name + " prepara pedido #" + pedidoId + " usando " + machineName);
                            Thread.sleep(2000);

                            out.println("employeeDeliverOrder " + name + " " + pedidoId);
                            in.readLine();
                        }
                    } else {
                        state = EmployeeState.ESPERANDO;
                        System.out.println(name + " no ve clientes ni pedidos, esperando...");
                        Thread.sleep(1000);
                    }
                }

            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
                running = false;
            }
        }
    }


}
