import javax.swing.*;
import java.awt.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import Agents.Employee;
import Agents.Machine;
import Agents.Client;
import Agents.DriveThru;
import Buffers.Counter;
import Buffers.Kitchen;
import Buffers.Store;

public class VentanaPrincipal extends JFrame{
    private JTextField numClientes = new JTextField();
    private JTextField numDriveThrough = new JTextField();
    private JTextField numEmpleados = new JTextField();
    private JTextField numMaquinas = new JTextField();
    private JButton inicializarBtn = new JButton("Inicializar");


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

        //inicializar ventanas de cliente empleado
        Employee[] employees = new Employee[numEmployees];
        Client[] clients = new Client[numClients];
        Counter counter = new Counter();

        Machine[] machines = new Machine[numMachines];
        for (int i = 0; i < numMachines; i++) {
            Machine m = new Machine("Machine"+i);
            machines[i] = m;
            machines[i].start();
        }

        Kitchen kitchen = new Kitchen(machines);

        for (int i = 0; i < numEmployees; i++) {
            Employee employee = new Employee("Employee" + (i+1));
            employees[i] = employee;
            employees[i].start();
        }

        for (int i = 0; i < numClients; i++) {
            Client client = new Client("Client" + (i+1));
            clients[i] = client;
            clients[i].start();
        }

        DriveThru[] driveThrus = new DriveThru[0]; // aún no implementado
        GeneralTable generalTable = new GeneralTable(clients, employees, machines, driveThrus, counter, kitchen, new Store(10));
        Thread generalThread = new Thread(generalTable);
        generalThread.start();

        StateTable stateTable = new StateTable(clients, employees, machines);
        Thread stateThread = new Thread(stateTable);
        stateThread.start();

        EmployeeStateTable employeeStateTable = new EmployeeStateTable(employees);
        Thread employeeStateThread = new Thread(employeeStateTable);
        employeeStateThread.start();


    }
}