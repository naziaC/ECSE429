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

public class TodosPerformanceTest extends PerformanceTestTools {
    static final OperatingSystemMXBean osBean = (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();
    private static final HttpClient client = HttpClient.newHttpClient();
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static HttpResponse<String> createTodo(String title, String description) throws IOException, InterruptedException{
        Map<String, String> todoContent = new HashMap<>() {{
            put("title", title);
            put("description", description);
        }};

        // Todo to be created
        var requestBody = HttpRequest.BodyPublishers.ofString(objectMapper.writeValueAsString(todoContent));

        // Send the request
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:4567/todos"))
                .POST(requestBody)
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        return response; 
    }

    public static HttpResponse<String> updateTodo(int id, String title, String description) throws IOException, InterruptedException{
        Map<String, String> todoContent = new HashMap<>() {{
            put("title", title);
            put("description", description);
        }};

        // Updated content of the todo
        var requestBody = HttpRequest.BodyPublishers.ofString(objectMapper.writeValueAsString(todoContent));

        // Send the request
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(String.format("http://localhost:4567/todos/%s", id)))
                .PUT(requestBody)
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        return response; 
    }

    public static HttpResponse<String> deleteTodo(int id) throws IOException, InterruptedException{
        // Send the request
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(String.format("http://localhost:4567/todos/%s", id)))
                .DELETE()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        return response; 
    }

    @Test
    public void testCreateTodosObjectPerformance() throws IOException, InterruptedException{
        double[] elapsedTime = new double[objectCount.length];
        double[] cpuUsage = new double[objectCount.length];
        double[] memoryUsage = new double[objectCount.length];

        long startTime = System.nanoTime();

        // Iterating over the object count array
        for (int i = 0; i < objectCount.length; i++){
            int currObjectCount = objectCount[i];

            // Creating ObjectCount[i] todo objects
            for (int j = 0; j < currObjectCount; j++){
                createTodo("title", "description");
            }

            // Time elapsed in ms
            elapsedTime[i] = (System.nanoTime() - startTime) / 1000000.0;
            // Memory usage in mb
            memoryUsage[i] = (double) osBean.getFreeMemorySize() / 1000000;
            // CPU usage (percentage, so 0-100)
            cpuUsage[i] = osBean.getProcessCpuLoad() * 100;
        }

        // Writing Data
        writeCSV("createTodoData.csv", elapsedTime, memoryUsage, cpuUsage);
    }

    @Test
    public void testUpdateTodosObjectPerformance() throws IOException, InterruptedException{
        // Iterating over the object count array (setting up existing Todos)
        for (int i = 0; i < objectCount.length; i++){
            int currObjectCount = objectCount[i];

            // Creating ObjectCount[i] todo objects
            for (int j = 0; j < currObjectCount; j++){
                createTodo("title", "description");
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

            // Udpating ObjectCount[i] todo objects
            for (int j = 0; j < currObjectCount; j++){
                updateTodo(lastID+1+j, "newTitle", "newDescription");
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
        writeCSV("updateTodoData.csv", elapsedTime, memoryUsage, cpuUsage);
    }

    @Test
    public void testDeleteTodosObjectPerformance() throws IOException, InterruptedException{
        // Iterating over the object count array (setting up existing Todos)
        for (int i = 0; i < objectCount.length; i++){
            int currObjectCount = objectCount[i];

            // Creating ObjectCount[i] todo objects
            for (int j = 0; j < currObjectCount; j++){
                createTodo("title", "description");
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

            // Udpating ObjectCount[i] todo objects
            for (int j = 0; j < currObjectCount; j++){
                deleteTodo(lastID+1+j);
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
        writeCSV("deleteTodoData.csv", elapsedTime, memoryUsage, cpuUsage);
    }

}
