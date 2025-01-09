package org.example;

import java.io.*;
import java.net.*;

public class EchoServer {

    public static void main(String[] args) {
        int port = 8081; // Porta do servidor

        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Echo Server está ouvindo na porta " + port);

            while (true) {
                // Espera por uma conexão de um cliente
                Socket clientSocket = serverSocket.accept();
                System.out.println("Cliente conectado: " + clientSocket.getInetAddress());

                // Criação de streams para ler e escrever dados
                BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);

                String inputLine;
                // Interação com o cliente: enquanto o cliente enviar dados
                while ((inputLine = in.readLine()) != null) {
                    System.out.println("Recebido: " + inputLine);
                    out.println("Mensagem recebida: " + inputLine);
                }

                // Fechar a conexão com o cliente
                clientSocket.close();
                System.out.println("Conexão encerrada com o cliente.");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
