import java.io.*;
import java.net.*;
import java.util.*;

public class HostServer implements Runnable {

    public int serverPort;
    public String userName;

    public HostServer(int serverPort, String userName) {
        this.serverPort = serverPort;
        this.userName = userName;
    }


    @Override
    public void run() {
        ServerSocket welcomeSocket = null;
        try {
            welcomeSocket = new ServerSocket(serverPort);
            System.out.println("created Server Socket");
        }
        catch(Exception e) {
                e.printStackTrace();
        }

        while (true) {
            try {
                HostServerThread hostWorkerThread = new HostServerThread(welcomeSocket.accept(), userName);
                System.out.println("accepted socket");
                Thread thread = new Thread(hostWorkerThread);
                thread.start();
                System.out.println("started hostWorkerThread");
            } catch (Exception e) {
                e.printStackTrace();//TODO: handle exception
            }
        }
    }
}
