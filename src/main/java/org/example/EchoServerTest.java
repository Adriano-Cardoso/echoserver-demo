package org.example;

import java.io.*;
import java.net.*;

public class EchoServerTest {

    private static Socket socket;
    private static PrintWriter out;
    private static BufferedReader in;

    public static void main(String[] args) throws IOException {
        try {
            setup();
            testEchoMessage();
            testExitCommand();
        } finally {
            tearDown();
        }
    }

    public static void setup() throws IOException {
        // Inicia a conexão com o servidor
        socket = new Socket("localhost", 8081); // Certifique-se de que o servidor esteja rodando
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        out = new PrintWriter(socket.getOutputStream(), true);

        // Verifica se a conexão foi bem estabelecida
        assert out != null : "PrintWriter should not be null";
        assert in != null : "BufferedReader should not be null";
    }

    public static void tearDown() throws IOException {
        // Fecha a conexão após o teste
        if (socket != null && !socket.isClosed()) {
            socket.close();
        }
    }

    public static void testEchoMessage() throws IOException {
        String message = "Hello, Echo Server!";
        out.println(message); // Envia a mensagem para o servidor
        String response = in.readLine(); // Recebe a resposta

        // Verifica se a resposta está conforme esperado
        assert response != null : "Response should not be null";
        assert response.equals("Message received: " + message) : "The message sent and received should be the same";
        System.out.println("testEchoMessage passed");
    }

    public static void testExitCommand() throws IOException {
        out.println("exit"); // Envia comando "exit" para o servidor
        String response = in.readLine(); // Recebe a resposta

        // Verifica se a resposta está conforme esperado
        assert response != null : "Response should not be null";
        assert response.equals("Message received: exit") : "Server should correctly respond to 'exit'";
        System.out.println("testExitCommand passed");
    }
}
