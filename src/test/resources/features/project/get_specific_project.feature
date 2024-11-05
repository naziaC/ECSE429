Feature: Get a Specific Project
  As a user,
  I want to get a specific project,
  so that I can view its details.

  Background:
    Given the REST API todo list Manager is running
    And the following projects exist in the system:
      | project_id | title    | completed | active | description  |
      | 2          | ProjectA | false     | true   | DescriptionA |
      | 3          | ProjectB | false     | true   | DescriptionB |
      | 4          | ProjectC | false     | true   | DescriptionC |

  Scenario Outline: Get project by ID (Normal flow)
    When a user sends a GET request for an existing project with ID "<project_id>"
    Then the status code 200 will be received
    And the response body should contain project details with title "<title>", completed "<completed>", active "<active>", and description "<description>"
    Examples:
      | project_id | title     | completed | active | description   |
      | 2          | ProjectA  | false     | true   | DescriptionA  |

  Scenario Outline: Get project by title (Alternate flow)
    When a user sends a GET request for an existing project with title "<title>"
    Then the status code 200 will be received
    And the response body should contain project details with title "<title>", completed "<completed>", active "<active>", and description "<description>"
    Examples:
      | title    | completed | active | description  |
      | ProjectB | false     | true   | DescriptionB |

  Scenario Outline: Get a project with invalid ID (Error flow)
    When a user sends a GET request for a nonexistent project with ID "<project_id>"
    Then the status code 404 will be received
    And the response body should contain the error message "Could not find an instance with projects/<project_id>"
    Examples:
      | project_id  |
      | 100         |
