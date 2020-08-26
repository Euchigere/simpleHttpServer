package core;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static core.ServerListenerThread.executorService;

import static org.junit.jupiter.api.Assertions.*;

class HttpServerWorkerThreadTest {
    static int port = 5050;
    static ServerListenerThread serverListenerThread;

    @BeforeAll
    static void setUp() {
        try {
            serverListenerThread = new ServerListenerThread(port);
            executorService.submit(serverListenerThread);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    void testIndexPathSocketConnectionAndContent() {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:5050"))
                .build();

        HttpResponse<String> response;
        File file = new File("src/main/resources/index.html");
        String expectedContent = new String(HttpServerWorkerThread.contentGenerator.apply(file));

        try {
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
            assertAll(
                    () -> assertEquals("[text/html]", response.headers().allValues("content-type").toString()),
                    () -> assertEquals(200, response.statusCode()),
                    () -> assertEquals(expectedContent, response.body())
            );
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    void testJsonPathSocketConnectionAndContent() {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:5050/json"))
                .build();

        HttpResponse<String> response;
        File file = new File("src/main/resources/sample.json");
        String expectedContent = new String(HttpServerWorkerThread.contentGenerator.apply(file));

        try {
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
            assertAll(
                    () -> assertEquals("[application/json]", response.headers().allValues("content-type").toString()),
                    () -> assertEquals(200, response.statusCode()),
                    () -> assertEquals(expectedContent, response.body())
            );
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    void testUnknownPathSocketConnection() {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:5050/unknown"))
                .build();

        HttpResponse<String> response;
        File file = new File("src/main/resources/unknown");
        String expectedContent = new String(HttpServerWorkerThread.contentGenerator.apply(file));

        try {
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
            assertAll(
                    () -> assertEquals("[text/html]", response.headers().allValues("content-type").toString()),
                    () -> assertEquals(404, response.statusCode()),
                    () -> assertEquals(expectedContent, response.body())
            );
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}