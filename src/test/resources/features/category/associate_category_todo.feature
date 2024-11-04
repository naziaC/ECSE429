Feature: Associate Category Todos
  As a user,
  I want to link a category to a todo item,
  so that I can manage todo items associated with each category.

  Background:
    Given the REST API todo list Manager is running
    And the following categories exist in the system:
      | category_id | title     | description  |
      | 3           | CategoryA | DescriptionA |
      | 4           | CategoryB | DescriptionB |
      | 5           | CategoryC | DescriptionC |
    And the following categories and todos are associated with each other:
      | category_id | todo_id |
      | 3           | 1       |
      | 3           | 2       |

  Scenario Outline: Create a relationship between an existing category and an existing todo item (Normal flow)
    When a user sends a POST request to associate a todo id "<todo_id>" of title "<todo_title>" with a category id "<category_id>"
    Then the status code 201 will be received from categoryAPI
    And a new relationship is created between category id "<category_id>" and the todo item with id "<todo_id>" and title "<todo_title>"
    Examples:
      | category_id | todo_id | todo_title     |
      | 4           | 1       | scan paperwork |

  Scenario Outline: Create a relationship between an existing category and a nonexistent todo item (Alternate flow)
    When a user sends a POST request to associate a todo id "<todo_id>" of title "<todo_title>" with a category id "<category_id>"
    Then the status code 201 will be received from categoryAPI
    And a new relationship is created between category id "<category_id>" and the todo item with id "<todo_id>" and title "<todo_title>"
    Examples:
      | category_id | todo_id | todo_title |
      | 4           |         | ToDoA      |

  Scenario Outline: Create a relationship between a nonexistent category and an existing todo item (Error flow)
    When a user sends a POST request to associate a todo id "<todo_id>" of title "<todo_title>" with a category id "<category_id>"
    Then the status code 404 will be received from categoryAPI
    And the response body should contain the error message "Could not find parent thing for relationship categories/<category_id>/todos" from categoryAPI
    Examples:
      | category_id | todo_id | todo_title     |
      | 100         | 1       | scan paperwork |
