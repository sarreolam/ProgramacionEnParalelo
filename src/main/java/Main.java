
import Agents.Employee;
import Agents.Machine;
import Agents.Client;
import Buffers.Counter;
import Buffers.Kitchen;

import java.util.Scanner;


public class Main {
    public static void main(String[] args) {
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

                for (int i = 0; i < numEmployees; i++) {
                    Employee employee = new Employee("Employee" + (i + 1));
                    employees[i] = employee;
                    employees[i].start();
                }

                for (int i = 0; i < numClients; i++) {
                    Client client = new Client("Client" + (i+1));
                    clients[i] = client;
                    clients[i].start();
                    try {
                        Thread.sleep(10);
                    } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
                break;
                default:
                    System.out.println("Invalid option. Closing program");
                    break;
        }
    }
}