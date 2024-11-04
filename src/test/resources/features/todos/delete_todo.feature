Feature: Delete a Todo
  As a user,
  I want to delete a todo,
  so that it can no longer be found or viewed in the system.

  Background:
    Given the REST API todo list Manager is running
    And the following todos exist in the system:
      | id | title | doneStatus | description  |
      | 3  | todoA | false      | descriptionA |
      | 4  | todoB | true       | descriptionB |

  Scenario Outline: Delete an unfinished todo by ID (Normal flow)
    When a user sends a DELETE request for a todo with ID "<id>"
    Then the status code 200 will be received from the todoAPI
    And the todo with ID "<id>" should no longer exist in the system
    Examples:
      | id |
      | 3  |

  Scenario Outline: Delete a completed todo by ID (Alternate flow)
    When a user sends a DELETE request for a todo with ID "<id>"
    Then the status code 200 will be received from the todoAPI
    And the todo with ID "<id>" should no longer exist in the system
    Examples:
      | id | title | doneStatus | description  |
      | 4  | todoB | true       | descriptionA |

  Scenario Outline: Delete a nonexistent todo by ID (Error flow)
    When a user sends a DELETE request for a todo with ID "<id>"
    Then the status code 404 will be received from the todoAPI
    And the response body from the todoAPI should contain the error message "Could not find any instances with todos/<id>"
    Examples:
      | id |
      | 13 |
