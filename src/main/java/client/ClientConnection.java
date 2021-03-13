package client;

import server.ChatServer;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Logger;

public class ClientConnection implements Runnable{
    private final Socket socket;
    private final Logger logger;
    private final ChatServer server;

    public ClientConnection(Socket socket, Logger logger, ChatServer server) {
        this.socket = socket;
        this.logger = logger;
        this.server = server;
    }

    @Override
    public void run() {
        try(
            final InputStream inputStream = socket.getInputStream();
            final OutputStream outputStream = socket.getOutputStream()
        ) {
            Scanner input = new Scanner(inputStream);
            PrintWriter output = new PrintWriter(outputStream, true);
            while (input.hasNext()) {
                String content = input.nextLine();
                List<ClientConnection> other = server.getClients();
                sendToAll(content, other);
            }
            logger.info("Client closed connection: " + socket.getInetAddress());
        } catch (IOException e) {
            logger.warning("Can't get connection with client!");
            e.printStackTrace();
        }
    }

    public OutputStream getOutput() throws IOException {
        return socket.getOutputStream();
    }

    public void sendToAll(String message, List<ClientConnection> clients){
        clients.forEach(client -> {
            try{
               PrintWriter writer = new PrintWriter(client.getOutput(), true);
               if (client != this ) {
                   writer.println(message);
               }
            } catch (IOException e) {
               logger.warning("Cant send message to other client!");
            }
        });
    }
}
