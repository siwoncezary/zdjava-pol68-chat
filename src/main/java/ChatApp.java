import server.ChatServer;

import java.io.IOException;
import java.util.logging.Logger;

public class ChatApp {
    static final Logger logger = Logger.getLogger(ChatApp.class.getName());
    public static void main(String[] args) throws IOException {
        ChatServer server = new ChatServer(5555, logger);
        server.start();
    }
}
