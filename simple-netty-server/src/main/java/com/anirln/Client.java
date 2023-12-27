package com.anirln;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;

public class Client {
    public static void main(String[] args) {
        int numClients = 5;

        for (int i = 0; i < numClients; i++) {
            new Thread(new ClientTask()).start();
        }
    }

    private static class ClientTask implements Runnable {
        @Override
        public void run() {
            InetSocketAddress host = new InetSocketAddress("localhost", 33333);
            try (SocketChannel client = SocketChannel.open(host)) {
                System.out.println("Client: connected to server at " + host);

                String message = "Hello from client";
                client.write(ByteBuffer.wrap(message.getBytes(StandardCharsets.UTF_8)));

                ByteBuffer buffer = ByteBuffer.allocate(256);
                client.read(buffer);

                String serverMessage = new String(buffer.array(), StandardCharsets.UTF_8).trim();
                System.out.println("Client: Received from server " + serverMessage);
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }

        }
    }
}
