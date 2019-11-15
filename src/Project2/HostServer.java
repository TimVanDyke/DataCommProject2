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
        }
        catch(Exception e) {
            e.printStackTrace();
        }

        while (true) {
            try {
                HostServerThread hostWorkerThread = new HostServerThread(welcomeSocket.accept(), userName);
                Thread thread = new Thread(hostWorkerThread);
                thread.start();
            } catch (Exception e) {
                e.printStackTrace();//TODO: handle exception
            }
        }
    }
}
