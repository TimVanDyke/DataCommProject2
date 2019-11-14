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
    ArrayList<HostConnection> listHosts;
    String username;
    String host;
    String speed;
    ArrayList<String> searchResults = new ArrayList<String>();
    boolean threadNotDone = true;
    ArrayList<HostFile> listfiles = new ArrayList<HostFile>();

    ServerWorkerThread(Socket connectionSocket, ArrayList<HostConnection> listHosts) {
        this.connectionSocket = connectionSocket;
        this.listHosts = listHosts;
    }

    public void run() throws RuntimeException {
        // while (threadNotDone) {
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
                    System.out.println("addhost");
                    listHosts.add(c);
                }

                // Socket dataSocket = new Socket(connectionSocket.getInetAddress(), port);
                if (clientCommand.equals("search")) {
                    String searchWord = tokens.nextToken();
                    Socket dataSocket = new Socket(connectionSocket.getInetAddress(), port);
                    //DataOutputStream outWord = new DataOutputStream(dataSocket.getOutputStream());
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
                    String result = "";
                    System.out.println("Before WriteUTF");
                    if (searchResults.size() > 0) {
                        for (int i = 0; i < searchResults.size(); i++) {
                            // outWord.writeUTF(searchResults.get(i));
                            // outWord.flush();
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
                    threadNotDone = false;
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
                    System.out.println("bye");
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
                        //fileName = "tmp_" + fileName;
                        // Receive file
                        // OutputStream fileOut = new FileOutputStream("./"+fileName);
                        // byte[] bytes = new byte[16 * 1024];
                        // int count;

                        // // Write data to file
                        // while ((count = inData.read(bytes)) > 0) {
                        //     fileOut.write(bytes, 0, count);
                        // }
                        // fileOut.close();

                        //TODO: Parse the .xml file saved as "tmp_" + fileName

                        //Code taken from https://howtodoinjava.com/xml/read-xml-dom-parser-example/
                        //Get Document Builder
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
                            System.out.println("Inside loop thru xml");
                            Node node = nList.item(temp);
                            //System.out.println("");
                            // System.out.println(node.getNodeType());
                            // System.out.println(Node.ELEMENT_NODE);
                            if (node.getNodeType() == Node.ELEMENT_NODE)
                            {
                                //Get each file name and description
                                Element eElement = (Element) node;
                                String name = eElement.getElementsByTagName("name").item(0).getTextContent();
                                String fileDesc = eElement.getElementsByTagName("description").item(0).getTextContent();
                                System.out.println(fileDesc);
                                //Parse description into array of strings
                                String[] fileDescArr = fileDesc.split("\\W+");
                                System.out.println(fileDescArr[0]);
                                //Turn string array into arraylist
                                List l = Arrays.asList(fileDescArr);
                                ArrayList<String> fileDescArrLst = new ArrayList<String>(l);
                                // ArrayList<String> fileDescArrLst = new ArrayList<String>();
                                // fileDescArrLst = (ArrayList<String>) Arrays.asList(fileDescArr);
                                
                                //Create file object
                                HostFile file = new HostFile(name, username, fileDescArrLst);
                                listfiles.add(file);
                                System.out.println("file uploaded");
                                System.out.println(file.getName());
                                //If files are new to server, add to file list
                                //TODO: Is this necessary?
                                // for (int i = 0; i < listHosts.size(); i++) {
                                //     if (listHosts.get(i).hostname.equals(c.hostname)
                                //             && listHosts.get(i).username.equals(c.username)) {
                                //         listHosts.get(i).fileList.add(f);
                                //         System.out.println("File uploaded");
                                //     }
                                //  }
                            }
                        }

                        ///////////////////////////////////////////////////////////////////////////
                        // Socket dataSocket = new Socket(connectionSocket.getInetAddress(), port);
//                        ArrayList<String> fileDescription = new ArrayList<String>();
//                        String description = tokens.nextToken();
//                        String filename = tokens.nextToken();
//                        String[] tokens1 = description.split(",");
//                        for (int i = 0; i < tokens1.length; i++) {
//                            fileDescription.add(tokens1[i]);
//                        }
//                        // System.out.println(filename);
//                        // System.out.println(username);
//                        HostFile f = new HostFile(filename, username, fileDescription);
//                        // f = (HostFile) dataFromClient.readObject();
//                        for (int i = 0; i < listHosts.size(); i++) {
//                            if (listHosts.get(i).hostname.equals(c.hostname)
//                                    && listHosts.get(i).username.equals(c.username)) {
//                                // System.out.println(listHosts.get(i).fileList.size());
//                                listHosts.get(i).fileList.add(f);
//                                // System.out.println(listHosts.get(i).fileList.size());
//                                // System.out.println(listHosts.get(i).fileList.get(i).getName());
//                                // System.out.println(listHosts.get(i).fileList.get(i).getUser());
//                                System.out.println("File uploaded");
//                            }
//                        }
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
        // }
        System.out.println("ThreadEnded");
    }
}
