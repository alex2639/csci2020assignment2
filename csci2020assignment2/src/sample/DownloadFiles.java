package sample;

import java.io.*;
import java.net.*;

public class DownloadFiles extends Thread {

    private File file;
    public void run(File file){
        try{
            ServerSocket serverSocket=new ServerSocket(8080);
            //for(File entryFile:file.listFiles())
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public DownloadFiles(File file){
        this.file=file;
    }

}
