package core;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * ServerListenerThread class implements Runnable
 * Listens for request from client
 */
public class ServerListenerThread implements Runnable {
    public static ExecutorService executorService = Executors.newFixedThreadPool(10);
    private final ServerSocket serverSocket;
    private int port;

    public ServerListenerThread(int port) throws IOException {
        this.port = port;
        this.serverSocket = new ServerSocket(port);
    }

    // creates a new HttpServerWorkerThread once request is gotten
    // and submits to the executorService
    @Override
    public void run() {
        System.out.println("server started\nWaiting for request on 'http://localhost:" + port + "/'...");
        while (serverSocket.isBound() && !serverSocket.isClosed()) {
            try {
                Socket socket = serverSocket.accept();
                HttpServerWorkerThread httpServerWorkerThread = new HttpServerWorkerThread(socket);
                executorService.submit(httpServerWorkerThread);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
