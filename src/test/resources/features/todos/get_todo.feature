Feature: Get Todo
  As a user,
  I want to get a todo,
  so that I can view its details.

  Background:
    Given the REST API todo list Manager is running
    And the following todos exist in the system:
      | id | title | doneStatus | description  |
      | 3  | todoA | false      | descriptionA |
      | 4  | todoB | false      | descriptionB |

  Scenario Outline: Get todo by ID (Normal flow)
    When a user sends a GET request for an existing todo with id "<id>"
    Then the status code 200 will be received from the todoAPI
    And the response body should contain todo details with with title "<title>", doneStatus "<doneStatus>" and description "<description>"
    Examples:
      | id | title | doneStatus | description  |
      | 3  | todoA | false      | descriptionA |

  Scenario Outline: Get todo by title (Alternate flow)
    When a user sends a GET request for an existing todo with title "<title>"
    Then the status code 200 will be received from the todoAPI
    And the response body should contain todo details with with title "<title>", doneStatus "<doneStatus>" and description "<description>"
    Examples:
      | id | title | doneStatus | description  |
      | 4  | todoB | false      | descriptionB |


  Scenario Outline: Get a todo with invalid ID (Error flow)
    When a user sends a GET request for a nonexistent todo with ID "<id>"
    Then the status code 404 will be received from the todoAPI
    And the response body from the todoAPI should contain the error message "Could not find an instance with todos/<id>"
    Examples:
      | id  |
      | 13  |
