import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.concurrent.Semaphore;

import Agents.Employee;
import Agents.Machine;
import Agents.Client;
import Agents.DriveThru;
import Buffers.Counter;
import Buffers.Kitchen;
import Buffers.Store;

public class VentanaPrincipal extends JFrame{
    private JTextField numClientes = new JTextField("5");
    private JTextField numDriveThrough = new JTextField("0");
    private JTextField numEmpleados = new JTextField("3");
    private JTextField numMaquinas = new JTextField("5");
    private JButton inicializarBtn = new JButton("Inicializar Simulación");
    private JCheckBox mostrarAnimacionCheck = new JCheckBox("Mostrar Animación Visual", true);


    public VentanaPrincipal() {
        setTitle("Simulador McDonalds");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(400, 400);
        setLayout(new BorderLayout(10, 10));

        JLabel tituloLabel = new JLabel("Simulador McDonalds", SwingConstants.CENTER);
        tituloLabel.setFont(new Font("Arial", Font.BOLD, 18));
        add(tituloLabel, BorderLayout.NORTH);

        JPanel panelCentro = new JPanel(new GridLayout(4, 2, 5, 5));
        panelCentro.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10));

        panelCentro.add(new JLabel("Número de Clientes:"));
        panelCentro.add(numClientes);

        panelCentro.add(new JLabel("Número de Drive-Through:"));
        panelCentro.add(numDriveThrough);

        panelCentro.add(new JLabel("Número de Empleados:"));
        panelCentro.add(numEmpleados);

        panelCentro.add(new JLabel("Número de Máquinas:"));
        panelCentro.add(numMaquinas);

        add(panelCentro, BorderLayout.CENTER);

        JPanel panelBoton = new JPanel();
        panelBoton.add(inicializarBtn);
        add(panelBoton, BorderLayout.SOUTH);

        inicializarBtn.addActionListener(e -> {
            try {

                InicializarAgentes();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this,
                        "Ingresa solo números válidos",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        });
    }

    public void InicializarAgentes(){

        int numClients = Integer.parseInt(numClientes.getText());
        int drive = Integer.parseInt(numDriveThrough.getText());
        int numEmployees = Integer.parseInt(numEmpleados.getText());
        int numMachines = Integer.parseInt(numMaquinas.getText());
        boolean mostrarAnimacion = mostrarAnimacionCheck.isSelected();

        if (numClients <= 0 || numEmployees <= 0 || numMachines <= 0) {
            JOptionPane.showMessageDialog(this,
                    "Los valores deben ser mayores a 0",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        inicializarBtn.setEnabled(false);
        inicializarBtn.setText("Simulación en curso...");

        Employee[] employeesArray = new Employee[numEmployees];
        Client[] clientsArray = new Client[numClients];
        ArrayList<Employee> employeesList = new ArrayList<>();

        Counter counter = new Counter();

        Machine[] machines = new Machine[numMachines];
        for (int i = 0; i < numMachines; i++) {
            Machine m = new Machine("Machine" + i);
            machines[i] = m;
            machines[i].start();
        }

        Kitchen kitchen = new Kitchen(machines);
        Store store = new Store(10);

        for (int i = 0; i < numEmployees; i++) {
            Employee employee = new Employee("Employee" + (i + 1));
            employeesArray[i] = employee;
            employeesList.add(employee);
            employee.start();
        }

        for (int i = 0; i < numClients; i++) {
            Client client = new Client("Client" + (i + 1));
            clientsArray[i] = client;
            client.start();

            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        DriveThru[] driveThrus = new DriveThru[drive];

        if (mostrarAnimacion) {
            SwingUtilities.invokeLater(() -> {
                JFrame animationFrame = new JFrame("Simulación Visual - McDonald's");
                JTextArea textArea = new JTextArea();
                Semaphore semaphore = new Semaphore(1);

                StoreVisual storeVisual = new StoreVisual(textArea, semaphore, employeesList);
                storeVisual.setSize(800, 600);

                animationFrame.add(storeVisual);
                animationFrame.setSize(800, 600);
                animationFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                animationFrame.setLocationRelativeTo(null);
                animationFrame.setVisible(true);

            });
        }

        GeneralTable generalTable = new GeneralTable(clientsArray, employeesArray, machines, driveThrus, counter, kitchen, store);
        Thread generalThread = new Thread(generalTable);
        generalThread.start();

        StateTable stateTable = new StateTable(clientsArray, employeesArray, machines);
        Thread stateThread = new Thread(stateTable);
        stateThread.start();

        EmployeeStateTable employeeStateTable = new EmployeeStateTable(employeesArray);
        Thread employeeStateThread = new Thread(employeeStateTable);
        employeeStateThread.start();
    }
}