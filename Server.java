import java.io.*;
import java.net.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {
    public static void main(String[] args) throws IOException {
        if (args.length != 2) {
            System.err.println("Usage: java Server <port_number> <max_clients>");
            System.exit(1);
        }

        int portNumber = Integer.parseInt(args[0]);
        int maxClients = Integer.parseInt(args[1]);

        // Create server socket
        try (ServerSocket serverSocket = new ServerSocket(portNumber)) {
            System.out.println("Server is listening on port " + portNumber);

            // Ask for the string and N only once
            BufferedReader consoleInput = new BufferedReader(new InputStreamReader(System.in));
            System.out.println("Enter string to send: ");
            String str = consoleInput.readLine();
            System.out.println("Enter a positive integer N: ");
            int N = Integer.parseInt(consoleInput.readLine());

            //check if N is positive 
            if (N <= 0){
                System.err.println("N must be a positive int");
                System.exit(1);
            }

            ExecutorService pool = Executors.newFixedThreadPool(maxClients);

            while (true) {
                // Accept client connection
                Socket clientSocket = serverSocket.accept();

                // Create a new client handler
                ClientHandler client = new ClientHandler(clientSocket, str, N);
                pool.execute(client);
            }
        } catch (IOException e) {
            System.err.println("Could not listen on port " + portNumber);
            System.exit(-1);
        }
    }
}

class ClientHandler implements Runnable {
    private Socket clientSocket;
    private String str;
    private int N;

    public ClientHandler(Socket socket, String str, int N) {
        this.clientSocket = socket;
        this.str = str;
        this.N = N;
    }

    public void run() {
        try {
            PrintWriter outStream = new PrintWriter(clientSocket.getOutputStream(), true);
            BufferedReader inStream = new BufferedReader( new InputStreamReader(clientSocket.getInputStream()));

            //send N to Buffer
            String clientType = inStream.readLine();
            if("BUFFER".equals(clientType)){
                outStream.println(N);
            }
            //outStream.println(N);

            // Transmit string N times to client
            for (int i = 1; i <= N; i++) {
                //if (i < N) {
                    outStream.println(str); // No newline for first N-1 transmissions
                    Thread.sleep(1000);
               /// } else {
               //     outStream.println(str); // Newline for the last transmission
              //  }
                Thread.sleep(1000); // 1-second pause between transmissions
            }
        } catch (IOException | InterruptedException e) {
            System.err.println("Error handling client: " + e.getMessage());
        } finally {
            try {
                clientSocket.close();
            } catch (IOException e) {
                System.err.println("Error closing client socket: " + e.getMessage());
            }
        }
    }
}
