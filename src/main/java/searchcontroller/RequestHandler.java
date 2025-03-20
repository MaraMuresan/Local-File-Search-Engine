package searchcontroller;

public class RequestHandler {
    public String validateQuery(String query) {
        return query != null && !query.isBlank() ? query.trim() : null;
    }
}
