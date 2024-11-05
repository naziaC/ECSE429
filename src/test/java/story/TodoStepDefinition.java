package story;

import com.fasterxml.jackson.databind.JsonNode;
// import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;

import java.io.IOException;
import java.net.http.HttpResponse;

import static org.junit.jupiter.api.Assertions.*;

public class TodoStepDefinition {

    private HttpResponse<String> response;

    @Given("the following todos exist in the system:")
    public void the_following_todos_exist_in_the_system(io.cucumber.datatable.DataTable dataTable) {
        dataTable.asMaps().forEach(row -> {
            String title = row.get("title");
            boolean doneStatus =  Boolean.parseBoolean(row.get("doneStatus"));
            String description = row.get("description");
            try {
                HelperTodo.createTodo("", title, doneStatus, description);
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        });
    }

    // ------ create_todo.feature ------- 
    
    // Scenario Outline: Create a todo with all some, or no fields (Normal, Alternate and Error flow)
    @When("a user sends a POST request with title {string}, doneStatus {string} and description {string}")
    public void a_user_sends_a_post_request_with_title_done_status_and_description(
        String title, String doneStatus, String description) throws IOException, InterruptedException {
        // Send the POST request using the Helper method
        response = HelperTodo.createTodo("", title, Boolean.parseBoolean(doneStatus), description);
    }

    @Then("a new todo is created with title {string}, doneStatus {string} and description {string}")
    public void a_new_todo_is_created_with_title_done_status_and_description(
            String title, String doneStatus, String description) throws IOException {

        // Parse the response body as JSON
        JsonNode responseBody = CommonHelper.getObjectFromResponse(response);

        // Check if the project is created successfully
        assertTrue(responseBody.has("id"));
        assertEquals(title, responseBody.get("title").asText());
        assertEquals(Boolean.parseBoolean(doneStatus), responseBody.get("doneStatus").asBoolean());
        assertEquals(description, responseBody.get("description").asText());
    }

    // ------ delete_todo.feature ------- 

    // Scenario Outline: Delete an unfinished/completed/nonexistent todo by ID (Normal, Alternate and Error flow)
    @When("a user sends a DELETE request for a todo with ID {string}")
    public void a_user_sends_a_delete_request_for_a_todo_with_id(String todoId) throws IOException, InterruptedException{
        // Send the DELETE request
        response = HelperTodo.deleteTodo(todoId);
    }

    @Then("the todo with ID {string} should no longer exist in the system")
    public void the_todo_with_id_should_no_longer_exist_in_the_system(String todoId) throws IOException, InterruptedException{
        // Send a GET request to verify the project does not exist
        response = HelperTodo.getSpecificTodo(todoId, "");

        // Verify the project does not exist
        JsonNode responseBody = CommonHelper.getObjectFromResponse(response);
        assertTrue(responseBody.has("errorMessages"));
    }

    // ------ amend_todo.feature ------- 

    // Scenario Outline: Amend a todo with POST (Normal flow)
    @When("a user sends a POST request with description {string} for an existing todo with ID {string}")
    public void a_user_sends_a_post_request_with_description_for_an_existing_todo_with_id(
        String description, String todoId) throws IOException, InterruptedException {
            response = HelperTodo.amendTodoPost(todoId, description);
    }

    // Scenario Outline: Amend a todo with PUT (Alternate flow) --> fails 
    @When("a user sends a PUT request with description {string} for an existing todo with ID {string}")
    public void a_user_sends_a_put_request_with_description_for_an_existing_todo_with_id(
        String description, String todoId) throws IOException, InterruptedException {
            response = HelperTodo.amendTodoPut(todoId, description);
    }

    // Scenario Outline: Amend a todo that does not exist (Error flow)
    @When("a user sends a POST request for an nonexistent todo with ID {string}")
    public void a_user_sends_a_post_request_for_an_nonexistent_todo_with_id(String todoId) throws IOException, InterruptedException {
        response = HelperTodo.amendTodoPost(todoId, "");
    }

    @Then("the todo is updated with description {string} while title {string} and doneStatus {string} remain the same")
    public void the_todo_is_updated_with_description_while_title_and_done_status_remain_the_same(
        String description, String title, String doneStatus) throws IOException, InterruptedException {

        JsonNode responseBody = CommonHelper.getObjectFromResponse(response);
        assertEquals(title, responseBody.get("title").asText());
        assertEquals(description, responseBody.get("description").asText());
        assertEquals(Boolean.parseBoolean(doneStatus), responseBody.get("doneStatus").asBoolean());
    }

    // ------ get_todo.feature ------- 

    // Scenario Outline: Get todo by ID (Normal flow)
    @When("a user sends a GET request for an existing todo with id {string}")
    public void a_user_sends_a_get_request_for_an_existing_todo_with_id(String todoId) throws IOException, InterruptedException{
        response = HelperTodo.getSpecificTodo(todoId, "");
    }

    // Scenario Outline: Get todo by title (Alternate flow)
    @When("a user sends a GET request for an existing todo with title {string}")
    public void a_user_sends_a_get_request_for_an_existing_todo_with_title(String title) throws IOException, InterruptedException{
        // Write code here that turns the phrase above into concrete actions
        response = HelperTodo.getSpecificTodo("", title);
    }

    // Scenario Outline: Get a todo with invalid ID (Error flow)
    @When("a user sends a GET request for a nonexistent todo with ID {string}")
    public void a_user_sends_a_get_request_for_a_nonexistent_todo_with_id(String todoId) throws IOException, InterruptedException{
        response = HelperTodo.getSpecificTodo(todoId, "");
    }

    @Then("the response body should contain todo details with with title {string}, doneStatus {string} and description {string}")
    public void the_response_body_should_contain_todo_details_with_with_title_done_status_and_description(
        String title, String doneStatus, String description)  throws IOException, InterruptedException{
        JsonNode responseBody = CommonHelper.getObjectFromResponse(response);
        JsonNode todoArray = responseBody.get("todos");

        // check that the todo array has at least one todo inside ofit 
        if (todoArray != null && todoArray.isArray() && todoArray.size() > 0) {
            JsonNode todo = todoArray.get(0);
            assertEquals(title, todo.get("title").asText());
            assertEquals(Boolean.parseBoolean(doneStatus), todo.get("doneStatus").asBoolean());
            assertEquals(description, todo.get("description").asText());
        } 
        else {
            fail("No todos found in the response body.");
        }
    }
    
    // ------ associate_todo_project.feature ------

    // Scenario Outline: Create a task relationship between an existing todo and an existing project (Normal flow)
    // Scenario Outline: Create a task relationship between a nonexisting todo and an existing project (Error flow)
    @When("a user sends a POST request to associate a todo id {string} with a project id {string}")
    public void a_user_sends_a_post_request_to_associate_a_todo_id_with_a_project_id(String todo_id, String project_id) throws IOException, InterruptedException {
        response = HelperTodo.associateTodoProject(todo_id, project_id);
    }

    // Scenario Outline: Create a task relationship between an existing todo and a nonexistent project (Alternate flow)
    @When("a user sends a POST request to associate a todo id {string} with an empty body for the project ID")
    public void a_user_sends_a_post_request_to_associate_a_todo_id_with_an_empty_body_for_the_project_id(String todo_id) throws IOException, InterruptedException {
        response = HelperTodo.associateTodoProject(todo_id, "");
    }

    @Then("the todo id {string} is associated with project id {string}")
    public void the_todo_id_is_associated_with_project_id(String todo_id, String project_id) throws IOException, InterruptedException {
        // Get the todos for category with ID
        response = HelperProject.getProjectTask(project_id);
        JsonNode responseBody = CommonHelper.getObjectFromResponse(response);
        JsonNode todosArray = responseBody.get("todos");

        boolean todoFound = false;
        // Ensure the todos array is not null and contains todo with title
        if (todosArray != null && todosArray.isArray() && todosArray.size() > 0) {
            for (JsonNode todo : todosArray) {
                if (todo.get("id").asText().equals(todo_id)) {
                    todoFound = true;
                    break;
                }
            }
        }

        assertTrue(todoFound);
    }

    @Then("a project is created with id {string}, title {string} and description {string}")
    public void a_project_is_created_with_id_title_and_description(String project_id, String title, String description) throws IOException, InterruptedException {
        JsonNode responseBody = CommonHelper.getObjectFromResponse(response);
        assertEquals(project_id, responseBody.get("id"));
        assertEquals(title, responseBody.get("title"));
        assertEquals(description, responseBody.get("description"));
    }
    

    @Then("the project id {string} is associated with todo id {string}")
    public void the_project_id_is_associated_with_todo_id(String project_id, String todo_id) throws IOException, InterruptedException {
        // Get the project related to do with ID
        response = HelperTodo.getProjectsOfTask(todo_id);
        JsonNode responseBody = CommonHelper.getObjectFromResponse(response);
        JsonNode projectArray = responseBody.get("projects");

        boolean projectFound = false;
        // Ensure the todos array is not null and contains todo with title
        if (projectArray != null && projectArray.isArray() && projectArray.size() > 0) {
            for (JsonNode project : projectArray) {
                if (project.get("id").asText().equals(project_id)) {
                    projectFound = true;
                    break;
                }
            }
        }

        assertTrue(projectFound);
    }

    // ------ COMMON -------
    @Then("the status code {int} will be received from the todoAPI")
    public void the_status_code_will_be_received_from_the_todo_api(int expectedStatusCode) {
        assertEquals(expectedStatusCode, response.statusCode());
    }

    @Then("the response body from the todoAPI should contain the error message {string}")
    public void the_response_body_from_the_todo_api_should_contain_the_error_message(String errorMessage) throws IOException {
        JsonNode responseBody = CommonHelper.getObjectFromResponse(response);
        assertTrue(responseBody.has("errorMessages"));
        assertEquals(errorMessage, responseBody.get("errorMessages").get(0).asText());
    }

}

