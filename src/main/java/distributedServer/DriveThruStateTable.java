import Agents.DriveThru;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class DriveThruStateTable extends JFrame implements Runnable {

    private final DefaultTableModel model;
    private final DriveThru[] cars;

    public DriveThruStateTable(DriveThru[] cars) {
        this.cars = cars;

        model = new DefaultTableModel(
                new String[]{"Estado del Carro", "Cantidad"}, 0
        );
    }

    @Override
    public void run() {
        SwingUtilities.invokeLater(this::createGUI);

        while (true) {
            try {
                SwingUtilities.invokeLater(this::refresh);
                Thread.sleep(300);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void createGUI() {
        JFrame frame = new JFrame("Estados de Drive-Thru");
        JTable table = new JTable(model);
        table.setFillsViewportHeight(true);
        table.setRowHeight(25);

        JScrollPane scrollPane = new JScrollPane(table);
        frame.add(scrollPane, BorderLayout.CENTER);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(420, 320);
        frame.setVisible(true);
    }

    private void refresh() {
        model.setRowCount(0);

        int llegando = 0;
        int esperandoVentanilla = 0;
        int enVentanilla = 0;
        int esperandoOrden = 0;
        int recibiendoOrden = 0;
        int saliendo = 0;
        int fuera = 0;

        for (DriveThru car : cars) {
            if (car == null) continue;

            DriveThru.DriveThruState state = car.getDTState();

            switch (state) {
                case LLEGANDO -> llegando++;
                case ESPERANDO_VENTANILLA -> esperandoVentanilla++;
                case EN_VENTANILLA -> enVentanilla++;
                case ESPERANDO_ORDEN -> esperandoOrden++;
                case SALIENDO -> saliendo++;
                case FUERA -> fuera++;
            }
        }

        model.addRow(new Object[]{"LLEGANDO", llegando});
        model.addRow(new Object[]{"ESPERANDO_VENTANILLA", esperandoVentanilla});
        model.addRow(new Object[]{"EN_VENTANILLA", enVentanilla});
        model.addRow(new Object[]{"ESPERANDO_ORDEN", esperandoOrden});
        model.addRow(new Object[]{"SALIENDO", saliendo});
        model.addRow(new Object[]{"FUERA", fuera});
    }
}
