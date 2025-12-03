import Agents.Client;

import java.awt.*;
import java.util.ArrayList;

public class PaintingAgentsE implements Runnable {
    private final ArrayList<Client> clients;

    private final Canvas canvas;
    private volatile boolean running = true;
    private static final int FRAME_DELAY = 16;

    public PaintingAgentsE(ArrayList<Client> clients, Canvas canvas) {
        this.canvas = canvas;
        this.clients = clients;
    }

    @Override
    public void run() {
        boolean ready = false;
        Graphics g = null;
        while (!ready) {
            g = canvas.getGraphics();
            if (g == null) {
                try {
                    Thread.sleep(16);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } else {
                ready = true;
            }
        }

        while (running) {
            for (Client client : clients) {
                client.UpdateAnimation();
            }

            canvas.update(g);

            try {
                Thread.sleep(16);  // ~60 FPS
            } catch (InterruptedException e) {
                e.printStackTrace();
                running = false;
            }
        }
    }
}