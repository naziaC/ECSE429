Feature: Create a project
  As a user,
  I want to create a project,
  so that I can manage my tasks and organize my work.

  Background:
    Given the REST API todo list Manager is running

  Scenario Outline: Create a project with all fields (Normal flow)
    When a user sends a POST request with title "<title>", description "<description>", completed "<completed>", and active "<active>"
    Then the status code 201 will be received
    And a new project is created with title "<title>", description "<description>", completed "<completed>", and active "<active>"
    Examples:
      | title    | completed | active | description  |
      | ProjectD | false     | false  | DescriptionD |

  Scenario Outline: Create a project with no fields (Alternate flow)
    When a user sends a POST request with title "<title>", description "<description>", completed "<completed>", and active "<active>"
    Then the status code 201 will be received
    And a new project is created with title "<title>", description "<description>", completed "<completed>", and active "<active>"
    Examples:
      | title | completed | active | description |
      |       | false     | false  |             |

  Scenario Outline: Create a project with invalid field (Error flow)
    When a user sends a POST request with id "<project_id>"
    Then the status code 400 will be received
    And the response body should contain the error message "Invalid Creation: Failed Validation: Not allowed to create with id"
    Examples:
      | project_id |
      | 100        |
