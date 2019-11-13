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
import javafx.scene.control.ChoiceBox;
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
    String serverHostName = new String();
    String hostName = new String();
    int port1;
    
    boolean isOpen = true;
    boolean clientgo = true;
    boolean notEnd = true;
    Scene scene = new Scene(new Group(), 900, 500, Color.SKYBLUE);
    Group root = (Group) scene.getRoot();

    Label serverHostNameLabel = new Label("Server Hostname: ");
    Label portLabel = new Label("Port: ");
    Label userNameLabel = new Label("Username: ");
    Label hostNameLabel = new Label("Hostname: ");
    Label speedLabel = new Label("Speed: ");
    Label searchLabel = new Label("Search: ");
    Label commandLabel = new Label("Enter Command: ");

    TextField serverHostNameTextField = new TextField();
    TextField portTextField = new TextField();
    TextField userNameTextField = new TextField();
    TextField hostNameTextField = new TextField();
    TextField searchTextField = new TextField();
    TextField commandTextField = new TextField();
    
    ChoiceBox<String> speedChoice = new ChoiceBox<String>();
    
    Button connectButton = new Button("Connect");
    Button searchButton = new Button("Search");
    Button runCommandButton = new Button("Run Command");
    
    TextArea searchResultsTextArea = new TextArea();
    TextArea commandResultsTextArea = new TextArea();
    
    GridPane grid = new GridPane();
    
    @Override
    public void start(Stage primaryStage) throws Exception {
        serverHostNameTextField.setText("localhost");
        portTextField.setText("12000");
        userNameTextField.setText("vinay");
        hostNameTextField.setText("DCCLIENT/localhost");
        
        speedChoice.getItems().add("Ethernet");
        speedChoice.getItems().add("T1");
        
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
        
        grid.add(connectButton, 1, 2);
        grid.add(speedLabel, 2, 2);
        grid.add(speedChoice, 3, 2);

        grid.add(searchLabel, 0, 3);
        grid.add(searchTextField, 1, 3);
        grid.add(searchButton, 2, 3);
        grid.add(searchResultsTextArea, 3, 3);

        grid.add(commandLabel, 0, 4);
        grid.add(commandTextField, 1, 4);
        grid.add(runCommandButton, 2, 4);
        grid.add(commandResultsTextArea, 3, 4);
        
        root.getChildren().add(grid);
        primaryStage.setScene(scene);
        primaryStage.show();

        connectButton.setOnAction(this::handleConnectAction);
        runCommandButton.setOnAction(this::runAction);
        searchButton.setOnAction(this::search);

    }

    public void search(ActionEvent event) {
        System.out.println("REE");
    }

    public void handleConnectAction(ActionEvent event) {
        try {
            hostName = serverHostNameTextField.getText();
            port1 = Integer.parseInt(portTextField.getText());
            notEnd = true;
            Socket ControlSocket = new Socket(hostName, port1);
            DataOutputStream outToServer = new DataOutputStream(ControlSocket.getOutputStream());
            // DataInputStream inFromServer = new DataInputStream(new
            // BufferedInputStream(ControlSocket.getInputStream()));
            // send username to server
            outToServer.writeBytes(userNameTextField.getText() + " " + port1 + " " + hostName + " " + speedChoice.getValue().toString()
                    + " " + "connect" + "\n");
            outToServer.flush();
            commandResultsTextArea.setText("You are connected to " + hostName);
            connected = true;


            //TODO: TEST FILE UPLOAD

            // filename to store
            String fileName = hostName + ".xml";
            File toStore = new File("./"+fileName);

            outToServer.writeBytes(userNameTextField.getText() + " " + port1 + " " + hostName + " " + speedChoice.getValue().toString()
                    + " " + "upload" + hostName + ".xml" + "\n");
            // Check that it is a file
            if(toStore.isFile()) {
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
            ControlSocket.close();
        } catch (Exception e) {
            commandResultsTextArea.setText("Something went wrong");
        }
    }

    public void sendToServerAction(ActionEvent event) {
        try {
            Socket ControlSocket = new Socket(hostName, port1);
            DataOutputStream outToServer = new DataOutputStream(ControlSocket.getOutputStream());
            DataInputStream inFromServer = new DataInputStream(new BufferedInputStream(ControlSocket.getInputStream()));
            // SEND USERNAME AND HOSTNAME TO SERVER
            outToServer.writeBytes(userNameTextField.getText() + " " + port1 + " " + hostName + " " + (String) speedChoice.getValue()
                    + " " + "connect" + "\n");
            commandResultsTextArea.setText("Your username and hostname has been saved successfully to the server");
            ControlSocket.close();
        } catch (Exception e) {
            commandResultsTextArea.setText("Something went wrong");
        }

    }

    public void uploadToServerAction(ActionEvent event) {

        try {
            Socket ControlSocket = new Socket(hostName, port1);
            DataOutputStream outToServer = new DataOutputStream(ControlSocket.getOutputStream());
            ObjectOutputStream outFile = new ObjectOutputStream(ControlSocket.getOutputStream());
            ArrayList<String> fileDescription = new ArrayList<String>();
            String s = "";
            String description = "FIXME REMOVE THIS ";
            String delims = "[ ]";
            String[] tokens = description.split(delims);
            for (int i = 0; i < tokens.length; i++) {
                // fileDescription.add(tokens[i]);
                s += " " + tokens[i];
            }
            outToServer.flush();
            //HostFile payload = new HostFile(fileNameText.getText(), hostNameText2.getText(), fileDescription);
            String str = userNameTextField.getText();
            System.out.print(str);
            outToServer.writeBytes(str + " " + port1 + " " + hostName + " " + (String) speedChoice.getValue()
                    + " " + "upload" + s + " " + "FIXME THIS NEEDS TO BE REMOVED (Was filenametextfield)" + "\n");
            // outFile.writeObject(payload);

            // Note: it should prompt for speedlink e.g Ethernet if it is the textfield is
            // blank.
            commandResultsTextArea
                    .setText("Your filename, file description and speed has been saved successfully to the server ");
            ControlSocket.close();
        } catch (Exception e) {
            commandResultsTextArea.setText("Something went wrong");
        }
    }

    public void runAction(ActionEvent event) {
        System.out.println(commandTextField.getText());
        if (commandTextField.getText().equals("quit")) {

            try {
                int port = port1 + 2;
                Socket ControlSocket = new Socket(hostName, port1);
                DataOutputStream outToServer = new DataOutputStream(ControlSocket.getOutputStream());
                DataInputStream inFromServer = new DataInputStream(
                        new BufferedInputStream(ControlSocket.getInputStream()));

                outToServer.writeBytes(userNameTextField.getText() + " " + port1 + " " + hostName + " " + (String) speedChoice.getValue()
                        + " " + "quit" + "\n");

                isOpen = false;
                clientgo = false;
                ControlSocket.close();
                commandResultsTextArea.setText("You have been disconnected from the server");
                connected = false;
            }

            catch (Exception e) {
                commandResultsTextArea.setText("Something went wrong");
            }
        }
        if (commandTextField.getText().equals("retr")) {
            try {
                int port = port1 + 2;
                ServerSocket welcomeData = new ServerSocket(port);
                Socket dataSocket = welcomeData.accept();
                DataInputStream inData = new DataInputStream(new BufferedInputStream(dataSocket.getInputStream()));
                DataOutputStream outData = new DataOutputStream(dataSocket.getOutputStream());
                outData.writeBytes(userNameTextField.getText() + " " + port1 + " " + hostName + " " + (String) speedChoice.getValue()
                        + " " + "retr" + "\n");
                // Parse filename from command, send it to server
                String fileName = "FIXME WE DON't NEED THIS ANYMORE";
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
                commandResultsTextArea.setText("Something went wrong");
            }
        }

        if (commandTextField.getText().contains("search")) {
            // if (connected) {
                try {
                    StringTokenizer tokens = new StringTokenizer(commandTextField.getText());
                    String searchWord = tokens.nextToken();
                    searchWord = tokens.nextToken();
                    int port = port1 + 2;
                    ServerSocket welcomeData = new ServerSocket(port);
                    // Socket dataSocket = welcomeData.accept();
                    Socket ControlSocket = new Socket(hostName, port1);
                    DataOutputStream outToServer = new DataOutputStream(ControlSocket.getOutputStream());
                    DataInputStream inFromServer = new DataInputStream(
                            new BufferedInputStream(ControlSocket.getInputStream()));
                    // notEnd = true;
                    StringBuilder results = new StringBuilder(300);
                    //String resultText = "";
                    System.out.println(userNameTextField.getText() + " " + port1 + " " + hostName + " " + (String) speedChoice.getValue()
                            + " " + "search " + searchWord + "\n");
                    outToServer.writeBytes(userNameTextField.getText() + " " + port1 + " " + hostName + " "
                            + (String) speedChoice.getValue() + " " + "search " + searchWord + "\n");
                    // Server will send 'eof' when it is done sending files

                    //while (inFromServer.available() > 0) {
                    while (true) {
                        results.append(inFromServer.readUTF());
                        searchResultsTextArea.setText(results.toString());
                        System.out.println(results.toString());
                        hostNameTextField.setText("Your Mom Gay");
                        //resultText += results + "\n";
                    }
                    //results.append(" " + "\n");
                    // System.out.println(results);
                    // String resultText = results.toString();
                    // // while (notEnd) {
                    // // if (inFromServer.readUTF().endsWith("eof")) {
                    // // notEnd = false;
                    // // }
                    // // else {
                    // // results = inFromServer.readUTF();
                    // // resultText += results + "\n";
                    // // }
                    // // }
                    // resultsTextArea.setText(resultText);
                    // welcomeData.close();
                    // // dataSocket.close();
                    // inFromServer.close();
                    // outToServer.close();
                    // ControlSocket.close();
                } catch (Exception e) {

                }
            // } else {
            //     resultsTextArea.setText("Connect to the server first!");
            // }
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
