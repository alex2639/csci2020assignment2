package sample;

import java.net.*;
import java.io.*;

public class Client {
    public static void main(String[] args){
        File clientFile=new File("./clientFiles");
        try{
            ServerSocket serverSocket=null;
            Socket socket=null;
            BufferedOutputStream bos=null;

            while(true){
                System.out.println("waiting...");
                serverSocket=new ServerSocket(8080);
                socket=serverSocket.accept();
                bos=new BufferedOutputStream(socket.getOutputStream());
                for(File entryFile:clientFile.listFiles()){
                    byte[] buffer=new byte[(int)entryFile.length()];
                    FileInputStream fis=new FileInputStream(entryFile);
                    BufferedInputStream bis=new BufferedInputStream(fis);
                    bis.read(buffer, 0,buffer.length);
                    bos.write(buffer,0,buffer.length);
                    System.out.println("File: "+entryFile.getName()+" sent");
                }
                bos.flush();
                bos.close();
                socket.close();

            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }
}
