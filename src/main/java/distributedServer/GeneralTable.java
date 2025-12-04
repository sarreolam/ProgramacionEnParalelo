import Agents.ClientInServer;
import Agents.DriveThru;
import Agents.Employee;
import Agents.Machine;
import Buffers.Counter;
import Buffers.Kitchen;
import Buffers.Store;
import Buffers.Window;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class GeneralTable extends JFrame implements Runnable {
    private final DefaultTableModel model;
    private final ClientInServer[] clients = new ClientInServer[0];
    private final Employee[] employees;
    private final Machine[] machines;
    private DriveThru[] driveThrus;
    private Counter counter;
    private Kitchen kitchen;
    private Window window;


    public GeneralTable(Employee[] employees, Machine[] machines, DriveThru[] driveThrus,
                        Counter counter, Kitchen kitchen, Window window) {
        this.employees = employees;
        this.machines = machines;
        this.driveThrus = driveThrus;
        this.counter = counter;
        this.kitchen = kitchen;
        this.window = window;


        model = new DefaultTableModel(new String[]{"Tipo", "Nombre / Recurso", "Cantidad"}, 0);
    }

    public GeneralTable(Employee[] employees, Machine[] machines) {
        this.employees = employees;
        this.machines = machines;
        model = new DefaultTableModel(new String[]{"Tipo", "Nombre / Recurso", "Cantidad"}, 0);
    }

    @Override
    public void run() {
        SwingUtilities.invokeLater(this::createGUI);
        while (true) {
            try {
                SwingUtilities.invokeLater(this::refresh);
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
        model.addRow(new Object[]{"Agente", "Machines", machines.length});
        model.addRow(new Object[]{"Agente", "DriveThru", driveThrus.length});

        // --- Buffers ---
        int clientesEnCounter = counter.getClientesEnFila();
        int pedidosEnKitchen = kitchen.getPedidosPendientes();

        model.addRow(new Object[]{"Búfer", "Counter clientes en fila (M)", clientesEnCounter});
        model.addRow(new Object[]{"Búfer", "Kitcken empleados en cocina (E)", pedidosEnKitchen});


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

        model.addRow(new Object[]{"Zona crítica", "Ventanilla Ocupada", window.ventanillaOcupada()});
    }

    private void refreshCall() {
        try (Socket socket = new Socket("localhost", 5000)) {
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
            model.addRow(new Object[]{"Agente", "Machines", parts[0]});
            model.addRow(new Object[]{"Agente", "DriveThru", driveThrus != null ? driveThrus.length : 0});

            model.addRow(new Object[]{"Búfer", "Counter (clientes en fila)", parts[1]});
            model.addRow(new Object[]{"Búfer", "Kitchen (pedidos pendientes)", parts[2]});

            model.addRow(new Object[]{"Zona crítica", "Máquinas en uso", parts[4]});
            model.addRow(new Object[]{"Zona crítica", "Máquinas averiadas", parts[5]});

        } catch (IOException e) {
            System.err.println("Error: " + e.getMessage());
        }


    }

}
