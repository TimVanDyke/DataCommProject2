import java.io.*;
import java.net.*;
import java.util.*;

public class HostServer implements Runnable {

    public void run() throws RuntimeException {
        try {
            ServerSocket welcomeSocket = new ServerSocket(12000);
            while (true) {
                HostServerThread workerThread = new HostServerThread(welcomeSocket.accept());
                Thread thread = new Thread(workerThread);
                thread.start();
            }
        } catch (Exception e) {

        }
    }
}