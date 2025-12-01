import Agents.ClientInServer;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.concurrent.Semaphore;

public class VisualWindow extends Canvas {
    public Image image;
    JTextArea textArea;
    ArrayList<ClientInServer> clients;
    Semaphore semaphore;

    public VisualWindow(JTextArea tArea, Semaphore sem, ArrayList<ClientInServer> agents) {
        textArea = tArea;
        clients = agents;
        semaphore = sem;
        String currentPath = System.getProperty("user.dir");
        image = Toolkit.getDefaultToolkit().getImage(currentPath + "Images/plinkIdle_0.gif");
    }

}
