//imports
import java.net.*;
import java.util.Random;

//if port number and IP address not in argument - exit 

public class Listener {
    public static void main(String[] args) throws Exception {
        if (args.length < 2) {
            System.out.println("Usage: java Listener <ListenerPort> <TalkerIP>");
            return;
        }

       

        //extract data 
        int listenerPort = Integer.parseInt(args[0]);
        InetAddress talkerIP = InetAddress.getByName(args[1]);

        //UDP socket datagram - to send and receive packets 
        DatagramSocket socket = new DatagramSocket();

        // Send connection request to Talker
        String connectionRequest = "Connection request";
        byte[] connReqData = connectionRequest.getBytes(); //convert to bytes
        DatagramPacket connReqPacket = new DatagramPacket(connReqData, connReqData.length, talkerIP, listenerPort);
        socket.send(connReqPacket);

        System.out.println("Connection request sent to Talker at " + talkerIP + ":" + listenerPort);

        // Wait for acknowledgment of connection
        DatagramPacket connAckPacket = new DatagramPacket(new byte[1024], 1024);
        socket.receive(connAckPacket);
        //connection accepted or not
        String connAck = new String(connAckPacket.getData(), 0, connAckPacket.getLength());
        System.out.println("Received from Talker: " + connAck);

        // Prepare to receive messages
        System.out.println("Starting to receive messages...");

        int numpackets = 5; //we will recieve 5 packets
        String[] receivedMessages = new String[numpackets];
        int expectedSequence = 0;
        int totalMessages = -1;

        // Receive messages - incoming packets stored in a buffer
        boolean receiving = true;
        while (receiving) {
            DatagramPacket packet = new DatagramPacket(new byte[1024], 1024);
            socket.receive(packet);

            String message = new String(packet.getData(), 0, packet.getLength());
            //splits received packet into sequence number and message content 
            String[] parts = message.split(":", 2); //just formatting
            int sequenceNumber = Integer.parseInt(parts[0].trim());
            String content = parts[1];

            //if sequence number matches the expected value store and 
            //print them out
            if (sequenceNumber == expectedSequence) {
                System.out.println("Received: " + message);
                receivedMessages[sequenceNumber] = content;

                    //if sequence number 0 - extract total number of messages from content
                if (sequenceNumber == 0) {
                    // Message 0 contains total number of messages
                    totalMessages = Integer.parseInt(content.trim());
                    System.out.println("Total messages to receive: " + totalMessages);
                }

                

                // Randomly decide whether to ACK the message
                Random random = new Random();
                boolean sendACK = random.nextBoolean();

                    //decide whether to send ACK for received message
                sendACK = true; // for now; in second part will be commented out
                if (sendACK) {
                    String ackResponse = String.valueOf(sequenceNumber + 1); //send ack with next message header
                    byte[] ackData = ackResponse.getBytes();
                    DatagramPacket ackPacket = new DatagramPacket(ackData, ackData.length, packet.getAddress(),
                            packet.getPort());
                    socket.send(ackPacket);
                    System.out.println("ACK sent for: " + sequenceNumber);
                    //if ACK sent - increment expectedSequence to wait for next message
                    expectedSequence++;

                    //if last message - end loop
                    if (sequenceNumber == totalMessages) {
                        receiving = false;
                    }
                    //if ACK not sent - message is "dropped"
                } else {
                    System.out.println("Dropped ACK for: " + sequenceNumber);
                }
                //handles out-of-order/duplicate messages
            } else {
                System.out.println("Unexpected sequence number. Expected: " + expectedSequence + ", Received: " + sequenceNumber);
                // Optionally send ACK for the last acknowledged sequence number to help resynchronization
            }
        }

        socket.close();

        // Concatenate messages and display
        StringBuilder result = new StringBuilder();
        for (int i = 1; i <= totalMessages; i++) {
            result.append(receivedMessages[i]);
        }

        System.out.println("Full message received: " + result.toString());
    }
}
