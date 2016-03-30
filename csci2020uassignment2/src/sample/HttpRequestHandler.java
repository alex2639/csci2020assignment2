package sample;

import java.io.*;
import java.net.*;
import java.util.StringTokenizer;
import java.util.NoSuchElementException;
import java.util.Date;

public final class HttpRequestHandler implements Runnable {
    public static String WEB_DIR = "clientFiles";
    private Socket socket = null;
    private BufferedReader requestInput = null;
    private DataOutputStream requestOutput = null;

    public HttpRequestHandler(Socket socket) {
        this.socket = socket;
        try {
            requestInput = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            requestOutput = new DataOutputStream(socket.getOutputStream());
        } catch (IOException e) {
            System.err.println("Server Error while processing new socket\r\n");
            e.printStackTrace();
        }
    }

    public void run() {
        String mainRequestLine = null;
        try {
            mainRequestLine = requestInput.readLine();
            handleRequest(mainRequestLine);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                requestInput.close();
                requestOutput.close();
                socket.close();
            } catch (IOException e2) {}
        }
    }

    private void handleRequest(String mainRequestLine) throws IOException { 
        try {
            StringTokenizer requestTokenizer = new StringTokenizer(mainRequestLine);
            String command = null;
            String uri = null;
            String httpVersion = null;
            
            command = requestTokenizer.nextToken();
            uri = requestTokenizer.nextToken();
            if (!uri.startsWith("/")) {
                uri = "/" + uri;
                System.out.println(uri);
            }
            
            httpVersion = requestTokenizer.nextToken();
            if (command.equalsIgnoreCase("GET") || command.equalsIgnoreCase("POST")) {
                File baseDir = new File(WEB_DIR);
                sendFile(new File(baseDir, uri));
            } else {
                sendError(405, "Method Not Allowed", 
                               "The method ("+command+") requested by your client is not allowed.");
                System.err.println("HTTP/1.1 405 Method Not Allowed: "+mainRequestLine+"\r\n");
            }
        } catch (NoSuchElementException e) {
            sendError(400, "Bad Request", "The request sent by your client software was invalid.");
            System.err.println("HTTP/1.1 400 Bad Request: "+mainRequestLine+"\r\n");
            e.printStackTrace();
        }
    }

    private void sendFile(File file) throws IOException {
        String header = "HTTP/1.1 200 OK\r\n";
        String contentType = "text/plain";//getContentType(file.getName());
        byte[] content = new byte[(int)file.length()];
        FileInputStream fileInput = null;

        try {
            fileInput = new FileInputStream(file);
        } catch (FileNotFoundException e) {
            fileInput = null;
        }
        
        if (fileInput != null) {
            fileInput.read(content);
            fileInput.close();
            sendResponse(header, contentType, content);
        } else {
            sendError(404, "File Not Found", 
                      "The requested file ("+file.getName()+") does not exist on the server.");
        }
    }

    private void sendResponse(String header, String contentType, 
                              byte[] content) throws IOException {
        sendResponse(header, contentType, content, 0);
    }

    private void sendResponse(String header, String contentType, 
                              byte[] content, long lastModified) throws IOException {
        requestOutput.writeBytes(header);
        requestOutput.writeBytes("Content-Type: "+contentType+"\r\n");
        requestOutput.writeBytes("Server: Simple-Http-Server/1.0\r\n");
        requestOutput.writeBytes("Date: "+(new Date())+"\r\n");
        requestOutput.writeBytes("Content-Length: "+content.length+"\r\n");
        if (lastModified > 0) {
            requestOutput.writeBytes("Last-Modified: "+(new Date(lastModified))+"\r\n");
        }
        requestOutput.writeBytes("Connection: Close\r\n\r\n");
        requestOutput.write(content);
        requestOutput.writeBytes("\r\n");
        requestOutput.flush();
    }


    private void sendError(int code, String title, String message) throws IOException {
        String header = "HTTP/1.1 "+code+" "+title+"\r\n";
        String contentType = "text/html";
        String content = "<!DOCTYPE html>" +
                         "<html>\r\n" +
                         "  <head>\r\n" +
                         "    <title>Http Error "+code+" "+title+"</title>\r\n" +
                         "  </head>\r\n" +
                         "  <body>\r\n" +
                         "    <h1>Http Error "+code+" "+title+"</h1>\r\n" +
                         "    <p>"+message+"</p>\r\n" +
                         "  </body>\r\n" +
                         "</html>\r\n";
        sendResponse(header, contentType, content.getBytes());
    }

}
