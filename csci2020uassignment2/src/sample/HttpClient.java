package sample;

import java.io.*;
import java.net.*;

public class HttpClient {
    public static void main(String[] args) {
        Socket socket;
        BufferedReader in;
        PrintWriter out;

        // get the host name
        String hostName = "localhost";

        // get the port number, if there is one
        int portNumber = 8080;

        try {
            // connect to the server (3-way connection establishment handshake)
            socket = new Socket(hostName, portNumber);

            // wrap the input streams into readers and writers
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream());

            // send the HTTP request GET /yahoo/yahoo.html HTTP/1.0\n\n
            String request = "GET /yahoo/yahoo.html HTTP/1.0";
            String header = "Host: " + hostName;
            String delim = "\r\n";
            out.print(request + delim + header + delim + delim);
            out.flush();

            // read and print the response
            String response;
            System.out.println("Response:");
            while ((response = in.readLine()) != null) {
                System.out.println(response);
            }

            // close the connection (3-way tear down handshake)
            out.close();
            in.close();
            socket.close();
        } catch (IOException e) {
            System.out.println(e);
            e.printStackTrace();
        }
    }
}

