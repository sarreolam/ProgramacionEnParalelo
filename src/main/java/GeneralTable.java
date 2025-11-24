import Agents.Client;
import Agents.Employee;
import Agents.Machine;
import Agents.DriveThru;
import Buffers.Counter;
import Buffers.Kitchen;
import Buffers.Store;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.*;
import java.net.Socket;

public class GeneralTable extends JFrame implements Runnable {
    private DefaultTableModel model;
    private Client[] clients;
    private Employee[] employees;
    private Machine[] machines;
    private DriveThru[] driveThrus;
    private Counter counter;
    private Kitchen kitchen;
    private Store store;

    public GeneralTable(Client[] clients, Employee[] employees, Machine[] machines, DriveThru[] driveThrus,
                        Counter counter, Kitchen kitchen, Store store) {
        this.clients = clients;
        this.employees = employees;
        this.machines = machines;
        this.driveThrus = driveThrus;
        this.counter = counter;
        this.kitchen = kitchen;
        this.store = store;

        model = new DefaultTableModel(new String[]{"Tipo", "Nombre / Recurso", "Cantidad"}, 0);
    }

    public GeneralTable(Client[] clients, Employee[] employees, Machine[] machines){
        this.clients = clients;
        this.employees = employees;
        this.machines = machines;
        model = new DefaultTableModel(new String[]{"Tipo", "Nombre / Recurso", "Cantidad"}, 0);
    }

    @Override
    public void run() {
        SwingUtilities.invokeLater(this::createGUI);
        while (true) {
            try {
                SwingUtilities.invokeLater(this::refreshCall);
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void createGUI() {
        JFrame frame = new JFrame("General Overview");
        JTable table = new JTable(model);
        table.setFillsViewportHeight(true);
        table.setRowHeight(25);
        frame.add(new JScrollPane(table), BorderLayout.CENTER);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 400);
        frame.setVisible(true);
    }

    private void refresh() {
        model.setRowCount(0);

        // --- Agentes ---
        model.addRow(new Object[]{"Agente", "Employees", employees.length});
        model.addRow(new Object[]{"Agente", "Clients", clients.length});
        model.addRow(new Object[]{"Agente", "Machines", machines.length});
        model.addRow(new Object[]{"Agente", "DriveThru", driveThrus != null ? driveThrus.length : 0});

        // --- Buffers ---
        int clientesEnCounter = counter.getClientesEnFila();
        int pedidosEnKitchen = kitchen.getPedidosPendientes();
        int clientesEnStore = store.getClientesDentro();

        model.addRow(new Object[]{"Búfer", "Counter (clientes en fila)", clientesEnCounter});
        model.addRow(new Object[]{"Búfer", "Kitchen (pedidos pendientes)", pedidosEnKitchen});
        model.addRow(new Object[]{"Búfer", "Store (clientes adentro)", clientesEnStore});

        // --- Zonas críticas (Máquinas) ---
        int maquinasUsadas = 0, maquinasAveriadas = 0;
        for (Machine m : machines) {
            switch (m.getEstado()) {
                case EN_USO -> maquinasUsadas++;
                case AVERIADA, REPARANDO -> maquinasAveriadas++;
            }
        }

        model.addRow(new Object[]{"Zona crítica", "Máquinas en uso", maquinasUsadas});
        model.addRow(new Object[]{"Zona crítica", "Máquinas averiadas", maquinasAveriadas});
    }

    private void refreshCall(){
        try(Socket socket = new Socket("localhost", 5000)) {
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            out.println("tableGetStatus Generaltable");
            String response = in.readLine();

            System.out.println("DEBUG: Raw response: '" + response + "'");
            System.out.println("DEBUG: Response length: " + (response != null ? response.length() : "null"));

            if (response == null) {
                System.err.println("Response is null!");
                return;
            }

            String[] parts = response.trim().split(",");
            System.out.println("DEBUG: Parts length: " + parts.length);
            for (int i = 0; i < parts.length; i++) {
                System.out.println("DEBUG: Part " + i + ": '" + parts[i] + "'");
            }

            if (parts.length < 6) {
                System.err.println("Expected 6 parts, got " + parts.length);
                return;
            }
            for (int i = 0; i < parts.length; i++) {
                parts[i] = parts[i].split("=")[1].trim();
            }

            model.setRowCount(0);
            model.addRow(new Object[]{"Agente", "Employees", employees.length});
            model.addRow(new Object[]{"Agente", "Clientes", clients.length});
            model.addRow(new Object[]{"Agente", "Machines", parts[0]});
            model.addRow(new Object[]{"Agente", "DriveThru", driveThrus != null ? driveThrus.length : 0});

            model.addRow(new Object[]{"Búfer", "Counter (clientes en fila)", parts[1]});
            model.addRow(new Object[]{"Búfer", "Kitchen (pedidos pendientes)", parts[2]});
            model.addRow(new Object[]{"Búfer", "Store (clientes adentro)", parts[3]});

            model.addRow(new Object[]{"Zona crítica", "Máquinas en uso", parts[4]});
            model.addRow(new Object[]{"Zona crítica", "Máquinas averiadas", parts[5]});

        } catch (IOException e) {
            System.err.println("Error: " + e.getMessage());
        }


    }

}
