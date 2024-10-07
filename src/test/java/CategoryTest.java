import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class CategoryTest extends ApiTest{

    private ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Test GET /categories
     * Expected: 200 OK status with list of categories in response body
     */
    @Test
    public void test_get_categories_200() throws IOException, InterruptedException {
        // Send the request
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:4567/categories"))
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // Check response status code
        assertEquals(200, response.statusCode());

        // Check if response body is in JSON and contains list of categories
        assertNotNull(response.body());
        JsonNode jsonResponse = objectMapper.readTree(response.body());
        JsonNode responseCategories = jsonResponse.get("categories");
        assertTrue(responseCategories.isArray());
    }

    /**
     *
     * Test POST /categories
     * Input: body with title, description
     * Expected: 201 Created status with created object in response body
     */
    @Test
    public void test_post_categories_201() throws IOException, InterruptedException {
        // Category to be created
        Map<String, String> category = new HashMap<>();
        category.put("title", "School");
        category.put("description", "assignments to do");
        var requestBody = HttpRequest.BodyPublishers.ofString(objectMapper.writeValueAsString(category));

        // Send the request
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:4567/categories"))
                .POST(requestBody)
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // Check response status code
        assertEquals(201, response.statusCode());

        // Validate content of response body with request body
        assertNotNull(response.body());
        JsonNode jsonResponse = objectMapper.readTree(response.body());
        assertEquals(category.get("title"), jsonResponse.get("title").asText());
        assertEquals(category.get("description"), jsonResponse.get("description").asText());
    }
}
