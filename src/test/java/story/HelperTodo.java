package story;


import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.Map;

public class HelperTodo {

    private static final HttpClient client = HttpClient.newHttpClient();

    /**
     * For get_todo.feature
     */
    public static HttpResponse<String> getSpecificTodo(String todoId, String queryString) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:4567/todos" + (!queryString.isEmpty() ? "?title=" + queryString : "/" + todoId)))
                .GET()
                .build();
        return client.send(request, HttpResponse.BodyHandlers.ofString());
    }

    /**
     * For create_todo.feature
     */
    public static HttpResponse<String> createTodo(String todoId, String title, boolean doneStatus, String description) throws IOException, InterruptedException {

        // Todo to be created
        Map<String, Object> todo = new HashMap<>();
        if (!todoId.isEmpty()) todo.put("id", todoId);
        todo.put("title", title);
        todo.put("doneStatus", doneStatus);
        todo.put("description", description);
        var requestBody = HttpRequest.BodyPublishers.ofString(CommonHelper.getStringFromObject(todo));

        // Send the request
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:4567/todos"))
                .POST(requestBody)
                .build();
        return client.send(request, HttpResponse.BodyHandlers.ofString());
    }

    /**
     * For delete_todo.feature
     */
    public static HttpResponse<String> deleteTodo(String todoId) throws IOException, InterruptedException {
        // Send the request
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(String.format("http://localhost:4567/todos/%s", todoId)))
                .DELETE()
                .build();
        return client.send(request, HttpResponse.BodyHandlers.ofString());
    }

    /**
     * For amend_todo.feature
     */
    public static HttpResponse<String> amendTodoPost(String todoId, String description) throws IOException, InterruptedException {
        // Todo to be created
        Map<String, Object> todo = new HashMap<>();
        todo.put("description", description);
        var requestBody = HttpRequest.BodyPublishers.ofString(CommonHelper.getStringFromObject(todo));

        // Send the request
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:4567/todos/" + todoId))
                .POST(requestBody)
                .build();
        return client.send(request, HttpResponse.BodyHandlers.ofString());
    }

    // /**
    //  * For amend_project.feature
    //  */
    // public static HttpResponse<String> amendProjectPost(String projectId, String title, String description) throws IOException, InterruptedException {
    //     // Project to be created
    //     Map<String, Object> project = new HashMap<>();
    //     project.put("title", title);
    //     project.put("description", description);
    //     var requestBody = HttpRequest.BodyPublishers.ofString(CommonHelper.getStringFromObject(project));

    //     // Send the request
    //     HttpRequest request = HttpRequest.newBuilder()
    //             .uri(URI.create("http://localhost:4567/projects/" + projectId))
    //             .POST(requestBody)
    //             .build();
    //     return client.send(request, HttpResponse.BodyHandlers.ofString());
    // }

    /**
     * For amend_todo.feature
     */
    public static HttpResponse<String> amendTodoPut(String todoId, String description) throws IOException, InterruptedException {
        // Todo to be created
        Map<String, Object> todo = new HashMap<>();
        // todo.put("doneStatus", doneStatus);
        todo.put("description", description);
        var requestBody = HttpRequest.BodyPublishers.ofString(CommonHelper.getStringFromObject(todo));

        // Send the request
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:4567/todos/" + todoId))
                .PUT(requestBody)
                .build();
        return client.send(request, HttpResponse.BodyHandlers.ofString());
    }

    public static HttpResponse<String> getAllTodos() throws IOException, InterruptedException {
        // Get all projects
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:4567/todos"))
                .GET()
                .build();
        return client.send(request, HttpResponse.BodyHandlers.ofString());
    }

}
