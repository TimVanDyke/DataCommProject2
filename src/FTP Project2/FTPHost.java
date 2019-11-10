import java.io.*;
import java.net.*;
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

        TextField hostNameText = new TextField();
        TextField portText = new TextField();

        TextField userNameText = new TextField();
        TextField hostNameText2 = new TextField();
        TextField speedText = new TextField();

        TextField fileNameText = new TextField();
        TextField fileDescriptionText = new TextField();

        String serverName;
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

                Scene scene = new Scene(new Group(), 500, 250, Color.SKYBLUE);

                Button connectButton = new Button("Connect");
                Button sendButton = new Button("Send to Server");
                Button uploadButton = new Button("Upload to Server");
                Button runCommandButton = new Button("Run");

                hostNameText.setText("148.61.112.49");
                portText.setText("6531");
                userNameText.setText("vinay");
                hostNameText2.setText("DCCLIENT/148.61.112.49");
                speedText.setText("Ethernet");
                fileNameText.setText("test1.txt");
                fileDescriptionText.setText("pretty, beautiful, happy, place");

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
                serverName = hostNameText.getText();
                port1 = Integer.parseInt(portText.getText());
                notEnd = true;

                resultsTextArea.setText("You are connected to " + serverName);

        }

        public void sendToServerAction(ActionEvent event) {
                try {
                        Socket ControlSocket = new Socket(serverName, port1);

                        DataOutputStream outToServer = new DataOutputStream(ControlSocket.getOutputStream());

                        DataInputStream inFromServer = new DataInputStream(
                                        new BufferedInputStream(ControlSocket.getInputStream()));

                        String userName = userNameText.getText();
                        String hostName = hostNameText2.getText();

                        // SEND USERNAME AND HOSTNAME TO SERVER

                        resultsTextArea.setText(
                                        "You're username and hostname has been saved successfully to the server");
                } catch (Exception e) {
                        resultsTextArea.setText("Something went wrong");
                }

        }

        public void uploadToServerAction(ActionEvent event) {

                try {
                        Socket ControlSocket = new Socket(serverName, port1);

                        DataOutputStream outToServer = new DataOutputStream(ControlSocket.getOutputStream());

                        DataInputStream inFromServer = new DataInputStream(
                                        new BufferedInputStream(ControlSocket.getInputStream()));

                        String fileName = fileNameText.getText();
                        String fileDescription = fileDescriptionText.getText();
                        String speed = speedText.getText();

                        // UPLOAD FILE NAME, FILE DESCRIPTION AND SPEED TO SERVER
                        // Note: it should prompt for speedlink e.g Ethernet if it is the textfield is
                        // blank.

                        resultsTextArea.setText(
                                        "You're filename, file description and speed has been saved successfully to the server ");
                } catch (Exception e) {
                        resultsTextArea.setText("Something went wrong");
                }
        }

        public void runAction(ActionEvent event) {
                if (commandText.getText() == "quit" || commandText.getText() == "disconnect") {

                        try {
                                Socket ControlSocket = new Socket(serverName, port1);

                                DataOutputStream outToServer = new DataOutputStream(ControlSocket.getOutputStream());

                                DataInputStream inFromServer = new DataInputStream(
                                                new BufferedInputStream(ControlSocket.getInputStream()));

                                int port = port1 + 2;
                                outToServer.writeBytes(port + " " + commandText.getText() + " " + '\n');

                                isOpen = false;
                                clientgo = false;
                                ControlSocket.close();
                                resultsTextArea.setText("You have been disconnected from the server");
                        }

                        catch (Exception e) {
                                resultsTextArea.setText("Something went wrong");
                        }
                }
        }

        public static void main(String[] args) {
                Application.launch(args);

        }

}
