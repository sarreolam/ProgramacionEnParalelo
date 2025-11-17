package Agents;
import java.util.Random;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import Buffers.Counter;


public class Employee extends Thread{

    private String name;
    private Counter counter;
    private int orderToMake;
    private final Lock kitchenLock;
    private boolean isRunning = true;

    public Employee (String name, Counter counter, Lock kitchenLock) {
        this.name = name;
        this.counter = counter;
        this.kitchenLock = kitchenLock;
    }

    public void Decision() {
        Random rand = new Random();
        int choice = rand.nextInt(101);
        try{
            if(choice <= 30) {
                System.out.println("Agent has chosen to go to the counter.");
                counter.GetOrder();
                for (int i = 0; i < 1_000_000; i++) {} //Segun yo esto iba a hacer que se viera el runnable pero no se ve en la tabla
                System.out.println("Order number: " + orderToMake);
            } else if(choice >= 50 && choice <= 70) {
                System.out.println("Agent has chosen to go to the kitchen.");
                synchronized (kitchenLock) { //Esto se supone que muestra el blocked pero ñao ñao
                    System.out.println(getName() + " entered the kitchen (RUNNABLE inside sync).");
                    Thread.sleep(3000);
                }
            } else if(choice >= 80 && choice <= 99) { //Timed waiting
                System.out.println("Agent has chosen to go to the store.");
                Thread.sleep(5000);
            } else if(choice >= 100) {
                isRunning = false; //Terminated
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void run(){
        while(isRunning) {
            try{
                Decision();
                Thread.sleep(3000);
            } catch(Exception e){
                System.out.println(e);
            }
        }
    }
}
