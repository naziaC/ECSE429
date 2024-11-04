Feature: Create a todo
  As a user,
  I want to create a todo,
  so that I can keep track of the tasks that need to be done.

  Background:
    Given the REST API todo list Manager is running

  Scenario Outline: Create a todo with a title, doneStatus and description (Normal flow)
    When a user sends a POST request with title "<title>", doneStatus "<doneStatus>" and description "<description>"
    Then the status code 201 will be received from the todoAPI
    And a new todo is created with title "<title>", doneStatus "<doneStatus>" and description "<description>"
    Examples:
      | title    | doneStatus | description  |
      | todoA    | false      | descriptionA |

  Scenario Outline: Create a todo without a doneStatus or a description (Alternate flow)
    When a user sends a POST request with title "<title>", doneStatus "<doneStatus>" and description "<description>"
    Then the status code 201 will be received from the todoAPI
    And a new todo is created with title "<title>", doneStatus "<doneStatus>" and description "<description>"
    Examples:
      | title    | doneStatus | description  |
      | todoA    |            |              |

  Scenario Outline: Create a todo without a title (Error flow)
    When a user sends a POST request with title "<title>", doneStatus "<doneStatus>" and description "<description>"
    Then the status code 400 will be received from the todoAPI
    And the response body from the todoAPI should contain the error message "Failed Validation: title : can not be empty"
    Examples:
      | title    | doneStatus | description  |
      |          | false      | descriptionA |
