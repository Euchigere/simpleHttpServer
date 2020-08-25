package serverUtil;

import org.junit.jupiter.api.Test;

import java.io.*;
import java.nio.charset.Charset;

import static org.junit.jupiter.api.Assertions.*;

class HttpRequestParserTest {

    @Test
    void testParserAndGetterMethods() {
        String sampleRequestHeader = "GET / HTTP/1.1\n"
            + "Connection: Upgrade, HTTP2-Settings\n"
            + "Content-Length: 0\n"
            + "Host: localhost:5050\n"
            + "HTTP2-Settings: AAEAAEAAAAIAAAABAAMAAABkAAQBAAAAAAUAAEAA\n"
            + "Upgrade: h2c\n"
            + "User-Agent: Java-http-client/14.0.2";
        InputStream stream = new ByteArrayInputStream(sampleRequestHeader.getBytes(Charset.forName("UTF-8")));
        HttpRequestParser requestParser = new HttpRequestParser();
        requestParser.parse(stream);
        String expectedPath = "/";
        String expectedMethod = "get";
        String expectedHeaders = "[Connection: Upgrade, HTTP2-Settings, "
                + "Content-Length: 0, "
                + "Host: localhost:5050, "
                + "HTTP2-Settings: AAEAAEAAAAIAAAABAAMAAABkAAQBAAAAAAUAAEAA, "
                + "Upgrade: h2c, "
                + "User-Agent: Java-http-client/14.0.2]";
        assertAll(
                () -> assertTrue(expectedPath.equals(requestParser.getPath())),
                () -> assertTrue(expectedMethod.equals(requestParser.getMethod())),
                () -> assertTrue(expectedHeaders.equals(requestParser.getHeaders().toString()))
        );


    }
}