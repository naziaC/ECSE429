Feature: Amend a Project
  As a user,
  I want to amend a project,
  so that I can update the details of a project.

  Background:
    Given the REST API todo list Manager is running
    And the following projects exist in the system:
      | project_id | title    | completed | active | description  |
      | 2          | ProjectA | false     | false  | DescriptionA |
      | 3          | ProjectB | false     | true   | DescriptionB |
      | 4          | ProjectC | false     | false  | DescriptionC |

  Scenario Outline: Amend a project with POST (Normal flow)
    When a user sends a POST request with title "<title>" and description "<description>" for an existing project with ID "<project_id>"
    Then the status code 200 will be received
    And the project is updated with title "<title>" and description "<description>", with same completed "<completed>" and active "<active>" fields
    Examples:
      | project_id | title           | description         | completed | active |
      | 2          | Amended Project | Amended description | false     | false  |

  Scenario Outline: Amend a project with PUT (Alternate flow)
    When a user sends a PUT request with title "<title>" and description "<description>" for an existing project with ID "<project_id>"
    Then the status code 200 will be received
    And the project is updated with title "<title>" and description "<description>", with same completed "<completed>" and active "<active>" fields
    Examples:
      | project_id | title           | description         | completed | active |
      | 3          | Amended Project | Amended description | false     | true   |

  Scenario Outline: Amend a project that does not exist (Error flow)
    When a user sends a POST request for an nonexistent project with ID "<project_id>"
    Then the status code 404 will be received
    And the response body should contain the error message "No such project entity instance with GUID or ID <project_id> found"
    Examples:
      | project_id |
      | 100        |
