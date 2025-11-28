import Agents.Client;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.concurrent.Semaphore;

public class VisualWindow extends Canvas {
    JTextArea textArea;
    ArrayList<Client> clients;
    Semaphore semaphore;
    public Image image;

    public VisualWindow(JTextArea tArea,Semaphore sem,ArrayList <Client> agents){
        textArea = tArea;
        clients = agents;
        semaphore = sem;
        String currentPath = System.getProperty("user.dir");
        image = Toolkit.getDefaultToolkit().getImage(currentPath+"Images/plinkIdle_0.gif");
    }

}
