package org.example;

import java.io.*;
import java.net.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EchoClient {

    // Create the logger instance for logging important events
    private static final Logger logger = LoggerFactory.getLogger(EchoClient.class);

    public static void main(String[] args) {
        String serverAddress = "localhost";  // The server address to connect to
        int port = 8081;  // The port the server is listening on

        try (Socket socket = new Socket(serverAddress, port);  // Establishing a connection to the server
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));  // Input stream to read server responses
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);  // Output stream to send messages to the server
             BufferedReader userInput = new BufferedReader(new InputStreamReader(System.in))) {  // Reading user input from console

            // Log successful connection to the server
            logger.info("Connected to server at {} on port {}", serverAddress, port);

            String userMessage;
            logger.info("Enter your message (or 'exit' to close the connection):");

            // Loop to send messages until the user types "exit"
            while ((userMessage = userInput.readLine()) != null) {
                // If the user types 'exit', the loop breaks and the connection is closed
                if (userMessage.equalsIgnoreCase("exit")) {
                    logger.info("Closing the connection with the server.");
                    break;
                }

                // Send the message to the server
                out.println(userMessage);
                // Log the server's response
                String serverResponse = in.readLine();
                logger.info("Server responded: {}", serverResponse);

                // Validation: Check if the message received is the same as sent
                if (serverResponse != null && serverResponse.equals("Message received: " + userMessage)) {
                    logger.info("Message sent and received successfully!");
                } else {
                    logger.error("Message mismatch: Sent '{}' but received '{}'", userMessage, serverResponse);
                }
            }

        } catch (UnknownHostException e) {
            // Log error if the host is unknown
            logger.error("Unknown host: {}", e.getMessage());
        } catch (IOException e) {
            // Log error if there's an I/O issue during communication
            logger.error("Error in communication with the server: {}", e.getMessage());
        }
    }
}
