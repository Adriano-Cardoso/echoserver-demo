package org.example;

import java.io.*;
import java.net.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EchoServer {

    private static final Logger logger = LoggerFactory.getLogger(EchoServer.class);
    private static final int PORT = 8081;
    private static final int MAX_CLIENTS = 10;

    public static void main(String[] args) {
        ExecutorService clientHandlerPool = Executors.newFixedThreadPool(MAX_CLIENTS);

        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            logger.info("Echo Server is listening on port {}", PORT);

            while (!Thread.currentThread().isInterrupted()) {
                try {
                    Socket clientSocket = serverSocket.accept();
                    logger.info("Client connected: {}", clientSocket.getInetAddress());
                    clientHandlerPool.submit(() -> handleClient(clientSocket));
                } catch (IOException e) {
                    if (Thread.currentThread().isInterrupted()) {
                        break;
                    }
                    logger.error("Error accepting client connection: {}", e.getMessage());
                }
            }
        } catch (IOException e) {
            logger.error("Error starting Echo Server: {}", e.getMessage());
        } finally {
            clientHandlerPool.shutdown();
            logger.info("Server has been shut down.");
        }
    }

    private static void handleClient(Socket clientSocket) {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
             PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true)) {

            // Read the HTTP request (ignoring headers here for simplicity)
            String requestLine = in.readLine();
            logger.info("Received: {}", requestLine);

            // Read and discard the HTTP headers
            String header;
            while (!(header = in.readLine()).isEmpty()) {
                logger.debug("Header: {}", header);
            }

            // Handle POST request
            if (requestLine != null && requestLine.startsWith("POST")) {
                String inputLine;
                StringBuilder requestBody = new StringBuilder();
                while ((inputLine = in.readLine()) != null) {
                    requestBody.append(inputLine).append("\n");
                }

                logger.info("Received body: {}", requestBody.toString());

                // Respond with HTTP/1.1 response
                out.println("HTTP/1.1 200 OK");
                out.println("Content-Type: text/plain");
                out.println("Content-Length: " + requestBody.length());
                out.println();
                out.println("Message Received: " + requestBody.toString());
            } else {
                // Respond with HTTP/1.1 for other methods (like GET)
                out.println("HTTP/1.1 405 Method Not Allowed");
                out.println("Content-Type: text/plain");
                out.println();
                out.println("Only POST method is allowed");
            }

            if ("exit".equalsIgnoreCase(requestLine)) {
                logger.info("Client requested to close the connection.");
            }
        } catch (IOException e) {
            logger.error("Error handling client connection: {}", e.getMessage());
        } finally {
            try {
                clientSocket.close();
                logger.info("Connection closed with the client.");
            } catch (IOException e) {
                logger.error("Error closing client connection: {}", e.getMessage());
            }
        }
    }
}
