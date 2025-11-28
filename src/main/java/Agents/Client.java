package Agents;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.Socket;
import java.util.Objects;
import java.util.Random;
import Buffers.Counter;

import javax.imageio.ImageIO;


public class Client extends Thread {
    private final String name;
    private int port = 5000;
    private boolean running = true;

    public enum ClientState { NACIENDO, CAMINANDO, ESPERANDO, PIDIENDO, SALIENDO, FUERA }
    private ClientState state;

    private double positionX, positionY;
    private double targetX, targetY;
    private double speed = 2.0f;

    private BufferedImage[] walkAnim;
    private BufferedImage[] idleAnim;
    private BufferedImage[] leavingAnim;

    private BufferedImage[] currentAnim;
    private int frameIndex = 0;
    private long lastTime = 0;
    private int frameDelay = 120;

    public Client(String name) {
        this.name = name;
        this.state = ClientState.NACIENDO;
        this.positionX = 0;
        this.positionY = 100;

        this.targetX = 300;
        this.targetY = 100;
        LoadSprites();
        UpdateAnimationArray();
    }

    public Client(String name, String port) {
        this.name = name;
        this.port = Integer.parseInt(port);
        this.state = ClientState.NACIENDO;
        this.positionX = 0;
        this.positionY = 100;

        this.targetX = 300;
        this.targetY = 100;
        LoadSprites();
        UpdateAnimationArray();
    }

    public ClientState getClientState() {
        return state;
    }
    public double getX() { return positionX; }
    public double getY() { return positionY; }


    private void LoadSprites(){
        try{
            walkAnim = LoadAnim("src/main/java/Images/walkingClient_", 11);
            idleAnim = LoadAnim("src/main/java/Images/plinkIdle_",20);
            //leavingAnim = loadAnim(10);
        } catch (RuntimeException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    private BufferedImage[] LoadAnim(String base, int count) throws IOException {
        BufferedImage[] anim = new BufferedImage[count];
        for(int i = 0; i < count; i++){
            anim[i] = ImageIO.read(new File(base + i + ".gif"));
        }
        return anim;
    }

    public void UpdateAnimation() {
        switch (state) {
            case NACIENDO:
                break;
            case CAMINANDO:
                moveTowardsTarget();
                break;
            case ESPERANDO:
                break;
            case PIDIENDO:
                break;
            case SALIENDO:
                moveTowardsExit();
                break;
            default:
                break;
        }
    }

    public void UpdateAnimationArray() {
        switch (state) {
            case CAMINANDO:
                currentAnim = walkAnim;
                break;
            case ESPERANDO:
            case PIDIENDO:
                currentAnim = idleAnim;
                break;
            case SALIENDO:
                currentAnim = walkAnim;
                break;
            default:
                currentAnim = idleAnim;
                break;
        }
    }

    public BufferedImage getCurrentSprite() {
        if (currentAnim == null) return null;

        long now = System.currentTimeMillis();

        if (now - lastTime > frameDelay) {
            frameIndex = (frameIndex + 1) % currentAnim.length;
            lastTime = now;
        }

        return currentAnim[frameIndex];
    }

    private void moveTowardsTarget() {
        double dx = targetX - positionX;
        double dy = targetY - positionY;
        double distance = Math.sqrt(dx * dx + dy * dy);
        if(distance < 1) return;
        positionX += (dx/distance)*speed;
        positionY += (dy/distance)*speed;
    }

    private void moveTowardsExit() {
        targetX = -50;
        targetY = positionY;
        moveTowardsTarget();
    }


    @Override
    public void run() {
        System.out.println("Client " + name + " thread started");

        //try {
            //Socket socket = new Socket("localhost", port);

            //PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            //BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            //System.out.println("Client " + name + " input stream created");

            state = ClientState.CAMINANDO;
            UpdateAnimationArray();
            esperar(1000);

            state = ClientState.PIDIENDO;
            UpdateAnimationArray();
            //String message = "clientArrive " + name;
            //out.println(message);
            //out.flush();
            //System.out.println("Client " + name + " waiting for response...");

            //String resp = in.readLine();

            //if(resp != null && resp.equals("OK")){
                //System.out.println("Client " + name + " got OK, leaving");
                state = ClientState.SALIENDO;
                UpdateAnimationArray();
                esperar(1000);
                state = ClientState.FUERA;
                UpdateAnimationArray();
                //running = false;
            //} else {
                //System.out.println("Client " + name + " did NOT get OK");
            //}

            //System.out.println("Client " + name + " closing socket");
            //socket.close();

        //} catch (IOException e){
            //System.out.println("Client " + name + " IOException: " + e.getMessage());
            //e.printStackTrace();
        //}

        /*System.out.println("Cliente " + name + " ha iniciado su turno");
        state = ClientState.CAMINANDO;
        esperar(1000);
        state = ClientState.PIDIENDO;
        counter.clienteLlega(name);
        state = ClientState.SALIENDO;
        esperar(1000);
        state = ClientState.FUERA;
        System.out.println(name + " salió del restaurante.");*/

    }

    private void esperar(int t){
		try{
			Thread.sleep(t);
		}catch(InterruptedException e){
			System.out.println(e);		
		}
	}
}
