package sample;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        //Parent root = FXMLLoader.load(getClass().getResource("sample.fxml"));
        primaryStage.setTitle("File Sharer");
        BorderPane layout=new BorderPane();

        GridPane buttons=new GridPane();
        Button upload=new Button("Upload");

        Button download=new Button("Download");
        buttons.add(upload,0,0);
        buttons.add(download,1,0);

        TableView tableClient=new TableView();
        TableColumn<String, String> clientColumn=new TableColumn<>();
        clientColumn.setMinWidth(400);
        tableClient.getColumns().add(clientColumn);

        TableView tableServer=new TableView();
        TableColumn<String, String> serverColumn=new TableColumn<>();
        serverColumn.setMinWidth(400);
        tableServer.getColumns().add(serverColumn);

        layout.setTop(buttons);
        layout.setLeft(tableClient);
        layout.setRight(tableServer);

        primaryStage.setScene(new Scene(layout, 800, 600));
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
