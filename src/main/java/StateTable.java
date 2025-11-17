import Agents.Client;
import Agents.Employee;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.List;

public class StateTable extends JFrame implements Runnable {
    private DefaultTableModel model;
    private Client[] clientsList;
    private Employee[] employeeList;

    public StateTable(Client[] clientsList, Employee[] employeeList) {
        this.clientsList = clientsList;
        this.employeeList = employeeList;

        model = new DefaultTableModel(new String[]{"Thread type", "Runnable", "Timed Waiting", "Blocked", "Terminated",}, 0);

    }

    @Override
    public void run() {
        SwingUtilities.invokeLater(this::createGUI);

        while(true){
            try{
                SwingUtilities.invokeLater(this::refresh);
                Thread.sleep(100);
            } catch (InterruptedException e) {
                System.out.println("Interrupted " + e.getMessage());
            }
        }
    }

    private void createGUI() {
        JFrame frame = new JFrame("StateTable");
        JTable table = new JTable(model);
        table.setFillsViewportHeight(true);
        frame.add(new JScrollPane(table), BorderLayout.CENTER);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(450, 300);
        frame.setVisible(true);
    }

    private void refresh(){
        model.setRowCount(0);

        int[] clientStates = countThreadStates(clientsList);
        model.addRow(new Object[]{"Clients", clientStates[0], clientStates[1], clientStates[2], clientStates[3]});

        int[] employeeStates = countThreadStates(employeeList);
        model.addRow(new Object[]{"Employees", employeeStates[0], employeeStates[1], employeeStates[2], employeeStates[3]});
    }

    private int[] countThreadStates(Thread[] list){
        int numRunnable = 0, numTimedWaiting = 0, numBlocked = 0, numTerminated = 0;

        for(Thread t : list){
            if(t.isAlive()){
                switch(t.getState()){
                    case RUNNABLE -> numRunnable++;
                    case TIMED_WAITING -> numTimedWaiting++;
                    case BLOCKED -> numBlocked++;
                }
            } else{
                numTerminated++;
            }
        }
        return new int[]{numRunnable, numTimedWaiting, numBlocked, numTerminated};
    }


}
