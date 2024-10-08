import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class TodoTest extends ApiTest{
    private final Map<String, String> school_todo = new HashMap<>() {{
        put("title", "School");
        put("description", "Assignments to do");
    }};

    private final Map<String, String> work_todo = new HashMap<>() {{
        put("title", "Work");
        put("description", "Work to do");
    }};

    private final ObjectMapper objectMapper = new ObjectMapper();

    // ----------------------- /todos -----------------------

    /**
     * DOCUMENTED
     * Test GET /todos
     * Input: N/A
     * Expected: 200 OK status with list of todos in response body
     */
    @Test
    public void test_get_todos_200() throws IOException, InterruptedException {
        // Send the request
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:4567/todos"))
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // Check response status code
        assertEquals(200, response.statusCode());

        // Check if response body is in JSON and contains list of todos
        assertNotNull(response.body());
        JsonNode jsonResponse = objectMapper.readTree(response.body());
        JsonNode responseTodos = jsonResponse.get("todos");
        assertTrue(responseTodos.isArray());
    }

    /**
     * UNDOCUMENTED
     * Test PUT /todos
     * Input: N/A
     * Expected: 405 Method Not Allowed
     */
    @Test
    public void test_put_todos_405() throws IOException, InterruptedException {
        // Todo to be created
        var requestBody = HttpRequest.BodyPublishers.ofString(objectMapper.writeValueAsString(school_todo));

        // Send the request
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:4567/todos"))
                .PUT(requestBody)
                .build();
        HttpResponse<Void> response = client.send(request, HttpResponse.BodyHandlers.discarding());

        // Check response status code
        assertEquals(405, response.statusCode());
    }

    /**
     * DOCUMENTED
     * Test POST /todos
     * Input: body with title, description
     * Expected: 201 Created status with created object in response body
     */
    @Test
    public void test_post_todos_201() throws IOException, InterruptedException {
        // Todo to be created
        var requestBody = HttpRequest.BodyPublishers.ofString(objectMapper.writeValueAsString(school_todo));

        // Send the request
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:4567/todos"))
                .POST(requestBody)
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // Check response status code
        assertEquals(201, response.statusCode());

        // Validate content of response body with request body
        assertNotNull(response.body());
        JsonNode jsonResponse = objectMapper.readTree(response.body());
        assertEquals(school_todo.get("title"), jsonResponse.get("title").asText());
        assertEquals(school_todo.get("description"), jsonResponse.get("description").asText());
    }

   /**
     * DOCUMENTED
     * Test POST /todos
     * Input: body with title, description, color
     * Expected: 400 Bad Request with error message
     */
    @Test
    public void test_post_todos_400() throws IOException, InterruptedException {
        // todo to be created
        school_todo.put("color", "blue");
        var requestBody = HttpRequest.BodyPublishers.ofString(objectMapper.writeValueAsString(school_todo));
        school_todo.remove("blue");

        // Send the request
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:4567/todos"))
                .POST(requestBody)
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // Check response status code
        assertEquals(400, response.statusCode());

        // Validate content of response body with request body
        validateErrorMessage(objectMapper, response);
    }

    /**
     * UNDOCUMENTED
     * Test DELETE /todos
     * Input: N/A
     * Expected: 405 Method Not Allowed
     */
    @Test
    public void test_delete_todos_405() throws IOException, InterruptedException {
        // Send the request
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:4567/todos"))
                .DELETE()
                .build();
        HttpResponse<Void> response = client.send(request, HttpResponse.BodyHandlers.discarding());

        // Check response status code
        assertEquals(405, response.statusCode());
    }

    /**
     * UNDOCUMENTED
     * Test OPTIONS /todos
     * Input: N/A
     * Expected: 200 OK with endpoint options
     */
    @Test
    public void test_option_todos_200() throws IOException, InterruptedException {
        // Send the request
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:4567/todos"))
                .method("OPTIONS", HttpRequest.BodyPublishers.noBody())
                .build();
        HttpResponse<Void> response = client.send(request, HttpResponse.BodyHandlers.discarding());

        // Check response status code
        assertEquals(200, response.statusCode());

        // Get the headers from the response
        Map<String, List<String>> headers = response.headers().map();
        List<String> options = Arrays.asList(headers.get("Allow").get(0).split(",\\s*"));
        assertTrue(options.containsAll(Arrays.asList("OPTIONS", "GET", "HEAD", "POST")));
    }

    /**
     * DOCUMENTED
     * Test HEAD /todos
     * Input: N/A
     * Expected: 200 OK, returning headers
     */
    @Test
    public void test_head_todos_200() throws IOException, InterruptedException {
        // Send the request
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:4567/todos"))
                .method("HEAD", HttpRequest.BodyPublishers.noBody())
                .build();
        HttpResponse<Void> response = client.send(request, HttpResponse.BodyHandlers.discarding());

        // Check response status code
        assertEquals(200, response.statusCode());

        // Get the headers from the response
        Map<String, List<String>> headers = response.headers().map();
        assertFalse(headers.isEmpty());
    }

    /**
     * UNDOCUMENTED
     * Test PATCH /todos
     * Input: N/A
     * Expected: 405 Method Not Allowed
     */
    @Test
    public void test_patch_todos_405() throws IOException, InterruptedException {
        // Send the request
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:4567/todos"))
                .method("PATCH", HttpRequest.BodyPublishers.noBody())
                .build();
        HttpResponse<Void> response = client.send(request, HttpResponse.BodyHandlers.discarding());

        // Check response status code
        assertEquals(405, response.statusCode());
    }

    // ----------------------- /todos/:id -----------------------

    /**
     * DOCUMENTED
     * Test GET /todos/:id
     * Input: path variable id
     * Expected: 200 OK with todos of id in response body
     */
    @Test
    public void test_get_todos_id_200() throws IOException, InterruptedException {
        String id = "1";

        // Send the request
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(String.format("http://localhost:4567/todos/%s", id)))
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // Check response status code
        assertEquals(200, response.statusCode());

        // Check if response body is in JSON and contains list of todos
        assertNotNull(response.body());
        JsonNode jsonResponse = objectMapper.readTree(response.body());
        JsonNode responseTodos = jsonResponse.get("todos");
        assertTrue(responseTodos.isArray());
    }

    /**
     * DOCUMENTED
     * Test GET /todos/:id
     * Input: invalid path variable id
     * Expected: 404 Not Found with error message
     */
    @Test
    public void test_get_todos_id_404() throws IOException, InterruptedException {
        String id = "10";

        // Send the request
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(String.format("http://localhost:4567/todos/%s", id)))
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // Check response status code
        assertEquals(404, response.statusCode());

        // Validate content of response body with request body
        validateErrorMessage(objectMapper, response);
    }

    /**
     * DOCUMENTED
     * Test PUT /todos/:id
     * Input: path variable id, request body with title, description
     * Expected: 200 OK with todo of id in response body
     */
    @Test
    public void test_put_todos_id_200() throws IOException, InterruptedException, ParserConfigurationException, SAXException {
        String id = "1";

        // Todo to be edited XML
        String todo = "<todo>"
                + "<title>" + work_todo.get("title") + "</title>"
                + "<description>" + work_todo.get("description") + "</description>"
                + "</todo>";
        var requestBody = HttpRequest.BodyPublishers.ofString(todo);

        // Send the request
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(String.format("http://localhost:4567/todos/%s", id)))
                .header("Content-Type", "application/xml") // Send as XML
                .header("Accept", "application/xml") // Expect XML in response
                .PUT(requestBody)
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // Check response status code
        assertEquals(200, response.statusCode());

        // Validate XML content of response body with request body
        assertNotNull(response.body());
        Document doc = parseXmlResponse(response.body());
        assertEquals(work_todo.get("title"), doc.getElementsByTagName("title").item(0).getTextContent());
        assertEquals(work_todo.get("description"), doc.getElementsByTagName("description").item(0).getTextContent());
    }

    /**
     * DOCUMENTED
     * Test PUT /todos/:id
     * Input: invalid path variable id, request body with title, description
     * Expected: 404 Not Found with error message
     */
    @Test
    public void test_put_todos_id_404() throws IOException, InterruptedException {
        String id = "10";

        // Todo to be edited
        var requestBody = HttpRequest.BodyPublishers.ofString(objectMapper.writeValueAsString(work_todo));

        // Send the request
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(String.format("http://localhost:4567/todos/%s", id)))
                .PUT(requestBody)
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // Check response status code
        assertEquals(404, response.statusCode());

        // Validate content of response body with request body
        validateErrorMessage(objectMapper, response);
    }

    /**
     * DOCUMENTED
     * Test PUT /todos/:id
     * Input: path variable id, request body with title, description, color
     * Unexpected: 400 Bad Request with error message
     */
    @Test
    public void test_put_todos_id_400() throws IOException, InterruptedException {
        String id = "1";

        // Todo to be edited
        work_todo.put("color", "red");
        var requestBody = HttpRequest.BodyPublishers.ofString(objectMapper.writeValueAsString(work_todo));
        work_todo.remove("color");

        // Send the request
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(String.format("http://localhost:4567/todos/%s", id)))
                .PUT(requestBody)
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // Check response status code
        assertEquals(400, response.statusCode());

        // Validate content of response body with request body
        validateErrorMessage(objectMapper, response);
    }

    /**
     * DOCUMENTED
     * Test POST /todos/:id
     * Input: path variable id, request body with title, description
     * Expected: 200 OK with todo of id in response body
     */
    @Test
    public void test_post_todos_id_200() throws IOException, InterruptedException {
        String id = "1";

        // Todo to be edited
        var requestBody = HttpRequest.BodyPublishers.ofString(objectMapper.writeValueAsString(work_todo));

        // Send the request
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(String.format("http://localhost:4567/todos/%s", id)))
                .POST(requestBody)
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // Check response status code
        assertEquals(200, response.statusCode());

        // Validate content of response body with request body
        assertNotNull(response.body());
        JsonNode jsonResponse = objectMapper.readTree(response.body());
        assertEquals(work_todo.get("title"), jsonResponse.get("title").asText());
        assertEquals(work_todo.get("description"), jsonResponse.get("description").asText());
    }

    /**
     * DOCUMENTED
     * Test POST /todos/:id
     * Input: invalid path variable id, request body with title, description
     * Expected: 404 Not Found with error message
     */
    @Test
    public void test_post_todos_id_404() throws IOException, InterruptedException {
        String id = "10";

        // Todo to be edited
        var requestBody = HttpRequest.BodyPublishers.ofString(objectMapper.writeValueAsString(work_todo));

        // Send the request
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(String.format("http://localhost:4567/todos/%s", id)))
                .POST(requestBody)
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // Check response status code
        assertEquals(404, response.statusCode());

        // Validate content of response body with request body
        validateErrorMessage(objectMapper, response);
    }

    /**
     * DOCUMENTED
     * Test POST /todos/:id
     * Input: path variable id, request body with title, description, color
     * Unexpected: 400 Bad Request with error message
     */
    @Test
    public void test_post_todos_id_400() throws IOException, InterruptedException {
        String id = "1";

        // Todo to be edited
        work_todo.put("color", "yellow");
        var requestBody = HttpRequest.BodyPublishers.ofString(objectMapper.writeValueAsString(work_todo));
        work_todo.remove("color");

        // Send the request
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(String.format("http://localhost:4567/todos/%s", id)))
                .POST(requestBody)
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // Check response status code
        assertEquals(400, response.statusCode());

        // Validate content of response body with request body
        validateErrorMessage(objectMapper, response);
    }

    /**
     * DOCUMENTED
     * Test DELETE /todos/:id
     * Input: path variable id
     * Expected: 200 OK
     */
    @Test
    public void test_delete_todos_id_200() throws IOException, InterruptedException {
        // Todo to be deleted
        var requestBody = HttpRequest.BodyPublishers.ofString(objectMapper.writeValueAsString(school_todo));

        // Send the request
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:4567/todos"))
                .POST(requestBody)
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // Check response status code
        assertEquals(201, response.statusCode());

        // Get id of todo created
        JsonNode jsonResponse = objectMapper.readTree(response.body());
        String id = jsonResponse.get("id").asText();

        // Send the request
        request = HttpRequest.newBuilder()
                .uri(URI.create(String.format("http://localhost:4567/todos/%s", id)))
                .DELETE()
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // Check response status code
        assertEquals(200, response.statusCode());
    }

    /**
     * DOCUMENTED
     * Test DELETE /todos/:id
     * Input: invalid path variable id
     * Expected: 404 Not Found with error message
     */
    @Test
    public void test_delete_todos_id_404() throws IOException, InterruptedException {
        String id = "10";

        // Send the request
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(String.format("http://localhost:4567/todos/%s", id)))
                .DELETE()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // Check response status code
        assertEquals(404, response.statusCode());

        // Validate content of response body with request body
        validateErrorMessage(objectMapper, response);
    }

    /**
     * UNDOCUMENTED
     * Test OPTIONS /todos/:id
     * Input: path variable id
     * Expected: 200 OK with endpoint options
     */
    @Test
    public void test_option_todos_id_200() throws IOException, InterruptedException {
        String id = "1";

        // Send the request
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(String.format("http://localhost:4567/todos/%s", id)))
                .method("OPTIONS", HttpRequest.BodyPublishers.noBody())
                .build();
        HttpResponse<Void> response = client.send(request, HttpResponse.BodyHandlers.discarding());

        // Check response status code
        assertEquals(200, response.statusCode());

        // Get the headers from the response
        Map<String, List<String>> headers = response.headers().map();
        List<String> options = Arrays.asList(headers.get("Allow").get(0).split(",\\s*"));
        assertTrue(options.containsAll(Arrays.asList("OPTIONS", "GET", "HEAD", "POST", "PUT", "DELETE")));
    }
    
    /**
     * DOCUMENTED
     * Test HEAD /todos/:id
     * Input: path variable id
     * Expected: 200 OK, returning headers
     */
    @Test
    public void test_head_todos_id_200() throws IOException, InterruptedException {
        String id = "1";

        // Send the request
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(String.format("http://localhost:4567/todos/%s", id)))
                .method("HEAD", HttpRequest.BodyPublishers.noBody())
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // Check response status code
        assertEquals(200, response.statusCode());

        // Get the headers from the response
        Map<String, List<String>> headers = response.headers().map();
        assertFalse(headers.isEmpty());
    }

    /**
     * DOCUMENTED
     * Test HEAD /todos/:id
     * Input: invalid path variable id
     * Expected: 404 Not Found with error message
     */
    @Test
    public void test_head_todos_id_404() throws IOException, InterruptedException {
        String id = "10";

        // Send the request
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(String.format("http://localhost:4567/todos/%s", id)))
                .method("HEAD", HttpRequest.BodyPublishers.noBody())
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // Check response status code
        assertEquals(404, response.statusCode());
    }

    /**
     * UNDOCUMENTED
     * Test PATCH /todos/:id
     * Input: path variable id
     * Expected: 405 Method Not Allowed
     */
    @Test
    public void test_patch_todos_id_405() throws IOException, InterruptedException {
        String id = "1";

        // Send the request
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(String.format("http://localhost:4567/todos/%s", id)))
                .method("PATCH", HttpRequest.BodyPublishers.noBody())
                .build();
        HttpResponse<Void> response = client.send(request, HttpResponse.BodyHandlers.discarding());

        // Check response status code
        assertEquals(405, response.statusCode());
    }

    // ----------------------- /todos/:id/categories -----------------------
    // ----------------------- /todos/:id/categories/:id -----------------------
    // ----------------------- /todos/:id/taskof -----------------------
    // ----------------------- /todos/:id/taskof/:id -----------------------
}
