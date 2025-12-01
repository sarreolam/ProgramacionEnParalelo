package Utils;
import Agents.Client;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class ClientStateTable extends JFrame implements Runnable {

    private final DefaultTableModel model;
    private final Client[] clients;

    public ClientStateTable(Client[] clients) {
        this.clients = clients;

        model = new DefaultTableModel(
                new String[]{"Estado del Cliente", "Cantidad"}, 0
        );
    }

    @Override
    public void run() {
        SwingUtilities.invokeLater(this::createGUI);

        while (true) {
            try {
                SwingUtilities.invokeLater(this::refresh);
                Thread.sleep(300); // actualiza cada 0.3 segundos
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void createGUI() {
        JFrame frame = new JFrame("Estados de Clientes");
        JTable table = new JTable(model);
        table.setFillsViewportHeight(true);
        table.setRowHeight(25);

        JScrollPane scrollPane = new JScrollPane(table);
        frame.add(scrollPane, BorderLayout.CENTER);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 300);
        frame.setVisible(true);
    }

    private void refresh() {
        model.setRowCount(0);

        int caminandoTienda = 0;
        int caminandoMostrador = 0;
        int caminandoAsientos = 0;
        int caminando = 0;
        int esperando = 0;
        int pidiendo = 0;
        int saliendo = 0;
        int fuera = 0;
        int sentado = 0;

        for (Client c : clients) {
            if (c == null) continue;

            Client.ClientState state = c.getClientState();

            switch (state) {
                case CAMINANDO_A_LA_TIENDA -> caminandoTienda++;
                case CAMINANDO_AL_MOSTRADOR -> caminandoMostrador++;
                case CAMINANDO_A_ASIENTOS -> caminandoAsientos++;
                case CAMINANDO -> caminando++;
                case ESPERANDO -> esperando++;
                case PIDIENDO -> pidiendo++;
                case SALIENDO -> saliendo++;
                case FUERA -> fuera++;
                case SENTADO -> sentado++;
            }
        }

        model.addRow(new Object[]{"CAMINANDO_A_LA_TIENDA", caminandoTienda});
        model.addRow(new Object[]{"CAMINANDO_AL_MOSTRADOR", caminandoMostrador});
        model.addRow(new Object[]{"CAMINANDO_A_ASIENTOS", caminandoAsientos});
        model.addRow(new Object[]{"CAMINANDO", caminando});
        model.addRow(new Object[]{"PIDIENDO", pidiendo});
        model.addRow(new Object[]{"SENTADO", sentado});
        model.addRow(new Object[]{"ESPERANDO", esperando});
        model.addRow(new Object[]{"SALIENDO", saliendo});
        model.addRow(new Object[]{"FUERA", fuera});
    }
}
