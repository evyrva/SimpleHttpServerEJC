package http;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static java.nio.charset.StandardCharsets.UTF_8;

public class HttpResponse {
    private static final String VERSION = "HTTP/1.1";
    Logger logger = LogManager.getLogger(HttpResponse.class);
    List<String> headers = new ArrayList<>();

    byte[] body;

    public HttpResponse(HttpRequest req) {
        final String method = req.getMethod();
        switch (method) {
            case "GET":
                final String url = req.getUrl();
                logger.debug(url);
                final Path path = Paths.get(".", url);
                logger.debug(path.toAbsolutePath());

                if (!Files.exists(path)) {
                    fillHeaders(HttpStatus.NOT_FOUND);
                    fillBody("<h1>The requested resource is not found</h1>");
                    return;
                }

                if (Files.isDirectory(path)){
                    //TODO show html listing for directory with links to files

                    fillHeaders(HttpStatus.OK);
                    fillBody(showFilesInDirectory(path));

                } else{
                    sendFile(path);
                }

                break;
            case "POST":
                break;
            default:
                break;
        }
    }

    private String showFilesInDirectory(Path path) {
        final StringBuilder sb = new StringBuilder("");
        try {
            Files.walk(path,1)
                    .collect(Collectors.toList())
                    .forEach(e -> sb.append(createHyperReference(e) + "\n"));
        } catch (IOException e) {
            logger.debug("", e);
        }
        return sb.toString();
    }

    private String createHyperReference(Path path) {
        StringBuilder sb = new StringBuilder("<p><a href=\"\\");
        sb.append(path.toString() + "\">");
        if (path.toString().contains("/"))
            sb.append(path.toString().substring(path.toString().lastIndexOf("/")));
        else sb.append(path.toString());
        sb.append("</a></p>");
        return sb.toString();
    }

    public void write(OutputStream os) throws IOException {
        //write headers
        headers.forEach(s -> writeString(os, s));
        //write empty line
        writeString(os,"");
        //write body
        os.write(body);
    }

    private void writeString(OutputStream os, String str) {
        try {
            os.write((str + "\r\n").getBytes(UTF_8));
        } catch (IOException e) {
            logger.error("", e);
        }
    }

    private void fillHeaders(HttpStatus status) {
        headers.add(VERSION + " " + status);
        headers.add("Server: Simple Http Server");
        headers.add("Connection: close");
    }

    private void fillBody(String s) {
        body = s.getBytes(UTF_8);
    }

    private void sendFile(Path path) {
        try {
            body = Files.readAllBytes(path);
            fillHeaders(HttpStatus.OK);
        } catch (IOException e) {
            logger.error("", e);
            fillHeaders(HttpStatus.SERVER_ERROR);
            fillBody("<p>Error showing file</p>");
        }
    }
}
