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

            String inputLine;
            StringBuilder requestHeaders = new StringBuilder();

            // Lê os cabeçalhos da requisição
            while ((inputLine = in.readLine()) != null && !inputLine.isEmpty()) {
                requestHeaders.append(inputLine).append("\n");
                logger.info("Received Header: {}", inputLine);
            }

            // Exibe os cabeçalhos para depuração
            logger.info("Request Headers:\n{}", requestHeaders.toString());

            // Envia a resposta HTTP antes de processar os dados
            out.println("HTTP/1.1 200 OK");
            out.println("Content-Type: text/plain");
            out.println(""); // Linha em branco para separar cabeçalhos do corpo

            // Interage com o cliente e envia a resposta
            while ((inputLine = in.readLine()) != null) {
                logger.info("Received: {}", inputLine);
                out.println("Message Received: " + inputLine);

                // Fecha a conexão se o cliente enviar "exit"
                if ("exit".equalsIgnoreCase(inputLine)) {
                    logger.info("Client requested to close the connection.");
                    break;
                }
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
