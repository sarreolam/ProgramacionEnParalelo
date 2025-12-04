package Utils;

import Agents.Client;
import Buffers.Store;

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
    private final Client[] clients;
    private final Store store;
    private final int chairs;
//    private Store store;

    public GeneralTable(Client[] clients, Store store) {
        this.clients = clients;
        this.store = store;
        this.chairs = store.getChairsNum();

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

        int clientesPidiendo = 0;
        int clientesSentados = 0;

        // --- CONTAR ESTADOS DE CLIENTES ---
        for (Client c : clients) {
            if (c == null) continue;

            switch (c.getClientState()) {
                case PIDIENDO -> clientesPidiendo++;
                case SENTADO -> clientesSentados++;

            }
        }

        model.addRow(new Object[]{"Agentes", "Total clientes", clients.length});

        model.addRow(new Object[]{"Agentes", "Clientes pidiendo", clientesPidiendo});
        model.addRow(new Object[]{"Agentes", "Clientes sentados", clientesSentados});


        model.addRow(new Object[]{"Búfer", "Clientes dentro de Store (C)", store.getClientesDentro()});

        model.addRow(new Object[]{"Zona crítica", "Sillas Ocupadas (B)", clientesSentados});
        model.addRow(new Object[]{"Zona crítica", "Sillas Libres (B)", chairs - clientesSentados});
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
            model.addRow(new Object[]{"Agente", "Clientes", clients.length});

            model.addRow(new Object[]{"Búfer", "Counter (clientes en fila)", parts[1]});
            model.addRow(new Object[]{"Búfer", "Store (clientes adentro)", parts[3]});

            model.addRow(new Object[]{"Zona crítica", "Máquinas en uso", parts[4]});
            model.addRow(new Object[]{"Zona crítica", "Máquinas averiadas", parts[5]});

        } catch (IOException e) {
            System.err.println("Error: " + e.getMessage());
        }


    }

}
