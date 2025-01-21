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

            StringBuilder request = new StringBuilder();
            String line;

            // Read HTTP request headers
            while ((line = in.readLine()) != null && !line.isEmpty()) {
                logger.info("Header: {}", line);
                request.append(line).append("\r\n");
            }

            // Read HTTP request body (if any)
            char[] buffer = new char[1024];
            int bytesRead;
            StringBuilder requestBody = new StringBuilder();
            if (in.ready() && (bytesRead = in.read(buffer)) > 0) {
                requestBody.append(buffer, 0, bytesRead);
            }

            logger.info("Request Body: {}", requestBody);

            // Respond with the message sent by the client
            String responseBody = "Message Received: " + requestBody.toString().trim() + "\n";
            String httpResponse = "HTTP/1.1 200 OK\r\n" +
                                  "Content-Type: text/plain\r\n" +
                                  "Content-Length: " + responseBody.length() + "\r\n" +
                                  "\r\n" +
                                  responseBody;

            out.write(httpResponse);
            out.flush();

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
