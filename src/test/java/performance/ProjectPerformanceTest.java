package performance;

import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.Map;

import com.sun.management.OperatingSystemMXBean;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.ObjectMapper;

public class ProjectPerformanceTest extends PerformanceTestTools {
    static final OperatingSystemMXBean osBean = (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();
    private static final HttpClient client = HttpClient.newHttpClient();
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final String OBJECT_NAME = "Project";

    public static HttpResponse<String> createProject(String title, Boolean active, Boolean completed, String description) throws IOException, InterruptedException{
        // Project to be created
        Map<String, Object> project = new HashMap<>() {{
            put("title", title);
            put("active", active);
            put("completed", completed);
            put("description", description);
        }};

        var requestBody = HttpRequest.BodyPublishers.ofString(objectMapper.writeValueAsString(project));

        // Send the request
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:4567/projects"))
                .POST(requestBody)
                .build();

        return client.send(request, HttpResponse.BodyHandlers.ofString());
    }

    public static HttpResponse<String> updateProject(int id, String title, Boolean active, Boolean completed, String description) throws IOException, InterruptedException{
        Map<String, Object> project = new HashMap<>() {{
            put("title", title);
            put("active", active);
            put("completed", completed);
            put("description", description);
        }};

        // Updated content of the project
        var requestBody = HttpRequest.BodyPublishers.ofString(objectMapper.writeValueAsString(project));

        // Send the request
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(String.format("http://localhost:4567/projects/%s", id)))
                .PUT(requestBody)
                .build();

        return client.send(request, HttpResponse.BodyHandlers.ofString());
    }

    public static HttpResponse<String> deleteProject(int id) throws IOException, InterruptedException{
        // Send the request
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(String.format("http://localhost:4567/projects/%s", id)))
                .DELETE()
                .build();

        return client.send(request, HttpResponse.BodyHandlers.ofString());
    }

    @Test
    public void testCreateProjectObjectPerformance() throws IOException, InterruptedException{
        double[] elapsedTime = new double[objectCount.length];
        double[] cpuUsage = new double[objectCount.length];
        double[] memoryUsage = new double[objectCount.length];

        long startTime = System.nanoTime();

        // Iterating over the object count array
        for (int i = 0; i < objectCount.length; i++){
            int currObjectCount = objectCount[i];

            // Creating objectCount[i] project objects
            for (int j = 0; j < currObjectCount; j++){
                createProject("title", false, false, "description");
            }

            // Time elapsed in ms
            elapsedTime[i] = (System.nanoTime() - startTime) / 1000000.0;
            // Memory usage in mb
            memoryUsage[i] = (double) osBean.getFreeMemorySize() / 1000000;
            // CPU usage (percentage, so 0-100)
            cpuUsage[i] = osBean.getProcessCpuLoad() * 100;
        }

        // Writing Data
        writeCSV("createProjectData.csv", OBJECT_NAME, elapsedTime, memoryUsage, cpuUsage);
    }

    @Test
    public void testUpdateProjectObjectPerformance() throws IOException, InterruptedException{
        // Iterating over the object count array (setting up existing projects)
        for (int currObjectCount : objectCount) {
            // Creating objectCount[i] project objects
            for (int j = 0; j < currObjectCount; j++) {
                createProject("title", false, false, "description");
            }
        }

        double[] elapsedTime = new double[objectCount.length];
        double[] cpuUsage = new double[objectCount.length];
        double[] memoryUsage = new double[objectCount.length];

        long startTime = System.nanoTime();
        int lastID = 2;

        // Iterating over the object count array
        for (int i = 0; i < objectCount.length; i++){
            int currObjectCount = objectCount[i];

            // Updating objectCount[i] project objects
            for (int j = 0; j < currObjectCount; j++){
                updateProject(lastID+1+j, "newTitle", true, false, "newDescription");
            }

            // Time elapsed in ms
            elapsedTime[i] = (System.nanoTime() - startTime) / 1000000.0;
            // Memory usage in mb
            memoryUsage[i] = (double) osBean.getFreeMemorySize() / 1000000;
            // CPU usage (percentage, so 0-100)
            cpuUsage[i] = osBean.getProcessCpuLoad() * 100;

            lastID = lastID + currObjectCount;
        }

        // Writing Data
        writeCSV("updateProjectData.csv", OBJECT_NAME, elapsedTime, memoryUsage, cpuUsage);
    }

    @Test
    public void testDeleteProjectObjectPerformance() throws IOException, InterruptedException{
        // Iterating over the object count array (setting up existing projects)
        for (int currObjectCount : objectCount) {
            // Creating objectCount[i] project objects
            for (int j = 0; j < currObjectCount; j++) {
                createProject("title", false, false, "description");
            }
        }

        double[] elapsedTime = new double[objectCount.length];
        double[] cpuUsage = new double[objectCount.length];
        double[] memoryUsage = new double[objectCount.length];

        long startTime = System.nanoTime();
        int lastID = 2;

        // Iterating over the object count array
        for (int i = 0; i < objectCount.length; i++){
            int currObjectCount = objectCount[i];

            // Updating objectCount[i] project objects
            for (int j = 0; j < currObjectCount; j++){
                deleteProject(lastID+1+j);
            }

            // Time elapsed in ms
            elapsedTime[i] = (System.nanoTime() - startTime) / 1000000.0;
            // Memory usage in mb
            memoryUsage[i] = (double) osBean.getFreeMemorySize() / 1000000;
            // CPU usage (percentage, so 0-100)
            cpuUsage[i] = osBean.getProcessCpuLoad() * 100;
            //cpuUsage[i] = osBean.getCpuLoad() * 100;


            lastID = lastID + currObjectCount;
        }

        // Writing Data
        writeCSV("deleteProjectData.csv", OBJECT_NAME, elapsedTime, memoryUsage, cpuUsage);
    }
}
