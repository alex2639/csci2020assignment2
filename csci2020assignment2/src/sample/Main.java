package sample;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.io.*;

public class Main extends Application {
    private static String name, directory;

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
        TableColumn<TestFile, String> clientColumn=new TableColumn<>();
        clientColumn.setMinWidth(400);
        clientColumn.setCellValueFactory(new PropertyValueFactory<>("filename"));
        tableClient.getColumns().add(clientColumn);

        File clientFile=new File("./"+directory);

        ObservableList<TestFile> testFiles= FXCollections.observableArrayList();

        for(File entryFile:clientFile.listFiles()){
            TestFile testFile=new TestFile(entryFile, entryFile.getName());
            testFiles.add(testFile);
            tableClient.getItems().add(testFile);
        }


        TableView tableServer=new TableView();
        TableColumn<TestFile, String> serverColumn=new TableColumn<>();
        serverColumn.setMinWidth(400);
        tableServer.getColumns().add(serverColumn);

        layout.setTop(buttons);
        layout.setLeft(tableClient);
        layout.setRight(tableServer);

        primaryStage.setScene(new Scene(layout, 800, 600));
        primaryStage.show();
    }

    public static void main(String[] args) {
        //In the settings, the program arguments are set to 100548033 and clientFiles
        if(args.length!=2){
            System.out.println("Incorrect amount of arguments. <computer name> <directory> expected");
            System.exit(0);
        }
        name=args[0];
        directory=args[1];

        launch(args);
    }
}
