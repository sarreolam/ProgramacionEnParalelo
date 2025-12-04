import Agents.DriveThru;
import Agents.Employee;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.concurrent.Semaphore;

public class MyCanvasE extends Canvas {
    public Image image;
    JTextArea textArea;
    ArrayList<Employee> employees;
    ArrayList<DriveThru> driveThrus;
    Semaphore semaphore;

    public MyCanvasE(JTextArea tArea, Semaphore sem, ArrayList<Employee> agents, ArrayList<DriveThru> agents1) {
        textArea = tArea;
        employees = agents;
        driveThrus = agents1;
        semaphore = sem;
        String currentPath = System.getProperty("user.dir");
        image = Toolkit.getDefaultToolkit().getImage(currentPath + "Images/plinkIdle_0.gif");
    }
}
