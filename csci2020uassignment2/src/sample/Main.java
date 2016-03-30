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
import java.net.Socket;

public class Main extends Application {
    private static String name, directory;
    private int bufferSize=1024;
    private TestFile selectedFile=null;
    public File file;
    private int countServer=0;
    private int countClient=0;
    private boolean repeat=false;

    @Override
    public void start(Stage primaryStage) throws Exception{
        primaryStage.setTitle("File Sharer");
        BorderPane layout=new BorderPane();
        directory="clientFiles";
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
            serverFiles.add(testFile);
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
                    //System.out.println(observable.getValue());
                }
            }
        });

        tableServer.getSelectionModel().selectedItemProperty().addListener(new ChangeListener() {
            @Override
            public void changed(ObservableValue observable, Object oldValue, Object newValue) {
                if (tableServer.getSelectionModel().getSelectedItem()!=null){
                    selectedFile=(TestFile) newValue;
                    file=selectedFile.getFile();
                    //System.out.println(selectedFile.getFilename());
                }
            }
        });

        GridPane buttons = new GridPane();
        Button upload=new Button("Upload");
        upload.setOnAction(new EventHandler<ActionEvent>() {//upload file
            @Override
            public void handle(ActionEvent event) {
                if(file!=null){
                    TestFile testFile=new TestFile(file, file.getName());
                    for(int i=0;i<serverFiles.size();i++){
                        if(serverFiles.get(i)==testFile){
                            repeat=true;
                            break;
                        }
                        //repeat=false;
                    }
                    if(!repeat){//if the file doesn't exist on the server
                        serverFiles.add(testFile);
                        System.out.println(file.getAbsolutePath());
                        File addFile=new File("serverFiles/"+file.getName());
                        try{
                            FileReader reader=new FileReader(file);
                            FileWriter writer=new FileWriter(addFile);
                            int c=reader.read();
                            while(c!=-1){
                                writer.write(c);
                                c=reader.read();
                            }
                            writer.close();
                            reader.close();
                        }catch (Exception e){
                            e.printStackTrace();
                        }

                        upload("clientFiles/"+file.getName());
                    }
                    repeat=false;
                }else{
                    System.out.println("No file selected");
                }
            }
        });

        Button download=new Button("Download");
        download.setOnAction(new EventHandler<ActionEvent>() {//download file
            @Override
            public void handle(ActionEvent event) {
                if (file!=null){

                    TestFile testFile=new TestFile(file,file.getName());
                    for (int i=0;i<testFiles.size();i++){
                        if(testFiles.get(i)==testFile){
                            repeat=true;
                            break;
                        }

                    }
                    if(!repeat){
                        testFiles.add(countClient,testFile);
                        File addFile=new File("clientFiles/"+file.getName());
                        try{
                            FileReader reader=new FileReader(file);
                            FileWriter writer=new FileWriter(addFile);
                            int c=reader.read();
                            while(c!=-1){
                                writer.write(c);
                                c=reader.read();
                            }
                            writer.close();
                            reader.close();
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                        download(file.getName());
                    }
                    repeat=false;
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

    public void upload(String filename){
        try{
            Socket socket=new Socket("localhost",8080);
            OutputStream out=socket.getOutputStream();
            FileInputStream fis=new FileInputStream(filename);
            byte[] buffer=new byte[1024];
            int numBytes=-1;
            while((numBytes=fis.read(buffer))>0){
                out.write(buffer);
            }

            socket.close();

        }catch(Exception e){
            e.printStackTrace();
        }

    }

    public void download(String uri){
        try {
            Socket socket = new Socket("localhost", 8080);

            PrintWriter out = new PrintWriter(socket.getOutputStream());
            out.println("GET " + uri + " HTTP/1.0\r\n");
            out.println("Host: localhost\r\n\r\n");
            out.flush();

            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            String line = null;
            while ((line = in.readLine()) != null) {
                System.out.println(line);
            }

            out.close();
            in.close();
            socket.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
