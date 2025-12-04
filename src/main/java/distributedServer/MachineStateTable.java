import Agents.Machine;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class MachineStateTable extends JFrame implements Runnable {

    private final DefaultTableModel model;
    private final Machine[] machines;

    public MachineStateTable(Machine[] machines) {
        this.machines = machines;

        model = new DefaultTableModel(
                new String[]{"Estado de la Máquina", "Cantidad"}, 0
        );
    }

    @Override
    public void run() {
        SwingUtilities.invokeLater(this::createGUI);

        while (true) {
            try {
                SwingUtilities.invokeLater(this::refresh);
                Thread.sleep(300);  // Actualiza cada 0.3 sec
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void createGUI() {
        JFrame frame = new JFrame("Estados de Máquinas");
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

        int operativa = 0;
        int enUso = 0;
        int averiada = 0;
        int reparando = 0;

        for (Machine m : machines) {
            if (m == null) continue;

            Machine.EstadoMaquina state = m.getEstado();

            switch (state) {
                case OPERATIVA -> operativa++;
                case EN_USO -> enUso++;
                case AVERIADA -> averiada++;
                case REPARANDO -> reparando++;
            }
        }

        model.addRow(new Object[]{"OPERATIVA", operativa});
        model.addRow(new Object[]{"EN_USO", enUso});
        model.addRow(new Object[]{"AVERIADA", averiada});
        model.addRow(new Object[]{"REPARANDO", reparando});
    }
}
