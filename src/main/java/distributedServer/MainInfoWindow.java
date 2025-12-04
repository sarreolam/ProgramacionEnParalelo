import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MainInfoWindow extends JFrame {
    private JFrame frame = new JFrame("Programacion en paralelo - Parcial 3");
    private ImageIcon image = new ImageIcon("Images/info.png");
    private JLabel label = new JLabel(image, JLabel.CENTER);
    private JButton button = new JButton("Start simulation");
    private VentanaPrincipal ventanaPrincipal;

    public MainInfoWindow(VentanaPrincipal ventana) {
        this.ventanaPrincipal = ventana;
        frame.setLayout(new BorderLayout());
        frame.add(label, BorderLayout.NORTH);
        frame.setSize(image.getIconWidth(), image.getIconHeight());
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        button.addActionListener(e -> {
            ventanaPrincipal.setVisible(true);
            frame.dispose();
        });
        frame.add(button, BorderLayout.SOUTH);
        frame.setVisible(true);
    }
}
