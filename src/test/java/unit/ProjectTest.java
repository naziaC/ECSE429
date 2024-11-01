package unit;

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
public class ProjectTest extends ApiTest {
    private final Map<String, Object> project = new HashMap<>() {
        {
            put("title", "Project");
            put("active", false);
            put("description", "Project Description");
        }
    };

    private final Map<String, String> amendedProject = new HashMap<>() {
        {
            put("title", "Amended Project");
            put("description", "AmendedProject Description");
        }
    };

    private final ObjectMapper objectMapper = new ObjectMapper();

    // ---------------- /projects ----------------------

    /**
     * DOCUMENTED
     * Test GET /projects
     * Input: N/A
     * Expected: 200 OK status with all the projects in response body
     */
    @Test
    public void test_get_projects_200() throws IOException, InterruptedException {
        // Send the request
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:4567/projects"))
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // Check response status code
        assertEquals(200, response.statusCode());

        // Check if response body is in JSON and contains list of projects
        assertNotNull(response.body());
        JsonNode jsonResponse = objectMapper.readTree(response.body());
        JsonNode responseProjects = jsonResponse.get("projects");
        assertTrue(responseProjects.isArray());
    }

    /**
     * DOCUMENTED
     * Test PUT /projects
     * Input: project
     * Expected: 405 Method Not Allowed
     */
    @Test
    public void test_put_projects_405() throws IOException, InterruptedException {
        // Project to be created
        var requestBody = HttpRequest.BodyPublishers.ofString(objectMapper.writeValueAsString(project));

        // Send the request
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:4567/projects"))
                .PUT(requestBody)
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // Check response status code
        assertEquals(405, response.statusCode());

        // Check if response body is empty
        assertEquals("", response.body());
    }

    /**
     * DOCUMENTED
     * Test POST /projects
     * Input: project
     * Expected: 201 Created status with created object in response body
     */
    @Test
    public void test_post_projects_201() throws IOException, InterruptedException {
        // Project to be created
        var requestBody = HttpRequest.BodyPublishers.ofString(objectMapper.writeValueAsString(project));

        // Send the request
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:4567/projects"))
                .POST(requestBody)
                .build();
        HttpResponse<String> firstResponse = client.send(request, HttpResponse.BodyHandlers.ofString());

        // Check response status code
        assertEquals(201, firstResponse.statusCode());

        // Validate content of response body with request body
        assertNotNull(firstResponse.body());
        JsonNode firstJsonResponse = objectMapper.readTree(firstResponse.body());
        assertEquals(project.get("title"), firstJsonResponse.get("title").asText());
        assertEquals(project.get("description"), firstJsonResponse.get("description").asText());
        assertEquals(project.get("active").toString(), firstJsonResponse.get("active").asText());

        // Sending request again
        HttpResponse<String> secondResponse = client.send(request, HttpResponse.BodyHandlers.ofString());
        // Check response status code
        assertEquals(201, secondResponse.statusCode());

        // Validate that the response body is same as first response body with id being
        // different
        assertNotNull(secondResponse.body());
        JsonNode secondJsonResponse = objectMapper.readTree(secondResponse.body());
        assertEquals(project.get("title"), secondJsonResponse.get("title").asText());
        assertEquals(project.get("description"), secondJsonResponse.get("description").asText());
        assertEquals(project.get("active").toString(), secondJsonResponse.get("active").asText());
        assertNotEquals(firstJsonResponse.get("id").asText(), secondJsonResponse.get("id").asText());
    }

    /**
     * DOCUMENTED
     * Test POST /projects
     * Input: body with title, active and description
     * Expected: 400 Bad Request with Error when creating a project
     */
    @Test
    public void test_post_projects_400() throws IOException, InterruptedException {
        // Project to be created
        Map<String, Object> project = new HashMap<>();
        project.put("title", "New Project");
        project.put("active", "false");
        project.put("description", "New Project Description");
        var requestBody = HttpRequest.BodyPublishers.ofString(objectMapper.writeValueAsString(project));

        // Send the request
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:4567/projects"))
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
     * Test DELETE /projects
     * Input: N/A
     * Expected: 405 Method Not Allowed
     */
    @Test
    public void test_delete_projects_405() throws IOException, InterruptedException {
        // Send the request
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:4567/projects"))
                .DELETE()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // Check response status code
        assertEquals(405, response.statusCode());

        // Check if response body is empty
        assertEquals("", response.body());
    }

    /**
     * UNDOCUMENTED
     * Test OPTIONS /projects
     * Input: N/A
     * Expected: 200 OK status with all Options for endpoint of projects
     */
    @Test
    public void test_options_projects_200() throws IOException, InterruptedException {
        // Send the request
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:4567/projects"))
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
     * DOCUMENTED
     * Test HEAD /projects
     * Input: N/A
     * Expected: 200 OK status with headers for all the instances of project
     */
    @Test
    public void test_head_projects_200() throws IOException, InterruptedException {
        // Send the request
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:4567/projects"))
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
     * Test PATCH /projects
     * Input: N/A
     * Expected: 405 Method Not Allowed
     */
    @Test
    public void test_patch_projects_405() throws IOException, InterruptedException {
        // Send the request
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:4567/projects"))
                .method("PATCH", HttpRequest.BodyPublishers.noBody())
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // Check response status code
        assertEquals(405, response.statusCode());

        // Check if response body is empty
        assertEquals("", response.body());
    }

    // ---------------- /projects/:id ----------------------

    /**
     * DOCUMENTED
     * Test GET /projects/:id
     * Input: path variable id
     * Expected: 200 OK status with all the projects in response body
     */
    @Test
    public void test_get_projects_id_200() throws IOException, InterruptedException {
        // Send the request
        String projectId = "1";
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:4567/projects/" + projectId))
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // Check response status code
        assertEquals(200, response.statusCode());

        // Check if response body is in JSON and contains list of projects
        assertNotNull(response.body());
        JsonNode jsonResponse = objectMapper.readTree(response.body());
        JsonNode responseProjects = jsonResponse.get("projects");
        assertTrue(responseProjects.isArray());
    }

    /**
     * DOCUMENTED
     * Test GET /projects/:id
     * Input: path variable id
     * Expected: 404 Not Found with error message - Did not find project with given
     * id
     */
    @Test
    public void test_get_projects_id_404() throws IOException, InterruptedException {
        // Send the request
        String projectId = "100";
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:4567/projects/" + projectId))
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
     * Test PUT /projects/:id
     * Input: path variable id, request body with title, description
     * Expected: 200 OK - Replaces the specific project details
     */
    @Test
    public void test_put_projects_id_200()
            throws IOException, InterruptedException, ParserConfigurationException, SAXException {
        // Project to be created
        String project = "<project>"
                + "<title>" + amendedProject.get("title") + "</title>"
                + "<description>" + amendedProject.get("description") + "</description>"
                + "</project>";
        var requestBody = HttpRequest.BodyPublishers.ofString(project);

        // Send the request
        String projectId = "1";
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:4567/projects/" + projectId))
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
        assertEquals(amendedProject.get("title"), doc.getElementsByTagName("title").item(0).getTextContent());
        assertEquals(amendedProject.get("description"),
                doc.getElementsByTagName("description").item(0).getTextContent());
    }

    /**
     * DOCUMENTED
     * Test PUT /projects/:id
     * Input: path variable id, request body with title, description, field
     * Unexpected: 400 Bad Request (this does not appear in the swagger)
     */
    @Test
    public void test_put_projects_id_400() throws IOException, InterruptedException {
        // Project to be edited
        amendedProject.put("field", "nonsense");
        var requestBody = HttpRequest.BodyPublishers.ofString(objectMapper.writeValueAsString(amendedProject));
        amendedProject.remove("field");

        // Send the request
        String projectId = "1";
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:4567/projects/" + projectId))
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
     * Test PUT /projects/:id
     * Input: path variable id, request body with title, description
     * Expected: 404 Not Found status - Did not find project with given id
     */
    @Test
    public void test_put_projects_id_404() throws IOException, InterruptedException {
        // Project to be created
        var requestBody = HttpRequest.BodyPublishers.ofString(objectMapper.writeValueAsString(amendedProject));

        // Send the request
        String projectId = "100";
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:4567/projects/" + projectId))
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
     * Test POST /projects/:id
     * Input: path variable id, request body with title, description
     * Expected: 200 OK with project of id in response body
     */
    @Test
    public void test_post_projects_id_200() throws IOException, InterruptedException {
        // Project to be edited
        var requestBody = HttpRequest.BodyPublishers.ofString(objectMapper.writeValueAsString(amendedProject));

        // Send the request
        String projectId = "1";
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:4567/projects/" + projectId))
                .POST(requestBody)
                .build();
        HttpResponse<String> firstResponse = client.send(request, HttpResponse.BodyHandlers.ofString());

        // Check response status code
        assertEquals(200, firstResponse.statusCode());

        // Validate content of response body with request body
        assertNotNull(firstResponse.body());
        JsonNode firstJsonResponse = objectMapper.readTree(firstResponse.body());
        assertEquals(amendedProject.get("title"), firstJsonResponse.get("title").asText());
        assertEquals(amendedProject.get("description"), firstJsonResponse.get("description").asText());
    }

    /**
     * DOCUMENTED
     * Test POST /projects/:id
     * Input: path variable id, request body with title, description, field
     * Expected: 400 Bad Request with Error when creating a project
     */
    @Test
    public void test_post_projects_id_400() throws IOException, InterruptedException {
        // Project to be edited
        amendedProject.put("field", "nonsense");
        var requestBody = HttpRequest.BodyPublishers.ofString(objectMapper.writeValueAsString(amendedProject));
        amendedProject.remove("field");

        // Send the request
        String projectId = "1";
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:4567/projects/" + projectId))
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
     * Test POST /projects/:id
     * Input: path variable id, request body with title, description, field
     * Expected: 404 Not Found with error message
     */
    @Test
    public void test_post_projects_id_404() throws IOException, InterruptedException {
        // Project to be edited
        var requestBody = HttpRequest.BodyPublishers.ofString(objectMapper.writeValueAsString(amendedProject));

        // Send the request
        String projectId = "100";
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:4567/projects/" + projectId))
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
     * Test DELETE /projects/:id
     * Input: path variable id
     * Expected: 200 OK
     */
    @Test
    public void test_delete_projects_id_200() throws IOException, InterruptedException {
        // Category to be created
        var requestBody = HttpRequest.BodyPublishers.ofString(objectMapper.writeValueAsString(project));

        // Send the request
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:4567/projects"))
                .POST(requestBody)
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // Check response status code
        assertEquals(201, response.statusCode());

        // Get id of category created
        JsonNode jsonResponse = objectMapper.readTree(response.body());
        String projectId = jsonResponse.get("id").asText();

        // Send the request
        request = HttpRequest.newBuilder()
                .uri(URI.create(String.format("http://localhost:4567/projects/%s", projectId)))
                .DELETE()
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // Check response status code
        assertEquals(200, response.statusCode());
    }

    /**
     * DOCUMENTED
     * Test DELETE /projects/:id twice
     * Input: path variable id
     * Expected: 200 OK
     */
    @Test
    public void test_delete_twice_projects_id_404() throws IOException, InterruptedException {
        // Category to be created
        var requestBody = HttpRequest.BodyPublishers.ofString(objectMapper.writeValueAsString(project));

        // Send the request
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:4567/projects"))
                .POST(requestBody)
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // Check response status code
        assertEquals(201, response.statusCode());

        // Get id of category created
        JsonNode jsonResponse = objectMapper.readTree(response.body());
        String projectId = jsonResponse.get("id").asText();

        // Send the request
        request = HttpRequest.newBuilder()
                .uri(URI.create(String.format("http://localhost:4567/projects/%s", projectId)))
                .DELETE()
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // Check response status code
        assertEquals(200, response.statusCode());

        // Send the request
        request = HttpRequest.newBuilder()
                .uri(URI.create(String.format("http://localhost:4567/projects/%s", projectId)))
                .DELETE()
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // Check response status code
        assertEquals(404, response.statusCode());
    }

    /**
     * DOCUMENTED
     * Test DELETE /projects/:id
     * Input: path variable id
     * Expected: 404 Not Found with error message
     */
    @Test
    public void test_delete_projects_id_404() throws IOException, InterruptedException {
        String projectId = "10";

        // Send the request
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(String.format("http://localhost:4567/projects/%s", projectId)))
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
     * Test OPTIONS /projects/:id
     * Input: path variable id
     * Expected: 200 OK status with all Options for endpoint of projects
     */
    @Test
    public void test_options_projects_id_200() throws IOException, InterruptedException {
        // Send the request
        String projectId = "1";
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:4567/projects/" + projectId))
                .method("OPTIONS", HttpRequest.BodyPublishers.noBody())
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // Check response status code
        assertEquals(200, response.statusCode());

        // Get the headers from the response
        Map<String, List<String>> headers = response.headers().map();
        List<String> options = Arrays.asList(headers.get("Allow").get(0).split(",\\s*"));
        assertTrue(options.containsAll(Arrays.asList("OPTIONS", "GET", "HEAD", "POST", "PUT", "DELETE")));
    }

    /**
     * DOCUMENTED
     * Test OPTIONS /projects/:id
     * Input: path variable id for a project that does not exist
     * Expected: 200 OK status with all Options for endpoint of projects
     */
    @Test
    public void test_options_two_projects_id_200() throws IOException, InterruptedException {
        // Send the request
        String projectId = "100";
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:4567/projects/" + projectId))
                .method("OPTIONS", HttpRequest.BodyPublishers.noBody())
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // Check response status code
        assertEquals(200, response.statusCode());

        // Get the headers from the response
        Map<String, List<String>> headers = response.headers().map();
        List<String> options = Arrays.asList(headers.get("Allow").get(0).split(",\\s*"));
        assertTrue(options.containsAll(Arrays.asList("OPTIONS", "GET", "HEAD", "POST", "PUT", "DELETE")));
    }

    /**
     * DOCUMENTED
     * Test HEAD /projects/:id
     * Input: path variable id
     * Expected: 200 OK status with headers for all the instances of project
     */
    @Test
    public void test_head_projects_id_200() throws IOException, InterruptedException {
        // Send the request
        String projectId = "1";
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:4567/projects/" + projectId))
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
     * Test HEAD /projects/:id
     * Input: path variable id
     * Expected: 404 Not Found status
     */
    @Test
    public void test_head_projects_id_404() throws IOException, InterruptedException {
        // Send the request
        String projectId = "100";
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:4567/projects/" + projectId))
                .method("HEAD", HttpRequest.BodyPublishers.noBody())
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // Check response status code
        assertEquals(404, response.statusCode());

        // Check if response body is empty
        assertEquals("", response.body());
    }

    /**
     * UNDOCUMENTED
     * Test PATCH /projects/:id
     * Input: path variable id
     * Expected: 405 Method Not Allowed
     */
    @Test
    public void test_patch_projects_id_405() throws IOException, InterruptedException {
        // Send the request
        String projectId = "1";
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:4567/projects/" + projectId))
                .method("PATCH", HttpRequest.BodyPublishers.noBody())
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // Check response status code
        assertEquals(405, response.statusCode());

        // Check if response body is empty
        assertEquals("", response.body());
    }

    // ---------------- /projects/:id/tasks ----------------------

    /**
     * DOCUMENTED
     * Test GET /projects/:id/tasks
     * Input: path variable id
     * Expected: 200 OK status with all the projects in response body
     */
    @Test
    public void test_get_projects_id_tasks_200() throws IOException, InterruptedException {
        // Send the request
        String projectId = "1";
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(String.format("http://localhost:4567/projects/%s/tasks", projectId)))
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // Check response status code
        assertEquals(200, response.statusCode());

        // Check if response body is in JSON and contains list of projects
        assertNotNull(response.body());
        JsonNode jsonResponse = objectMapper.readTree(response.body());
        JsonNode responseTodos = jsonResponse.get("todos");
        assertTrue(responseTodos.isArray());
    }

    /**
     * DOCUMENTED
     * Test GET /projects/:id/tasks
     * Input: path variable id of project that does not exist
     * Expected: 200 OK status with all the projects in response body
     */
    @Test
    public void test_get_projects_id_tasks_two_200() throws IOException, InterruptedException {
        // Send the request
        String projectId = "100";
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(String.format("http://localhost:4567/projects/%s/tasks", projectId)))
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // Check response status code
        assertEquals(200, response.statusCode());

        // Check if response body is in JSON and contains list of projects
        assertNotNull(response.body());
        JsonNode jsonResponse = objectMapper.readTree(response.body());
        JsonNode responseTodos = jsonResponse.get("todos");
        assertTrue(responseTodos.isArray());
    }

    /**
     * UNDOCUMENTED
     * Test PUT /projects/:id/tasks
     * Input: path variable id
     * Expected: 405 Method Not Allowed
     */
    @Test
    public void test_put_projects_id_tasks_405() throws IOException, InterruptedException {
        // Project to be created
        var requestBody = HttpRequest.BodyPublishers.ofString(objectMapper.writeValueAsString(amendedProject));

        // Send the request
        String projectId = "1";
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(String.format("http://localhost:4567/projects/%s/tasks", projectId)))
                .PUT(requestBody)
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // Check response status code
        assertEquals(405, response.statusCode());

        // Check if response body is empty
        assertEquals("", response.body());
    }

    /**
     * DOCUMENTED
     * Test POST /projects/:id/tasks
     * Input: path variable id, request body with id
     * Expected: 201 Created the relationship
     */
    @Test
    public void test_post_projects_id_tasks_201() throws IOException, InterruptedException {
        String projectId = "1";
        Map<String, Object> task = new HashMap<>() {
            {
                put("id", "2");
            }
        };
        var requestBody = HttpRequest.BodyPublishers.ofString(objectMapper.writeValueAsString(task));

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(String.format("http://localhost:4567/projects/%s/tasks", projectId)))
                .POST(requestBody)
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // Check response status code
        assertEquals(201, response.statusCode());

        // Add it again
        response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // Check response status code
        assertEquals(201, response.statusCode());

        // Get projects
        request = HttpRequest.newBuilder()
                .uri(URI.create(String.format("http://localhost:4567/projects/%s/tasks", projectId)))
                .GET()
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // Check response status code
        assertEquals(200, response.statusCode());

        // Check if response body is in JSON and contains list of tasks
        assertNotNull(response.body());
        JsonNode jsonResponse = objectMapper.readTree(response.body());
        JsonNode responseTodos = jsonResponse.get("todos");
        assertTrue(responseTodos.isArray());
    }

    /**
     * DOCUMENTED
     * Test POST /projects/:id/tasks
     * Input: path variable id, no request body
     * Expected: 400 Bad Request with Error
     */
    @Test
    public void test_post_projects_id_tasks_400() throws IOException, InterruptedException {
        // Send the request
        String projectId = "1";
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(String.format("http://localhost:4567/projects/%s/tasks", projectId)))
                .POST(HttpRequest.BodyPublishers.noBody())
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // Check response status code
        assertEquals(400, response.statusCode());

        // Validate content of response body with request body
        validateErrorMessage(objectMapper, response);
    }

    /**
     * DOCUMENTED
     * Test POST /projects/:id/tasks
     * Input: path variable id, request body with id
     * Unexpected: 404 Not Found with error message (this does not appear in the swagger)
     */
    @Test
    public void test_post_projects_id_tasks_404() throws IOException, InterruptedException {
        // Project to be edited
        Map<String, Object> task = new HashMap<>() {
            {
                put("id", "2");
            }
        };
        var requestBody = HttpRequest.BodyPublishers.ofString(objectMapper.writeValueAsString(task));

        // Send the request
        String projectId = "100";
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(String.format("http://localhost:4567/projects/%s/tasks", projectId)))
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
     * Test DELETE /projects/:id/tasks
     * Input: path variable id
     * Expected: 405 Method Not Allowed
     */
    @Test
    public void test_delete_projects_id_tasks_405() throws IOException, InterruptedException {
        String projectId = "1";

        // Send the request
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(String.format("http://localhost:4567/projects/%s/tasks", projectId)))
                .DELETE()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // Check response status code
        assertEquals(405, response.statusCode());

        // Check if response body is empty
        assertEquals("", response.body());
    }

    /**
     * UNDOCUMENTED
     * Test OPTIONS /projects/:id/tasks
     * Input: path variable id
     * Expected: 200 OK status with all Options for endpoint of projects
     */
    @Test
    public void test_options_projects_id_tasks_200() throws IOException, InterruptedException {
        // Send the request
        String projectId = "1";
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(String.format("http://localhost:4567/projects/%s/tasks", projectId)))
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
     * DOCUMENTED
     * Test OPTIONS /projects/:id/tasks
     * Input: path variable id of project that does not exist
     * Expected: 200 OK status with all Options for endpoint of projects
     */
    @Test
    public void test_options_two_projects_id_tasks_200() throws IOException, InterruptedException {
        // Send the request
        String projectId = "100";
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(String.format("http://localhost:4567/projects/%s/tasks", projectId)))
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
     * DOCUMENTED
     * Test HEAD /projects/:id/tasks
     * Input: path variable id
     * Expected: 200 OK status with headers for all the instances of project
     */
    @Test
    public void test_head_projects_id_tasks_200() throws IOException, InterruptedException {
        // Send the request
        String projectId = "1";
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(String.format("http://localhost:4567/projects/%s/tasks", projectId)))
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
     * Test PATCH /projects/:id/tasks
     * Input: path variable id
     * Expected: 405 Method Not Allowed
     */
    @Test
    public void test_patch_projects_id_tasks_405() throws IOException, InterruptedException {
        // Send the request
        String projectId = "1";
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(String.format("http://localhost:4567/projects/%s/tasks", projectId)))
                .method("PATCH", HttpRequest.BodyPublishers.noBody())
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // Check response status code
        assertEquals(405, response.statusCode());

        // Check if response body is empty
        assertEquals("", response.body());
    }

    // ---------------- /projects/:id/tasks/:id ----------------------

    /**
     * UNDOCUMENTED
     * Test GET /projects/:id/tasks/:id
     * Input: path variable id for project and id for task
     * Expected: 405 Method Not Allowed
     */
    @Test
    public void test_get_projects_id_tasks_id_405() throws IOException, InterruptedException {
        String projectId = "1";
        String taskId = "1";

        // Send the request
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(String.format("http://localhost:4567/projects/%s/tasks/%s", projectId, taskId)))
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // Check response status code
        assertNotEquals(405, response.statusCode());
    }

    /**
     * UNDOCUMENTED
     * Test GET /projects/:id/tasks/:id
     * Input: path variable id for project and id for task
     * Unexpected: 404 Not Found
     */
    @Test
    public void test_get_projects_id_tasks_id_404() throws IOException, InterruptedException {
        String projectId = "1";
        String taskId = "1";

        // Send the request
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(String.format("http://localhost:4567/projects/%s/tasks/%s", projectId, taskId)))
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // Check response status code
        assertEquals(404, response.statusCode());
    }

    /**
     * UNDOCUMENTED
     * Test PUT /projects/:id/tasks/:id
     * Input: path variable id for project and id for task
     * Expected: 405 Method Not Allowed
     */
    @Test
    public void test_put_projects_id_tasks_id_405() throws IOException, InterruptedException {
        String projectId = "1";
        String taskId = "1";

        // Send the request
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(String.format("http://localhost:4567/projects/%s/tasks/%s", projectId, taskId)))
                .PUT(HttpRequest.BodyPublishers.noBody())
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // Check response status code
        assertEquals(405, response.statusCode());
    }

    /**
     * UNDOCUMENTED
     * Test POST /projects/:id/tasks/:id
     * Input: path variable id for project and id for task
     * Expected: 405 Method Not Allowed
     */
    @Test
    public void test_post_projects_id_tasks_id_405() throws IOException, InterruptedException {
        String projectId = "1";
        String taskId = "1";

        // Send the request
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(String.format("http://localhost:4567/projects/%s/tasks/%s", projectId, taskId)))
                .POST(HttpRequest.BodyPublishers.noBody())
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // Check response status code
        assertNotEquals(405, response.statusCode());
    }

    /**
     * UNDOCUMENTED
     * Test POST /projects/:id/tasks/:id
     * Input: path variable id for project and id for task
     * Unexpected: 404 Not Found
     */
    @Test
    public void test_post_projects_id_tasks_id_404() throws IOException, InterruptedException {
        String projectId = "1";
        String taskId = "1";

        // Send the request
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(String.format("http://localhost:4567/projects/%s/tasks/%s", projectId, taskId)))
                .POST(HttpRequest.BodyPublishers.noBody())
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // Check response status code
        assertEquals(404, response.statusCode());
    }

    /**
     * DOCUMENTED
     * Test DELETE /projects/:id/tasks/:id
     * Input: path variable id for project and id for task
     * Expected: 200 OK
     */
    @Test
    public void test_delete_projects_id_tasks_id_200() throws IOException, InterruptedException {
        String projectId = "1";
        String taskId = "1";

        // Task to add to project
        Map<String, String> task = new HashMap<>() {
            {
                put("id", taskId);
            }
        };
        var requestBody = HttpRequest.BodyPublishers.ofString(objectMapper.writeValueAsString(task));

        // Send the request
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(String.format("http://localhost:4567/projects/%s/tasks", projectId)))
                .POST(requestBody)
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // Check response status code
        assertEquals(201, response.statusCode());

        // Send the request
        request = HttpRequest.newBuilder()
                .uri(URI.create(String.format("http://localhost:4567/projects/%s/tasks/%s", projectId, taskId)))
                .DELETE()
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // Check response status code
        assertEquals(200, response.statusCode());
    }

    /**
     * DOCUMENTED
     * Test DELETE /projects/:id/tasks/:id twice
     * Input: path variable id for project and id for task
     * Expected: 200 OK
     */
    @Test
    public void test_delete_twice_projects_id_tasks_id_404() throws IOException, InterruptedException {
        String projectId = "1";
        String taskId = "1";

        // Task to add to project
        Map<String, String> task = new HashMap<>() {
            {
                put("id", taskId);
            }
        };
        var requestBody = HttpRequest.BodyPublishers.ofString(objectMapper.writeValueAsString(task));

        // Send the request
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(String.format("http://localhost:4567/projects/%s/tasks", projectId)))
                .POST(requestBody)
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // Check response status code
        assertEquals(201, response.statusCode());

        // Send the request
        request = HttpRequest.newBuilder()
                .uri(URI.create(String.format("http://localhost:4567/projects/%s/tasks/%s", projectId, taskId)))
                .DELETE()
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // Check response status code
        assertEquals(200, response.statusCode());

        // Send the request again
        request = HttpRequest.newBuilder()
                .uri(URI.create(String.format("http://localhost:4567/projects/%s/tasks/%s", projectId, taskId)))
                .DELETE()
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // Check response status code
        assertEquals(404, response.statusCode());
    }

    /**
     * DOCUMENTED
     * Test DELETE /projects/:id/tasks/:id
     * Input: path variable id for project and id for task
     * Expected: 404 Not Found with error message
     */
    @Test
    public void test_delete_projects_id_tasks_id_404() throws IOException, InterruptedException {
        String projectId = "100";
        String taskId = "100";

        // Send the request
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(String.format("http://localhost:4567/projects/%s/tasks/%s", projectId, taskId)))
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
     * Test OPTIONS /projects/:id/tasks/:id
     * Input: path variable id for project and id for task
     * Expected: 200 OK status with all Options for endpoint of projects
     */
    @Test
    public void test_options_projects_id_tasks_id_200() throws IOException, InterruptedException {
        String projectId = "1";
        String taskId = "1";

        // Send the request
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(String.format("http://localhost:4567/projects/%s/tasks/%s", projectId, taskId)))
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
     * UNDOCUMENTED
     * Test HEAD /projects/:id/tasks/:id
     * Input: path variable id for project and id for task
     * Expected: 405 Method Not Allowed
     */
    @Test
    public void test_head_projects_id_tasks_id_405() throws IOException, InterruptedException {
        String projectId = "1";
        String taskId = "1";

        // Send the request
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(String.format("http://localhost:4567/projects/%s/tasks/%s", projectId, taskId)))
                .method("HEAD", HttpRequest.BodyPublishers.noBody())
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // Check response status code
        assertNotEquals(405, response.statusCode());
    }

    /**
     * UNDOCUMENTED
     * Test HEAD /projects/:id/tasks/:id
     * Input: path variable id for project and id for task
     * Unexpected: 404 Not Found
     */
    @Test
    public void test_head_projects_id_tasks_id_404() throws IOException, InterruptedException {
        String projectId = "1";
        String taskId = "1";

        // Send the request
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(String.format("http://localhost:4567/projects/%s/tasks/%s", projectId, taskId)))
                .method("HEAD", HttpRequest.BodyPublishers.noBody())
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // Check response status code
        assertEquals(404, response.statusCode());
    }

    /**
     * UNDOCUMENTED
     * Test PATCH /projects/:id/tasks/:id
     * Input: path variable id for project and id for task
     * Expected: 405 Method Not Allowed
     */
    @Test
    public void test_patch_projects_id_tasks_id_405() throws IOException, InterruptedException {
        String projectId = "1";
        String taskId = "1";

        // Send the request
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(String.format("http://localhost:4567/projects/%s/tasks/%s", projectId, taskId)))
                .method("PATCH", HttpRequest.BodyPublishers.noBody())
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // Check response status code
        assertEquals(405, response.statusCode());

        // Check if response body is empty
        assertEquals("", response.body());
    }

    // ---------------- /projects/:id/categories ----------------------

    /**
     * DOCUMENTED
     * Test GET /projects/:id/categories
     * Input: path variable id
     * Expected: 200 OK with "categories" list of projects with id in response body
     */
    @Test
    public void test_get_projects_id_categories_200() throws IOException, InterruptedException {
        String projectId = "1";

        // Send the request
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(String.format("http://localhost:4567/projects/%s/categories", projectId)))
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
     * Test POST /projects/:id/categories
     * Input: path variable id, request body with category id
     * Expected: 201 Created
     */
    @Test
    public void test_post_projects_id_categories_201() throws IOException, InterruptedException {
        String projectId = "1";

        // Category to be edited
        Map<String, String> category = new HashMap<>() {
            {
                put("id", "1");
            }
        };
        var requestBody = HttpRequest.BodyPublishers.ofString(objectMapper.writeValueAsString(category));

        // Send the request
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(String.format("http://localhost:4567/projects/%s/categories", projectId)))
                .POST(requestBody)
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // Check response status code
        assertEquals(201, response.statusCode());

        // Add it again
        response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // Check response status code
        assertEquals(201, response.statusCode());

        // Get projects
        request = HttpRequest.newBuilder()
                .uri(URI.create(String.format("http://localhost:4567/projects/%s/categories", projectId)))
                .GET()
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // Check response status code
        assertEquals(200, response.statusCode());

        // Check if response body is in JSON and contains list of categories
        assertNotNull(response.body());
        JsonNode jsonResponse = objectMapper.readTree(response.body());
        JsonNode responseCategories = jsonResponse.get("categories");
        assertTrue(responseCategories.isArray());
        assertEquals(1, responseCategories.size());
    }

    /**
     * DOCUMENTED
     * Test POST /projects/:id/categories
     * Input: path variable id, request body with invalid field
     * Expected: 400 Bad Request with error message
     */
    @Test
    public void test_post_projects_id_categories_400() throws IOException, InterruptedException {
        String projectId = "1";

        // Category to be edited
        Map<String, String> category = new HashMap<>() {{
            put("name", "project1");
        }};
        var requestBody = HttpRequest.BodyPublishers.ofString(objectMapper.writeValueAsString(category));

        // Send the request
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(String.format("http://localhost:4567/projects/%s/categories", projectId)))
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
     * Test POST /projects/:id/categories
     * Input: path variable id, request body with invalid category id
     * Expected: 404 Not Found with error message
     */
    @Test
    public void test_post_projects_id_categories_404() throws IOException, InterruptedException {
        String projectId = "1";

        // Category to be edited
        Map<String, String> category = new HashMap<>() {
            {
                put("id", "100");
            }
        };
        var requestBody = HttpRequest.BodyPublishers.ofString(objectMapper.writeValueAsString(category));

        // Send the request
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(String.format("http://localhost:4567/projects/%s/categories", projectId)))
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
     * Test PUT /projects/:id/categories
     * Input: path variable id, request body with category id
     * Expected: 405 Method Not Allowed
     */
    @Test
    public void test_put_projects_id_categories_405() throws IOException, InterruptedException {
        String projectId = "1";

        // Category to be edited
        Map<String, String> category = new HashMap<>() {
            {
                put("id", "1");
            }
        };
        var requestBody = HttpRequest.BodyPublishers.ofString(objectMapper.writeValueAsString(category));

        // Send the request
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(String.format("http://localhost:4567/projects/%s/categories", projectId)))
                .PUT(requestBody)
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // Check response status code
        assertEquals(405, response.statusCode());
    }

    /**
     * UNDOCUMENTED
     * Test PATCH /projects/:id/categories
     * Input: path variable id
     * Expected: 405 Method Not Allowed
     */
    @Test
    public void test_patch_projects_id_categories_405() throws IOException, InterruptedException {
        String projectId = "1";

        // Send the request
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(String.format("http://localhost:4567/projects/%s/categories", projectId)))
                .method("PATCH", HttpRequest.BodyPublishers.noBody())
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // Check response status code
        assertEquals(405, response.statusCode());
    }

    /**
     * UNDOCUMENTED
     * Test DELETE /projects/:id/categories
     * Input: path variable id
     * Expected: 405 Method Not Allowed
     */
    @Test
    public void test_delete_projects_id_categories_405() throws IOException, InterruptedException {
        String projectId = "1";

        // Send the request
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(String.format("http://localhost:4567/projects/%s/categories", projectId)))
                .DELETE()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // Check response status code
        assertEquals(405, response.statusCode());
    }

    /**
     * DOCUMENTED
     * Test HEAD /projects/:id/categories
     * Input: path variable id
     * Expected: 200 OK, returning headers
     */
    @Test
    public void test_head_projects_id_categories_200() throws IOException, InterruptedException {
        String projectId = "1";

        // Send the request
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(String.format("http://localhost:4567/projects/%s/categories", projectId)))
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
     * Test OPTIONS /projects/:id/categories
     * Input: path variable id
     * Expected: 200 OK with endpoint options
     */
    @Test
    public void test_option_projects_id_categories_200() throws IOException, InterruptedException {
        String projectId = "1";

        // Send the request
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(String.format("http://localhost:4567/projects/%s/categories", projectId)))
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
    
    // ---------------- /projects/:id/categories/:id ----------------------
    /**
     * UNDOCUMENTED
     * Test GET /projects/:id/categories/:id
     * Input: path variable id for project and id for category
     * Expected: 405 Method Not Allowed
     */
    @Test
    public void test_get_projects_id_categories_id_405() throws IOException, InterruptedException {
        String projectId = "1";
        String categoryId = "1";

        // Send the request
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(String.format("http://localhost:4567/projects/%s/categories/%s", projectId, categoryId)))
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // Check response status code
        assertNotEquals(405, response.statusCode());
    }

    /**
     * UNDOCUMENTED
     * Test GET /projects/:id/categories/:id
     * Input: path variable id for project and id for category
     * Unexpected: 404 Not Found
     */
    @Test
    public void test_get_projects_id_categories_id_404() throws IOException, InterruptedException {
        String projectId = "1";
        String categoryId = "1";

        // Send the request
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(String.format("http://localhost:4567/projects/%s/categories/%s", projectId, categoryId)))
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // Check response status code
        assertEquals(404, response.statusCode());
    }

    /**
     * UNDOCUMENTED
     * Test POST /projects/:id/categories/:id
     * Input: path variable id for project and id for category
     * Expected: 405 Method Not Allowed
     */
    @Test
    public void test_post_projects_id_categories_id_405() throws IOException, InterruptedException {
        String projectId = "1";
        String categoryId = "1";

        // Send the request
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(String.format("http://localhost:4567/projects/%s/categories/%s", projectId, categoryId)))
                .POST(HttpRequest.BodyPublishers.noBody())
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // Check response status code
        assertNotEquals(405, response.statusCode());
    }

    /**
     * UNDOCUMENTED
     * Test POST /projects/:id/categories/:id
     * Input: path variable id for project and id for category
     * Unexpected: 404 Not Found
     */
    @Test
    public void test_post_projects_id_categories_id_404() throws IOException, InterruptedException {
        String projectId = "1";
        String categoryId = "1";

        // Send the request
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(String.format("http://localhost:4567/projects/%s/categories/%s", projectId, categoryId)))
                .POST(HttpRequest.BodyPublishers.noBody())
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // Check response status code
        assertEquals(404, response.statusCode());
    }

    /**
     * UNDOCUMENTED
     * Test PUT /projects/:id/categories/:id
     * Input: path variable id for project and id for category
     * Expected: 405 Method Not Allowed
     */
    @Test
    public void test_put_projects_id_categories_id_405() throws IOException, InterruptedException {
        String projectId = "1";
        String categoryId = "1";

        // Send the request
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(String.format("http://localhost:4567/projects/%s/categories/%s", projectId, categoryId)))
                .PUT(HttpRequest.BodyPublishers.noBody())
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // Check response status code
        assertEquals(405, response.statusCode());
    }

    /**
     * UNDOCUMENTED
     * Test PATCH /projects/:id/categories/:id
     * Input: path variable id for project and id for category
     * Expected: 405 Method Not Allowed
     */
    @Test
    public void test_patch_projects_id_categories_id_405() throws IOException, InterruptedException {
        String projectId = "1";
        String categoryId = "1";

        // Send the request
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(String.format("http://localhost:4567/projects/%s/categories/%s", projectId, categoryId)))
                .method("PATCH", HttpRequest.BodyPublishers.noBody())
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // Check response status code
        assertEquals(405, response.statusCode());
    }

    /**
     * DOCUMENTED
     * Test DELETE /projects/:id/categories/:id
     * Input: path variable id for project and id for category
     * Expected: 200 OK
     */
    @Test
    public void test_delete_projects_id_categories_id_200() throws IOException, InterruptedException {
        String projectId = "1";
        String categoryId = "1";

        // Project to add to category
        Map<String, String> category = new HashMap<>() {{
            put("id", categoryId);
        }};
        var requestBody = HttpRequest.BodyPublishers.ofString(objectMapper.writeValueAsString(category));

        // Send the request
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(String.format("http://localhost:4567/projects/%s/categories", projectId)))
                .POST(requestBody)
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // Check response status code
        assertEquals(201, response.statusCode());

        // Send the request
        request = HttpRequest.newBuilder()
                .uri(URI.create(String.format("http://localhost:4567/projects/%s/categories/%s", projectId, categoryId)))
                .DELETE()
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // Check response status code
        assertEquals(200, response.statusCode());
    }

    /**
     * DOCUMENTED
     * Test DELETE /projects/:id/categories/:id twice
     * Input: path variable id for project and id for category
     * Expected: 200 OK
     */
    @Test
    public void test_delete_twice_projects_id_categories_id_404() throws IOException, InterruptedException {
        String projectId = "1";
        String categoryId = "1";

        // Project to add to category
        Map<String, String> category = new HashMap<>() {{
            put("id", categoryId);
        }};
        var requestBody = HttpRequest.BodyPublishers.ofString(objectMapper.writeValueAsString(category));

        // Send the request
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(String.format("http://localhost:4567/projects/%s/categories", projectId)))
                .POST(requestBody)
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // Check response status code
        assertEquals(201, response.statusCode());

        // Send the request
        request = HttpRequest.newBuilder()
                .uri(URI.create(String.format("http://localhost:4567/projects/%s/categories/%s", projectId, categoryId)))
                .DELETE()
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // Check response status code
        assertEquals(200, response.statusCode());

        // Send the delete request again
        request = HttpRequest.newBuilder()
                .uri(URI.create(String.format("http://localhost:4567/projects/%s/categories/%s", projectId, categoryId)))
                .DELETE()
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // Check response status code
        assertEquals(404, response.statusCode());
    }

    /**
     * DOCUMENTED
     * Test DELETE /projects/:id/categories/:id
     * Input: path variable id for project and id for category, but no relationship
     * Expected: 404 Not Found with error message
     */
    @Test
    public void test_delete_projects_id_categories_id_404() throws IOException, InterruptedException {
        String projectId = "100";
        String categoryId = "100";

        // Send the request
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(String.format("http://localhost:4567/projects/%s/categories/%s", projectId, categoryId)))
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
     * Test HEAD /projects/:id/categories/:id
     * Input: path variable id for project and id for category
     * Expected: 405 Method Not Allowed
     */
    @Test
    public void test_head_projects_id_categories_id_405() throws IOException, InterruptedException {
        String projectId = "1";
        String categoryId = "1";

        // Send the request
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(String.format("http://localhost:4567/projects/%s/categories/%s", projectId, categoryId)))
                .method("HEAD", HttpRequest.BodyPublishers.noBody())
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // Check response status code
        assertNotEquals(405, response.statusCode());
    }

    /**
     * UNDOCUMENTED
     * Test HEAD /projects/:id/categories/:id
     * Input: path variable id for project and id for category
     * Unexpected: 404 Not Found
     */
    @Test
    public void test_head_projects_id_categories_id_404() throws IOException, InterruptedException {
        String projectId = "1";
        String categoryId = "1";

        // Send the request
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(String.format("http://localhost:4567/projects/%s/categories/%s", projectId, categoryId)))
                .method("HEAD", HttpRequest.BodyPublishers.noBody())
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // Check response status code
        assertEquals(404, response.statusCode());
    }

    /**
     * UNDOCUMENTED
     * Test OPTIONS /projects/:id/categories/:id
     * Input: path variable id for project and id for category
     * Expected: 200 OK with endpoint options
     */
    @Test
    public void test_option_projects_id_categories_id_200() throws IOException, InterruptedException {
        String projectId = "1";
        String categoryId = "1";

        // Send the request
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(String.format("http://localhost:4567/projects/%s/categories/%s", projectId, categoryId)))
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
