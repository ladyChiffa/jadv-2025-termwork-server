import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicInteger;

public class Server {
    protected ExecutorService threadPool;
    protected ServerChat chatlog = new ServerChat();
    public static AtomicInteger counter = new AtomicInteger(0);

    public Server(int numberOfThreads) {
        threadPool = Executors.newFixedThreadPool(numberOfThreads);
    }

    public void start(int port) {
        try (final var serverSocket = new ServerSocket(port)) {
            while (true) {
                final var socket = serverSocket.accept();
                threadPool.execute(() -> new ClientChat(counter.incrementAndGet()).process(chatlog, socket));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
