package performance;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.management.OperatingSystemMXBean;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.Map;

public class CategoryPerformanceTest extends PerformanceTestTools {
    static final OperatingSystemMXBean osBean = (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();
    private static final HttpClient client = HttpClient.newHttpClient();
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final String BASE_URL = "http://localhost:4567/categories";
    private static final String OBJECT_NAME = "Category";

    public static HttpResponse<String> createCategory(String title, String description) throws IOException, InterruptedException {
        // Category to be created
        Map<String, Object> category = new HashMap<>() {{
            put("title", title);
            put("description", description);
        }};

        var requestBody = HttpRequest.BodyPublishers.ofString(objectMapper.writeValueAsString(category));

        // Send the request
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL))
                .POST(requestBody)
                .build();

        return client.send(request, HttpResponse.BodyHandlers.ofString());
    }

    public static HttpResponse<String> updateCategory(int id, String title, String description) throws IOException, InterruptedException {
        // Updated content of the category
        Map<String, Object> category = new HashMap<>() {{
            put("title", title);
            put("description", description);
        }};

        var requestBody = HttpRequest.BodyPublishers.ofString(objectMapper.writeValueAsString(category));

        // Send the request
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(String.format("%s/%d", BASE_URL, id)))
                .PUT(requestBody)
                .build();

        return client.send(request, HttpResponse.BodyHandlers.ofString());
    }

    public static HttpResponse<String> deleteCategory(int id) throws IOException, InterruptedException {
        // Send the request
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(String.format("%s/%d", BASE_URL, id)))
                .DELETE()
                .build();

        return client.send(request, HttpResponse.BodyHandlers.ofString());
    }

    @Test
    public void testCreateCategoryObjectPerformance() throws IOException, InterruptedException {
        double[] elapsedTime = new double[objectCount.length];
        double[] cpuUsage = new double[objectCount.length];
        double[] memoryUsage = new double[objectCount.length];

        long startTime = System.nanoTime();

        // Iterating over the object count array
        for (int i = 0; i < objectCount.length; i++){
            int currObjectCount = objectCount[i];

            // Creating objectCount[i] category objects
            for (int j = 0; j < currObjectCount; j++){
                createCategory("title", "description");
            }

            // Time elapsed in ms
            elapsedTime[i] = (System.nanoTime() - startTime) / 1000000.0;
            // Memory usage in mb
            memoryUsage[i] = (double) osBean.getFreeMemorySize() / 1000000;
            // CPU usage (percentage, so 0-100)
            cpuUsage[i] = osBean.getProcessCpuLoad() * 100;
        }

        // Writing Data
        writeCSV("createCategoryData.csv", OBJECT_NAME, elapsedTime, memoryUsage, cpuUsage);
    }

    @Test
    public void testUpdateCategoryObjectPerformance() throws IOException, InterruptedException {
        // Iterating over the object count array (setting up existing categories)
        for (int currObjectCount : objectCount) {
            // Creating objectCount[i] category objects
            for (int j = 0; j < currObjectCount; j++) {
                createCategory("title", "description");
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

            // Updating objectCount[i] category objects
            for (int j = 0; j < currObjectCount; j++){
                updateCategory(lastID+1+j, "newTitle", "newDescription");
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
        writeCSV("updateCategoryData.csv", OBJECT_NAME, elapsedTime, memoryUsage, cpuUsage);
    }

    @Test
    public void testDeleteCategoryObjectPerformance() throws IOException, InterruptedException {
        // Iterating over the object count array (setting up existing categories)
        for (int currObjectCount : objectCount) {
            // Creating objectCount[i] category objects
            for (int j = 0; j < currObjectCount; j++) {
                createCategory("title", "description");
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

            // Updating objectCount[i] category objects
            for (int j = 0; j < currObjectCount; j++){
                deleteCategory(lastID+1+j);
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
        writeCSV("deleteCategoryData.csv", OBJECT_NAME, elapsedTime, memoryUsage, cpuUsage);
    }
}
