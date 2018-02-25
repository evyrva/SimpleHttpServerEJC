package http;

public enum HttpStatus {
    OK("200 OK"),
    FORBIDDEN("403 Forbidden"),
    NOT_FOUND("404 Not Found"),
    SERVER_ERROR("500 Internal Server Error")
    ;

    final String status;

    HttpStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return status;
    }
}
