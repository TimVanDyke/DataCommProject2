import java.io.*;
import java.net.*;
import java.util.*;

public class TestServer {

    public static void main(String argv[]) throws IOException {
        ServerSocket welcomeSocket = new ServerSocket(12000);

        while (true) {
            ServerWorkerThread workerThread = new ServerWorkerThread(welcomeSocket.accept());
            Thread thread = new Thread(workerThread);
            thread.start();
            System.out.println("New connection");
        }
    }
}
