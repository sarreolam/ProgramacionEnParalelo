import Agents.Client;
import Buffers.Chair;
import Buffers.CounterClient;
import Buffers.Store;
import Utils.ClientStateTable;
import Utils.GeneralTable;
import Utils.StateTable;

import javax.swing.*;
import java.awt.*;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.Semaphore;

public class VentanaPrincipal extends JFrame {
    private final JTextField numClientes = new JTextField("5");
    private final JTextField numSillas = new JTextField("5");
    private final JButton inicializarBtn = new JButton("Inicializar Simulación");
    private final JCheckBox mostrarAnimacionCheck = new JCheckBox("Mostrar Animación Visual", true);
    ArrayList<Client> clientList = new ArrayList<>();

    SpinnerNumberModel orderProb = new SpinnerNumberModel(20, 1, 100, 1);
    JSpinner orderSpin = new JSpinner(orderProb);
    SpinnerNumberModel sitProb = new SpinnerNumberModel(20, 1, 100, 1);
    JSpinner sitSpin = new JSpinner(sitProb);
    SpinnerNumberModel walkProb = new SpinnerNumberModel(20, 1, 100, 1);
    JSpinner walkSpin = new JSpinner(walkProb);
    SpinnerNumberModel exitProb = new SpinnerNumberModel(20, 1, 100, 1);
    JSpinner exitSpin = new JSpinner(exitProb);
    SpinnerNumberModel nothingProb = new SpinnerNumberModel(20, 1, 100, 1);
    JSpinner nothingSpin = new JSpinner(nothingProb);


    SpinnerNumberModel sitTime = new SpinnerNumberModel(1, 1, 10, 1);
    JSpinner sitTimeSpin = new JSpinner(sitTime);
    SpinnerNumberModel waitTime = new SpinnerNumberModel(1, 1, 10, 1);
    JSpinner waitTimeSpin = new JSpinner(waitTime);


    private int orderProbability, sitProbability, walkProbability, exitProbability, sitTimeWait, waitTimeWait, nothingProbability;

    public VentanaPrincipal() {
        setTitle("Simulador McDonalds");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(400, 400);
        setLayout(new BorderLayout(10, 10));

        JLabel tituloLabel = new JLabel("Simulador McDonalds", SwingConstants.CENTER);
        tituloLabel.setFont(new Font("Arial", Font.BOLD, 18));
        add(tituloLabel, BorderLayout.NORTH);

        JPanel panelCentro = new JPanel();
        panelCentro.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10));
        panelCentro.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        int row = 0;

        addRow(panelCentro, gbc, row++, "Número de Clientes:", numClientes);
        addRow(panelCentro, gbc, row++, "Número de sillas:", numSillas);
        addRow(panelCentro, gbc, row++, "Probabilidad de ordenar:", orderSpin);
        addRow(panelCentro, gbc, row++, "Probabilidad de sentarse:", sitSpin);
        addRow(panelCentro, gbc, row++, "Probabilidad de caminar:", walkSpin);
        addRow(panelCentro, gbc, row++, "Probabilidad de salir:", exitSpin);
        addRow(panelCentro, gbc, row++, "Probabilidad de no hacer nada:", nothingSpin);
        addRow(panelCentro, gbc, row++, "Tiempo de sentarse:", sitTimeSpin);
        addRow(panelCentro, gbc, row++, "Tiempo de espera general:", waitTimeSpin);
        panelCentro.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10));

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


        orderProbability = (int)orderProb.getValue();
        sitProbability = (int)sitProb.getValue();
        walkProbability = (int)walkProb.getValue();
        exitProbability = (int)exitProb.getValue();
        nothingProbability = (int)nothingProb.getValue();
        sitTimeWait = (int)sitTime.getValue();
        waitTimeWait = (int)waitTime.getValue();
        normalizeProbabilities();
        
        if (numClients <= 0 || numSillas1 < 0) {
            JOptionPane.showMessageDialog(this,
                    "Los valores deben ser mayores a 0",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }
        int numberOfCounters = tryServerCounter();
        CounterClient counterClient = new CounterClient(numberOfCounters);

        Chair[] chairs = new Chair[numSillas1];
        for (int i = 0; i < numSillas1; i++) {
            chairs[i] = new Chair("Silla" + (i + 1), i);
        }


        inicializarBtn.setEnabled(false);
        inicializarBtn.setText("Simulación en curso...");

        for (int i = 0; i < numSillas1; i++){
            chairs[i] = new Chair("Silla"+ (i+1), i);
        }

        Store store = new Store(10, chairs);
        Client[] clientsArray = new Client[numClients];

        for (int i = 0; i < numClients; i++) {
            Agents.Client client = new Client("Client" + (i + 1), counterClient, store, 5, 10.5, orderProbability,
                    sitProbability,
                    walkProbability,
                    exitProbability,
                    nothingProbability, sitTimeWait, waitTimeWait);
            clientsArray[i] = client;
            clientList.add(client);
            client.start();

            try {
                Thread.sleep(50);
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

        GeneralTable generalTable = new GeneralTable(clientsArray, store);
        Thread generalThread = new Thread(generalTable);
        generalThread.start();

        ClientStateTable clientStateTable = new ClientStateTable(clientsArray);
        Thread clientTableThread = new Thread(clientStateTable);
        clientTableThread.start();

        StateTable stateTable = new StateTable(clientsArray);
        Thread stateThread = new Thread(stateTable);
        stateThread.start();


    }
    private void normalizeProbabilities() {
        int sum = orderProbability + sitProbability + walkProbability +
                exitProbability + nothingProbability;

        if (sum == 0) return; // avoid division by zero

        orderProbability    = (orderProbability    * 100) / sum;
        sitProbability      = (sitProbability      * 100) / sum;
        walkProbability     = (walkProbability     * 100) / sum;
        exitProbability     = (exitProbability     * 100) / sum;
        nothingProbability  = (nothingProbability  * 100) / sum;
    }

    private void addRow(JPanel panel, GridBagConstraints gbc, int row, String text, JComponent comp) {
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0.3;
        panel.add(new JLabel(text), gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.7;
        panel.add(comp, gbc);
    }
    public int tryServerCounter(){
        int maxClientes = 5;
        try (Socket socket = new Socket("localhost", 5000);
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            maxClientes = Integer.parseInt(in.readLine());

            in.close();
            out.close();
            socket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return maxClientes;
    }
}