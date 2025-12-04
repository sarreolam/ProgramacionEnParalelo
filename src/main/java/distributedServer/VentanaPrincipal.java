import Agents.DriveThru;
import Agents.Employee;
import Agents.Machine;
import Buffers.Counter;
import Buffers.CounterServer;
import Buffers.Kitchen;
import Buffers.Window;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.concurrent.Semaphore;

public class VentanaPrincipal extends JFrame {
    private final JTextField numDriveThrough = new JTextField("5");
    private final JTextField numEmpleados = new JTextField("3");
    private final JTextField numMaquinas = new JTextField("5");
    private final JButton inicializarBtn = new JButton("Inicializar Simulación");
    private final JCheckBox mostrarAnimacionCheck = new JCheckBox("Mostrar Animación Visual", true);
    private final JTextField tmpAtendiendo = new JTextField("1");
    private final JTextField tmpPreparando = new JTextField("2");
    private final JTextField tmpReparando = new JTextField("2");

    private final JTextField velMovimiento= new JTextField("3");

    SpinnerNumberModel machineCount = new SpinnerNumberModel(1, 1, 5, 1);
    JSpinner machineCountSpin = new JSpinner(machineCount);

    public VentanaPrincipal() {
        setTitle("Simulador McDonalds");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(400, 400);
        setLayout(new BorderLayout(10, 10));

        JLabel tituloLabel = new JLabel("Simulador McDonalds", SwingConstants.CENTER);
        tituloLabel.setFont(new Font("Arial", Font.BOLD, 18));
        add(tituloLabel, BorderLayout.NORTH);

        JPanel panelCentro = new JPanel(new GridLayout(8, 3, 5, 5));
        panelCentro.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10));

        panelCentro.add(new JLabel("Número de Drive-Through:"));
        panelCentro.add(numDriveThrough);

        panelCentro.add(new JLabel("Número de Empleados:"));
        panelCentro.add(numEmpleados);

        panelCentro.add(new JLabel("Número de Máquinas:"));
        panelCentro.add(numMaquinas);
        panelCentro.add(new JLabel("Tiempo Atendiendo (s):"));
        panelCentro.add(tmpAtendiendo);
        panelCentro.add(new JLabel("Tiempo de Preparación (s):"));
        panelCentro.add(tmpPreparando);
        panelCentro.add(new JLabel("Tiempo de Reparacion (s):"));
        panelCentro.add(tmpReparando);
        panelCentro.add(new JLabel("Velocidad del empleado:"));
        panelCentro.add(velMovimiento);
        panelCentro.add(new JLabel("Capacidad Máxima del Counter:"));
        panelCentro.add(machineCountSpin);

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

    public void InicializarAgentes() {

        int drive = Integer.parseInt(numDriveThrough.getText());
        int numEmployees = Integer.parseInt(numEmpleados.getText());
        int numMachines = Integer.parseInt(numMaquinas.getText());
        boolean mostrarAnimacion = mostrarAnimacionCheck.isSelected();
        int tiempoAtender = Integer.parseInt(tmpAtendiendo.getText());
        int tiempoPreparar = Integer.parseInt(tmpPreparando.getText());
        int tiempoReparar = Integer.parseInt(tmpReparando.getText());

        int velocidadMovimiento = Integer.parseInt(velMovimiento.getText());
        int capacidadMaximaCounter = (int)machineCount.getValue();

        if (numEmployees <= 0 || numMachines <= 0 || drive <= 0) {
            JOptionPane.showMessageDialog(this,
                    "Los valores deben ser mayores a 0",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        inicializarBtn.setEnabled(false);
        inicializarBtn.setText("Simulación en curso...");

        Employee[] employeesArray = new Employee[numEmployees];
        ArrayList<Employee> employeesList = new ArrayList<>();

        Counter counter = new Counter(capacidadMaximaCounter);
        CounterServer counterServer = new CounterServer(counter);
        counterServer.start();

        Machine[] machines = new Machine[numMachines];
        for (int i = 0; i < numMachines; i++) {
            Machine m = new Machine("Machine" + i, tiempoPreparar, tiempoReparar);
            machines[i] = m;
            machines[i].start();
        }

        Window window = new Window();
        Kitchen kitchen = new Kitchen(machines);

        for (int i = 0; i < numEmployees; i++) {
            Employee employee = new Employee("Employee" + (i + 1), counter,window, kitchen, tiempoAtender, velocidadMovimiento);
            employeesArray[i] = employee;
            employeesList.add(employee);
            employee.start();
        }


        DriveThru[] driveThrusArray = new DriveThru[drive];
        ArrayList<DriveThru> driveThrusList = new ArrayList<>();

        for (int i = 0; i < drive; i++) {
            DriveThru driveThru = new DriveThru("DriveThru" + (i + 1), window);
            driveThrusArray[i] = driveThru;
            driveThrusList.add(driveThru);
            driveThru.start();
        }

        if (mostrarAnimacion) {
            SwingUtilities.invokeLater(() -> {
                JFrame animationFrame = new JFrame("Simulación Visual - McDonald's");
                JTextArea textArea = new JTextArea();
                Semaphore semaphore = new Semaphore(1);

                KitchenVisual storeVisual = new KitchenVisual(textArea, semaphore, employeesList, driveThrusList, capacidadMaximaCounter, numMachines);
                storeVisual.setSize(800, 600);

                animationFrame.add(storeVisual);
                animationFrame.setSize(800, 600);
                animationFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                animationFrame.setLocationRelativeTo(null);
                animationFrame.setVisible(true);

            });
        }

        GeneralTable generalTable = new GeneralTable(employeesArray, machines, driveThrusArray, counter, kitchen, window);
        Thread generalThread = new Thread(generalTable);
        generalThread.start();

        StateTable stateTable = new StateTable(employeesArray, machines, driveThrusArray);
        Thread stateThread = new Thread(stateTable);
        stateThread.start();

        EmployeeStateTable employeeStateTable = new EmployeeStateTable(employeesArray);
        Thread employeeStateThread = new Thread(employeeStateTable);
        employeeStateThread.start();

        DriveThruStateTable driveThruStateTable = new DriveThruStateTable(driveThrusArray);
        Thread driveThruStateThread = new Thread(driveThruStateTable);
        driveThruStateThread.start();

        MachineStateTable machineStateTable = new MachineStateTable(machines);
        Thread machineStateThread = new Thread(machineStateTable);
        machineStateThread.start();
    }
}