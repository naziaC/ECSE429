Feature: Delete a Project
  As a user,
  I want to delete a project,
  so that it cannot be viewed from the system when it is no longer needed.

  Background:
    Given the REST API todo list Manager is running
    And the following projects exist in the system:
      | project_id | title    | completed | active | description  |
      | 2          | ProjectA | false     | false  | DescriptionA |
      | 3          | ProjectB | false     | true   | DescriptionB |
      | 4          | ProjectC | false     | false  | DescriptionC |

  Scenario Outline: Delete an active project by ID (Normal flow)
    When a user sends a DELETE request for a project with ID "<project_id>"
    Then the status code 200 will be received
    And the project with ID "<project_id>" should no longer exist in the system
    Examples:
      | project_id |
      | 3          |

  Scenario Outline: Delete an incomplete project by ID (Alternate flow)
    When a user sends a DELETE request for a project with ID "<project_id>"
    Then the status code 200 will be received
    And the project with ID "<project_id>" should no longer exist in the system
    Examples:
      | project_id |
      | 2          |

  Scenario Outline: Delete a nonexistent project by ID (Error flow)
    When a user sends a DELETE request for a project with ID "<project_id>"
    Then the status code 404 will be received
    And the response body should contain the error message "Could not find any instances with projects/<project_id>"
    Examples:
      | project_id |
      | 100        |
