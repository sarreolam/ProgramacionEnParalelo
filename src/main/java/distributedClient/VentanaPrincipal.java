import Agents.Client;
import Buffers.Chair;
import Buffers.CounterClient;
import Buffers.Store;
import Utils.ClientStateTable;
import Utils.GeneralTable;
import Utils.StateTable;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.concurrent.Semaphore;

public class VentanaPrincipal extends JFrame {
    private final JTextField numClientes = new JTextField("5");
    private final JTextField numSillas = new JTextField("5");
    private final JButton inicializarBtn = new JButton("Inicializar Simulación");
    private final JCheckBox mostrarAnimacionCheck = new JCheckBox("Mostrar Animación Visual", true);
    ArrayList<Client> clientList = new ArrayList<>();


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
        panelCentro.add(new JLabel("Número de sillas:"));
        panelCentro.add(numSillas);


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

        int numClients = Integer.parseInt(numClientes.getText());
        int numSillas1 = Integer.parseInt(numSillas.getText());
        boolean mostrarAnimacion = mostrarAnimacionCheck.isSelected();

        CounterClient counterClient = new CounterClient(5);

        Chair[] chairs = new Chair[numSillas1];
        for (int i = 0; i < numSillas1; i++) {
            chairs[i] = new Chair("Silla" + (i + 1), i);
        }


        if (numClients <= 0 || numSillas1 < 0) {
            JOptionPane.showMessageDialog(this,
                    "Los valores deben ser mayores a 0",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        inicializarBtn.setEnabled(false);
        inicializarBtn.setText("Simulación en curso...");

        for (int i = 0; i < numSillas1; i++){
            chairs[i] = new Chair("Silla"+ (i+1), i);
        }

        Store store = new Store(10, chairs);
        Client[] clientsArray = new Client[numClients];

        for (int i = 0; i < numClients; i++) {
            Agents.Client client = new Client("Client" + (i + 1), counterClient, store, 5, 10.5);
            clientsArray[i] = client;
            clientList.add(client);
            client.start();

            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        if (mostrarAnimacion) {
            SwingUtilities.invokeLater(() -> {
                JFrame animationFrame = new JFrame("Simulación Visual - McDonald's");
                JTextArea textArea = new JTextArea();
                Semaphore semaphore = new Semaphore(1);

                ClientsVisual visual = new ClientsVisual(textArea, semaphore, clientList, 5, numSillas1);
                visual.setSize(800, 600);

                animationFrame.add(visual);
                animationFrame.setSize(800, 600);
                animationFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                animationFrame.setLocationRelativeTo(null);
                animationFrame.setVisible(true);
            });
        }

        GeneralTable generalTable = new GeneralTable(clientsArray);
        Thread generalThread = new Thread(generalTable);
        generalThread.start();

        ClientStateTable clientStateTable = new ClientStateTable(clientsArray);
        Thread clientTableThread = new Thread(clientStateTable);
        clientTableThread.start();

        StateTable stateTable = new StateTable(clientsArray);
        Thread stateThread = new Thread(stateTable);
        stateThread.start();


    }
}