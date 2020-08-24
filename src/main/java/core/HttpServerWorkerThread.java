package core;

import serverUtil.HttpRequestParser;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.function.Supplier;

/**
 * HttpServerWorkerThread class implements Runnable
 * Processes request from client and serves response as appropriate
 */

class HttpServerWorkerThread implements Runnable {
    Socket socket;

    public HttpServerWorkerThread(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try (InputStream clientRequest = socket.getInputStream();
             OutputStream clientResponse = socket.getOutputStream()) {

            // instantiate HttpRequestParser and parse client request
            HttpRequestParser requestParser = new HttpRequestParser();
            requestParser.parse(clientRequest);

            String method = requestParser.getMethod();
            String path = requestParser.getPath();
            String response;

            // generate response based on method and path
            if (!"get".equals(method)) {
                response = getResponse("error");
                clientResponse.write(response.getBytes());
            } else if ("/".equals(path)) {
                response = getResponse("index");
                clientResponse.write(response.getBytes());
            } else if ("/json".equals(path)) {
                response = getResponse("json");
                clientResponse.write(response.getBytes());
            } else {
                response = getResponse("error");
                clientResponse.write(response.getBytes());
            }
            clientResponse.flush();

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (socket != null) {
                    socket.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // method to generate response
    private String getResponse(String type) {
        String relativePath = "src/main/resources/";

        // function to supply content depending on specified path
        Supplier<String> contentGenerator = () -> {
            String _content = "";
            switch (type) {
                case "index":
                    try {
                        byte[] htmlFileBytes = Files.readAllBytes(Paths.get(relativePath + "index.html"));
                        _content = new String(htmlFileBytes);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
                case "json":
                    try {
                        byte[] jsonFileBytes = Files.readAllBytes(Paths.get(relativePath + "html.json"));
                        _content = new String(jsonFileBytes);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
                default:
                    _content = "<b>Requested resource not found ....";
            }
            return _content;
        };

        String CRLF = "\r\n";
        String statusLine = ("error".equals(type) ? "HTTP/1.1 404 Not Found" : "HTTP/1.1 200 OK") + CRLF;
        String serverDetails = "Server: Java HTTPServer" + CRLF;
        String contentType = ("json".equals(type) ? "Content-Type: application/json" : "Content-Type: text/html") + CRLF;
        String content = contentGenerator.get() + CRLF + CRLF;
        String contentLength = "Content-Length: " + content.getBytes().length + CRLF + CRLF;

        StringBuilder responseBuilder = new StringBuilder();
        responseBuilder.append(statusLine)
                .append(serverDetails)
                .append(contentType)
                .append(contentLength)
                .append(content);

        return responseBuilder.toString();
    }
}
