import java.io.IOException;
import core.ServerListenerThread;
import static core.ServerListenerThread.executorService;

/**
 * HttpServer class
 * contains the main method to start the server
 */
public class HttpServer {
    public static void main(String[] args) {
        // port to listen for request
        int port = 5050;

        // create instance of ServerListenerThread class and submit to executor service
        try{
            ServerListenerThread serverListenerThread = new ServerListenerThread(port);
            executorService.submit(serverListenerThread);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
