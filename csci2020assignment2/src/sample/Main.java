package sample;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.io.*;
import java.net.*;
import java.util.Arrays;

public class Main extends Application{
    private static String name, directory;
    private int bufferSize=1024;

    @Override
    public void start(Stage primaryStage) throws Exception{

        primaryStage.setTitle("File Sharer");
        BorderPane layout=new BorderPane();
        //directory="clientFiles";
        File clientFile=new File("./"+directory);

        TableView tableClient=new TableView();
        TableColumn<TestFile, String> clientColumn=new TableColumn<>();
        clientColumn.setMinWidth(400);
        clientColumn.setCellValueFactory(new PropertyValueFactory<>("filename"));
        tableClient.getColumns().add(clientColumn);

        ObservableList<TestFile> testFiles= FXCollections.observableArrayList();

        for(File entryFile:clientFile.listFiles()){
            TestFile testFile=new TestFile(entryFile, entryFile.getName());
            testFiles.add(testFile);
            tableClient.getItems().add(testFile);
        }

        TableView tableServer=new TableView();
        TableColumn<TestFile, String> serverColumn=new TableColumn<>();
        serverColumn.setMinWidth(400);
        serverColumn.setCellValueFactory(new PropertyValueFactory<>("filename"));
        tableServer.getColumns().add(serverColumn);

        ObservableList<TestFile> serverFiles=FXCollections.observableArrayList();


        GridPane buttons=new GridPane();
        Button upload=new Button("Upload");
        upload.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                try{
                    Socket socket=new Socket("localhost",8080);
                    InputStream is=socket.getInputStream();
                    OutputStream os=socket.getOutputStream();
                    for(File entryFile:clientFile.listFiles()){
                        FileInputStream fis=new FileInputStream(entryFile);
                        byte[] buffer=new byte[bufferSize];
                        Integer bytesRead=0;
                        while ((bytesRead=fis.read(buffer))>0){//read from file
                            os.write(bytesRead);
                            os.write(Arrays.copyOf(buffer, buffer.length));
                        }
                        TestFile serverFile=new TestFile(entryFile, entryFile.getName());
                        serverFiles.add(serverFile);//add file to server table

                    }
                    //remove contents from client folder
                    testFiles.removeAll(testFiles);
                    //close down everything when done
                    os.flush();
                    is.close();
                    socket.close();

                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });

        Button download=new Button("Download");
        download.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {

                try{
                    ServerSocket serverSocket=new ServerSocket(8080);
                    for(int i=0;i<serverFiles.size();i++){
                        testFiles.add(serverFiles.get(i));
                        while(true){
                            Socket socket=serverSocket.accept();
                            //DownloadFiles downloadFile=new DownloadFiles(serverFiles.get(i).getFile());
                            OutputStream os=socket.getOutputStream();
                            InputStream is=socket.getInputStream();
                            FileOutputStream fos=new FileOutputStream(is.toString());
                            byte[] buffer=new byte[bufferSize];
                            Integer bytesRead=0;
                            do{
                                
                            }while(bytesRead==bufferSize);

                        }
                    }


                }catch(Exception e){
                    e.printStackTrace();
                }
                serverFiles.removeAll(serverFiles);
            }
        });

        buttons.add(upload,0,0);
        buttons.add(download,1,0);

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
