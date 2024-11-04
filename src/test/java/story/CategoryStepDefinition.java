package story;

import com.fasterxml.jackson.databind.JsonNode;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.io.IOException;
import java.net.http.HttpResponse;

import static org.junit.jupiter.api.Assertions.*;

public class CategoryStepDefinition {

    private HttpResponse<String> response;

    // ------ BACKGROUND -------

    @Given("the following categories exist in the system:")
    public void the_following_categories_exist_in_the_system(DataTable dataTable) {
        // Create categories from the data table
        dataTable.asMaps().forEach(row -> {
            String title = row.get("title");
            String description = row.get("description");

            try {
                HelperCategory.createCategory("", title, description);
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        });
    }

    @Given("the following categories and todos are associated with each other:")
    public void the_following_categories_and_todos_are_associated_with_each_other(DataTable dataTable) {
        // Create todos for category
        dataTable.asMaps().forEach(row -> {
            String categoryId = row.get("category_id");
            String todoId = row.get("todo_id");

            try {
                HelperCategory.associateCategoryTodo(categoryId, todoId, "");
                HelperCategory.associateTodoCategory(todoId, categoryId);
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        });
    }

    // ------ get_specific_category.feature -------

    // Scenario Outline: Get category by ID (Normal flow)
    @When("a user sends a GET request for an existing category with id {string}")
    public void a_user_sends_a_GET_request_for_an_existing_category_with_id(String categoryId) throws IOException, InterruptedException {
        response = HelperCategory.getSpecificCategory(categoryId, "");
    }

    // Scenario Outline: Get category by title (Alternate flow)
    @When("a user sends a GET request for an existing category with title {string}")
    public void a_user_sends_a_GET_request_for_an_existing_category_with_title(String title) throws IOException, InterruptedException {
        response = HelperCategory.getSpecificCategory("", title);
    }

    // Scenario Outline: Get a category with invalid ID (Error flow)
    @When("a user sends a GET request for a nonexistent category with id {string}")
    public void a_user_sends_a_GET_request_for_a_nonexistent_category_with_id(String categoryId) throws IOException, InterruptedException {
        response = HelperCategory.getSpecificCategory(categoryId, "");
    }

    @Then("the response body should contain category details with id {string}, title {string}, and description {string}")
    public void the_response_body_should_contain_category_details_with_id_title_and_description(String categoryId, String title, String description) throws IOException {
        // Parse the response body as JSON
        JsonNode responseBody = CommonHelper.getObjectFromResponse(response);
        JsonNode categoriesArray = responseBody.get("categories");

        // Ensure the categories array is not null and contains at least one category
        if (categoriesArray != null && categoriesArray.isArray() && categoriesArray.size() > 0) {
            JsonNode category = categoriesArray.get(0);
            assertEquals(categoryId, category.get("id").asText());
            assertEquals(title, category.get("title").asText());
            assertEquals(description, category.get("description").asText());
        } else {
            fail("No categories found in the response body.");
        }
    }

    // ------ create_category.feature -------

    // Scenario Outline: Create a category with all fields (Normal flow)
    // Scenario Outline: Create a category with no description (Alternate flow)
    // Scenario Outline: Create a category with no title (Error flow)
    @When("a user sends a POST request with title {string} and description {string}")
    public void a_user_sends_a_POST_request_with_title_and_description(String title, String description) throws IOException, InterruptedException {
        response = HelperCategory.createCategory("", title, description);
    }

    // Scenario Outline: Create a category with invalid field (Error flow)
    @When("a user sends a POST request with category id {string}")
    public void a_user_sends_a_POST_request_with_category_id(String categoryId) throws IOException, InterruptedException {
        response = HelperCategory.createCategory(categoryId, "", "");
    }

    // ------ amend_category.feature -------

    // Scenario Outline: Amend a category with POST (Normal flow)
    // Scenario Outline: Amend a category with POST and duplicate title (Error flow)
    @When("a user sends a POST request with category id {string}, title {string}, and description {string}")
    public void a_user_sends_a_POST_request_with_title_and_description_for_an_existing_category_with_id(String categoryId, String title, String description) throws IOException, InterruptedException {
        response = HelperCategory.amendCategoryPost(categoryId, title, description);
    }

    // Scenario Outline: Amend a category with PUT (Alternate flow)
    @When("a user sends a PUT request with category id {string}, title {string}, and description {string}")
    public void a_user_sends_a_PUT_request_with_title_and_description_for_an_existing_category_with_id(String categoryId, String title, String description) throws IOException, InterruptedException {
        response = HelperCategory.amendCategoryPut(categoryId, title, description);
    }

    // Scenario Outline: Amend a category that does not exist (Error flow)
    @When("a user sends a POST request for a nonexistent category with id {string}")
    public void a_user_sends_a_POST_request_for_a_nonexistent_category_with_id(String categoryId) throws IOException, InterruptedException {
        response = HelperCategory.amendCategoryPost(categoryId, "", "");
    }

    // ------ associate_category_todo.feature -------

    // Scenario Outline: Create a relationship between an existing category and an existing todo item (Normal flow)
    // Scenario Outline: Create a relationship between an existing category and a nonexistent todo item (Alternate flow)
    // Scenario Outline: Create a relationship between a nonexistent category and an existing todo item (Error flow)
    @When("a user sends a POST request to associate a todo id {string} of title {string} with a category id {string}")
    public void a_user_sends_a_POST_request_to_associate_a_todo_id_of_title_with_a_category_id(String todoId, String todoTitle, String categoryId) throws IOException, InterruptedException {
        response = HelperCategory.associateCategoryTodo(categoryId, todoId, todoTitle);
    }

    @Then("a new relationship is created between category id {string} and the todo item with id {string} and title {string}")
    public void a_new_relationship_is_created_between_category_id_and_the_todo_item_with_id_and_title(String categoryId, String todoId, String todoTitle) throws IOException, InterruptedException {
        // Get the todos for category with ID
        response = HelperCategory.getCategoryTodos(categoryId);
        JsonNode responseBody = CommonHelper.getObjectFromResponse(response);
        JsonNode todosArray = responseBody.get("todos");

        boolean todoFound = false;
        // Ensure the todos array is not null and contains todo with title
        if (todosArray != null && todosArray.isArray() && todosArray.size() > 0) {
            for (JsonNode todo : todosArray) {
                if (todo.get("title").asText().equals(todoTitle)) {
                    todoFound = true;
                    break;
                }
            }
        }

        // Get the categories for todo with ID
        response = HelperCategory.getTodoCategories(todoId);
        responseBody = CommonHelper.getObjectFromResponse(response);
        JsonNode categoriesArray = responseBody.get("categories");

        boolean categoryFound = false;
        // Ensure the categories array is not null and contains category with title
        if (categoriesArray != null && categoriesArray.isArray() && categoriesArray.size() > 0) {
            for (JsonNode category : categoriesArray) {
                if (category.get("id").asText().equals(categoryId)) {
                    categoryFound = true;
                    break;
                }
            }
        }

        assertTrue(todoFound && categoryFound);
    }

    // ------ delete_category.feature -------

    // Scenario Outline: Delete a category by ID with no associated todos (Normal flow)
    // Scenario Outline: Delete a category by ID with associated todos (Alternate flow)
    // Scenario Outline: Delete a nonexistent category by ID (Error flow)
    @When("a user sends a DELETE request for a category with id {string}")
    public void a_user_sends_a_DELETE_request_for_a_category_with_id(String category_id) throws IOException, InterruptedException {
        response = HelperCategory.deleteCategory(category_id);
    }

    @Then("the category with id {string} should no longer exist in the system")
    public void the_category_with_id_should_no_longer_exist_in_the_system(String categoryId) throws IOException, InterruptedException {
        // Send a GET request to verify the category no longer exists
        response = HelperCategory.getSpecificCategory(categoryId, "");

        // Verify the category does not exist
        JsonNode responseBody = CommonHelper.getObjectFromResponse(response);
        assertTrue(responseBody.has("errorMessages"));
    }

    @Then("the relationship will be removed between the category with id {string} and the associated todo items")
    public void the_relationship_will_be_removed_between_the_category_with_id_and_the_associated_todo_items(String categoryId) throws IOException, InterruptedException {
        // Send a GET requests to verify the todos have no relation to the category
        response = HelperCategory.getAllTodos();

        // Verify the deleted category is not related to any todos
        JsonNode responseBody = CommonHelper.getObjectFromResponse(response);
        JsonNode todosArray = responseBody.get("todos");

        // Ensure the todos array is not null
        if (todosArray != null && todosArray.isArray()) {
            for (JsonNode todo : todosArray) {
                JsonNode categoriesArray = todo.get("categories");
                if (categoriesArray != null && categoriesArray.isArray()) {
                    for (JsonNode category : categoriesArray) {
                        if (category.get("id").asText().equals(categoryId)) fail("Deleted category still related to todo item.");
                    }
                }
            }
        }
    }

    // ------ COMMON -------
    @Then("the status code {int} will be received from categoryAPI")
    public void the_status_code_will_be_received_from_categoryAPI(int expectedStatusCode) {
        assertEquals(expectedStatusCode, response.statusCode());
    }

    @Then("a new category is created with title {string} and description {string}")
    @Then("the category is updated with title {string} and description {string}")
    public void a_new_category_is_created_with_title_and_description(String title, String description) throws IOException {
        // Parse the response body as JSON
        JsonNode responseBody = CommonHelper.getObjectFromResponse(response);

        // Check if the category is created successfully
        assertTrue(responseBody.has("id"));
        assertEquals(title, responseBody.get("title").asText());
        assertEquals(description, responseBody.get("description").asText());
    }

    @Then("the response body should contain the error message {string} from categoryAPI")
    public void the_response_body_should_contain_the_error_message_from_categoryAPI(String errorMessage) throws IOException {
        JsonNode responseBody = CommonHelper.getObjectFromResponse(response);
        assertTrue(responseBody.has("errorMessages"));
        assertEquals(errorMessage, responseBody.get("errorMessages").get(0).asText());
    }
}
