import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

import java.io.IOException;

class ApiTest {

    @BeforeEach
    void setupProcess() throws IOException {

        // Start runTodoManagerRestApi-1.5.5.jar
        ProcessBuilder runTodoManagerRestAPI = new ProcessBuilder("java", "-jar", "runTodoManagerRestAPI-1.5.5.jar");
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
        ProcessBuilder shutdown = new ProcessBuilder("curl", "http://localhost:4567/shutdown");
        try {
            Process shutdownProcess = shutdown.start();
            shutdownProcess.waitFor();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
