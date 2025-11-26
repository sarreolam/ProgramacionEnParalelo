package Agents;

import Buffers.Counter;
import Buffers.Kitchen;
import Buffers.Store;

import java.io.*;
import java.net.Socket;

public class ClientHandler implements Runnable {
    private final Socket socket;
    private final Counter counter;
    private final Kitchen kitchen;
    private final Store store;

    public ClientHandler(Socket socket, Counter counter, Kitchen kitchen, Store store) {
        this.socket = socket;
        this.counter = counter;
        this.kitchen = kitchen;
        this.store = store;
    }

    @Override
    public void run() {
        System.out.println("!!! NEW ClientHandler.run() STARTED for socket: " + socket);
        System.out.flush();
        try {
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            String line;
            while ((line = in.readLine()) != null) {
                System.out.println("=== ClientHandler received: '" + line + "'");

                String[] parts = line.trim().split(" ");
                System.out.println("=== Parts[0] (task): '" + parts[0] + "'");

                String task = parts[0];

                StringBuilder nameBuilder = new StringBuilder();
                int startIndex = 1;
                int endIndex = parts.length;

                if (task.equals("employeeAddOrder") || task.equals("employeeDeliverOrder")) {
                    endIndex = parts.length - 1;
                }

                for (int i = startIndex; i < endIndex; i++) {
                    if (i > startIndex) nameBuilder.append(" ");
                    nameBuilder.append(parts[i]);
                }
                String name = nameBuilder.toString();
                System.out.println("=== Name: '" + name + "'");
                System.out.println("=== Entering switch for task: '" + task + "'");

                switch (task) {
                    case "employeeCheckClients": {
                        System.out.println("=== Case: employeeCheckClients");
                        boolean waiting = counter.hayClientesEsperando();
                        out.println(waiting);
                        break;
                    }

                    case "employeeArrive": {
                        System.out.println("=== Case: employeeArrive");
                        int pedidoId = counter.empleadoLlega(name);
                        out.println(pedidoId);
                        break;
                    }

                    case "employeeAddOrder": {
                        System.out.println("=== Case: employeeAddOrder");
                        if (parts.length >= 3) {
                            int orderId = Integer.parseInt(parts[parts.length - 1]);
                            kitchen.agregarPedido(orderId);
                            out.println("OK");
                        } else {
                            out.println("ERROR: Missing orderId");
                        }
                        break;
                    }

                    case "employeeCheckOrders": {
                        System.out.println("=== Case: employeeCheckOrders");
                        boolean hasOrders = kitchen.hayPedidosEnEspera();
                        out.println(hasOrders);
                        break;
                    }

                    case "employeeTakeOrder": {
                        System.out.println("=== Case: employeeTakeOrder");
                        Integer order = kitchen.tomarPedido();
                        out.println(order != null ? order : -1);
                        break;
                    }

                    case "employeeGetMachine": {
                        System.out.println("=== Case: employeeGetMachine");
                        Machine machine = kitchen.obtenerMaquinaLibre();

                        if (machine != null) {
                            System.out.println("✓ Máquina obtenida: " + machine.getNombre());
                            //lamar a preparar par que la maquina libere el lock
                            machine.preparar(name);
                            System.out.println("Máquina " + machine.getNombre() + " terminó de preparar");
                            out.println(machine.getNombre());
                        } else {
                            System.out.println("No hay máquinas disponibles");
                            out.println("none");
                        }
                        break;
                    }

                    case "employeeDeliverOrder": {
                        System.out.println("=== Case: employeeDeliverOrder");
                        if (parts.length >= 3) {
                            int id = Integer.parseInt(parts[parts.length - 1]);
                            counter.entregarPedido(id, name);
                            System.out.println("Pedido #" + id + " entregado por " + name);
                            out.println("OK");
                        } else {
                            out.println("ERROR: Missing orderId");
                        }
                        break;
                    }

                    case "clientArrive": {
                        System.out.println("=== Case: clientArrive");
                        counter.clienteLlega(name);
                        out.println("OK");
                        break;
                    }

                    case "tableGetStatus": {
                        System.out.println("=== Case: tableGetStatus");
                        String status = String.format("MachinesLength=%d,CounterClients=%d,KitchenOrders=%d,ClientsInStore=%d,UsedMachines=%d,UnusedMachines=%d",
                                kitchen.getMachinesLength(), counter.getClientesEnFila(), kitchen.getPedidosPendientes(),
                                store.getClientesDentro(), kitchen.getUsedMachines(), kitchen.getUnusedMachines());
                        System.out.println("Sending status: " + status);
                        out.println(status);
                        break;
                    }

                    case "QUIT": {
                        out.println("Goodbye " + name);
                        System.out.println(name + " disconnected.");
                        return;
                    }

                    default:
                        System.out.println("=== Case: default (unknown command)");
                        out.println("ERROR: Unknown command " + task);
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                socket.close();
            } catch (IOException ignored) {}
        }
    }
}