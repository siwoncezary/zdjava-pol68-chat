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
        ) {
            if (printer == null){
                printer = new PrintWriter(outputStream);
            }
            Scanner input = new Scanner(inputStream);
            while (input.hasNext()) {
                server.broadcast(read(input), this);
            }
            //client po rozłaczeniu musi usunąć swoje połączenie z serwera
            server.closeConnection(this);
            logger.info("Client closed connection: " + socket.getInetAddress());
        } catch (IOException e) {
            logger.warning("Can't get connection with client!");
            e.printStackTrace();
        }
    }

    public void send(String message){
        if (printer == null){
            return;
        }
        synchronized (printer){
            printer.println(message);
        }
    }

    private String read(Scanner in){
        return in.nextLine();
    }
}
