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
    String username;
    String host;
    String speed;
    ArrayList<String> searchResults = new ArrayList<String>();

    ServerWorkerThread(Socket connectionSocket, ArrayList<HostConnection> listHosts) {
        this.connectionSocket = connectionSocket;
        this.listHosts = listHosts;
    }

    public void run() throws RuntimeException {
        try {
            DataOutputStream outToClient = new DataOutputStream(connectionSocket.getOutputStream());
            BufferedReader inFromClient = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
            fromClient = inFromClient.readLine();

            // StringTokenizer tokens = new StringTokenizer(fromClient);
            // host = tokens.nextToken();
            // clientCommand = tokens.nextToken();
            System.out.println(fromClient);
            if (fromClient != null) {
                StringTokenizer tokens = new StringTokenizer(fromClient);
                username = tokens.nextToken();
                frstln = tokens.nextToken();
                int port = Integer.parseInt(frstln);
                host = tokens.nextToken();
                speed = tokens.nextToken();
                clientCommand = tokens.nextToken();

                // add this connection if not in list already
                HostConnection c = new HostConnection(username, host, speed);
                boolean hostHereAlready = false;
                for (int i = 0; i < listHosts.size(); i++) {
                    if (listHosts.get(i).hostname.equals(c.hostname)) {
                        hostHereAlready = true;
                    }
                }
                if (!hostHereAlready) {
                    listHosts.add(c);
                }

                // Socket dataSocket = new Socket(connectionSocket.getInetAddress(), port);
                if (clientCommand.equals("search")) {
                    String searchWord = tokens.nextToken();
                    Socket dataSocket = new Socket(connectionSocket.getInetAddress(), port);
                    DataOutputStream outWord = new DataOutputStream(dataSocket.getOutputStream());
                    searchResults.clear();
                    for (int i = 0; i < listHosts.size(); i++) {
                        for (int j = 0; j < listHosts.get(i).fileList.size(); j++) {
                            for (int q = 0; q < listHosts.get(i).fileList.get(j).keywords.size(); q++) {
                                if (listHosts.get(i).fileList.get(j).keywords.get(q).equals(searchWord)) {
                                    searchResults.add(listHosts.get(i).fileList.get(j).name);
                                }
                            }
                        }
                    }
                    System.out.println("Before WriteUTF");
                    if (searchResults.size() > 0) {
                        for (int i = 0; i < searchResults.size(); i++) {
                            outWord.writeUTF(searchResults.get(i));
                            outWord.flush();
                        }
                    }
                    else {
                        //System.out.println("Sorry there are no files with that keyword");
                        outWord.writeBytes("NoFiles");
                        outWord.flush();
                    }
                    System.out.println("After WriteUTF");
                    dataSocket.close();
                }
                if (clientCommand.equals("quit")) {
                    Socket dataSocket = new Socket(connectionSocket.getInetAddress(), port);

                    for (int i = 0; i < listHosts.size(); i++) {
                        // find connection that has hostname user and delete
                        if (listHosts.get(i).hostname == host) {
                            listHosts.remove(i);
                        }
                    }
                    outToClient.close();
                    inFromClient.close();
                    dataSocket.close();
                    System.out.println("bye");
                }

                if (clientCommand.equals("upload")) {
                    try {
                        Socket dataSocket = new Socket(connectionSocket.getInetAddress(), port);
                        ObjectInputStream dataFromClient = new ObjectInputStream(
                                new BufferedInputStream(dataSocket.getInputStream()));

                        ArrayList<String> fileDescription = new ArrayList<String>();
                        String description = tokens.nextToken();
                        String filename = tokens.nextToken();
                        String delims = "[ ]";
                        String[] tokens1 = description.split(delims);
                        for (int i = 0; i < tokens1.length; i++) {
                            fileDescription.add(tokens1[i]);
                        }
                        HostFile f = new HostFile(username, filename, fileDescription);
                        // f = (HostFile) dataFromClient.readObject();
                        for (int i = 0; i < listHosts.size(); i++) {
                            if (listHosts.get(i).equals(c)) {
                                listHosts.get(i).fileList.add(f);
                            }
                        }
                        dataSocket.close();
                    } catch (Exception e) {

                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
