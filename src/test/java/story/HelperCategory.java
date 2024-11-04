package story;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.Map;

public class HelperCategory {

    private static final HttpClient client = HttpClient.newHttpClient();

    private static final String BASE_URL = "http://localhost:4567";

    private static final String CATEGORY_URL = BASE_URL.concat("/categories");

    // Category-related functions

    /**
     * For get_specific_category.feature
     */
    public static HttpResponse<String> getSpecificCategory(String categoryId, String queryString) throws IOException, InterruptedException {
        StringBuilder url = new StringBuilder(CATEGORY_URL);

        if (!queryString.isEmpty()) url.append("?title=").append(queryString);
        else url.append("/").append(categoryId);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url.toString()))
                .GET()
                .build();

        return client.send(request, HttpResponse.BodyHandlers.ofString());
    }

    /**
     * For create_category.feature
     */
    public static HttpResponse<String> createCategory(String categoryId, String title, String description) throws IOException, InterruptedException {
        // Category to be created
        Map<String, Object> category = new HashMap<>() {
            {
                if (!categoryId.isEmpty()) put("id", categoryId);
                put("title", title);
                put("description", description);
            }
        };
        var requestBody = HttpRequest.BodyPublishers.ofString(CommonHelper.getStringFromObject(category));

        // Send the request
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(CATEGORY_URL))
                .POST(requestBody)
                .build();

        return client.send(request, HttpResponse.BodyHandlers.ofString());
    }

    /**
     * For amend_category.feature
     */
    public static HttpResponse<String> amendCategoryPost(String categoryId, String title, String description) throws IOException, InterruptedException {
        // Category to be created
        Map<String, Object> category = new HashMap<>() {
            {
                put("title", title);
                put("description", description);
            }
        };
        var requestBody = HttpRequest.BodyPublishers.ofString(CommonHelper.getStringFromObject(category));

        // Send the request
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(String.format("%s/%s", CATEGORY_URL, categoryId)))
                .POST(requestBody)
                .build();

        return client.send(request, HttpResponse.BodyHandlers.ofString());
    }

    /**
     * For amend_category.feature
     */
    public static HttpResponse<String> amendCategoryPut(String categoryId, String title, String description) throws IOException, InterruptedException {
        // Category to be created
        Map<String, Object> category = new HashMap<>() {
            {
                put("title", title);
                put("description", description);
            }
        };
        var requestBody = HttpRequest.BodyPublishers.ofString(CommonHelper.getStringFromObject(category));

        // Send the request
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(String.format("%s/%s", CATEGORY_URL, categoryId)))
                .PUT(requestBody)
                .build();

        return client.send(request, HttpResponse.BodyHandlers.ofString());
    }

    /**
     * For associate_category_todo.feature
     */
    public static HttpResponse<String> associateCategoryTodo(String categoryId, String todoId, String todoTitle) throws IOException, InterruptedException {
        Map<String, Object> todo = new HashMap<>() {
            {
                if (!todoId.isEmpty()) put("id", todoId);
                else put("title", todoTitle);
            }
        };
        var requestBody = HttpRequest.BodyPublishers.ofString(CommonHelper.getStringFromObject(todo));

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(String.format("%s/%s/todos", CATEGORY_URL, categoryId)))
                .POST(requestBody)
                .build();

        return client.send(request, HttpResponse.BodyHandlers.ofString());
    }

    /**
     * For associate_category_todo.feature
     */
    public static HttpResponse<String> getCategoryTodos(String categoryId) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(String.format("%s/%s/todos", CATEGORY_URL, categoryId)))
                .GET()
                .build();

        return client.send(request, HttpResponse.BodyHandlers.ofString());
    }

    /**
     * For delete_category.feature
     */
    public static HttpResponse<String> deleteCategory(String categoryId) throws IOException, InterruptedException {
        // Send the request
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(String.format("%s/%s", CATEGORY_URL, categoryId)))
                .DELETE()
                .build();

        return client.send(request, HttpResponse.BodyHandlers.ofString());
    }

    /**
     * For delete_category.feature
     */
    public static HttpResponse<String> getAllTodos() throws IOException, InterruptedException {
        // Send the request
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(String.format("%s/todos", BASE_URL)))
                .GET()
                .build();

        return client.send(request, HttpResponse.BodyHandlers.ofString());
    }

    /**
     * For associate_category_todo.feature and delete_category.feature
     */
    public static HttpResponse<String> associateTodoCategory(String todoId, String categoryId) throws IOException, InterruptedException {
        Map<String, Object> category = new HashMap<>() {
            {
                put("id", categoryId);
            }
        };
        var requestBody = HttpRequest.BodyPublishers.ofString(CommonHelper.getStringFromObject(category));

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(String.format("%s/todos/%s/categories", BASE_URL, todoId)))
                .POST(requestBody)
                .build();

        return client.send(request, HttpResponse.BodyHandlers.ofString());
    }

    /**
     * For associate_category_todo.feature and delete_category.feature
     */
    public static HttpResponse<String> getTodoCategories(String todoId) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(String.format("%s/todos/%s/categories", BASE_URL, todoId)))
                .GET()
                .build();

        return client.send(request, HttpResponse.BodyHandlers.ofString());
    }
}
