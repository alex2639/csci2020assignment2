package sample;

import java.io.*;
import java.net.*;

public class WebServer extends Thread {

    private File file;
    public void run(File file){
        try{
            ServerSocket serverSocket=new ServerSocket(8080);
            //for(File entryFile:file.listFiles())
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public WebServer(File file){
        this.file=file;
    }

    public static void main(String[] args){
        FileInputStream fis=null;
        BufferedInputStream bis=null;
        OutputStream os=null;
        ServerSocket serverSocket=null;

        try{
            serverSocket=new ServerSocket(8080);
            while(true){
                System.out.println("waiting..");
                Socket socket=serverSocket.accept();
                System.out.println("Accepted connection: "+socket);
                File clientFiles=new File("./clientFiles");
                for(File entryFile:clientFiles.listFiles()){
                    byte[] buffer= new byte[(int)entryFile.length()];
                    fis=new FileInputStream(entryFile);
                    bis=new BufferedInputStream(fis);
                    bis.read(buffer, 0, buffer.length);
                    os=socket.getOutputStream();
                    System.out.println("Sending "+entryFile.getName()+" ("+buffer.length+" bytes)");
                    os.write(buffer, 0, buffer.length);
                    System.out.println("done");
                    os.flush();
                }
                bis.close();
                os.close();
                socket.close();
                serverSocket.close();
            }
        }catch(Exception e){
            e.printStackTrace();
        }

    }

}
