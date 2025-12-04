public class Main {

    public static void main(String[] args) {
        //GUI y tablas de monitoreo
        VentanaPrincipal ventana = new VentanaPrincipal();
        MainInfoWindow ventanaPrincipal = new MainInfoWindow(ventana);

        ventana.setVisible(true);
    }
}