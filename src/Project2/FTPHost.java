import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.TimeUnit;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import javax.swing.*;

public class FTPHost extends Application {

    boolean connected;
    String serverHostName = new String();
    String hostName = new String();
    String myHostServerThreadPort;
    String username = new String();
    Socket clientControlSocket;
    int port1;
    boolean clientConnected = false;
    Thread thread;

    Scene scene = new Scene(new Group(), 900, 550, Color.SKYBLUE);
    Group root = (Group) scene.getRoot();

    Label serverHostNameLabel = new Label("Server Hostname: ");
    Label portLabel = new Label("Port: ");
    Label userNameLabel = new Label("Username: ");
    Label hostNameLabel = new Label("Hostname: ");
    Label speedLabel = new Label("Speed: ");
    Label searchLabel = new Label("Search: ");
    Label commandLabel = new Label("Enter Command: ");
    Label myHostServerPortLabel = new Label("Your hosting port: ");

    TextField serverHostNameTextField = new TextField();
    TextField portTextField = new TextField();
    TextField userNameTextField = new TextField();
    TextField hostNameTextField = new TextField();
    TextField searchTextField = new TextField();
    TextField commandTextField = new TextField();
    TextField myHostServerPortTextField = new TextField();

    ChoiceBox<String> speedChoice = new ChoiceBox<String>();

    Button connectButton = new Button("Connect");
    Button searchButton = new Button("Search");
    Button runCommandButton = new Button("Run Command");

    TextArea searchResultsTextArea = new TextArea();
    TextArea commandResultsTextArea = new TextArea();

    GridPane grid = new GridPane();

    @Override
    public void start(Stage primaryStage) throws Exception {
        initVars(primaryStage);
    }

    public void handleConnectAction(ActionEvent event) {
        try {
            // myHostServerThreadPort = ++globalPortNumber;
            myHostServerThreadPort = myHostServerPortTextField.getText();
            serverHostName = serverHostNameTextField.getText();
            hostName = hostNameTextField.getText();
            String username = userNameTextField.getText();
            port1 = Integer.parseInt(portTextField.getText());
            // notEnd = true;
            Socket ControlSocket = new Socket(serverHostName, port1);
            DataOutputStream outToServer = new DataOutputStream(ControlSocket.getOutputStream());

            commandResultsTextArea.setText("You are connected to " + serverHostName);

            // filename to store
            String fileName = username + ".xml";
            File toStore = new File("./" + fileName);

            outToServer.writeBytes(
                    userNameTextField.getText() + " " + port1 + " " + hostName + " " + speedChoice.getValue().toString()
                            + " " + "upload" + " " + username + ".xml" + " " + myHostServerThreadPort + "\n");
            // Check that it is a file
            if (toStore.isFile()) {
                // Find file length
                long length = toStore.length();
                byte[] bytes = new byte[16 * 1024];

                // Instantiate file and output sockets
                InputStream in = new FileInputStream(toStore);
                OutputStream out = ControlSocket.getOutputStream();

                // Write bytes from file to output
                int count;
                while ((count = in.read(bytes)) > 0) {
                    out.write(bytes, 0, count);
                }
                out.close();
                in.close();
            }
            connected = true;

            // Start server thread
            HostServer ftpServer = new HostServer(Integer.parseInt(myHostServerThreadPort), username);
            thread = new Thread(ftpServer);
            thread.start();

            System.out.println("FTP Server Opened");
            ControlSocket.close();
        } catch (Exception e) {
            commandResultsTextArea.setText("Something went wrong");
        }
    }

    public void search(ActionEvent event) {
        System.out.println("searching");
        try {
            // StringTokenizer tokens = new StringTokenizer(searchTextField.getText());
            serverHostName = serverHostNameTextField.getText();
            port1 = Integer.parseInt(portTextField.getText());
            String searchWord = searchTextField.getText();
            Socket ControlSocket = new Socket(serverHostName, port1);
            DataOutputStream outToServer = new DataOutputStream(ControlSocket.getOutputStream());
            DataInputStream inFromServer = new DataInputStream(new BufferedInputStream(ControlSocket.getInputStream()));
            outToServer.writeBytes(userNameTextField.getText() + " " + port1 + " " + this.hostNameTextField.getText()
                    + " " + speedChoice.getValue().toString() + " " + "search" + " " + searchWord + "\n");
            StringBuilder results = new StringBuilder(300);
            TimeUnit.MILLISECONDS.sleep(500);
            while (inFromServer.available() > 0) {
                results.append(inFromServer.readUTF());
                searchResultsTextArea.setText(results.toString());
                System.out.println(results.toString());
            }
            commandResultsTextArea.setText("Search successful");
            ControlSocket.close();
        } catch (Exception e) {
            commandResultsTextArea.setText("Something went wrong");
        }
    }

    public void runAction(ActionEvent event) {

        StringTokenizer tokens = new StringTokenizer(commandTextField.getText());
        String command = tokens.nextToken();

        System.out.println(commandTextField.getText());
        if (command.compareTo("quit") == 0) {
            if (clientConnected == true) {
                try {
                    DataOutputStream outToServer = new DataOutputStream(clientControlSocket.getOutputStream());
                    int port = Integer.parseInt(myHostServerThreadPort) + 20;
                    outToServer.writeUTF(port + " quit");
                    clientConnected = false;
                    clientControlSocket.close();
                    thread.interrupt();
                } catch (Exception e) {
                    commandResultsTextArea.appendText("\nSomething went wrong.");
                }
            } else
                commandResultsTextArea.appendText("\nNot currently connected.");
        }
        if (command.compareTo("connect") == 0) {
            // Check if client is already connected
            if (clientConnected == false) {
                try {
                    String serverIP = tokens.nextToken();
                    String serverPort = tokens.nextToken();
                    clientControlSocket = new Socket(serverIP, Integer.parseInt(serverPort));
                    clientConnected = true;
                } catch (Exception e) {
                    commandResultsTextArea.setText("Something went wrong");
                }
            } else {
                commandResultsTextArea.appendText("\nERROR: Already connected to another client");
            }
        }
        if (command.compareTo("retr") == 0) {
            if (!portTextField.getText().equals("12000")) {
                try {
                    username = userNameTextField.getText();
                    serverHostName = serverHostNameTextField.getText();
                    port1 = Integer.parseInt(portTextField.getText());
                    String fileName = tokens.nextToken();
                    Socket ControlSocket = new Socket(serverHostName, port1);
                    DataOutputStream outToServer = new DataOutputStream(ControlSocket.getOutputStream());
                    // write out so server filename and port we open serversocket on
                    int portToReceiveFile = (Integer.parseInt(myHostServerThreadPort) + 1);
                    System.out.println(fileName + " " + portToReceiveFile);
                    outToServer.writeBytes(fileName + " " + portToReceiveFile + "\n");
                    // new serversocket
                    ServerSocket fileListenerSocket = new ServerSocket(portToReceiveFile);
                    // accept dataSocket through fileListenerSocket
                    Socket fileListenerDataSocket = fileListenerSocket.accept();
                    // make Streams to get file on
                    DataInputStream fileIn = new DataInputStream(
                            new BufferedInputStream(fileListenerDataSocket.getInputStream()));
                    // check to make sure we have file
                    boolean fileExists = (fileIn.readUTF().compareTo("200") == 0);
                    // get the file
                    if (fileExists) {
                        // Receive file // filePath: 
                        System.out.println("./" + username + "/" + fileName);
                        OutputStream fileOut = new FileOutputStream("./" + username + "/" + fileName);
                        byte[] bytes = new byte[16 * 1024];
                        int count;
                        while ((count = fileIn.read(bytes)) > 0) {
                            fileOut.write(bytes, 0, count);
                        }
                        fileOut.close();
                    } else {
                        ControlSocket.close();
                        fileListenerSocket.close();
                        throw new Exception("File not found");
                    }
                    // finish stuff that we're done with
                    commandResultsTextArea.setText("File " + fileName + " received");
                    ControlSocket.close();
                    fileListenerSocket.close();
                } catch (Exception e) {
                    commandResultsTextArea.setText("Something went wrong");
                }
            }
            else {
                commandResultsTextArea.appendText("\nPlease change your port");
            }
        }
    }

    public static void main(String[] args) {
        Application.launch(args);
    }

    private void initVars(Stage primaryStage) {
        serverHostNameTextField.setText("localhost");
        portTextField.setText("12000");
        userNameTextField.setText("vinay");
        hostNameTextField.setText("DCCLIENT/localhost");
        myHostServerPortTextField.setText("12005");

        speedChoice.getItems().add("Ethernet");
        speedChoice.getItems().add("T1");
        speedChoice.valueProperty().set("Ethernet");

        primaryStage.setTitle("GV-P2P");
        primaryStage.initStyle(StageStyle.DECORATED);

        grid.setVgap(5);
        grid.setHgap(10);
        grid.setPadding(new Insets(5, 5, 5, 5));

        grid.add(serverHostNameLabel, 0, 0);
        grid.add(serverHostNameTextField, 1, 0);
        grid.add(userNameLabel, 2, 0);
        grid.add(userNameTextField, 3, 0);

        grid.add(portLabel, 0, 1);
        grid.add(portTextField, 1, 1);
        grid.add(hostNameLabel, 2, 1);
        grid.add(hostNameTextField, 3, 1);

        grid.add(myHostServerPortLabel, 2, 2);
        grid.add(myHostServerPortTextField, 3, 2);

        grid.add(connectButton, 1, 3);
        grid.add(speedLabel, 2, 3);
        grid.add(speedChoice, 3, 3);

        grid.add(searchLabel, 0, 4);
        grid.add(searchTextField, 1, 4);
        grid.add(searchButton, 2, 4);
        grid.add(searchResultsTextArea, 3, 4);

        grid.add(commandLabel, 0, 5);
        grid.add(commandTextField, 1, 5);
        grid.add(runCommandButton, 2, 5);
        grid.add(commandResultsTextArea, 3, 5);

        root.getChildren().add(grid);
        primaryStage.setScene(scene);
        primaryStage.show();

        connectButton.setOnAction(this::handleConnectAction);

        runCommandButton.setOnAction(this::runAction);

        searchButton.setOnAction(this::search);
    }

}
