import Agents.Client;
import Buffers.CounterClient;

import javax.swing.*;
import java.awt.*;
import java.util.concurrent.Semaphore;

public class VentanaPrincipal extends JFrame {
    private final JTextField numClientes = new JTextField("5");
    private final JButton inicializarBtn = new JButton("Inicializar Simulación");
    private final JCheckBox mostrarAnimacionCheck = new JCheckBox("Mostrar Animación Visual", true);


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
        boolean mostrarAnimacion = mostrarAnimacionCheck.isSelected();

        if (numClients <= 0) {
            JOptionPane.showMessageDialog(this,
                    "Los valores deben ser mayores a 0",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        inicializarBtn.setEnabled(false);
        inicializarBtn.setText("Simulación en curso...");

        CounterClient counterClient = new CounterClient();

//        Store store = new Store(10);
        Client[] clientsArray = new Client[numClients];

        for (int i = 0; i < numClients; i++) {
            Agents.Client client = new Client("Client" + (i + 1), counterClient);
            clientsArray[i] = client;
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


                animationFrame.setSize(800, 600);
                animationFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                animationFrame.setLocationRelativeTo(null);
                animationFrame.setVisible(true);

            });
        }

        GeneralTable generalTable = new GeneralTable(clientsArray);
        Thread generalThread = new Thread(generalTable);
        generalThread.start();

        StateTable stateTable = new StateTable(clientsArray);
        Thread stateThread = new Thread(stateTable);
        stateThread.start();


    }
}