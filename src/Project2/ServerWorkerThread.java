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
    ArrayList<HostConnection> listHosts;
    ArrayList<HostFile> fileList;
    ArrayList<String> searchResults = new ArrayList<String>();

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
                    System.out.println(searchWord);
                    Socket dataSocket = new Socket(connectionSocket.getInetAddress(), port);
                    DataOutputStream outWord = new DataOutputStream(dataSocket.getOutputStream());
                    searchResults.clear();
                    for (int i = 0; i < fileList.size(); i++) {
                        for (int j = 0; j < fileList.get(i).keywords.size(); j++) {
                            if (fileList.get(i).keywords.get(j).equals(searchWord)) {
                                searchResults.add(fileList.get(i).name);
                            }
                        }
                    }
                    String result = "";
                    if (searchResults.size() > 0) {
                        for (int i = 0; i < searchResults.size(); i++) {
                            outToClient.writeUTF(searchResults.get(i));
                            outToClient.flush();
                            result += (searchResults.get(i) + " ");
                            // System.out.println(result);
                            outToClient.writeUTF(result + "\n");
                        }
                    } else {
                        System.out.println("Sorry there are no files with that keyword");
                        outToClient.writeUTF("No Files \n");
                        outToClient.flush();
                    }
                    System.out.println("After WriteUTF");
                    dataSocket.close();
                }
                if (clientCommand.equals("quit")) {
                    Socket dataSocket = new Socket(connectionSocket.getInetAddress(), port);
                    for (int i = 0; i < listHosts.size(); i++) {
                        // find connection that has hostname user and delete
                        if (listHosts.get(i).hostname == host) {
                            System.out.println("removed connection");
                            listHosts.remove(i);
                        }
                    }
                    outToClient.close();
                    inFromClient.close();
                    dataSocket.close();
                }
                if (clientCommand.equals("upload")) {
                    try {
                        System.out.println("upload");
                        Socket dataSocket = new Socket(connectionSocket.getInetAddress(), port);
                        DataInputStream inData = new DataInputStream(
                                new BufferedInputStream(dataSocket.getInputStream()));
                        // Get filename as the next token after upload. Should be <hostname>.xml;
                        String fileName = tokens.nextToken();
                        System.out.println(fileName);
                        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                        DocumentBuilder builder = factory.newDocumentBuilder();

                        //Build Document
                        Document document = builder.parse(new File(fileName));

                        //Normalize the XML Structure
                        document.getDocumentElement().normalize();

                        //Root node
                        Element root = document.getDocumentElement();

                        NodeList nList = document.getElementsByTagName("file");
                        
                        //Loop through XML elements
                        for (int temp = 0; temp < nList.getLength(); temp++)
                        {
                            Node node = nList.item(temp);
                            if (node.getNodeType() == Node.ELEMENT_NODE)
                            {
                                //Get each file name and description
                                Element eElement = (Element) node;
                                String name = eElement.getElementsByTagName("name").item(0).getTextContent();
                                String fileDesc = eElement.getElementsByTagName("description").item(0).getTextContent();
                                //Parse description into array of strings
                                String[] fileDescArr = fileDesc.split("\\W+");
                                //Turn string array into arraylist
                                List l = Arrays.asList(fileDescArr);
                                ArrayList<String> fileDescArrLst = new ArrayList<String>(l);

                                //Create file object
                                HostFile file = new HostFile(name, username, fileDescArrLst);
                                fileList.add(file);
                                System.out.println("file uploaded");
                                System.out.println(file.getName());
                            }
                        }
                        dataSocket.close();
                        //Delete temp file
                        //File file = new File("./"+fileName);
                    } catch (Exception e) {

                    }

                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("ThreadEnded");
    }
}
