import java.io.*;
import java.net.*;
import java.util.*;

public class CentralServer {
   
    public static void main(String argv[]) throws IOException {
        ServerSocket welcomeSocket = new ServerSocket(12000);
        ArrayList<HostConnection> listHosts = new ArrayList<HostConnection> ();
            while (true) {
            ServerWorkerThread workerThread = new ServerWorkerThread(welcomeSocket.accept(), listHosts);
            Thread thread = new Thread(workerThread);
            thread.start();
        }
    }




}