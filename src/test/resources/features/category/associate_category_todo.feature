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

  Scenario Outline: Create a relationship between an existing category and an existing todo item by todo id (Normal flow)
    When a user sends a POST request to associate a todo id "<todo_id>" with a category id "<category_id>"
    Then the status code 201 will be received from categoryAPI
    And the todo id "<todo_id>" is associated with category id "<category_id>"
    And the category id "<category_id>" is associated with todo id "<todo_id>"
    Examples:
      | category_id | todo_id | todo_title     |
      | 4           | 1       | scan paperwork |

  Scenario Outline: Create a relationship between an existing category and an existing todo item by todo title (Alternate flow)
    When a user sends a POST request to associate a todo title "<todo_title>" with a category id "<category_id>"
    Then the status code 201 will be received from categoryAPI
    And the todo id "<todo_id>" is associated with category id "<category_id>"
    And the category id "<category_id>" is associated with todo id "<todo_id>"
    Examples:
      | category_id | todo_id | todo_title     |
      | 4           | 2       | file paperwork |

  Scenario Outline: Create a relationship between a nonexistent category and an existing todo item (Error flow)
    When a user sends a POST request to associate a todo id "<todo_id>" with a category id "<category_id>"
    Then the status code 404 will be received from categoryAPI
    And the response body should contain the error message "Could not find parent thing for relationship categories/<category_id>/todos" from categoryAPI
    Examples:
      | category_id | todo_id |
      | 100         | 1       |
