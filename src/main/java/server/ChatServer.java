package server;

import client.ClientConnection;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Logger;

public class ChatServer {
    private final ServerSocket serverSocket;
    private final ExecutorService executorService = Executors.newFixedThreadPool(20);
    private final Logger logger;
    private final List<ClientConnection> connections;

    public ChatServer(int port, Logger logger) throws IOException {
        serverSocket = new ServerSocket(port);
        this.logger = logger;
        connections = Collections.synchronizedList(new ArrayList<>());
        logger.info(serverSocket.getInetAddress().getHostAddress());
    }

    public void start(){
        while(true) {
            try{
                final Socket socket = serverSocket.accept();
                logger.info("Connection with client: " + socket.getInetAddress());
                ClientConnection connection = new ClientConnection(socket, logger, this);
                connections.add(connection);
                executorService.execute(connection);
            } catch (IOException e) {
                logger.warning("Cant create connection with client!");
            }
        }
    }

    public List<ClientConnection> getClients(){
        return new ArrayList<>(connections);
    }
}
