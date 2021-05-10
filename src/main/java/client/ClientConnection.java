package client;

import server.ChatServer;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;
import java.util.logging.Logger;

public class ClientConnection implements Runnable{
    private final Socket socket;
    private final Logger logger;
    private final ChatServer server;
    private PrintWriter printer;

    public ClientConnection(Socket socket, Logger logger, ChatServer server) {
        this.socket = socket;
        this.logger = logger;
        this.server = server;
    }

    @Override
    public void run() {
        try(
            final InputStream inputStream = socket.getInputStream();
            final OutputStream outputStream = socket.getOutputStream();
            final Scanner input = new Scanner(inputStream);
        ) {
            if (printer == null){
                printer = new PrintWriter(outputStream, true);
            }
            while (input.hasNext()) {
                String message = read(input);
                logger.info("client at " + socket.getInetAddress() + ":" + socket.getPort() +  " send: " + message);
                server.broadcast( message, this);
            }
            //client po rozłączeniu musi usunąć swoje połączenie z serwera
            server.closeConnection(this);
            logger.info("Client closed connection: " + socket.getInetAddress());
        } catch (IOException e) {
            logger.warning("Can't get connection to client!");
            e.printStackTrace();
        }
    }

    public void send(String message){
        if (printer == null){
            return;
        }
        logger.info("Server send to " + socket.getInetAddress() + ":" + socket.getPort() + " message " + message);
        printer.println(message);
    }

    public Socket getSocket() {
        return socket;
    }

    private String read(Scanner in){
        return in.nextLine();
    }
}
