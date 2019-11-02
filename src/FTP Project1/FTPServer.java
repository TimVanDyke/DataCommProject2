import java.io.*;
import java.net.*;
import java.util.*;

public class FTPServer {

    public static void main(String argv[]) throws IOException {        
        ServerSocket welcomeSocket = new ServerSocket(12000);

        while (true) {
            WorkerThread workerThread = new WorkerThread(welcomeSocket.accept());
            Thread thread = new Thread(workerThread);
            thread.start();
        }
    }
}
