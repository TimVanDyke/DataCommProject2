import java.io.*;
import java.net.*;
import java.util.*;
import java.text.*;
import java.lang.*;
import javax.swing.*;

class FTPClient {

    public static void main(String argv[]) throws Exception {
        String sentence;
        String modifiedSentence;
        boolean isOpen = true;
        int number = 1;
        boolean notEnd = true;
        String statusCode;
        boolean clientgo = true;
        int port1;

        String username = "";
        String hostname = "";
        int connectionSpeed = 0;




        BufferedReader inFromUser = new BufferedReader(
                new InputStreamReader(System.in));
        sentence = inFromUser.readLine();
        StringTokenizer tokens = new StringTokenizer(sentence);

        if (sentence.startsWith("connect")) {
            String serverName = tokens.nextToken(); // pass the connect command
            serverName = tokens.nextToken();
            port1 = Integer.parseInt(tokens.nextToken());
            
            
            while (isOpen && clientgo) {
                System.out.println("You are connected to " + serverName);
                notEnd = true;
    
                Socket ControlSocket = new Socket(serverName, port1);

                System.out.println("What would you like to do next?" +
                        "\nlist || retr: file.txt || stor: file.txt  || close");

                DataOutputStream outToServer = new DataOutputStream(
                        ControlSocket.getOutputStream());

                DataInputStream inFromServer = new DataInputStream(
                        new BufferedInputStream(ControlSocket.getInputStream()));

                sentence = inFromUser.readLine();

                // Listing Files
                if (sentence.equals("list")) {
                    int port = port1 + 2;
                    outToServer.writeBytes(port + " " + sentence + " " + '\n');
                    ServerSocket welcomeData = new ServerSocket(port);
                    Socket dataSocket = welcomeData.accept();
                    DataInputStream inData = new DataInputStream(
                            new BufferedInputStream(dataSocket.getInputStream()));
                    notEnd = true;
                    // Server will send 'eof' when it is done sending files
                    while (notEnd) {
                        modifiedSentence = inData.readUTF();
                        if(modifiedSentence.compareTo("eof") == 0){
                            notEnd = false;
                        }
                        else{
                            System.out.println(modifiedSentence);
                        }
                    }
                    welcomeData.close();
                    dataSocket.close();
                    inData.close();
                }


/////////////////////////////////////////////////////////////////////////////////////////////
                if (sentence.equals("listN")) {
                    int port = port1 + 2;
                    outToServer.writeBytes(port + " " + sentence + " " + '\n');
                    ServerSocket welcomeData = new ServerSocket(port);
                    Socket dataSocket = welcomeData.accept();
                    DataInputStream inData = new DataInputStream(
                            new BufferedInputStream(dataSocket.getInputStream()));
                    notEnd = true;
                    // Server will send 'eof' when it is done sending files
                    int parameterInt = 0;
                    while (notEnd) {
                        modifiedSentence = inData.readUTF();
                        if(modifiedSentence.compareTo("eof") == 0){
                            notEnd = false;
                        }
                        else{
                            if (parameterInt == 0){
                                username = modifiedSentence;

                            }
                            if (parameterInt == 1){

                                hostname = modifiedSentence;
                             
                            }
                            if (parameterInt == 2){

                                connectionSpeed = Integer.parseInt(modifiedSentence.trim());
                            }

                            System.out.println("the username is" + username);
                            System.out.println("the hostname is" + hostname);
                            System.out.println("the connection speed is" + connectionSpeed);
                            parameterInt++;
                        }
                    }
                    welcomeData.close();
                    dataSocket.close();
                    inData.close();
                }
//////////////////////////////////////////////////////////////////////////////////////////////////
















                else if (sentence.startsWith("retr: ")) {

                    int port = port1 + 2;

                    outToServer.writeBytes(port + " " + sentence + " " +
                            '\n');
                    ServerSocket welcomeData = new ServerSocket(port);
                    Socket dataSocket = welcomeData.accept();

                    DataInputStream inData = new DataInputStream(
                            new BufferedInputStream(dataSocket.getInputStream()));

                    DataOutputStream outData = new DataOutputStream(
                            dataSocket.getOutputStream());

                    // Parse filename from command, send it to server
                    String fileName = sentence.substring(
                            sentence.lastIndexOf(" ") + 1);
                    outData.writeUTF(fileName);

                    boolean fileExists = (inData.readUTF().compareTo("200") == 0);

                    // Check file exists
                    if(fileExists) {
                        // Receive file
                        OutputStream fileOut = new FileOutputStream(
                                "../client_data/" + fileName);

                        byte[] bytes = new byte[16 * 1024];
                        int count;
                        while ((count = inData.read(bytes)) > 0) {
                            fileOut.write(bytes, 0, count);
                        }
                        fileOut.close();
                    }
                    // If file does not exist, print error.
                    else
                        System.out.println("File " + fileName + " not found.");
                    welcomeData.close();
                    dataSocket.close();
                    outData.close();
                    inData.close();
                }


                else if(sentence.startsWith("stor:")){
                    int port = port1 + 2;

                    outToServer.writeBytes(port + " " + sentence + " " +
                            '\n');
                    ServerSocket welcomeData = new ServerSocket(port);
                    Socket dataSocket = welcomeData.accept();


                    DataOutputStream dataOutputStream = new DataOutputStream(
                            dataSocket.getOutputStream());

                    // Parse for file name to store
                    String fileName = sentence.substring(
                            sentence.lastIndexOf(" ") + 1);
                    File toStore = new File("../client_data/" + fileName);

                    // Check that it is a file
                    if(toStore.isFile()) {

                        // Send 200 on successful file find
                        dataOutputStream.writeUTF("200");

                        // Send file name
                        dataOutputStream.writeUTF(fileName);

                        // Find file length
                        long length = toStore.length();
                        byte[] bytes = new byte[16 * 1024];

                        // Instantiate file and output sockets
                        InputStream in = new FileInputStream(toStore);
                        OutputStream out = dataSocket.getOutputStream();

                        // Write bytes from file to output
                        int count;
                        while ((count = in.read(bytes)) > 0) {
                            out.write(bytes, 0, count);
                        }
                        out.close();
                        in.close();
                    }
                    else{
                        // Send 500 as response code if not found
                        dataOutputStream.writeUTF("550");
                        System.out.println("File not found.");
                    }
                    dataSocket.close();
                    welcomeData.close();
                }

                else if(sentence.startsWith("close")){

                    int port = port1 + 2;
                    outToServer.writeBytes(port + " " + sentence + " " + '\n');
                    isOpen = false;
                    clientgo = false;
                    ControlSocket.close();
            }

            }
        }
    }
}
