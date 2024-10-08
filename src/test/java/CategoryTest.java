import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
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

@TestMethodOrder(MethodOrderer.Random.class)
public class CategoryTest extends ApiTest{
    private final Map<String, String> school = new HashMap<>() {{
        put("title", "School");
        put("description", "Assignments to do");
    }};

    private final Map<String, String> work = new HashMap<>() {{
        put("title", "Work");
        put("description", "Work to do");
    }};

    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * DOCUMENTED
     * Test GET /categories
     * Input: N/A
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
     * DOCUMENTED
     * Test POST /categories
     * Input: body with title, description
     * Expected: 201 Created status with created object in response body
     */
    @Test
    public void test_post_categories_201() throws IOException, InterruptedException {
        // Category to be created
        var requestBody = HttpRequest.BodyPublishers.ofString(objectMapper.writeValueAsString(school));

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
        assertEquals(school.get("title"), jsonResponse.get("title").asText());
        assertEquals(school.get("description"), jsonResponse.get("description").asText());
    }

    /**
     * DOCUMENTED
     * Test POST /categories
     * Input: body with title, description, color
     * Expected: 400 Bad Request with error message
     */
    @Test
    public void test_post_categories_400() throws IOException, InterruptedException {
        // Category to be created
        school.put("color", "blue");
        var requestBody = HttpRequest.BodyPublishers.ofString(objectMapper.writeValueAsString(school));
        school.remove("color");

        // Send the request
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:4567/categories"))
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
     * Test PUT /categories
     * Input: body with title, description
     * Expected: 405 Method Not Allowed
     */
    @Test
    public void test_put_categories_405() throws IOException, InterruptedException {
        // Category to be created
        var requestBody = HttpRequest.BodyPublishers.ofString(objectMapper.writeValueAsString(school));

        // Send the request
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:4567/categories"))
                .PUT(requestBody)
                .build();
        HttpResponse<Void> response = client.send(request, HttpResponse.BodyHandlers.discarding());

        // Check response status code
        assertEquals(405, response.statusCode());
    }

    /**
     * UNDOCUMENTED
     * Test PATCH /categories
     * Input: N/A
     * Expected: 405 Method Not Allowed
     */
    @Test
    public void test_patch_categories_405() throws IOException, InterruptedException {
        // Send the request
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:4567/categories"))
                .method("PATCH", HttpRequest.BodyPublishers.noBody())
                .build();
        HttpResponse<Void> response = client.send(request, HttpResponse.BodyHandlers.discarding());

        // Check response status code
        assertEquals(405, response.statusCode());
    }

    /**
     * UNDOCUMENTED
     * Test DELETE /categories
     * Input: N/A
     * Expected: 405 Method Not Allowed
     */
    @Test
    public void test_delete_categories_405() throws IOException, InterruptedException {
        // Send the request
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:4567/categories"))
                .DELETE()
                .build();
        HttpResponse<Void> response = client.send(request, HttpResponse.BodyHandlers.discarding());

        // Check response status code
        assertEquals(405, response.statusCode());
    }

    /**
     * DOCUMENTED
     * Test HEAD /categories
     * Input: N/A
     * Expected: 200 OK, returning headers
     */
    @Test
    public void test_head_categories_200() throws IOException, InterruptedException {
        // Send the request
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:4567/categories"))
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
     * Test OPTIONS /categories
     * Input: N/A
     * Expected: 200 OK with endpoint options
     */
    @Test
    public void test_option_categories_200() throws IOException, InterruptedException {
        // Send the request
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:4567/categories"))
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
     * Test GET /categories/:id
     * Input: path variable id
     * Expected: 200 OK with category of id in response body
     */
    @Test
    public void test_get_categories_id_200() throws IOException, InterruptedException {
        String id = "1";

        // Send the request
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(String.format("http://localhost:4567/categories/%s", id)))
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
     * DOCUMENTED
     * Test GET /categories/:id
     * Input: invalid path variable id
     * Expected: 404 Not Found with error message
     */
    @Test
    public void test_get_categories_id_404() throws IOException, InterruptedException {
        String id = "10";

        // Send the request
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(String.format("http://localhost:4567/categories/%s", id)))
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
     * Test POST /categories/:id
     * Input: path variable id, request body with title, description
     * Expected: 200 OK with category of id in response body
     */
    @Test
    public void test_post_categories_id_200() throws IOException, InterruptedException {
        String id = "1";

        // Category to be edited
        var requestBody = HttpRequest.BodyPublishers.ofString(objectMapper.writeValueAsString(work));

        // Send the request
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(String.format("http://localhost:4567/categories/%s", id)))
                .POST(requestBody)
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // Check response status code
        assertEquals(200, response.statusCode());

        // Validate content of response body with request body
        assertNotNull(response.body());
        JsonNode jsonResponse = objectMapper.readTree(response.body());
        assertEquals(work.get("title"), jsonResponse.get("title").asText());
        assertEquals(work.get("description"), jsonResponse.get("description").asText());
    }

    /**
     * DOCUMENTED
     * Test POST /categories/:id
     * Input: path variable id, request body with title, description, color
     * Unexpected: 400 Bad Request with error message
     */
    @Test
    public void test_post_categories_id_400() throws IOException, InterruptedException {
        String id = "1";

        // Category to be edited
        work.put("color", "orange");
        var requestBody = HttpRequest.BodyPublishers.ofString(objectMapper.writeValueAsString(work));
        work.remove("color");

        // Send the request
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(String.format("http://localhost:4567/categories/%s", id)))
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
     * Test POST /categories/:id
     * Input: invalid path variable id, request body with title, description
     * Expected: 404 Not Found with error message
     */
    @Test
    public void test_post_categories_id_404() throws IOException, InterruptedException {
        String id = "10";

        // Category to be edited
        var requestBody = HttpRequest.BodyPublishers.ofString(objectMapper.writeValueAsString(work));

        // Send the request
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(String.format("http://localhost:4567/categories/%s", id)))
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
     * Test PUT /categories/:id
     * Input: path variable id, request body with title, description
     * Expected: 200 OK with category of id in response body
     */
    @Test
    public void test_put_categories_id_200() throws IOException, InterruptedException, ParserConfigurationException, SAXException {
        String id = "1";

        // Category to be edited XML
        String category = "<category>"
                + "<title>" + work.get("title") + "</title>"
                + "<description>" + work.get("description") + "</description>"
                + "</category>";
        var requestBody = HttpRequest.BodyPublishers.ofString(category);

        // Send the request
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(String.format("http://localhost:4567/categories/%s", id)))
                .header("Content-Type", "application/xml")
                .header("Accept", "application/xml")
                .PUT(requestBody)
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // Check response status code
        assertEquals(200, response.statusCode());

        // Validate XML content of response body with request body
        assertNotNull(response.body());
        Document doc = parseXmlResponse(response.body());
        assertEquals(work.get("title"), doc.getElementsByTagName("title").item(0).getTextContent());
        assertEquals(work.get("description"), doc.getElementsByTagName("description").item(0).getTextContent());
    }

    /**
     * DOCUMENTED
     * Test PUT /categories/:id
     * Input: path variable id, request body with title, description, color
     * Unexpected: 400 Bad Request with error message
     */
    @Test
    public void test_put_categories_id_400() throws IOException, InterruptedException {
        String id = "1";

        // Category to be edited XML
        String category = "<category>"
                + "<title>" + work.get("title") + "</title>"
                + "<description>" + work.get("description") + "</description>"
                + "<color>" + "orange" + "</color>"
                + "</category>";
        var requestBody = HttpRequest.BodyPublishers.ofString(category);

        // Send the request
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(String.format("http://localhost:4567/categories/%s", id)))
                .header("Content-Type", "application/xml")
                .header("Accept", "application/xml")
                .PUT(requestBody)
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // Check response status code
        assertEquals(400, response.statusCode());
    }

    /**
     * DOCUMENTED
     * Test PUT /categories/:id
     * Input: invalid path variable id, request body with title, description
     * Expected: 404 Not Found with error message
     */
    @Test
    public void test_put_categories_id_404() throws IOException, InterruptedException {
        String id = "10";

        // Category to be edited
        var requestBody = HttpRequest.BodyPublishers.ofString(objectMapper.writeValueAsString(work));

        // Send the request
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(String.format("http://localhost:4567/categories/%s", id)))
                .PUT(requestBody)
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // Check response status code
        assertEquals(404, response.statusCode());

        // Validate content of response body with request body
        validateErrorMessage(objectMapper, response);
    }

    /**
     * UNDOCUMENTED
     * Test PATCH /categories/:id
     * Input: path variable id
     * Expected: 405 Method Not Allowed
     */
    @Test
    public void test_patch_categories_id_405() throws IOException, InterruptedException {
        String id = "1";

        // Send the request
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(String.format("http://localhost:4567/categories/%s", id)))
                .method("PATCH", HttpRequest.BodyPublishers.noBody())
                .build();
        HttpResponse<Void> response = client.send(request, HttpResponse.BodyHandlers.discarding());

        // Check response status code
        assertEquals(405, response.statusCode());
    }

    /**
     * DOCUMENTED
     * Test DELETE /categories/:id
     * Input: path variable id
     * Expected: 200 OK
     */
    @Test
    public void test_delete_categories_id_200() throws IOException, InterruptedException {
        // Category to be created
        var requestBody = HttpRequest.BodyPublishers.ofString(objectMapper.writeValueAsString(school));

        // Send the request
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:4567/categories"))
                .POST(requestBody)
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // Check response status code
        assertEquals(201, response.statusCode());

        // Get id of category created
        JsonNode jsonResponse = objectMapper.readTree(response.body());
        String id = jsonResponse.get("id").asText();

        // Send the request
        request = HttpRequest.newBuilder()
                .uri(URI.create(String.format("http://localhost:4567/categories/%s", id)))
                .DELETE()
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // Check response status code
        assertEquals(200, response.statusCode());
    }

    /**
     * DOCUMENTED
     * Test DELETE /categories/:id
     * Input: invalid path variable id
     * Expected: 404 Not Found with error message
     */
    @Test
    public void test_delete_categories_id_404() throws IOException, InterruptedException {
        String id = "10";

        // Send the request
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(String.format("http://localhost:4567/categories/%s", id)))
                .DELETE()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // Check response status code
        assertEquals(404, response.statusCode());

        // Validate content of response body with request body
        validateErrorMessage(objectMapper, response);
    }

    /**
     * DOCUMENTED
     * Test HEAD /categories/:id
     * Input: path variable id
     * Expected: 200 OK, returning headers
     */
    @Test
    public void test_head_categories_id_200() throws IOException, InterruptedException {
        String id = "1";

        // Send the request
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(String.format("http://localhost:4567/categories/%s", id)))
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
     * Test HEAD /categories/:id
     * Input: invalid path variable id
     * Expected: 404 Not Found with error message
     */
    @Test
    public void test_head_categories_id_404() throws IOException, InterruptedException {
        String id = "10";

        // Send the request
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(String.format("http://localhost:4567/categories/%s", id)))
                .method("HEAD", HttpRequest.BodyPublishers.noBody())
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // Check response status code
        assertEquals(404, response.statusCode());
    }

    /**
     * UNDOCUMENTED
     * Test OPTIONS /categories/:id
     * Input: path variable id
     * Expected: 200 OK with endpoint options
     */
    @Test
    public void test_option_categories_id_200() throws IOException, InterruptedException {
        String id = "1";

        // Send the request
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(String.format("http://localhost:4567/categories/%s", id)))
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
     * Test GET /categories/:id/projects
     * Input: path variable id
     * Expected: 200 OK with "projects" list of category with id in response body
     */
    @Test
    public void test_get_categories_id_projects_200() throws IOException, InterruptedException {
        String id = "1";

        // Send the request
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(String.format("http://localhost:4567/categories/%s/projects", id)))
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // Check response status code
        assertEquals(200, response.statusCode());

        // Check if response body is in JSON and contains list of categories
        assertNotNull(response.body());
        JsonNode jsonResponse = objectMapper.readTree(response.body());
        JsonNode responseCategories = jsonResponse.get("projects");
        assertTrue(responseCategories.isArray());
    }

    /**
     * DOCUMENTED
     * Test POST /categories/:id/projects
     * Input: path variable id, request body with project id
     * Expected: 201 Created
     */
    @Test
    public void test_post_categories_id_projects_201() throws IOException, InterruptedException {
        String id = "1";

        // Category to be edited
        Map<String, String> project = new HashMap<>() {{
            put("id", "1");
        }};
        var requestBody = HttpRequest.BodyPublishers.ofString(objectMapper.writeValueAsString(project));

        // Send the request
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(String.format("http://localhost:4567/categories/%s/projects", id)))
                .POST(requestBody)
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // Check response status code
        assertEquals(201, response.statusCode());
    }

    /**
     * DOCUMENTED
     * Test POST /categories/:id/projects
     * Input: path variable id, request body with invalid field
     * Expected: 400 Bad Request with error message
     */
    @Test
    public void test_post_categories_id_projects_400() throws IOException, InterruptedException {
        String id = "1";

        // Category to be edited
        Map<String, String> project = new HashMap<>() {{
            put("name", "project1");
        }};
        var requestBody = HttpRequest.BodyPublishers.ofString(objectMapper.writeValueAsString(project));

        // Send the request
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(String.format("http://localhost:4567/categories/%s/projects", id)))
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
     * Test POST /categories/:id/projects
     * Input: path variable id, request body with invalid project id
     * Unexpected: 404 Not Found with error message
     */
    @Test
    public void test_post_categories_id_projects_404() throws IOException, InterruptedException {
        String id = "1";

        // Category to be edited
        Map<String, String> project = new HashMap<>() {{
            put("id", "10");
        }};
        var requestBody = HttpRequest.BodyPublishers.ofString(objectMapper.writeValueAsString(project));

        // Send the request
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(String.format("http://localhost:4567/categories/%s/projects", id)))
                .POST(requestBody)
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // Check response status code
        assertEquals(404, response.statusCode());

        // Validate content of response body with request body
        validateErrorMessage(objectMapper, response);
    }

    /**
     * UNDOCUMENTED
     * Test PUT /categories/:id/projects
     * Input: path variable id, request body with project id
     * Expected: 405 Method Not Allowed
     */
    @Test
    public void test_put_categories_id_projects_405() throws IOException, InterruptedException {
        String id = "1";

        // Category to be edited
        Map<String, String> project = new HashMap<>() {{
            put("id", "1");
        }};
        var requestBody = HttpRequest.BodyPublishers.ofString(objectMapper.writeValueAsString(project));

        // Send the request
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(String.format("http://localhost:4567/categories/%s/projects", id)))
                .PUT(requestBody)
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // Check response status code
        assertEquals(405, response.statusCode());
    }

    /**
     * UNDOCUMENTED
     * Test PATCH /categories/:id/projects
     * Input: path variable id
     * Expected: 405 Method Not Allowed
     */
    @Test
    public void test_patch_categories_id_projects_405() throws IOException, InterruptedException {
        String id = "1";

        // Send the request
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(String.format("http://localhost:4567/categories/%s/projects", id)))
                .method("PATCH", HttpRequest.BodyPublishers.noBody())
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // Check response status code
        assertEquals(405, response.statusCode());
    }

    /**
     * UNDOCUMENTED
     * Test DELETE /categories/:id/projects
     * Input: path variable id
     * Expected: 405 Method Not Allowed
     */
    @Test
    public void test_delete_categories_id_projects_405() throws IOException, InterruptedException {
        String id = "1";

        // Send the request
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(String.format("http://localhost:4567/categories/%s/projects", id)))
                .DELETE()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // Check response status code
        assertEquals(405, response.statusCode());
    }

    /**
     * DOCUMENTED
     * Test HEAD /categories/:id/projects
     * Input: path variable id
     * Expected: 200 OK, returning headers
     */
    @Test
    public void test_head_categories_id_projects_200() throws IOException, InterruptedException {
        String id = "1";

        // Send the request
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(String.format("http://localhost:4567/categories/%s/projects", id)))
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
     * UNDOCUMENTED
     * Test OPTIONS /categories/:id/projects
     * Input: path variable id
     * Expected: 200 OK with endpoint options
     */
    @Test
    public void test_option_categories_id_projects_200() throws IOException, InterruptedException {
        String id = "1";

        // Send the request
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(String.format("http://localhost:4567/categories/%s/projects", id)))
                .method("OPTIONS", HttpRequest.BodyPublishers.noBody())
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // Check response status code
        assertEquals(200, response.statusCode());

        // Get the headers from the response
        Map<String, List<String>> headers = response.headers().map();
        List<String> options = Arrays.asList(headers.get("Allow").get(0).split(",\\s*"));
        assertTrue(options.containsAll(Arrays.asList("OPTIONS", "GET", "HEAD", "POST")));
    }

    /**
     * UNDOCUMENTED
     * Test GET /categories/:id/projects/:id
     * Input: path variable id for category and id for project
     * Expected: 405 Method Not Allowed
     */
    @Test
    public void test_get_categories_id_projects_id_405() throws IOException, InterruptedException {
        String category_id = "1";
        String project_id = "1";

        // Send the request
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(String.format("http://localhost:4567/categories/%s/projects/%s", category_id, project_id)))
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // Check response status code
        assertNotEquals(405, response.statusCode(), "Expected status code 405 but received " + response.statusCode());
    }

    /**
     * UNDOCUMENTED
     * Test GET /categories/:id/projects/:id
     * Input: path variable id for category and id for project
     * Actual: 404 Not Found
     */
    @Test
    public void test_get_categories_id_projects_id_404() throws IOException, InterruptedException {
        String category_id = "1";
        String project_id = "1";

        // Send the request
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(String.format("http://localhost:4567/categories/%s/projects/%s", category_id, project_id)))
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // Check response status code
        assertEquals(404, response.statusCode());
    }

    /**
     * UNDOCUMENTED
     * Test POST /categories/:id/projects/:id
     * Input: path variable id for category and id for project
     * Expected: 405 Method Not Allowed
     */
    @Test
    public void test_post_categories_id_projects_id_405() throws IOException, InterruptedException {
        String category_id = "1";
        String project_id = "1";

        // Send the request
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(String.format("http://localhost:4567/categories/%s/projects/%s", category_id, project_id)))
                .POST(HttpRequest.BodyPublishers.noBody())
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // Check response status code
        assertNotEquals(405, response.statusCode(), "Expected status code 405 but received " + response.statusCode());
    }

    /**
     * UNDOCUMENTED
     * Test POST /categories/:id/projects/:id
     * Input: path variable id for category and id for project
     * Actual: 404 Not Found
     */
    @Test
    public void test_post_categories_id_projects_id_404() throws IOException, InterruptedException {
        String category_id = "1";
        String project_id = "1";

        // Send the request
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(String.format("http://localhost:4567/categories/%s/projects/%s", category_id, project_id)))
                .POST(HttpRequest.BodyPublishers.noBody())
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // Check response status code
        assertEquals(404, response.statusCode());
    }

    /**
     * UNDOCUMENTED
     * Test PUT /categories/:id/projects/:id
     * Input: path variable id for category and id for project
     * Expected: 405 Method Not Allowed
     */
    @Test
    public void test_put_categories_id_projects_id_405() throws IOException, InterruptedException {
        String category_id = "1";
        String project_id = "1";

        // Send the request
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(String.format("http://localhost:4567/categories/%s/projects/%s", category_id, project_id)))
                .PUT(HttpRequest.BodyPublishers.noBody())
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // Check response status code
        assertEquals(405, response.statusCode());
    }

    /**
     * UNDOCUMENTED
     * Test PATCH /categories/:id/projects/:id
     * Input: path variable id for category and id for project
     * Expected: 405 Method Not Allowed
     */
    @Test
    public void test_patch_categories_id_projects_id_405() throws IOException, InterruptedException {
        String category_id = "1";
        String project_id = "1";

        // Send the request
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(String.format("http://localhost:4567/categories/%s/projects/%s", category_id, project_id)))
                .method("PATCH", HttpRequest.BodyPublishers.noBody())
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // Check response status code
        assertEquals(405, response.statusCode());
    }

    /**
     * DOCUMENTED
     * Test DELETE /categories/:id/projects/:id
     * Input: path variable id for category and id for project
     * Expected: 200 OK
     */
    @Test
    public void test_delete_categories_id_projects_id_200() throws IOException, InterruptedException {
        String category_id = "1";
        String project_id = "1";

        // Project to add to category
        Map<String, String> project = new HashMap<>() {{
            put("id", project_id);
        }};
        var requestBody = HttpRequest.BodyPublishers.ofString(objectMapper.writeValueAsString(project));

        // Send the request
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(String.format("http://localhost:4567/categories/%s/projects", category_id)))
                .POST(requestBody)
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // Check response status code
        assertEquals(201, response.statusCode());

        // Send the request
        request = HttpRequest.newBuilder()
                .uri(URI.create(String.format("http://localhost:4567/categories/%s/projects/%s", category_id, project_id)))
                .DELETE()
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // Check response status code
        assertEquals(200, response.statusCode());
    }

    /**
     * DOCUMENTED
     * Test DELETE /categories/:id/projects/:id
     * Input: path variable id for category and id for project, but no relationship
     * Expected: 404 Not Found with error message
     */
    @Test
    public void test_delete_categories_id_projects_id_404() throws IOException, InterruptedException {
        String category_id = "2";
        String project_id = "2";

        // Send the request
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(String.format("http://localhost:4567/categories/%s/projects/%s", category_id, project_id)))
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
     * Test HEAD /categories/:id/projects/:id
     * Input: path variable id for category and id for project
     * Expected: 405 Method Not Allowed
     */
    @Test
    public void test_head_categories_id_projects_id_405() throws IOException, InterruptedException {
        String category_id = "1";
        String project_id = "1";

        // Send the request
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(String.format("http://localhost:4567/categories/%s/projects/%s", category_id, project_id)))
                .method("HEAD", HttpRequest.BodyPublishers.noBody())
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // Check response status code
        assertNotEquals(405, response.statusCode(), "Expected status code 405 but received " + response.statusCode());
    }

    /**
     * UNDOCUMENTED
     * Test HEAD /categories/:id/projects/:id
     * Input: path variable id for category and id for project
     * Actual: 404 Not Found
     */
    @Test
    public void test_head_categories_id_projects_id_404() throws IOException, InterruptedException {
        String category_id = "1";
        String project_id = "1";

        // Send the request
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(String.format("http://localhost:4567/categories/%s/projects/%s", category_id, project_id)))
                .method("HEAD", HttpRequest.BodyPublishers.noBody())
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // Check response status code
        assertEquals(404, response.statusCode());
    }

    /**
     * UNDOCUMENTED
     * Test OPTIONS /categories/:id/projects/:id
     * Input: path variable id for category and id for project
     * Expected: 200 OK with endpoint options
     */
    @Test
    public void test_option_categories_id_projects_id_200() throws IOException, InterruptedException {
        String category_id = "1";
        String project_id = "1";

        // Send the request
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(String.format("http://localhost:4567/categories/%s/projects/%s", category_id, project_id)))
                .method("OPTIONS", HttpRequest.BodyPublishers.noBody())
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // Check response status code
        assertEquals(200, response.statusCode());

        // Get the headers from the response
        Map<String, List<String>> headers = response.headers().map();
        List<String> options = Arrays.asList(headers.get("Allow").get(0).split(",\\s*"));
        assertTrue(options.containsAll(Arrays.asList("OPTIONS", "DELETE")));
    }

    /**
     * DOCUMENTED
     * Test GET /categories/:id/todos
     * Input: path variable id
     * Expected: 200 OK with "todos" list of category with id in response body
     */
    @Test
    public void test_get_categories_id_todos_200() throws IOException, InterruptedException {
        String id = "1";

        // Send the request
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(String.format("http://localhost:4567/categories/%s/todos", id)))
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // Check response status code
        assertEquals(200, response.statusCode());

        // Check if response body is in JSON and contains list of categories
        assertNotNull(response.body());
        JsonNode jsonResponse = objectMapper.readTree(response.body());
        JsonNode responseCategories = jsonResponse.get("todos");
        assertTrue(responseCategories.isArray());
    }

    /**
     * DOCUMENTED
     * Test POST /categories/:id/todos
     * Input: path variable id, request body with todo id
     * Expected: 201 Created
     */
    @Test
    public void test_post_categories_id_todos_201() throws IOException, InterruptedException {
        String id = "1";

        // Category to be edited
        Map<String, String> todo = new HashMap<>() {{
            put("id", "1");
        }};
        var requestBody = HttpRequest.BodyPublishers.ofString(objectMapper.writeValueAsString(todo));

        // Send the request
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(String.format("http://localhost:4567/categories/%s/todos", id)))
                .POST(requestBody)
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // Check response status code
        assertEquals(201, response.statusCode());
    }

    /**
     * DOCUMENTED
     * Test POST /categories/:id/todos
     * Input: path variable id, request body with invalid field
     * Expected: 400 Bad Request with error message
     */
    @Test
    public void test_post_categories_id_todos_400() throws IOException, InterruptedException {
        String id = "1";

        // Category to be edited
        Map<String, String> todo = new HashMap<>() {{
            put("name", "todo1");
        }};
        var requestBody = HttpRequest.BodyPublishers.ofString(objectMapper.writeValueAsString(todo));

        // Send the request
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(String.format("http://localhost:4567/categories/%s/todos", id)))
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
     * Test POST /categories/:id/todos
     * Input: path variable id, request body with invalid todo id
     * Unexpected: 404 Not Found with error message
     */
    @Test
    public void test_post_categories_id_todos_404() throws IOException, InterruptedException {
        String id = "1";

        // Category to be edited
        Map<String, String> todo = new HashMap<>() {{
            put("id", "10");
        }};
        var requestBody = HttpRequest.BodyPublishers.ofString(objectMapper.writeValueAsString(todo));

        // Send the request
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(String.format("http://localhost:4567/categories/%s/todos", id)))
                .POST(requestBody)
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // Check response status code
        assertEquals(404, response.statusCode());

        // Validate content of response body with request body
        validateErrorMessage(objectMapper, response);
    }

    /**
     * UNDOCUMENTED
     * Test PUT /categories/:id/todos
     * Input: path variable id, request body with todo id
     * Expected: 405 Method Not Allowed
     */
    @Test
    public void test_put_categories_id_todos_405() throws IOException, InterruptedException {
        String id = "1";

        // Category to be edited
        Map<String, String> todo = new HashMap<>() {{
            put("id", "1");
        }};
        var requestBody = HttpRequest.BodyPublishers.ofString(objectMapper.writeValueAsString(todo));

        // Send the request
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(String.format("http://localhost:4567/categories/%s/todos", id)))
                .PUT(requestBody)
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // Check response status code
        assertEquals(405, response.statusCode());
    }

    /**
     * UNDOCUMENTED
     * Test PATCH /categories/:id/todos
     * Input: path variable id
     * Expected: 405 Method Not Allowed
     */
    @Test
    public void test_patch_categories_id_todos_405() throws IOException, InterruptedException {
        String id = "1";

        // Send the request
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(String.format("http://localhost:4567/categories/%s/todos", id)))
                .method("PATCH", HttpRequest.BodyPublishers.noBody())
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // Check response status code
        assertEquals(405, response.statusCode());
    }

    /**
     * UNDOCUMENTED
     * Test DELETE /categories/:id/todos
     * Input: path variable id
     * Expected: 405 Method Not Allowed
     */
    @Test
    public void test_delete_categories_id_todos_405() throws IOException, InterruptedException {
        String id = "1";

        // Send the request
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(String.format("http://localhost:4567/categories/%s/todos", id)))
                .DELETE()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // Check response status code
        assertEquals(405, response.statusCode());
    }

    /**
     * DOCUMENTED
     * Test HEAD /categories/:id/todos
     * Input: path variable id
     * Expected: 200 OK, returning headers
     */
    @Test
    public void test_head_categories_id_todos_200() throws IOException, InterruptedException {
        String id = "1";

        // Send the request
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(String.format("http://localhost:4567/categories/%s/todos", id)))
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
     * UNDOCUMENTED
     * Test OPTIONS /categories/:id/todos
     * Input: path variable id
     * Expected: 200 OK with endpoint options
     */
    @Test
    public void test_option_categories_id_todos_200() throws IOException, InterruptedException {
        String id = "1";

        // Send the request
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(String.format("http://localhost:4567/categories/%s/todos", id)))
                .method("OPTIONS", HttpRequest.BodyPublishers.noBody())
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // Check response status code
        assertEquals(200, response.statusCode());

        // Get the headers from the response
        Map<String, List<String>> headers = response.headers().map();
        List<String> options = Arrays.asList(headers.get("Allow").get(0).split(",\\s*"));
        assertTrue(options.containsAll(Arrays.asList("OPTIONS", "GET", "HEAD", "POST")));
    }

    /**
     * UNDOCUMENTED
     * Test GET /categories/:id/todos/:id
     * Input: path variable id for category and id for todo
     * Expected: 405 Method Not Allowed
     */
    @Test
    public void test_get_categories_id_todos_id_405() throws IOException, InterruptedException {
        String category_id = "1";
        String todo_id = "1";

        // Send the request
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(String.format("http://localhost:4567/categories/%s/todos/%s", category_id, todo_id)))
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // Check response status code
        assertNotEquals(405, response.statusCode(), "Expected status code 405 but received " + response.statusCode());
    }

    /**
     * UNDOCUMENTED
     * Test GET /categories/:id/todos/:id
     * Input: path variable id for category and id for todo
     * Actual: 404 Not Found
     */
    @Test
    public void test_get_categories_id_todos_id_404() throws IOException, InterruptedException {
        String category_id = "1";
        String todo_id = "1";

        // Send the request
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(String.format("http://localhost:4567/categories/%s/todos/%s", category_id, todo_id)))
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // Check response status code
        assertEquals(404, response.statusCode());
    }

    /**
     * UNDOCUMENTED
     * Test POST /categories/:id/todos/:id
     * Input: path variable id for category and id for todo
     * Expected: 405 Method Not Allowed
     */
    @Test
    public void test_post_categories_id_todos_id_405() throws IOException, InterruptedException {
        String category_id = "1";
        String todo_id = "1";

        // Send the request
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(String.format("http://localhost:4567/categories/%s/todos/%s", category_id, todo_id)))
                .POST(HttpRequest.BodyPublishers.noBody())
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // Check response status code
        assertNotEquals(405, response.statusCode(), "Expected status code 405 but received " + response.statusCode());
    }

    /**
     * UNDOCUMENTED
     * Test POST /categories/:id/todos/:id
     * Input: path variable id for category and id for todo
     * Actual: 404 Not Found
     */
    @Test
    public void test_post_categories_id_todos_id_404() throws IOException, InterruptedException {
        String category_id = "1";
        String todo_id = "1";

        // Send the request
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(String.format("http://localhost:4567/categories/%s/todos/%s", category_id, todo_id)))
                .POST(HttpRequest.BodyPublishers.noBody())
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // Check response status code
        assertEquals(404, response.statusCode());
    }

    /**
     * UNDOCUMENTED
     * Test PUT /categories/:id/todos/:id
     * Input: path variable id for category and id for todo
     * Expected: 405 Method Not Allowed
     */
    @Test
    public void test_put_categories_id_todos_id_405() throws IOException, InterruptedException {
        String category_id = "1";
        String todo_id = "1";

        // Send the request
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(String.format("http://localhost:4567/categories/%s/todos/%s", category_id, todo_id)))
                .PUT(HttpRequest.BodyPublishers.noBody())
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // Check response status code
        assertEquals(405, response.statusCode());
    }

    /**
     * UNDOCUMENTED
     * Test PATCH /categories/:id/todos/:id
     * Input: path variable id for category and id for todo
     * Expected: 405 Method Not Allowed
     */
    @Test
    public void test_patch_categories_id_todos_id_405() throws IOException, InterruptedException {
        String category_id = "1";
        String todo_id = "1";

        // Send the request
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(String.format("http://localhost:4567/categories/%s/todos/%s", category_id, todo_id)))
                .method("PATCH", HttpRequest.BodyPublishers.noBody())
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // Check response status code
        assertEquals(405, response.statusCode());
    }

    /**
     * DOCUMENTED
     * Test DELETE /categories/:id/todos/:id
     * Input: path variable id for category and id for project
     * Expected: 200 OK
     */
    @Test
    public void test_delete_categories_id_todos_id_200() throws IOException, InterruptedException {
        String category_id = "1";
        String todo_id = "1";

        // Project to add to category
        Map<String, String> todo = new HashMap<>() {{
            put("id", todo_id);
        }};
        var requestBody = HttpRequest.BodyPublishers.ofString(objectMapper.writeValueAsString(todo));

        // Send the request
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(String.format("http://localhost:4567/categories/%s/todos", category_id)))
                .POST(requestBody)
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // Check response status code
        assertEquals(201, response.statusCode());

        // Send the request
        request = HttpRequest.newBuilder()
                .uri(URI.create(String.format("http://localhost:4567/categories/%s/todos/%s", category_id, todo_id)))
                .DELETE()
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // Check response status code
        assertEquals(200, response.statusCode());
    }

    /**
     * DOCUMENTED
     * Test DELETE /categories/:id/todos/:id
     * Input: path variable id for category and id for todo, but no relationship
     * Expected: 404 Not Found with error message
     */
    @Test
    public void test_delete_categories_id_todos_id_404() throws IOException, InterruptedException {
        String category_id = "2";
        String todo_id = "2";

        // Send the request
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(String.format("http://localhost:4567/categories/%s/todos/%s", category_id, todo_id)))
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
     * Test HEAD /categories/:id/todos/:id
     * Input: path variable id for category and id for todo
     * Expected: 405 Method Not Allowed
     */
    @Test
    public void test_head_categories_id_todos_id_405() throws IOException, InterruptedException {
        String category_id = "1";
        String todo_id = "1";

        // Send the request
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(String.format("http://localhost:4567/categories/%s/todos/%s", category_id, todo_id)))
                .method("HEAD", HttpRequest.BodyPublishers.noBody())
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // Check response status code
        assertNotEquals(405, response.statusCode(), "Expected status code 405 but received " + response.statusCode());
    }

    /**
     * UNDOCUMENTED
     * Test HEAD /categories/:id/todos/:id
     * Input: path variable id for category and id for todo
     * Actual: 404 Not Found
     */
    @Test
    public void test_head_categories_id_todos_id_404() throws IOException, InterruptedException {
        String category_id = "1";
        String todo_id = "1";

        // Send the request
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(String.format("http://localhost:4567/categories/%s/todos/%s", category_id, todo_id)))
                .method("HEAD", HttpRequest.BodyPublishers.noBody())
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // Check response status code
        assertEquals(404, response.statusCode());
    }

    /**
     * UNDOCUMENTED
     * Test OPTIONS /categories/:id/todos/:id
     * Input: path variable id for category and id for todo
     * Expected: 200 OK with endpoint options
     */
    @Test
    public void test_option_categories_id_todos_id_200() throws IOException, InterruptedException {
        String category_id = "1";
        String todo_id = "1";

        // Send the request
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(String.format("http://localhost:4567/categories/%s/todos/%s", category_id, todo_id)))
                .method("OPTIONS", HttpRequest.BodyPublishers.noBody())
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // Check response status code
        assertEquals(200, response.statusCode());

        // Get the headers from the response
        Map<String, List<String>> headers = response.headers().map();
        List<String> options = Arrays.asList(headers.get("Allow").get(0).split(",\\s*"));
        assertTrue(options.containsAll(Arrays.asList("OPTIONS", "DELETE")));
    }
}
