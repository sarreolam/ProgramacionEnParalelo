import Agents.Client;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.Semaphore;
import java.util.stream.Collectors;

public class ClientsVisual extends MyCanvasE {

    private final int COUNTER_COUNT;
    private final int CHAIR_COUNT;

    private Image background;

    private BufferedImage backBuffer;
    private Graphics2D backGraphics;

    public int ventX, ventY;
    public ArrayList<Point> counterPoints = new ArrayList<>();
    public ArrayList<Point> chairPoints = new ArrayList<>();

    public Image counterImg;
    private Image chairImage;

    private final int numberOfCounters;
    private final int numberOfChairs;

    public ClientsVisual(JTextArea textArea, Semaphore sem, ArrayList<Client> clients,
                         int numCounters, int numChairs) {

        super(textArea, sem, clients);

        this.numberOfCounters = numCounters;
        this.numberOfChairs = numChairs;
        this.COUNTER_COUNT = numCounters;
        this.CHAIR_COUNT = numChairs;

        loadImages();
        generateCounterPositions();
        generateChairPositions();

        for (Client client : clients) {
            client.getMovement().setCounterPoints(counterPoints);
            client.getMovement().setChairPoints(chairPoints);
        }

        activatePaintingAgents();
    }

    private void loadImages() {
        try {
            background = ImageIO.read(new File("Images/background.jpg"));
            counterImg = ImageIO.read(new File("Images/counter.png"));
            chairImage = ImageIO.read(new File("Images/chair.jpg"));
        } catch (IOException e) {
            System.out.println("No se pudo cargar la imagen: " + e.getMessage());
        }
    }

    private void generateCounterPositions() {
        counterPoints.clear();
        for (int i = 0; i < numberOfCounters; i++) {
            counterPoints.add(new Point(50, 50 + i * 150));
        }
    }

    private void generateChairPositions() {
        chairPoints.clear();
        int startX = 300;
        int startY = 450;
        int spacing = 100;

        for (int i = 0; i < numberOfChairs; i++) {
            chairPoints.add(new Point(startX + (i * spacing), startY));
        }
    }

    void activatePaintingAgents() {
        PaintingAgentsE paintingAgents = new PaintingAgentsE(clients, this);
        Thread th = new Thread(paintingAgents);
        th.start();
    }

    @Override
    public void update(Graphics g) {
        paint(g);
    }

    @Override
    public void paint(Graphics g) {
        int w = getWidth();
        int h = getHeight();

        if (backBuffer == null || backBuffer.getWidth() != w || backBuffer.getHeight() != h) {
            backBuffer = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
            backGraphics = backBuffer.createGraphics();

            backGraphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);
        }

        drawToBuffer(backGraphics);
        g.drawImage(backBuffer, 0, 0, null);
    }

    private void drawToBuffer(Graphics2D g) {
        int w = getWidth();
        int h = getHeight();

        if (background != null) {
            g.drawImage(background, 0, 0, w, h, null);
        } else {
            g.setColor(Color.WHITE);
            g.fillRect(0, 0, w, h);
        }

        int[] counterQueues = new int[numberOfCounters];
        int[] chairQueues = new int[numberOfChairs];

        for (int counterIdx = 0; counterIdx < numberOfCounters; counterIdx++) {
            final int idx = counterIdx;

            List<Client> clientsAtCounter = clients.stream()
                    .filter(c -> {
                        Client.ClientState state = c.getClientState();
                        return (state == Client.ClientState.CAMINANDO_AL_MOSTRADOR ||
                                state == Client.ClientState.PIDIENDO) &&
                                c.getAssignedCounterIndex() == idx &&
                                c.getCounterArrivalTime() > 0;
                    })
                    .sorted(Comparator.comparingLong(Client::getCounterArrivalTime))
                    .collect(Collectors.toList());

            for (int i = 0; i < clientsAtCounter.size(); i++) {
                clientsAtCounter.get(i).setQueuePositionAtCounter(i);
            }

            counterQueues[idx] = clientsAtCounter.size();
        }

        for (int chairIdx = 0; chairIdx < numberOfChairs; chairIdx++) {
            final int idx = chairIdx;
            List<Client> clientsAtChair = clients.stream()
                    .filter(c -> {
                        Client.ClientState state = c.getClientState();
                        return (state == Client.ClientState.CAMINANDO_A_ASIENTOS ||
                                state == Client.ClientState.SENTADO) &&
                                c.getAssignedChairIndex() == idx &&
                                c.getChairArrivalTime() > 0;
                    })
                    .sorted(Comparator.comparingLong(Client::getChairArrivalTime))
                    .collect(Collectors.toList());

            for (int i = 0; i < clientsAtChair.size(); i++) {
                clientsAtChair.get(i).setQueuePositionAtChair(i);
            }
            chairQueues[idx] = clientsAtChair.size();
        }


        if (counterImg != null) {
            for (int i = 0; i < counterPoints.size(); i++) {
                Point p = counterPoints.get(i);
                g.drawImage(counterImg, p.x, p.y, 80, 80, null);
            }
        }

        if (chairImage != null) {
            for (int i = 0; i < chairPoints.size(); i++) {
                Point p = chairPoints.get(i);
                g.drawImage(chairImage, p.x, p.y, 80, 80, null);
            }
        }

        for (Client c : clients) {
            if (c.getCurrentSprite() != null) {
                g.drawImage(c.getCurrentSprite(),
                        (int) c.getX(),
                        (int) c.getY(),
                        70, 70,
                        null);

                g.setColor(Color.WHITE);
                g.drawString(c.getClientState().toString(), (int) c.getX(), (int) c.getY() - 5);
            }
        }
    }
}