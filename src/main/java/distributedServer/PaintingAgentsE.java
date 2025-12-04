import Agents.Employee;
import Agents.DriveThru;

import java.awt.*;
import java.util.ArrayList;

public class PaintingAgentsE implements Runnable {
    private final ArrayList<Employee> employees;
    private final ArrayList<DriveThru> driveThrus;
    private final Canvas canvas;
    private volatile boolean running = true;

    public PaintingAgentsE(ArrayList<Employee> employees, ArrayList<DriveThru> driveThrus, Canvas canvas) {
        this.canvas = canvas;
        this.employees = employees;
        this.driveThrus = driveThrus;
    }

    @Override
    public void run() {
        boolean ready = false;
        Graphics g = null;
        while (!ready) {
            g = canvas.getGraphics();
            if (g == null) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } else {
                ready = true;
            }
        }

        while (running) {
            for (Employee employee : employees) {
                employee.UpdateAnimation();
            }
            for (DriveThru driveThru : driveThrus) {
                driveThru.UpdateAnimation();
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