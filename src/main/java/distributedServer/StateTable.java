import Agents.ClientInServer;
import Agents.DriveThru;
import Agents.Employee;
import Agents.Machine;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class StateTable extends JFrame implements Runnable {
    private final DefaultTableModel model;
    private ClientInServer[] clientsList = new ClientInServer[0];
    private final Employee[] employeeList;
    private final Machine[] machineList;
    private final DriveThru[] driveThruList;

    public StateTable(Employee[] employeeList, Machine[] machineList, DriveThru[] driveThruList) {
        this.employeeList = employeeList;
        this.machineList = machineList;
        this.driveThruList = driveThruList;

        model = new DefaultTableModel(
                new String[]{
                        "Thread Type",
                        "NEW",
                        "RUNNABLE",
                        "BLOCKED",
                        "WAITING",
                        "TIMED_WAITING",
                        "TERMINATED"
                },
                0
        );
    }

    @Override
    public void run() {
        SwingUtilities.invokeLater(this::createGUI);

        while (true) {
            try {
                SwingUtilities.invokeLater(this::refresh);
                Thread.sleep(300); // refresco cada 0.3 segundos
            } catch (InterruptedException e) {
                System.out.println("Interrupted " + e.getMessage());
            }
        }
    }

    private void createGUI() {
        JFrame frame = new JFrame("Thread State Table");
        JTable table = new JTable(model);
        table.setFillsViewportHeight(true);
        table.setEnabled(false);
        table.setRowHeight(25);

        JScrollPane scrollPane = new JScrollPane(table);
        frame.add(scrollPane, BorderLayout.CENTER);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 300);
        frame.setVisible(true);
    }

    private void refresh() {
        model.setRowCount(0); // Limpia las filas antes de actualizar

        // Clientes
        int[] clientStates = countThreadStates(clientsList);
        model.addRow(new Object[]{
                "Clients",
                clientStates[0], clientStates[1], clientStates[2],
                clientStates[3], clientStates[4], clientStates[5]
        });

        // Empleados
        int[] employeeStates = countThreadStates(employeeList);
        model.addRow(new Object[]{
                "Employees",
                employeeStates[0], employeeStates[1], employeeStates[2],
                employeeStates[3], employeeStates[4], employeeStates[5]
        });

        // Máquinas
        int[] machineStates = countThreadStates(machineList);
        model.addRow(new Object[]{
                "Machines",
                machineStates[0], machineStates[1], machineStates[2],
                machineStates[3], machineStates[4], machineStates[5]
        });

        // Máquinas
        int[] driveThruStates = countThreadStates(driveThruList);
        model.addRow(new Object[]{
                "Drive Thru",
                driveThruStates[0], driveThruStates[1], driveThruStates[2],
                driveThruStates[3], driveThruStates[4], driveThruStates[5]
        });
    }

    private int[] countThreadStates(Thread[] list) {
        int newT = 0, runnable = 0, blocked = 0, waiting = 0, timed = 0, terminated = 0;

        for (Thread t : list) {
            if (t == null) continue;
            Thread.State state = t.getState();
            switch (state) {
                case NEW -> newT++;
                case RUNNABLE -> runnable++;
                case BLOCKED -> blocked++;
                case WAITING -> waiting++;
                case TIMED_WAITING -> timed++;
                case TERMINATED -> terminated++;
            }
        }

        return new int[]{newT, runnable, blocked, waiting, timed, terminated};
    }
}
