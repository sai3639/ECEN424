import java.net.*;
import java.util.Scanner;

public class Talker {
        
    //throw exception if argument length less than  1 
    //if no port number provided - exit 
    public static void main(String[] args) throws Exception {
        if (args.length < 1) {
            System.out.println("Usage: java Talker <TalkerPort>");
            return;
        }

        //get port number for the talker 
        int talkerPort = Integer.parseInt(args[0]);
        //DatagramSocket - for sending and receiving datagrams on conectionless datagram/message protocol -- UDP
        DatagramSocket socket = new DatagramSocket(talkerPort); //created for sending/receiving UDP messages 

        //create new scanner object - take input from user/command line
        Scanner scanner = new Scanner(System.in);

        //talker input
        System.out.println("Enter a string (up to 50 characters): ");
        String input = scanner.nextLine();

        //throw exception if string is too long 
        if (input.length() > 50) {
            System.out.println("Input string too long. Exiting.");
            return;
        }

        // Split the input into messages of up to 10 characters
        int totalMessages = (int) Math.ceil(input.length() / 10.0); //calculates how many messages made up of 10 charactes there will be based on input
        String[] messages = new String[totalMessages + 1]; // array number = the total number of messages + 1 for message 0
        messages[0] = String.valueOf(totalMessages); // Message 0: Number of messages to send

        //populates messages array with 10-character chunks of input
        for (int i = 1; i <= totalMessages; i++) {
            int start = (i - 1) * 10;
            //putting the user input into the messages array
            messages[i] = input.substring(start, Math.min(start + 10, input.length()));
        }

        System.out.println("Waiting for connection request from Listener...");

        // Wait for connection request from Listener
        //DatagramPacket - connectionless trasnfer of messages from one system to another
        //buf = actual message that is to be delivered/received - sent in byte array
        //offset? - offset into buffer array
        DatagramPacket connectionPacket = new DatagramPacket(new byte[1024], 1024); 
        //reads data into buffere parameter and returns number of bytes successfully read
        socket.receive(connectionPacket);

        // Extract Listener's IP address and port
        InetAddress listenerIP = connectionPacket.getAddress();
        int listenerPort = connectionPacket.getPort();

        System.out.println("Connection request received from Listener at " + listenerIP + ":" + listenerPort);

        // Send acknowledgment of connection
        String connectionAck = "Connection accepted";
        byte[] connAckData = connectionAck.getBytes();
        //buf, offset, length, inetAddress address 
        DatagramPacket connAckPacket = new DatagramPacket(connAckData, connAckData.length, listenerIP, listenerPort);
        socket.send(connAckPacket);

        System.out.println("Connection established. Starting to send messages...");

        byte[] buffer = new byte[1024];
        int sequenceNumber = 0; //id

        //loop to send all messages
        while (sequenceNumber <= totalMessages) {
            //prepends sequence number to each message and conver message to bytes to send to listener 
            String message = sequenceNumber + ":" + messages[sequenceNumber];
            buffer = message.getBytes();

            // Send message
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length, listenerIP, listenerPort);
            socket.send(packet);
            System.out.println("Sent: " + message);

            // Wait for ACK
            //extracts ACK sequence number from received packet 
            socket.setSoTimeout(2000); // 2-second timeout
            try {
                DatagramPacket ackPacket = new DatagramPacket(new byte[1024], 1024);
                socket.receive(ackPacket);
                String ack = new String(ackPacket.getData(), 0, ackPacket.getLength());
                int ackNumber = Integer.parseInt(ack.trim());

                //if received ACK matches expected sequence number - proceed to the next message 
                //otherwise retry sending the current message 
                if (ackNumber == sequenceNumber + 1) {
                    System.out.println("ACK received for: " + sequenceNumber);
                    sequenceNumber++;
                } else {
                    System.out.println("Incorrect ACK number. Expected: " + (sequenceNumber + 1) + ", Received: " + ackNumber);
                }
                //handle timeout - if no ACK received within 2 seconds, retry sending the current message 
            } catch (SocketTimeoutException e) {
                System.out.println("Timeout for message: " + sequenceNumber + ". Resending...");
            }
        }

        socket.close();
        System.out.println("All messages sent.");
    }
}
