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

        tableClient.setEditable(true);

        File serverFile=new File("./serverFiles");

        TableView tableServer=new TableView();
        TableColumn<TestFile, String> serverColumn=new TableColumn<>();
        serverColumn.setMinWidth(400);
        serverColumn.setCellValueFactory(new PropertyValueFactory<>("filename"));
        tableServer.getColumns().add(serverColumn);

        ObservableList<TestFile> serverFiles=FXCollections.observableArrayList();

        for(File entryFile:serverFile.listFiles()){
            TestFile testFile=new TestFile(entryFile, entryFile.getName());
            serverFiles.add(testFile);
            tableServer.getItems().add(testFile);
        }

        tableServer.setEditable(true);

        GridPane buttons=new GridPane();
        Button upload=new Button("Upload");
        upload.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                try{
                    byte[] buffer = new byte[1];
                    int bytesRead;
                    Socket socket=new Socket("localhost",8080);
                    InputStream is=socket.getInputStream();
                    ByteArrayOutputStream baos=new ByteArrayOutputStream();
                    if (is!=null){
                        FileOutputStream fos=null;
                        BufferedOutputStream bos=null;
                        for (File entryFile:clientFile.listFiles()){
                            fos=new FileOutputStream(entryFile);
                            bos=new BufferedOutputStream(fos);
                            bytesRead=is.read(buffer,0,buffer.length);
                            do{
                                baos.write(buffer);
                                bytesRead=is.read(buffer);
                            }while(bytesRead!=-1);
                            bos.write(baos.toByteArray());
                            System.out.println("File: "+entryFile.getName()+" sent");

                            TestFile testFile=new TestFile(entryFile, entryFile.getName());
                            tableServer.getItems().add(testFile);
                            serverFiles.add(testFile);
                            entryFile=new File("./serverFiles/"+entryFile.getName());//move the file

                        }

                        //close down everything when done
                        bos.flush();
                        is.close();
                        socket.close();

                        //remove contents from client folder
                        tableClient.getItems().removeAll(testFiles);
                        testFiles.removeAll(testFiles);

                    }


                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });

        Button download=new Button("Download");
        download.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {

                int bytesRead=0;
                int current=0;
                FileOutputStream fos=null;
                BufferedOutputStream bos=null;
                Socket socket=null;
                //File clientFiles=new File("./serverFiles");
                try{
                    socket=new Socket("localhost",8080);
                    System.out.println("Connecting...");

                    //receive file
                    for(File entryFile:serverFile.listFiles()){
                        TestFile testFile=new TestFile(entryFile,entryFile.getName());
                        testFiles.add(testFile);
                        tableClient.getItems().add(testFile);
                        //serverFiles.remove(testFile);
                        current=0;
                        bytesRead=0;
                        byte[] buffer=new byte[6022386];
                        InputStream is=socket.getInputStream();
                        fos=new FileOutputStream(entryFile);
                        bos=new BufferedOutputStream(fos);
                        bytesRead=is.read(buffer,0,buffer.length);
                        current=bytesRead;

                        do{
                            bytesRead=is.read(buffer,current,buffer.length-current);
                            if(bytesRead>=0) current+=bytesRead;

                        }while(bytesRead>-1);
                        bos.write(buffer,0,buffer.length);
                        bos.flush();
                        System.out.println("File: " +entryFile.getName() + " downloaded");
                        entryFile=new File("./clientFiles/"+entryFile.getName());//move the file

                    }

                    fos.close();
                    bos.close();
                    socket.close();
                }catch(Exception e){
                    e.printStackTrace();
                }
                tableServer.getItems().removeAll(serverFiles);
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
