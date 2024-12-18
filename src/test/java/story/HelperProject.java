package story;

import com.fasterxml.jackson.databind.JsonNode;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.Map;

public class HelperProject {

    private static final HttpClient client = HttpClient.newHttpClient();

    // Project-related functions
    /**
     * For get_specific_project.feature
     */
    public static HttpResponse<String> getSpecificProject(String projectId, String queryString) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:4567/projects" + (!queryString.isEmpty() ? "?title=" + queryString : "/" + projectId)))
                .GET()
                .build();
        return client.send(request, HttpResponse.BodyHandlers.ofString());
    }

    /**
     * For create_project.feature
     */
    public static HttpResponse<String> createProject(String projectId, String title, String description, boolean completed,
                                                     boolean active) throws IOException, InterruptedException {

        // Project to be created
        Map<String, Object> project = new HashMap<>();
        if (!projectId.isEmpty()) project.put("id", projectId);
        project.put("title", title);
        project.put("active", active);
        project.put("completed", completed);
        project.put("description", description);
        var requestBody = HttpRequest.BodyPublishers.ofString(CommonHelper.getStringFromObject(project));

        // Send the request
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:4567/projects"))
                .POST(requestBody)
                .build();
        return client.send(request, HttpResponse.BodyHandlers.ofString());
    }

    /**
     * For amend_project.feature
     */
    public static HttpResponse<String> amendProjectPost(String projectId, String title, String description) throws IOException, InterruptedException {
        // Project to be created
        Map<String, Object> project = new HashMap<>();
        project.put("title", title);
        project.put("description", description);
        var requestBody = HttpRequest.BodyPublishers.ofString(CommonHelper.getStringFromObject(project));

        // Send the request
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:4567/projects/" + projectId))
                .POST(requestBody)
                .build();
        return client.send(request, HttpResponse.BodyHandlers.ofString());
    }

    /**
     * For amend_project.feature
     */
    public static HttpResponse<String> amendProjectPut(String projectId, String title, String description) throws IOException, InterruptedException {
        // Project to be created
        Map<String, Object> project = new HashMap<>();
        project.put("title", title);
        project.put("description", description);
        var requestBody = HttpRequest.BodyPublishers.ofString(CommonHelper.getStringFromObject(project));

        // Send the request
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:4567/projects/" + projectId))
                .PUT(requestBody)
                .build();
        return client.send(request, HttpResponse.BodyHandlers.ofString());
    }

    /**
     * For delete_project.feature
     */
    public static HttpResponse<String> deleteProject(String projectId) throws IOException, InterruptedException {
        // Send the request
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(String.format("http://localhost:4567/projects/%s", projectId)))
                .DELETE()
                .build();
        return client.send(request, HttpResponse.BodyHandlers.ofString());
    }

    /**
     * For associate_project_task.feature
     */
    public static HttpResponse<String> associateProjectTask(String projectId, String todoId, String todoTitle) throws IOException, InterruptedException {
        Map<String, Object> task = new HashMap<>() {
            {
                if (todoTitle.isEmpty()) put("id", todoId);
                if (!todoTitle.isEmpty()) put("title", todoTitle);
            }
        };
        var requestBody = HttpRequest.BodyPublishers.ofString(CommonHelper.getStringFromObject(task));

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(String.format("http://localhost:4567/projects/%s/tasks", projectId)))
                .POST(requestBody)
                .build();
        return client.send(request, HttpResponse.BodyHandlers.ofString());
    }

    /**
     * For associate_project_task.feature
     */
    public static HttpResponse<String> getProjectTask(String projectId) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(String.format("http://localhost:4567/projects/%s/tasks", projectId)))
                .GET()
                .build();
        return client.send(request, HttpResponse.BodyHandlers.ofString());
    }

    public static HttpResponse<String> getAllProjects() throws IOException, InterruptedException {
        // Get all projects
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:4567/projects"))
                .GET()
                .build();
        return client.send(request, HttpResponse.BodyHandlers.ofString());
    }

    // todo: remove if unused
    public static void deleteAllProjects() throws IOException, InterruptedException {
        // Get all projects
        HttpResponse<String> response = getAllProjects();

        // Delete all projects
        if (response.statusCode() == 200) {
            JsonNode rootNode = CommonHelper.getObjectFromResponse(response);
            JsonNode projectsArray = rootNode.get("projects"); // Access the "projects" array

            if (projectsArray != null && projectsArray.isArray()) {
                projectsArray.forEach(project -> {
                    if (project != null && project.has("id")) {  // Check if project and project ID are not null
                        try {
                            deleteProject(project.get("id").asText());
                        } catch (IOException | InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        }
    }



}
