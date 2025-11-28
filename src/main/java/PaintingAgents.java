import Agents.Client;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class PaintingAgents implements Runnable {
    private ArrayList<Client> clients;
    private Canvas canvas;

    public PaintingAgents(ArrayList <Client> clients, Canvas canvas){
        this.canvas = canvas;
        this.clients = clients;
    }

    @Override
    public void run(){
        while (true){
            for(Client client : clients){
                client.UpdateAnimation();
            }
            canvas.repaint();
            try{
                Thread.sleep(15);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }


}
