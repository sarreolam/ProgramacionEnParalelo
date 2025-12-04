import Agents.Client;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.concurrent.Semaphore;

public class MyCanvasE extends Canvas {
    public Image image;
    JTextArea textArea;
    ArrayList<Client> clients;

    Semaphore semaphore;

    public MyCanvasE(JTextArea tArea, Semaphore sem, ArrayList<Client> clientArr) {
        textArea = tArea;
        clients = clientArr;
        semaphore = sem;
        String currentPath = System.getProperty("user.dir");
        image = Toolkit.getDefaultToolkit().getImage(currentPath + "Images/plinkIdle_0.gif");
    }
}
