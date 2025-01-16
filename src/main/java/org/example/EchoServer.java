package org.example;

import java.io.*;
import java.net.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EchoServer {

    // Create the logger instance for logging important events
    private static final Logger logger = LoggerFactory.getLogger(EchoServer.class);

    // Define server port and max clients allowed for concurrency
    private static final int PORT = 8081;
    private static final int MAX_CLIENTS = 10;  // Limit the number of concurrent client threads

    public static void main(String[] args) {
        // Executor service to handle multiple clients concurrently
        ExecutorService clientHandlerPool = Executors.newFixedThreadPool(MAX_CLIENTS);

        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            logger.info("Echo Server is listening on port {}", PORT);

            // Run the server loop until interrupted
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    // Wait for a client connection
                    Socket clientSocket = serverSocket.accept();
                    logger.info("Client connected: {}", clientSocket.getInetAddress());

                    // Handle the client in a separate thread
                    clientHandlerPool.submit(() -> handleClient(clientSocket));
                } catch (IOException e) {
                    if (Thread.currentThread().isInterrupted()) {
                        break; // Exit if the server was interrupted
                    }
                    logger.error("Error accepting client connection: {}", e.getMessage());
                }
            }
        } catch (IOException e) {
            logger.error("Error starting Echo Server: {}", e.getMessage());
        } finally {
            // Gracefully shutdown the client handler pool
            clientHandlerPool.shutdown();
            logger.info("Server has been shut down.");
        }
    }

    // Method to handle client connection in a separate thread
    private static void handleClient(Socket clientSocket) {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
             PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true)) {

            String inputLine;
            boolean headerEnd = false;

            // Interact with the client: while the client sends data
            while ((inputLine = in.readLine()) != null) {
                // Log the received lines (headers and body)
                logger.info("Received: {}", inputLine);

                // Check if the HTTP header has ended
                if (inputLine.isEmpty()) {
                    headerEnd = true;
                }

                // If the header has ended, the server sends an HTTP response
                if (headerEnd) {
                    // Send a simple HTTP header and the body with the client's message
                    out.println("HTTP/1.1 200 OK");
                    out.println("Content-Type: text/plain; charset=UTF-8");
                    out.println(""); // Empty line to indicate the end of the header
                    out.println("Message received: " + inputLine);
                    break; // Exit after sending the response
                }
            }

        } catch (IOException e) {
            logger.error("Error handling client connection: {}", e.getMessage());
        } finally {
            try {
                // Close the client socket connection
                clientSocket.close();
                logger.info("Connection closed with the client.");
            } catch (IOException e) {
                logger.error("Error closing client connection: {}", e.getMessage());
            }
        }
    }


}
