package http;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class HttpRequest {
    Logger logger = LogManager.getLogger(HttpRequest.class);
    private String method;
    private String url;
    private String version;
    private Map<String, String> headers = new HashMap<>();

    public HttpRequest(BufferedReader br) {
        try {
            //read the main request
            String line = br.readLine();
            parseRequestString(line);

            logger.debug("Request: {}", line);

            do{
                line = br.readLine();
                parseHeaderLine(line);
            }while (!"".equals(line));

            headers.forEach((k, v) -> logger.debug("{} => {}", k, v));

        } catch (IOException e) {
            logger.error("", e);
        }
    }

    private void parseRequestString(String line) {
        if (line == null) {
            return;
        }
        String[] parts = line.split("\\s+");
        method = parts[0];
        url = parts[1];
        version = parts[2];

    }

    private void parseHeaderLine(String line) {
        if (line == null || "".equals(line)) {
            return;
        }
//        logger.debug(line);
        String[] parts = line.split(":\\s+");

        headers.put(parts[0], parts[1]);
    }

    public String getMethod() {
        return method;
    }

    public String getUrl() {
        return url;
    }

    public String getVersion() {
        return version;
    }
}
