package Buffers;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Counter {

    public int MaxAmountClients = 5;
    private int idCounter = 0;
    private Lock getOrderLock = new ReentrantLock();
    private Lock makeOrderLock = new ReentrantLock();
    private Queue<Integer> orderQueue = new LinkedList<>();

    public int GetOrder(){
        try{
            getOrderLock.lock();
            System.out.println("Getting order from client");
            return orderQueue.poll();
        } catch (Exception e){
            System.out.println(e);
        } finally {
            getOrderLock.unlock();
        }

    }

    public void MakeOrder(){
        try{
            makeOrderLock.lock();
            System.out.println("Making order by client");
            orderQueue.add(idCounter);
            System.out.println("Order made " + idCounter);
            idCounter++;
        } catch (Exception e){
            System.out.println(e);
        } finally {
            makeOrderLock.unlock();
        }
    }

}
