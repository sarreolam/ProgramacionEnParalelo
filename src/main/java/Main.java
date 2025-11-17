import Agents.Employee;
import Agents.Client;
import Buffers.Counter;
import java.util.Scanner;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Main {
    public static void main(String[] args) {
        Lock kitchenLock = new ReentrantLock();
        Scanner scanner = new Scanner(System.in);
        System.out.println("1.- GUI 2.- console");
        int option = scanner.nextInt();
        switch (option) {
            case 1:
                VentanaPrincipal vp = new VentanaPrincipal();
                vp.setVisible(true);
                break;
            case 2:
                System.out.print("Enter number of employees: ");
                int numEmployees = scanner.nextInt();
                System.out.print("Enter number of clients: ");
                int numClients = scanner.nextInt();

                Employee[] employees = new Employee[numEmployees];
                Client[] clients = new Client[numClients];
                Counter counter = new Counter(0);

                for (int i = 0; i < numEmployees; i++) {
                    Employee employee = new Employee("Employee number " + (i + 1), counter, kitchenLock);
                    employees[i] = employee;
                    employees[i].start();
                }

                for (int i = 0; i < numClients; i++) {
                    Client client = new Client("Client number " + (i+1), counter);
                    clients[i] = client;
                    clients[i].start();
                }
                break;

        }
    }
}