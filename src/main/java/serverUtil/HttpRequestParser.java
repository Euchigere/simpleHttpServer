package serverUtil;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * HttpRequestParser class
 * Parses http request header to get the request method, path and other headers
 */

public class HttpRequestParser {
    private String method;
    private String path;
    private List<String> headers;

    public HttpRequestParser() {
        headers = new ArrayList<>();
    }

    public void parse(InputStream httpRequestStream) {
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(httpRequestStream));
            String line;
            StringBuilder requestBuilder = new StringBuilder();
            while(!(line = reader.readLine()).isBlank()) {
                requestBuilder.append(line + "\r\n");
            }
            String request = requestBuilder.toString();

            String[] requestLines = request.split("\r\n");
            String[] requestLine = requestLines[0].split(" ");

            this.method = requestLine[0].toLowerCase();
            this.path = requestLine[1];

            for (int h = 1; h < requestLines.length; h++) {
                String header = requestLines[h];
                headers.add(header);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // getter methods for relevant headers
    public String getMethod() {
        return method;
    }

    public String getPath() {
        return path;
    }

    public List<String> getHeaders() {
        return headers;
    }
}
