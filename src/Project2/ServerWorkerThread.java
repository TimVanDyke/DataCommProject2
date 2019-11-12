import java.io.*;
import java.net.*;
import java.util.*;

public class ServerWorkerThread implements Runnable {
    Socket connectionSocket;
    String fromClient;
    String clientCommand;
    byte[] data;
    String frstln;
    ArrayList<HostConnection> listHosts;

    ServerWorkerThread(Socket connectionSocket, ArrayList<HostConnection> listHosts) {
        this.connectionSocket = connectionSocket;
        this.listHosts = listHosts;
    }

    public void run() throws RuntimeException {
        try {
            DataOutputStream outToClient = new DataOutputStream(connectionSocket.getOutputStream());
            BufferedReader inFromClient = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
            fromClient = inFromClient.readLine();

            StringTokenizer tokens = new StringTokenizer(fromClient);
            frstln = tokens.nextToken();
            int port = Integer.parseInt(frstln);
            clientCommand = tokens.nextToken();

            if(clientCommand == "search"){
                //TODO fill search method
                
            }
            if(clientCommand == "quit"){
                //TODO close sockets, remove user files
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
