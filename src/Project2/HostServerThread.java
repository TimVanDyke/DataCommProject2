import java.io.*;
import java.net.*;
import java.util.*;

public class HostServerThread implements Runnable {
    Socket connectionSocket;
    String fromClient;
    String clientCommand;
    String userName;
    byte[] data;
    String frstln;

    HostServerThread(Socket connectionSocket, String userName) {
        this.connectionSocket = connectionSocket;
        this.userName = userName;
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

            if(clientCommand.equals("quit")){
                //FIXME Do something? Project 1 didn't have anything here for server
            }

            if (clientCommand.equals("retr")) {

                // Instantiate necessary sockets
                Socket dataSocket = new Socket(connectionSocket.getInetAddress(), port);
                DataInputStream dataFromClient = new DataInputStream(
                        new BufferedInputStream(dataSocket.getInputStream()));
                DataOutputStream statusCodeOut = new DataOutputStream(dataSocket.getOutputStream());

                // Receive file name
                String fileName = dataFromClient.readUTF();
                fileName = "../" + userName + "/" + fileName;
                File retrieve = new File(fileName);

                // If it is a file, proceed with sending file
                if (retrieve.isFile()) {

                    // Send status code 200 on successful file find
                    statusCodeOut.writeUTF("200");

                    // Get the size of the file
                    long length = retrieve.length();
                    byte[] bytes = new byte[16 * 1024];

                    // File writer and output stream
                    InputStream in = new FileInputStream(retrieve);
                    OutputStream out = dataSocket.getOutputStream();

                    // Write bytes from file to output stream
                    int count;
                    while ((count = in.read(bytes)) > 0) {
                        out.write(bytes, 0, count);
                    }

                    // Close sockets
                    out.close();
                    in.close();
                    statusCodeOut.close();

                }
                // If file is not found
                else {
                    // Send status code 500 for an error
                    statusCodeOut.writeUTF("500");
                    statusCodeOut.close();
                }
                dataSocket.close();
            }
            
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
