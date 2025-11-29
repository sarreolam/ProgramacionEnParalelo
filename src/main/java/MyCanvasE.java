import Agents.Client;
import Agents.Employee;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.concurrent.Semaphore;

public class MyCanvasE extends Canvas {
    JTextArea textArea;
    ArrayList<Employee> employees;
    Semaphore semaphore;
    public Image image;

    public MyCanvasE(JTextArea tArea,Semaphore sem,ArrayList <Employee> agents){
        textArea = tArea;
        employees = agents;
        semaphore = sem;
        String currentPath = System.getProperty("user.dir");
        image = Toolkit.getDefaultToolkit().getImage(currentPath+"Images/plinkIdle_0.gif");
    }
}
