import Agents.Employee;

import java.awt.*;
import java.util.ArrayList;

public class PaintingAgentsE implements Runnable {
    private final ArrayList<Employee> employees;
    private final Canvas canvas;
    private volatile boolean running = true;

    public PaintingAgentsE(ArrayList<Employee> employees, Canvas canvas) {
        this.canvas = canvas;
        this.employees = employees;
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

            canvas.update(g);

            try {
                Thread.sleep(16);
            } catch (InterruptedException e) {
                e.printStackTrace();
                running = false;
            }
        }
    }
}