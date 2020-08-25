package core;

import serverUtil.HttpRequestParser;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.function.Function;

/**
 * HttpServerWorkerThread class implements Runnable
 * Processes request from client and serves response as appropriate
 */

class HttpServerWorkerThread implements Runnable {
    private final Socket socket;

    public HttpServerWorkerThread(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        String relativePath = "src/main/resources/";
        try (InputStream clientRequest = socket.getInputStream();
             OutputStream clientResponse = socket.getOutputStream()) {

            // instantiate HttpRequestParser and parse client request
            HttpRequestParser requestParser = new HttpRequestParser();
            requestParser.parse(clientRequest);

            String method = requestParser.getMethod();
            String path = requestParser.getPath();

            // return no response if the method is not get
            if (!"get".equals(method)) {
                return;
            }

            //construct relative path from specified path
            relativePath += "/".equals(path) ? "index.html"
                    : "/json".equals(path) ? "html.json" : path;

            File file = new File(relativePath);

            // construct response headers and content
            String CRLF = "\r\n";
            String statusLine = (!file.exists() || file.isDirectory() ? "HTTP/1.1 404 Not Found" : "HTTP/1.1 200 OK") + CRLF;
            String serverDetails = "Server: Java HTTPServer" + CRLF;
            String contentType = "content-type: " + contentTypeProbe.apply(file) + CRLF;
            byte[] content = contentGenerator.apply(file);
            String contentLength = "Content-Length: " + content.length + CRLF + CRLF;
            String footer = CRLF + CRLF;

            // write response to output stream
            clientResponse.write(statusLine.getBytes());
            clientResponse.write(serverDetails.getBytes());
            clientResponse.write(contentType.getBytes());
            clientResponse.write(contentLength.getBytes());
            clientResponse.write(content);
            clientResponse.write(footer.getBytes());
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

    // generate content from the specified path
    protected static Function<File, byte[]> contentGenerator = (path) -> {
        byte[] _content = new byte[] {};
        // use default content if path does not exist else load content
        if (!path.exists() || path.isDirectory()) {
            _content = "<b>Requested resource not found ....".getBytes();
        } else {
            try {
                _content = Files.readAllBytes(Paths.get(path.getPath()));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return _content;
    };

    // probe content type using nio Files probeContentType method
    protected static Function<File, String> contentTypeProbe = (path) -> {
        String contentType;
        if (!path.exists() || path.isDirectory()) {
            contentType = "text/html";
        } else {
            try {
                contentType = Files.probeContentType(Paths.get(path.getPath()));
            } catch (IOException e) {
                contentType = "text/html";
                e.printStackTrace();
            }
        }
        return contentType;
    };
}
