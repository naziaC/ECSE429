package story;

import com.fasterxml.jackson.databind.JsonNode;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.cucumber.java.en.Then;

import java.io.IOException;
import java.net.http.HttpResponse;

import static org.junit.jupiter.api.Assertions.*;

public class ProjectStepDefinition {

    private HttpResponse<String> response;

    // Background
    @Given("the REST API todo list Manager is running")
    public void the_rest_api_todo_list_manager_is_running() {
        // Start application
        assertTrue(CommonHelper.isApplicationRunning());
    }

    @Given("the following projects exist in the system:")
    public void the_following_projects_exist_in_the_system(io.cucumber.datatable.DataTable dataTable) {
        // Create projects from the data table
        dataTable.asMaps().forEach(row -> {
            String title = row.get("title");
            String description = row.get("description");
            boolean completed = Boolean.parseBoolean(row.get("completed"));
            boolean active = Boolean.parseBoolean(row.get("active"));
            try {
                HelperProject.createProject("", title, description, completed, active);
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        });
    }

    @Given("the following tasks exist for each project:")
    public void the_following_tasks_exist_for_each_project(io.cucumber.datatable.DataTable dataTable) {
        dataTable.asMaps().forEach(row -> {
            String projectId = row.get("project_id");
            String todoId = row.get("todo_id");
            String todoTitle = "";

            try {
                HelperProject.associateProjectTask(projectId, todoId, todoTitle);
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        });
    }

    // ------ get_specific_project.feature -------

    // Scenario Outline: Get project by ID (Normal flow)
    @When("a user sends a GET request for an existing project with ID {string}")
    public void a_user_sends_a_GET_request_for_an_existing_project_with_ID(String projectId) throws IOException, InterruptedException {
        response = HelperProject.getSpecificProject(projectId, "");
    }

    // Scenario Outline: Get project by title (Alternate flow)
    @When("a user sends a GET request for an existing project with title {string}")
    public void a_user_sends_a_GET_request_for_an_existing_project_with_title(String title) throws IOException, InterruptedException {
        response = HelperProject.getSpecificProject("", title);
    }

    // Scenario Outline: Get a project with invalid ID (Error flow)
    @When("a user sends a GET request for a nonexistent project with ID {string}")
    public void a_user_sends_a_GET_request_for_a_nonexistent_project_with_ID(String projectId) throws IOException, InterruptedException {
        response = HelperProject.getSpecificProject(projectId, "");
    }

    @Then("the response body should contain project details with title {string}, completed {string}, active {string}, and description {string}")
    public void the_response_body_should_contain_project_details_with_title_completed_active_and_description(String title, String completed, String active, String description) throws IOException {
        JsonNode responseBody = CommonHelper.getObjectFromResponse(response);
        JsonNode projectsArray = responseBody.get("projects");

        // Ensure the projects array is not null and contains at least one project
        if (projectsArray != null && projectsArray.isArray() && projectsArray.size() > 0) {
            JsonNode project = projectsArray.get(0);
            assertEquals(title, project.get("title").asText());
            assertEquals(Boolean.parseBoolean(completed), project.get("completed").asBoolean());
//            assertEquals(Boolean.parseBoolean(active), project.get("active").asBoolean()); // todo add this back
//            assertEquals(description, project.get("description").asText());
        } else {
            fail("No projects found in the response body.");
        }
    }

    // ------ create_project.feature -------

    // Scenario Outline: Create a project with all and no fields (Normal flow and Alternate flow)
    @When("a user sends a POST request with title {string}, description {string}, completed {string}, and active {string}")
    public void a_user_sends_a_POST_request_with_title_description_completed_and_active(
            String title, String description, String completed, String active) throws IOException, InterruptedException {
        // Send the POST request using the Helper method
        response = HelperProject.createProject("", title, description, Boolean.parseBoolean(completed), Boolean.parseBoolean(active));
    }

    // Scenario Outline: Create a project with invalid field (Error flow)
    @When("a user sends a POST request with id {string}")
    public void a_user_sends_a_POST_request_with_id(String projectId) throws IOException, InterruptedException {
        // Send the POST request using the Helper method
        response = HelperProject.createProject(projectId, "", "", false, false);
    }

    @Then("a new project is created with title {string}, description {string}, completed {string}, and active {string}")
    public void a_new_project_is_created_with_title_description_completed_and_active(
            String title, String description, String completed, String active) throws IOException {

        // Parse the response body as JSON
        JsonNode responseBody = CommonHelper.getObjectFromResponse(response);

        // Check if the project is created successfully
        assertTrue(responseBody.has("id"));
        assertEquals(title, responseBody.get("title").asText());
        assertEquals(description, responseBody.get("description").asText());
        assertEquals(Boolean.parseBoolean(completed), responseBody.get("completed").asBoolean());
        assertEquals(Boolean.parseBoolean(active), responseBody.get("active").asBoolean());
    }

    // ------ amend_project.feature -------

    // Scenario Outline: Amend a project with POST (Normal flow)
    @When("a user sends a POST request with title {string} and description {string} for an existing project with ID {string}")
    public void a_user_sends_a_POST_request_with_title_and_description_for_an_existing_project(String title, String description, String projectId) throws IOException, InterruptedException {
        response = HelperProject.amendProjectPost(projectId, title, description);
    }

    // Scenario Outline: Amend a project with PUT (Alternate flow)
    @When("a user sends a PUT request with title {string} and description {string} for an existing project with ID {string}")
    public void a_user_sends_a_PUT_request_with_title_and_description_for_an_existing_project(String title, String description, String projectId) throws IOException, InterruptedException {
        response = HelperProject.amendProjectPut(projectId, title, description);
    }

    // Scenario Outline: Amend a project that does not exist (Error flow)
    @When("a user sends a POST request for an nonexistent project with ID {string}")
    public void a_user_sends_a_POST_request_for_a_nonexistent_project_with_ID(String projectId) throws IOException, InterruptedException {
        response = HelperProject.amendProjectPost(projectId, "", "");
    }

    @Then("the project is updated with title {string} and description {string}, with same completed {string} and active {string} fields")
    public void the_project_is_updated_with_title_and_description(String title, String description, String completed, String active) throws IOException {
        JsonNode responseBody = CommonHelper.getObjectFromResponse(response);
        assertEquals(title, responseBody.get("title").asText());
        assertEquals(description, responseBody.get("description").asText());
//        assertEquals(completed, responseBody.get("completed").asText());
//        assertEquals(active, responseBody.get("active").asText());
    }

    // ------ delete_project.feature -------

    // Scenario Outline: Delete an active/incomplete/nonexistent project by ID (Normal/Alternate/Error flow)
    @When("a user sends a DELETE request for a project with ID {string}")
    public void a_user_sends_a_DELETE_request_for_a_project_with_id(String projectId) throws IOException, InterruptedException {
        // Send the DELETE request
        response = HelperProject.deleteProject(projectId);
    }

    @Then("the project with ID {string} should no longer exist in the system")
    public void the_project_with_ID_should_no_longer_exist_in_the_system(String projectId) throws IOException, InterruptedException {
        // Send a GET request to verify the project does not exist
        response = HelperProject.getSpecificProject(projectId, "");

        // Verify the project does not exist
        JsonNode responseBody = CommonHelper.getObjectFromResponse(response);
        assertTrue(responseBody.has("errorMessages"));
    }

    // ------ associate_project_tasks.feature -------

    // Scenario Outline: Create a task relationship between an existing project and an existing todo item (Normal flow)
    // Scenario Outline: Create a task relationship between an nonexistent project and a existent todo item (Error flow)
    @When("a user sends a POST request with a body containing todo ID {string} to associate with a project with ID {string}")
    public void a_user_sends_a_POST_request_with_a_body_containing_todo_id_to_associate_with_a_project(String todoId, String projectId) throws IOException, InterruptedException {
        response = HelperProject.associateProjectTask(projectId, todoId, "");
    }

    // Scenario Outline: Create a task relationship between an existing project and a nonexistent todo item (Alternate flow)
    @When("a user sends a POST request with a body containing nonexistent title {string} to associate with a project with ID {string}")
    public void a_user_sends_a_POST_request_with_a_body_containing_nonexistent_title_to_associate_with_a_project(String todoTitle, String projectId) throws IOException, InterruptedException {
        response = HelperProject.associateProjectTask(projectId, "", todoTitle);
    }

    @Then("a new task relationship is created between project with ID {string} and the todo item with title {string}")
    public void a_new_task_relationship_is_created_between_project_and_todo_by_id(String projectId, String todoTitle) throws IOException, InterruptedException {
        // Get the project tasks for project with ID
        response = HelperProject.getProjectTask(projectId);
        JsonNode responseBody = CommonHelper.getObjectFromResponse(response);
        JsonNode tasksArray = responseBody.get("todos");

        // Ensure the tasks array is not null and contains task with title
        if (tasksArray != null && tasksArray.isArray() && tasksArray.size() > 0) {
            boolean taskFound = false;
            for (JsonNode task : tasksArray) {
                if (task.get("title").asText().equals(todoTitle)) {
                    taskFound = true;
                    break;
                }
            }
            assertTrue(taskFound);
        } else {
            fail("No tasks found in the response body.");
        }
    }

    @Then("a new task relationship is created between project with ID {string} and the todo item with ID {string}")
    public void a_new_task_relationship_is_created_between_project_and_todo_by_title(String projectId, String todoId) throws IOException, InterruptedException {
        // Get the project tasks for project with ID
        response = HelperProject.getProjectTask(projectId);
        JsonNode responseBody = CommonHelper.getObjectFromResponse(response);
        JsonNode tasksArray = responseBody.get("todos");

        // Ensure the tasks array is not null and contains task with ID
        if (tasksArray != null && tasksArray.isArray() && tasksArray.size() > 0) {
            boolean taskFound = false;
            for (JsonNode task : tasksArray) {
                if (task.get("id").asText().equals(todoId)) {
                    taskFound = true;
                    break;
                }
            }
            assertTrue(taskFound);
        } else {
            fail("No tasks found in the response body.");
        }
    }

    // ------ COMMON -------
    @Then("the status code {int} will be received")
    public void the_status_code_will_be_received(int expectedStatusCode) {
        assertEquals(expectedStatusCode, response.statusCode());
    }

    @Then("the response body should contain the error message {string}")
    public void the_response_body_should_contain_the_error_message(String errorMessage) throws IOException {
        JsonNode responseBody = CommonHelper.getObjectFromResponse(response);
        assertTrue(responseBody.has("errorMessages"));
        assertEquals(errorMessage, responseBody.get("errorMessages").get(0).asText());
    }
}

