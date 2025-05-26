import java.io.*;
import java.net.*;

public class mp3 {

    public static void Naive_Client(Socket clientSocket) throws IOException { //instantiating the naive client
        BufferedReader inFromServer = new BufferedReader(
            new InputStreamReader(clientSocket.getInputStream())
        );
        BufferedReader inFromUser = new BufferedReader(
            new InputStreamReader(System.in)
        );
        DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());
       
        // Prompt user for input and send to server
        System.out.print("Please input your message: ");
        String sentence = inFromUser.readLine();
        outToServer.writeBytes(sentence + '\n');  // Send the message to the server

        // Display characters immediately as they are received
        int character;
        while ((character = inFromServer.read()) != -1) {
            System.out.print((char) character);
            if (character == '\n') {
                break;
            }
        }
       // clientSocket.close();  // Close the client socket so it does not continue
    }

    public static void Buffer_Client(Socket clientSocket) throws IOException { //instantiating the buffer client
        BufferedReader inFromServer = new BufferedReader(
            new InputStreamReader(clientSocket.getInputStream())
        );
        BufferedReader inFromUser = new BufferedReader(
            new InputStreamReader(System.in)
        );
        DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());

        System.out.print("Please input your message: "); // Prompt user for input and send to server
        String sentence = inFromUser.readLine();
        outToServer.writeBytes(sentence + '\n');  // Send the message to the server

        // Buffer and output the full message once it's received
        String modifiedSentence = inFromServer.readLine();  
        System.out.println(modifiedSentence);  // Output the full message after newline

        //clientSocket.close();  // Close the client socket so it does not continue
    }




    //server setup and client differentiation
    public static void main(String[] args) throws IOException {
            if (args.length !=2){
                System.err.println("Usage: java m");
                System.exit(1);
            }

        String serverIp = args[0];
        int serverPort = Integer.parseInt(args[1]);
        Socket clientSocket = new Socket(serverIp, serverPort); // Set up the socket to connect to the server

        System.out.println("Choose the type of client (type 1 for Naive and 2 for buffer): ");
        BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in));
        int clientType = Integer.parseInt(inFromUser.readLine());

        if (clientType == 1) {
            Naive_Client(clientSocket);  // Start the Naive Client
        } else if (clientType == 2) {
            Buffer_Client(clientSocket);  // Start the Buffer Client
        } else {
            System.out.println("INVALID CLIENT TYPE"); //ensuring invalid case is caught
        }
        clientSocket.close(); //close sockets 
    }
}