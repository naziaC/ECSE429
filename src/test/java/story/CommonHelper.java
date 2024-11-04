package story;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.cucumber.java.After;
import io.cucumber.java.Before;

import java.io.IOException;
import java.net.http.HttpResponse;

public class CommonHelper {

    private static Process runTodoManagerRestAPIProcess;

    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Before
    public void startApplication() throws IOException {
        // Start runTodoManagerRestApi-1.5.5.jar
        ProcessBuilder runTodoManagerRestAPI = new ProcessBuilder("java", "-jar", "runTodoManagerRestAPI-1.5.5.jar");
        runTodoManagerRestAPIProcess = runTodoManagerRestAPI.start();

        // Wait for the application to start up
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Check if the process is running
        if (!runTodoManagerRestAPIProcess.isAlive()) {
            throw new IllegalStateException("Application failed to start.");
        }
    }

    @After
    public void shutdownApplication() throws InterruptedException {
        // Shutdown the application
        ProcessBuilder shutdown = new ProcessBuilder("curl", "http://localhost:4567/shutdown");
        try {
            Process shutdownProcess = shutdown.start();
            shutdownProcess.waitFor();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Check if the application is running
    public static boolean isApplicationRunning() {
        return runTodoManagerRestAPIProcess != null && runTodoManagerRestAPIProcess.isAlive();
    }

    public static JsonNode getObjectFromResponse(HttpResponse<String> response) throws IOException {
        return objectMapper.readTree(response.body());
    }

    public static String getStringFromObject(Object object) throws IOException {
        return objectMapper.writeValueAsString(object);
    }
}
