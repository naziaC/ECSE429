Feature: Associate Project Tasks
  As a user,
  I want to link a project to a todo item,
  so that I can keep track of specific tasks associated with each project.

  Background:
    Given the REST API todo list Manager is running
    And the following projects exist in the system:
      | project_id | title    | completed | active | description  |
      | 2          | ProjectA | false     | false  | DescriptionA |
      | 3          | ProjectB | false     | true   | DescriptionB |
      | 4          | ProjectC | false     | false  | DescriptionC |
    And the following tasks exist for each project:
      | project_id | todo_id |
      | 3          | 1       |
      | 3          | 2       |

  Scenario Outline: Create a task relationship between an existing project and an existing todo item (Normal flow)
    When a user sends a POST request with a body containing todo ID "<todo_id>" to associate with a project with ID "<project_id>"
    Then the status code 201 will be received
    And a new task relationship is created between project with ID "<project_id>" and the todo item with ID "<todo_id>"
    Examples:
      | project_id | todo_id |
      | 2          | 1       |

  Scenario Outline: Create a task relationship between an existing project and a nonexistent todo item (Alternate flow)
    When a user sends a POST request with a body containing nonexistent title "<todo_title>" to associate with a project with ID "<project_id>"
    Then the status code 201 will be received
    And a new task relationship is created between project with ID "<project_id>" and the todo item with title "<todo_title>"
    Examples:
      | project_id | todo_title |
      | 2          | Todo Item  |

  Scenario Outline: Create a task relationship between an nonexistent project and a existent todo item (Error flow)
    When a user sends a POST request with a body containing todo ID "<todo_id>" to associate with a project with ID "<project_id>"
    Then the status code 404 will be received
    And the response body should contain the error message "Could not find parent thing for relationship projects/100/tasks"
    Examples:
      | project_id | todo_id |
      | 100        | 2       |
