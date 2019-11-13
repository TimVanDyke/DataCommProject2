import java.io.*;
import java.net.*;
import java.util.*;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class FTPHost extends Application {

    ArrayList<HostFile> ourFiles = new ArrayList<HostFile>();
    boolean connected;

    TextField hostNameText = new TextField();
    TextField portText = new TextField();

    TextField userNameText = new TextField();
    TextField hostNameText2 = new TextField();
    TextField speedText = new TextField();

    TextField fileNameText = new TextField();
    TextField fileDescriptionText = new TextField();

    String hostName;
    int port1;
    boolean isOpen = true;
    boolean clientgo = true;
    boolean notEnd = true;

    TextField commandText = new TextField();
    TextArea resultsTextArea = new TextArea();

    @Override
    public void start(Stage primaryStage) throws Exception {

        primaryStage.setTitle("GV-P2P");
        primaryStage.initStyle(StageStyle.DECORATED);

        Scene scene = new Scene(new Group(), 1200, 350, Color.SKYBLUE);

        Button connectButton = new Button("Connect");
        Button sendButton = new Button("Send to Server");
        Button uploadButton = new Button("Upload to Server");
        Button runCommandButton = new Button("Run");

        hostNameText.setText("localhost");
        portText.setText("12000");
        userNameText.setText("vinay");
        hostNameText2.setText("DCCLIENT/localhost");
        speedText.setText("Ethernet");
        fileNameText.setText("test1.txt");
        fileDescriptionText.setText("pretty beautiful happy place");

        GridPane grid = new GridPane();
        grid.setVgap(5);
        grid.setHgap(10);
        grid.setPadding(new Insets(5, 5, 5, 5));
        grid.add(new Label("Server Hostname: "), 0, 0);
        grid.add(hostNameText, 1, 0);
        grid.add(new Label("Port: "), 2, 0);
        grid.add(portText, 3, 0);
        grid.add(connectButton, 6, 0);

        grid.add(new Label("Username: "), 0, 1);
        grid.add(userNameText, 1, 1);

        grid.add(new Label("Hostname: "), 2, 1);
        grid.add(hostNameText2, 3, 1);

        grid.add(new Label("Speed: "), 4, 1);
        grid.add(speedText, 5, 1);
        grid.add(sendButton, 6, 1);

        grid.add(new Label("File Name: "), 0, 2);
        grid.add(fileNameText, 1, 2);

        grid.add(new Label("File Description: "), 2, 2);
        grid.add(fileDescriptionText, 3, 2);
        grid.add(uploadButton, 6, 2);

        grid.add(new Label("Enter Command: "), 0, 5);
        grid.add(commandText, 1, 5);
        grid.add(runCommandButton, 2, 5);
        grid.add(resultsTextArea, 3, 5);

        Group root = (Group) scene.getRoot();
        root.getChildren().add(grid);

        primaryStage.setScene(scene);

        primaryStage.show();

        connectButton.setOnAction(this::handleConnectAction);
        sendButton.setOnAction(this::sendToServerAction);
        uploadButton.setOnAction(this::uploadToServerAction);
        runCommandButton.setOnAction(this::runAction);

    }

    public void handleConnectAction(ActionEvent event) {
        try {
            hostName = hostNameText.getText();
            port1 = Integer.parseInt(portText.getText());
            notEnd = true;
            Socket ControlSocket = new Socket(hostName, port1);
            DataOutputStream outToServer = new DataOutputStream(ControlSocket.getOutputStream());
            // DataInputStream inFromServer = new DataInputStream(new
            // BufferedInputStream(ControlSocket.getInputStream()));
            // send username to server
            outToServer.writeBytes(userNameText.getText() + " " + port1 + " " + hostName + " " + speedText.getText()
                    + " " + "connect" + "\n");
            resultsTextArea.setText("You are connected to " + hostName);
            connected = true;
            ControlSocket.close();
        } catch (Exception e) {
            resultsTextArea.setText("Something went wrong");
        }
    }

    public void sendToServerAction(ActionEvent event) {
        try {
            Socket ControlSocket = new Socket(hostName, port1);
            DataOutputStream outToServer = new DataOutputStream(ControlSocket.getOutputStream());
            DataInputStream inFromServer = new DataInputStream(new BufferedInputStream(ControlSocket.getInputStream()));
            // SEND USERNAME AND HOSTNAME TO SERVER
            outToServer.writeBytes(userNameText.getText() + " " + port1 + " " + hostName + " " + speedText.getText()
                    + " " + "connect" + "\n");
            resultsTextArea.setText("Your username and hostname has been saved successfully to the server");
            ControlSocket.close();
        } catch (Exception e) {
            resultsTextArea.setText("Something went wrong");
        }

    }

    public void uploadToServerAction(ActionEvent event) {

        try {
            Socket ControlSocket = new Socket(hostName, port1);
            DataOutputStream outToServer = new DataOutputStream(ControlSocket.getOutputStream());
            ObjectOutputStream outFile = new ObjectOutputStream(ControlSocket.getOutputStream());
            ArrayList<String> fileDescription = new ArrayList<String>();
            String s = "";
            String description = fileDescriptionText.getText();
            String delims = "[ ]";
            String[] tokens = description.split(delims);
            for (int i = 0; i < tokens.length; i++) {
                // fileDescription.add(tokens[i]);
                s += " " + tokens[i];
            }
            HostFile payload = new HostFile(fileNameText.getText(), hostNameText2.getText(), fileDescription);
            outToServer.writeBytes(userNameText.getText() + " " + port1 + " " + hostName + " " + speedText.getText()
                    + " " + "upload" + s + " " + fileNameText.getText() + "\n");
            // outFile.writeObject(payload);

            // Note: it should prompt for speedlink e.g Ethernet if it is the textfield is
            // blank.
            resultsTextArea
                    .setText("Your filename, file description and speed has been saved successfully to the server ");
            ControlSocket.close();
        } catch (Exception e) {
            resultsTextArea.setText("Something went wrong");
        }
    }

    public void runAction(ActionEvent event) {
        System.out.println(commandText.getText());
        if (commandText.getText().equals("quit")) {

            try {
                int port = port1 + 2;
                Socket ControlSocket = new Socket(hostName, port1);
                DataOutputStream outToServer = new DataOutputStream(ControlSocket.getOutputStream());
                DataInputStream inFromServer = new DataInputStream(
                        new BufferedInputStream(ControlSocket.getInputStream()));

                outToServer.writeBytes(userNameText.getText() + " " + port1 + " " + hostName + " " + speedText.getText()
                        + " " + "quit" + "\n");

                isOpen = false;
                clientgo = false;
                ControlSocket.close();
                resultsTextArea.setText("You have been disconnected from the server");
                connected = false;
            }

            catch (Exception e) {
                resultsTextArea.setText("Something went wrong");
            }
        }
        if (commandText.getText().equals("retr")) {
            try {
                int port = port1 + 2;
                ServerSocket welcomeData = new ServerSocket(port);
                Socket dataSocket = welcomeData.accept();
                DataInputStream inData = new DataInputStream(new BufferedInputStream(dataSocket.getInputStream()));
                DataOutputStream outData = new DataOutputStream(dataSocket.getOutputStream());
                outData.writeBytes(userNameText.getText() + " " + port1 + " " + hostName + " " + speedText.getText()
                        + " " + "retr" + "\n");
                // Parse filename from command, send it to server
                String fileName = fileNameText.getText();
                outData.writeUTF(fileName);
                boolean fileExists = (inData.readUTF().compareTo("200") == 0);
                // Check file exists
                if (fileExists) {
                    // Receive file
                    // FIXME make sure filepath is correct!
                    OutputStream fileOut = new FileOutputStream("../client_data/" + fileName);
                    byte[] bytes = new byte[16 * 1024];
                    int count;
                    while ((count = inData.read(bytes)) > 0) {
                        fileOut.write(bytes, 0, count);
                    }
                    fileOut.close();
                }
                // If file does not exist, print error.
                else {
                    System.out.println("File " + fileName + " not found.");
                    welcomeData.close();
                    dataSocket.close();
                    outData.close();
                    inData.close();
                }
            } catch (Exception e) {
                resultsTextArea.setText("Something went wrong");
            }
        }

        if (commandText.getText().contains("search")) {
            if (connected) {
                try {
                    StringTokenizer tokens = new StringTokenizer(commandText.getText());
                    String searchWord = tokens.nextToken();
                    searchWord = tokens.nextToken();
                    int port = port1 + 2;
                    ServerSocket welcomeData = new ServerSocket(port);
                    //Socket dataSocket = welcomeData.accept();
                    Socket ControlSocket = new Socket(hostName, port1);
                    DataOutputStream outToServer = new DataOutputStream(ControlSocket.getOutputStream());
                    DataInputStream inFromServer = new DataInputStream(
                            new BufferedInputStream(ControlSocket.getInputStream()));
                    // notEnd = true;
                            String results;
                    String resultText = "";
                    System.out.print(userNameText.getText() + " " + port1 + " " + hostName + " " + speedText.getText()
                            + " " + "search " + searchWord + "\n");
                    outToServer.writeBytes(userNameText.getText() + " " + port1 + " " + hostName + " "
                            + speedText.getText() + " " + "search " + searchWord + "\n");
                    // Server will send 'eof' when it is done sending files
                    
                    while (inFromServer.available()>0) {
                        results = inFromServer.read;
                        resultText += results + "\n";
                    }
                    // while (notEnd) {
                    //     if (inFromServer.readUTF().endsWith("eof")) {
                            // notEnd = false;
                    //     }
                    //     else {
                    //         results = inFromServer.readUTF();
                    //         resultText += results + "\n";
                    //     }
                    // }
                    resultsTextArea.setText(resultText);
                    welcomeData.close();
                    //dataSocket.close();
                    inFromServer.close();
                    outToServer.close();
                    ControlSocket.close();
                } catch (Exception e) {

                }
            } else {
                resultsTextArea.setText("Connect to the server first!");
            }
        }

    }

    // build a file
    private void buildFile() {

    }

    // send server info
    private void userRegister() {

    }

    private void search() {

    }

    public static void main(String[] args) {
        Application.launch(args);

    }

}
