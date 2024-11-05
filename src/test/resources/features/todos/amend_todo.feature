Feature: Amend Todo
  As a user,
  I want to amend a todo,
  so that I can update the details of a todo.

  Background:
    Given the REST API todo list Manager is running
    And the following todos exist in the system:
      | id | title | doneStatus | description  |
      | 3  | todoA | false      | descriptionA |
      | 4  | todoB | false      | descriptionB |

  Scenario Outline: Amend a todo with POST (Normal flow)
    When a user sends a POST request with description "<description>" for an existing todo with ID "<id>"
    Then the status code 200 will be received from the todoAPI
    And the todo is updated with description "<description>" while title "<title>" and doneStatus "<doneStatus>" remain the same
    Examples:
      | id | title | doneStatus | description         |
      | 3  | todoA | false      | updatedDescriptionA |

  Scenario Outline: Amend a todo with PUT (Alternate flow)
    When a user sends a PUT request with description "<description>" for an existing todo with ID "<id>"
    Then the status code 200 will be received from the todoAPI
    And the todo is updated with description "<description>" while title "<title>" and doneStatus "<doneStatus>" remain the same
    Examples:
      | id | title | doneStatus | description         |
      | 4  | todoB | false      | updatedDescriptionB |

  Scenario Outline: Amend a todo that does not exist (Error flow)
    When a user sends a POST request for an nonexistent todo with ID "<id>"
    Then the status code 404 will be received from the todoAPI
    And the response body from the todoAPI should contain the error message "No such todo entity instance with GUID or ID <id> found"
    Examples:
      | id  |
      | 13  |
