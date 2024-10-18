package Server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class GameServer {
    private static final int PORT = 12345;
    private static boolean running = true;
    private final ExecutorService threadPool = Executors.newVirtualThreadPerTaskExecutor();
    private final Matchmaker matchmaker;

    public GameServer(Matchmaker matchmaker) {
        this.matchmaker = matchmaker;
    }

    public void start() throws IOException {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Game server started on port " + PORT);
            while (running) {
                Socket clientSocket = serverSocket.accept();
                threadPool.execute(new ClientHandler(clientSocket, matchmaker));
            }
        }
    }

    public void stop() {
        running = false;
    }

    public static void main(String[] args) throws IOException {
        Matchmaker matchmaker = new Matchmaker(8, 1);
        GameServer server = new GameServer(matchmaker);
        server.start();
    }
}