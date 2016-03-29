package sample;

import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
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
    private TestFile selectedFile=null;
    public File file;
    private int countServer=0;
    private int countClient=0;


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
            testFiles.add(countClient,testFile);
            countClient++;
            //tableClient.getItems().add(testFile);
        }
        tableClient.setItems(testFiles);

        tableClient.setEditable(true);

        File serverFile=new File("./serverFiles");

        TableView <TestFile>tableServer=new TableView();
        TableColumn<TestFile, String> serverColumn=new TableColumn<>();
        serverColumn.setMinWidth(400);
        serverColumn.setCellValueFactory(new PropertyValueFactory<>("filename"));
        tableServer.getColumns().add(serverColumn);

        ObservableList<TestFile> serverFiles=FXCollections.observableArrayList();

        for(File entryFile:serverFile.listFiles()){
            TestFile testFile=new TestFile(entryFile, entryFile.getName());
            serverFiles.add(countServer,testFile);
            countServer++;
            //tableServer.getItems().add(testFile);
        }
        tableServer.setItems(serverFiles);

        tableServer.setEditable(true);

        //select file from table
        tableClient.getSelectionModel().selectedItemProperty().addListener(new ChangeListener() {
            @Override
            public void changed(ObservableValue observable, Object oldValue, Object newValue) {
                if (tableClient.getSelectionModel().getSelectedItem()!=null){
                    selectedFile=(TestFile) newValue;
                    file=selectedFile.getFile();
                    System.out.println(observable.getValue());
                }
            }
        });

        tableServer.getSelectionModel().selectedItemProperty().addListener(new ChangeListener() {
            @Override
            public void changed(ObservableValue observable, Object oldValue, Object newValue) {
                if (tableServer.getSelectionModel().getSelectedItem()!=null){
                    selectedFile=(TestFile) newValue;
                    file=selectedFile.getFile();
                    System.out.println(selectedFile.getFilename());
                }
            }
        });

        GridPane buttons = new GridPane();
        Button upload=new Button("Upload");
        upload.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if(file!=null){
                    try{
                        byte[] buffer = new byte[1];
                        int bytesRead;
                        Socket socket=new Socket("localhost",8080);
                        InputStream is=socket.getInputStream();
                        ByteArrayOutputStream baos=new ByteArrayOutputStream();
                        if (is!=null){
                            FileOutputStream fos=null;
                            BufferedOutputStream bos=null;
                            fos=new FileOutputStream(file);
                            bos=new BufferedOutputStream(fos);
                            bytesRead=is.read(buffer,0,buffer.length);
                            do{//write file to server
                                baos.write(buffer);
                                bytesRead=is.read(buffer);
                            }while(bytesRead!=-1);
                            bos.write(baos.toByteArray());
                            System.out.println("File: "+file.getName()+" sent");

                            TestFile testFile=new TestFile(file, file.getName());
                            //tableServer.getItems().add(testFile);
                            serverFiles.add(countServer,testFile);
                            countServer++;

                            file=new File("./serverFiles/"+file.getName());//move the file

                            //close down everything when done
                            bos.flush();
                            is.close();
                            socket.close();
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }else{
                    System.out.println("No file selected");
                }

            }
        });

        Button download=new Button("Download");
        download.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if (file!=null){
                    WebServer webServer=new WebServer(file);

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
                        TestFile testFile=new TestFile(file,file.getName());
                        testFiles.add(countClient,testFile);
                        countClient++;

                        current=0;
                        bytesRead=0;
                        byte[] buffer=new byte[6022386];
                        InputStream is=socket.getInputStream();
                        fos=new FileOutputStream(file);
                        bos=new BufferedOutputStream(fos);
                        bytesRead=is.read(buffer,0,buffer.length);
                        current=bytesRead;

                        do{//read file from server
                            bytesRead=is.read(buffer,current,buffer.length-current);
                            if(bytesRead>=0) current+=bytesRead;

                        }while(bytesRead>-1);
                        bos.write(buffer,0,buffer.length);
                        bos.flush();
                        System.out.println("File: " +file.getName() + " downloaded");

                        File movedFile=new File("./clientFiles/"+file.getName());

                        //file=new File("./clientFiles/"+file.getName());//move the file

                        fos.close();
                        bos.close();
                        socket.close();


                    }catch(Exception e){
                        e.printStackTrace();
                    }

                }else{
                    System.out.println("No file selected");
                }

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
