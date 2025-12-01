import Agents.ClientInServer;

import java.awt.*;
import java.util.ArrayList;

public class PaintingAgents implements Runnable {
    private final ArrayList<ClientInServer> clients;
    private final Canvas canvas;

    public PaintingAgents(ArrayList<ClientInServer> clients, Canvas canvas) {
        this.canvas = canvas;
        this.clients = clients;
    }

    @Override
    public void run() {
        while (true) {
            for (ClientInServer client : clients) {
                client.UpdateAnimation();
            }
            canvas.repaint();
            try {
                Thread.sleep(15);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }


}
