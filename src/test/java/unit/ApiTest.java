package unit;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.StringReader;
import java.net.http.HttpResponse;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ApiTest {

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

    void validateErrorMessage(ObjectMapper objectMapper, HttpResponse<String> response) throws JsonProcessingException {
        assertNotNull(response.body());
        JsonNode jsonResponse = objectMapper.readTree(response.body());
        JsonNode errorMessages = jsonResponse.get("errorMessages");
        assertTrue(errorMessages.isArray());
        assertTrue(errorMessages.size() > 0);
    }

    Document parseXmlResponse(String xmlContent) throws ParserConfigurationException, IOException, SAXException {
        // Create a DocumentBuilder for parsing XML
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();

        // Parse the XML content and return a Document object
        return builder.parse(new InputSource(new StringReader(xmlContent)));
    }
}
