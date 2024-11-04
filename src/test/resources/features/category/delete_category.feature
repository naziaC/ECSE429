Feature: Delete a Category
  As a user,
  I want to delete a category,
  so that it is removed from the management system when it is no longer needed.

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

  Scenario Outline: Delete a category by ID with no associated todos (Normal flow)
    When a user sends a DELETE request for a category with id "<category_id>"
    Then the status code 200 will be received from categoryAPI
    And the category with id "<category_id>" should no longer exist in the system
    Examples:
      | category_id |
      | 4           |

  Scenario Outline: Delete a category by ID with associated todos (Alternate flow)
    When a user sends a DELETE request for a category with id "<category_id>"
    Then the status code 200 will be received from categoryAPI
    And the category with id "<category_id>" should no longer exist in the system
    And the category id "<category_id>" is no longer associated with any todos
    Examples:
      | category_id |
      | 3           |

  Scenario Outline: Delete a nonexistent category by ID (Error flow)
    When a user sends a DELETE request for a category with id "<category_id>"
    Then the status code 404 will be received from categoryAPI
    And the response body should contain the error message "Could not find any instances with categories/<category_id>" from categoryAPI
    Examples:
      | category_id |
      | 100         |
