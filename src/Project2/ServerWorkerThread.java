import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.*;
import java.net.*;
import java.util.*;

import javax.xml.parsers.*;

public class ServerWorkerThread implements Runnable {
    Socket connectionSocket;
    String fromClient;
    String clientCommand;
    byte[] data;
    String frstln;
    String username;
    String host;
    String speed;
    String hostServerPort;
    ArrayList<HostConnection> listHosts;
    ArrayList<HostFile> fileList;
    ArrayList<HostFile> searchResults = new ArrayList<HostFile>();

    ServerWorkerThread(Socket connectionSocket, ArrayList<HostConnection> listHosts, ArrayList<HostFile> fileList) {
        this.connectionSocket = connectionSocket;
        this.listHosts = listHosts;
        this.fileList = fileList;
    }

    public void run() throws RuntimeException {
        try {
            DataOutputStream outToClient = new DataOutputStream(connectionSocket.getOutputStream());
            BufferedReader inFromClient = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
            fromClient = inFromClient.readLine();

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
                HostConnection c = new HostConnection(host, speed, username);
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
                    searchResults.clear();
                    for (int i = 0; i < fileList.size(); i++) {
                        for (int j = 0; j < fileList.get(i).keywords.size(); j++) {
                            if (fileList.get(i).keywords.get(j).equals(searchWord)) {
                                searchResults.add(fileList.get(i));
                            }
                        }
                    }
                    String result = "";
                    System.out.println("\n\n");
                    for (HostFile file : searchResults) {
                        result += "Speed: " + file.speed + "\thostname: " + file.hostname + ":" + file.port
                                + "\tfilename: " + file.name + "\n";
                    }
                    System.out.println(result);
                    if (searchResults.size() > 0) {
                        outToClient.writeUTF(result);
                        outToClient.flush();
                    } else {
                        outToClient.writeUTF("No Files \n");
                        outToClient.flush();
                    }
                    dataSocket.close();
                }
                if (clientCommand.equals("quit")) {
                    Socket dataSocket = new Socket(connectionSocket.getInetAddress(), port);
                    for (int i = 0; i < listHosts.size(); i++) {
                        // find connection that has hostname user and delete
                        if (listHosts.get(i).hostname.equals(username)) {
                            System.out.println("removed connection");
                            listHosts.remove(i);
                        }
                    }
                    ArrayList<Integer> filesToDelete = new ArrayList<Integer>();
                    for (int i = 0; i < fileList.size(); i++) {
                        if (fileList.get(i).user.equals(username)) {
                            filesToDelete.add(i);
                        }
                    }
                    for (int i = filesToDelete.size() - 1; i > -1; i--) {
                        fileList.remove(filesToDelete.get(i).intValue());
                    }
                    outToClient.close();
                    inFromClient.close();
                    dataSocket.close();
                }
                if (clientCommand.equals("upload")) {
                    try {
                        Socket dataSocket = new Socket(connectionSocket.getInetAddress(), port);
                        DataInputStream inData = new DataInputStream(
                                new BufferedInputStream(dataSocket.getInputStream()));
                        // Get filename as the next token after upload. Should be <hostname>.xml;
                        String fileName = tokens.nextToken();
                        hostServerPort = tokens.nextToken();
                        System.out.println(fileName);
                        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                        DocumentBuilder builder = factory.newDocumentBuilder();

                        // Build Document
                        Document document = builder.parse(new File(fileName));

                        // Normalize the XML Structure
                        document.getDocumentElement().normalize();

                        // Root node
                        Element root = document.getDocumentElement();

                        NodeList nList = document.getElementsByTagName("file");

                        // Loop through XML elements
                        for (int temp = 0; temp < nList.getLength(); temp++) {
                            Node node = nList.item(temp);
                            if (node.getNodeType() == Node.ELEMENT_NODE) {
                                // Get each file name and description
                                Element eElement = (Element) node;
                                String name = eElement.getElementsByTagName("name").item(0).getTextContent();
                                String fileDesc = eElement.getElementsByTagName("description").item(0).getTextContent();
                                // Parse description into array of strings
                                String[] fileDescArr = fileDesc.split("\\W+");
                                // Turn string array into arraylist
                                List l = Arrays.asList(fileDescArr);
                                ArrayList<String> fileDescArrLst = new ArrayList<String>(l);
                                // Create file object
                                HostFile file = new HostFile(name, username, speed, host, hostServerPort,
                                        fileDescArrLst);
                                fileList.add(file);
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
