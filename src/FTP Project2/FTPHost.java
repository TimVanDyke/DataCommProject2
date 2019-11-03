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

    @Override
    public void start(Stage primaryStage) throws Exception {

        primaryStage.setTitle("GV-P2P");
        primaryStage.initStyle(StageStyle.DECORATED);
        
        Scene scene = new Scene (new Group(), 500, 250, Color.SKYBLUE);

        TextField hostNameText = new TextField();
        TextField portText = new TextField();
        
        TextField userNameText = new TextField();
        TextField hostNameText2 = new TextField();
        TextField speedText = new TextField();
        
        TextField fileNameText = new TextField();
        TextField fileDescriptionText = new TextField();
        
        
        TextField commandText = new TextField();
        TextArea resultsTextArea = new TextArea(); 
        
      Button connectButton = new Button("Connect");
      Button sendButton = new Button("Send to Server");
      Button uploadButton = new Button ("Upload to Server");
      Button runCommandButton = new Button ("Run");
      
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
        grid.setPadding(new Insets(5,5,5,5));
        grid.add(new Label("Server Hostname: "), 0, 0);
        grid.add(hostNameText, 1, 0);
        grid.add(new Label("Port: "), 2, 0);
        grid.add(portText,3, 0);
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
    }

    public static void main(String[] args) {
        Application.launch(args);
    }
}
