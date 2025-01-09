package org.example;

import java.io.*;
import java.net.*;

public class EchoClient {

    public static void main(String[] args) {
        String serverAddress = "localhost"; // Endereço do servidor
        int port = 8081; // Porta do servidor (certifique-se que corresponde à do EchoServer)

        try (Socket socket = new Socket(serverAddress, port)) {
            // Criação de streams para comunicação
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader userInput = new BufferedReader(new InputStreamReader(System.in));

            String userMessage;
            System.out.println("Digite sua mensagem (ou 'sair' para encerrar):");

            // Loop para enviar mensagens até o usuário digitar "sair"
            while ((userMessage = userInput.readLine()) != null) {
                if (userMessage.equalsIgnoreCase("sair")) {
                    break; // Encerra o loop se o usuário digitar "sair"
                }
                out.println(userMessage); // Envia a mensagem para o servidor
                System.out.println("Servidor respondeu: " + in.readLine());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
