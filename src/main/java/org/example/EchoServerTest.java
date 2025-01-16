package org.example;

import java.io.*;
import java.net.*;

public class EchoServerTest {

    private static Socket socket;
    private static PrintWriter out;
    private static BufferedReader in;

    public static void main(String[] args) throws IOException {
        try {
            setup(); // Set up the test environment
            testEchoMessage(); // Test sending and receiving an echo message
            testExitCommand(); // Test sending the exit command
        } finally {
            tearDown(); // Ensure resources are cleaned up after tests
        }
    }

    public static void setup() throws IOException {
        // Establish connection to the Echo server
        socket = new Socket("localhost", 8081);
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        out = new PrintWriter(socket.getOutputStream(), true);

        // Verify that the connection was successfully established
        assert out != null : "PrintWriter should not be null";
        assert in != null : "BufferedReader should not be null";
    }

    public static void tearDown() throws IOException {
        // Close the connection after the test is complete
        if (socket != null && !socket.isClosed()) {
            socket.close();
        }
    }

    public static void testEchoMessage() throws IOException {
        String message = "Hello, Echo Server!";
        out.println(message); // Send the message to the server
        String response = in.readLine(); // Receive the response from the server

        // Verify that the response matches the expected echo message
        assert response != null : "Response should not be null";
        assert response.equals("Message received: " + message) : "The message sent and received should be the same";
        System.out.println("testEchoMessage passed");
    }

    public static void testExitCommand() throws IOException {
        out.println("exit"); // Send the "exit" command to the server
        String response = in.readLine(); // Receive the response from the server

        // Verify that the server correctly handles the "exit" command
        assert response != null : "Response should not be null";
        assert response.equals("Message received: exit") : "Server should correctly respond to 'exit'";
        System.out.println("testExitCommand passed");
    }
}
