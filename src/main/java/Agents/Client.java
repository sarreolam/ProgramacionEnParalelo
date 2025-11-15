package Agents;
import java.util.Random;
import Buffers.Counter;


public class Client extends Thread{
        private String name;
        private Counter counter;

        public Client (String name, Counter counter){
            this.name = name;
            this.counter = counter;
        }

        public void Decision() {
            Random rand = new Random();
            int choice = rand.nextInt(30);
            if(choice <= 30) {
                System.out.println("Client has chosen to go to the counter to order.");
                counter.MakeOrder();
            } else if(choice >= 50 && choice <= 70) {
                System.out.println("Agent has chosen to go to the kitchen.");
            } else if(choice >= 80 && choice <= 100) {
                System.out.println("Agent has chosen to go to the store.");
            }
        }

        @Override
        public void run(){
            while(true){
                try{
                    Decision();
                    Thread.sleep(3000);
                } catch(Exception e){
                    System.out.println(e);
                }

        }
    }
}
