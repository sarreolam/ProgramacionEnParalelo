import Agents.Employee;
import Agents.Client;
import Buffers.Counter;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);


        System.out.print("Enter number of employees: ");
        int numEmployees = scanner.nextInt();
        System.out.print("Enter number of clients: ");
        int numClients = scanner.nextInt();

        Employee[] employees = new Employee[numEmployees];
        Client[] clients = new Client[numClients];
        Counter counter;

        for (int i = 0; i < 10; i++) {
            Employee employee = new Employee("Employee number " + (i+1), counter);
            employees[i] = employee;
            employees[i].start();
        }

        for (int i = 0; i < 10; i++) {
            Client client = new Client("Client number " + (i+1), counter);
            clients[i] = client;
            clients[i].start();
        }

    }
}