package performance;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class PerformanceTestTools {
    static final int[] objectCount = {1, 5, 10, 25, 50, 75, 100, 250, 500, 750, 1000};

    // (This was taken from unit/ApiTest.java)
    @BeforeAll
    static void setupProcess() throws IOException {
        // Start runTodoManagerRestApi-1.5.5.jar
        ProcessBuilder runTodoManagerRestAPI = new ProcessBuilder("java", "-jar", "runTodoManagerRestAPI-1.5.5.jar");
        Process runTodoManagerRestAPIProcess = runTodoManagerRestAPI.start();

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

    // (This was taken from unit/ApiTest.java)
    @AfterAll
    static void shutdown() throws InterruptedException {
        // Shutdown the application
        ProcessBuilder shutdown = new ProcessBuilder("curl", "http://localhost:4567/shutdown");
        try {
            Process shutdownProcess = shutdown.start();
            shutdownProcess.waitFor();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Helper method to create csv file under /data
    public static void writeCSV(String fileName, String objectName, double[] elapsedTime, double[] memoryUsage, double[] cpuUsage){
        Path dataPath = Paths.get("src","test", "java", "performance", "data", fileName);

        try (FileWriter writer = new FileWriter(dataPath.toFile())){
            String[] header = {String.format("Number of %s Objects", objectName), "Transaction Time (ms)", "Memory Usage (mb)", "CPU Usage (%)"};
            writer.append(String.join(",", header));
            writer.append('\n');

            String dataRow = "";
            for (int i = 0; i<objectCount.length; i++){
                dataRow = objectCount[i] + "," + elapsedTime[i] + "," + memoryUsage[i] + "," + cpuUsage[i];
                writer.append(dataRow);
                writer.append('\n');
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


}
