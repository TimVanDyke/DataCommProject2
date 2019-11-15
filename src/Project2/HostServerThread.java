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
            System.out.println("after out2client");
            BufferedReader inFromClient = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
            System.out.println("after infromclient");
            fromClient = inFromClient.readLine();
            System.out.println("after readline");
            StringTokenizer tokens = new StringTokenizer(fromClient);
            System.out.println("From client: " + fromClient);
            String fileName = tokens.nextToken();
            String sendToPortString = tokens.nextToken();
            int sendToPortInt = Integer.parseInt(sendToPortString);
            System.out.println("after toekns");

            
            System.out.println(fileName);

            if (fileName.equals("quit")) {
                // FIXME Do something? Project 1 didn't have anything here for server
            }

            System.out.println("fileName not 'quit' ");
            // Instantiate necessary sockets
            Socket dataSocket = new Socket(connectionSocket.getInetAddress(), sendToPortInt);
            DataInputStream dataFromClient = new DataInputStream(new BufferedInputStream(dataSocket.getInputStream()));
            DataOutputStream statusCodeOut = new DataOutputStream(dataSocket.getOutputStream());

            System.out.println("sockets created");
            
            // Receive file name
            String filePath  = "./" + userName + "/" + fileName;
            System.out.println(filePath);
            File retrieve = new File(filePath);

            // If it is a file, proceed with sending file
            if (retrieve.isFile()) {

                System.out.println("fileName exists in file structure");
            
                // Send status code 200 on successful file find
                statusCodeOut.writeUTF("200");

                // Get the size of the file
                long length = retrieve.length();
                byte[] bytes = new byte[16 * 1024];

                // File writer and output stream
                InputStream in = new FileInputStream(retrieve);
                OutputStream out = dataSocket.getOutputStream();

                System.out.println("about to write fileBytes");
                // Write bytes from file to output stream
                int count;
                while ((count = in.read(bytes)) > 0) {
                    out.write(bytes, 0, count);
                }

                System.out.println("bytes written");

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

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
