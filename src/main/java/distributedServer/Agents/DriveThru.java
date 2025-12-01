package Agents;

import Buffers.Counter;
import Buffers.Kitchen;
import Buffers.Window;

public class DriveThru extends Thread {
    private final String name;
    private final Window window;

    public enum DriveThruState { LLEGANDO, ESPERANDO_VENTANILLA, EN_VENTANILLA, ESPERANDO_ORDEN, SALIENDO, FUERA }
    private DriveThruState state;

    public DriveThru(String name, Window window) {
        this.name = name;
        this.window = window;
    }

    @Override
    public void run() {
        try {
            state = DriveThruState.LLEGANDO;
            esperar(1000);
            state = DriveThruState.EN_VENTANILLA;
            window.carroLlega(name);
            state = DriveThruState.ESPERANDO_ORDEN;
            esperar(1000);
            state = DriveThruState.SALIENDO;

            System.out.println(name + " siguio su camino.");
        } catch (Exception e) {
            System.out.println("Error en drive-thru: " + e);
        }
    }
    private void esperar(int t) {
        try {
            Thread.sleep(t);
        } catch (InterruptedException e) {
            System.out.println(e);
        }
    }
}
