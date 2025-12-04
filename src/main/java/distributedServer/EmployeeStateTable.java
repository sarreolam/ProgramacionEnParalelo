import Agents.Employee;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class EmployeeStateTable extends JFrame implements Runnable {

    private final DefaultTableModel model;
    private final Employee[] employees;

    public EmployeeStateTable(Employee[] employees) {
        this.employees = employees;

        model = new DefaultTableModel(
                new String[]{"Estado del Empleado", "Cantidad"}, 0
        );
    }

    @Override
    public void run() {
        SwingUtilities.invokeLater(this::createGUI);
        while (true) {
            try {
                SwingUtilities.invokeLater(this::refresh);
                Thread.sleep(300); // actualiza cada 0.3s
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void createGUI() {
        JFrame frame = new JFrame("Estados de Empleados");
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

        int naciendo = 0;
        int caminandoMostrador = 0;
        int caminandoMaquina = 0;
        int esperando = 0;
        int atendiendo = 0;
        int preparando = 0;
        int saliendo = 0;
        int fuera = 0;

        for (Employee e : employees) {
            if (e == null) continue;
            Employee.EmployeeState state = e.getEmployeeState(); // <- añadiremos este método en Employee
            switch (state) {
                case ENTRA_A_LA_TIENDA -> naciendo++;
                case CAMINANDO_AL_MOSTRADOR -> caminandoMostrador++;
                case CAMINANDO_A_MAQUINA -> caminandoMaquina++;
                case ESPERANDO -> esperando++;
                case ATENDIENDO -> atendiendo++;
                case PREPARANDO -> preparando++;
                case SALIENDO -> saliendo++;
                case FUERA -> fuera++;
            }
        }

        model.addRow(new Object[]{"NACIENDO", naciendo});
        model.addRow(new Object[]{"CAMINANDO_AL_MOSTRADOR", caminandoMostrador});
        model.addRow(new Object[]{"ATENDIENDO", atendiendo});
        model.addRow(new Object[]{"CAMINANDO_A_MAQUINA", caminandoMaquina});
        model.addRow(new Object[]{"PREPARANDO", preparando});
        model.addRow(new Object[]{"ESPERANDO", esperando});
        model.addRow(new Object[]{"SALIENDO", saliendo});
        model.addRow(new Object[]{"FUERA", fuera});
    }
}
