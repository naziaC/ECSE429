import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;

import static org.junit.jupiter.api.Assertions.*;

class ApiTest {

    private ProcessBuilder runTodoManagerRestAPI;

    @BeforeEach
    void setupProcess() throws IOException {

        // Start runTodoManagerRestApi-1.5.5.jar
        runTodoManagerRestAPI = new ProcessBuilder("java", "-jar", "runTodoManagerRestAPI-1.5.5.jar");
        runTodoManagerRestAPI.start();

        // Wait for the application to start up
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @AfterEach
    void shutdown() throws InterruptedException {
        // http://localhost:4567/shutdown
        ProcessBuilder shutdown = new ProcessBuilder("curl", "http://localhost:8080/shutdown");
        try {
            Process shutdownProcess = shutdown.start();
            shutdownProcess.waitFor();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Example Category Test
    @Test
    void testCategoryGetRequest() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:4567/categories"))
                .build();

        HttpResponse<String> response = client.send(request, BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
    }
}
