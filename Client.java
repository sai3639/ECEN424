import java.io.*;
import java.net.*;

public class Client {
    public static void Naive_Client(Socket clientSocket) throws IOException { 
        PrintWriter outToServer = new PrintWriter(clientSocket.getOutputStream(), true);
        BufferedReader inFromServer = new BufferedReader(
            new InputStreamReader(clientSocket.getInputStream())
        );

        outToServer.println("NAIVE");

       

        //display each string/transmission immediately after they are received
        String line; 
        while ((line = inFromServer.readLine()) != null){
            System.out.println(line + "\n");

            try{
                Thread.sleep(1000);

            }
            catch (InterruptedException e){
                System.err.println("interrupted exception: " + e.getMessage());
            }
        }




    }

    public static void Buffer_Client(Socket clientSocket) throws IOException { 
        PrintWriter outToServer = new PrintWriter(clientSocket.getOutputStream(), true);
        BufferedReader inFromServer = new BufferedReader(
            new InputStreamReader(clientSocket.getInputStream())
        );

        outToServer.println("BUFFER");
        int N = Integer.parseInt(inFromServer.readLine().trim());

        // Buffer and output the full message once it's received
        StringBuilder message = new StringBuilder();
        int character;
        try {
            Thread.sleep(N*1000);
            
        } catch (InterruptedException e) {
            System.err.println("interrupted excpetion: " + e.getMessage() );
        }


        // while ((character = inFromServer.read()) != -1) {
        //     message.append((char) character);
        //     if (character == '\n') {
        //         break; // Stop when a newline character is received
        //     }
        // }

        String line; 
        while ((line = inFromServer.readLine()) != null){
            message.append(line).append("\n");
            // if(character == '\n'){
            //     break;
            // }
        }
        System.out.println(message.toString()); // Output the full message after newline
    }

    public static void main(String[] args) throws IOException {
        if (args.length != 2) {
            System.err.println("Usage: java Client <server_ip> <server_port>");
            System.exit(1);
        }

        String serverIp = args[0];
        int serverPort = Integer.parseInt(args[1]);
        Socket clientSocket = new Socket(serverIp, serverPort); 

        System.out.println("Choose the type of client (type 1 for Naive and 2 for Buffer): ");
        BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in));
        int clientType = Integer.parseInt(inFromUser.readLine());

        if (clientType == 1) {
            Naive_Client(clientSocket);
        } else if (clientType == 2) {
            Buffer_Client(clientSocket);
        } else {
            System.out.println("INVALID CLIENT TYPE");
        }

        clientSocket.close(); 
    }
}


 // Display characters immediately as they are received
        // int character;
        // while ((character = inFromServer.read()) != -1) {
        //     System.out.print((char) character);
        //     if (character == '\n') {
        //         break; // Stop when a newline character is received
        //     }
        //     try{
        //         Thread.sleep(1000);

        //     }catch (InterruptedException e){
        //         System.err.println("interrupted exception: " + e.getMessage());
        //     }

        //}