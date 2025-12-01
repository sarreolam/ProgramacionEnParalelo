package Agents;

import Buffers.CounterClient;

public class Client extends Thread {

    private final String name;
    private final CounterClient counterClient;

    public Client(String name, CounterClient counterClient) {
        this.name = name;
        this.counterClient = counterClient;
    }

    public void run() {

        counterClient.clienteLlega(name);


        //llamar a counter server
    }
}