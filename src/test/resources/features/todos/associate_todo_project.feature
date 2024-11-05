Feature: Associate Todo to Project
  As a user,
  I want to link a todo to a project,
  so that I can better manage the progression of projects.

  Background:
    Given the REST API todo list Manager is running
    And the following projects exist in the system:
      | project_id | title    | completed | active | description  |
      | 2          | ProjectA | false     | false  | DescriptionA |
      | 3          | ProjectB | false     | true   | DescriptionB |
      | 4          | ProjectC | false     | false  | DescriptionC |
    And the following todos exist in the system:
      | id | title | doneStatus | description  |
      | 3  | todoA | false      | descriptionA |
      | 4  | todoB | false      | descriptionB |
    And the following tasks exist for each project:
      | project_id | todo_id |
      | 3          | 1       |
      | 3          | 2       | 

  Scenario Outline: Create a task relationship between an existing todo and an existing project (Normal flow)
    When a user sends a POST request to associate a todo id "<todo_id>" with a project id "<project_id>"
    Then the status code 201 will be received from the todoAPI
    And the todo id "<todo_id>" is associated with project id "<project_id>"
    And the project id "<project_id>" is associated with todo id "<todo_id>"
    Examples:
      | todo_id    | project_id |
      | 3          | 3          |

  Scenario Outline: Create a task relationship between an existing todo and a nonexistent project (Alternate flow)
    When a user sends a POST request to associate a todo id "<todo_id>" with an empty body for the project ID
    Then the status code 201 will be received from the todoAPI
    And the todo id "<todo_id>" is associated with project id "<project_id>"
    And the project id "<project_id>" is associated with todo id "<todo_id>"
    Examples:
      | todo_id    | project_id |
      | 3          | 5          |

  Scenario Outline: Create a task relationship between a nonexisting todo and an existing project (Error flow)
    When a user sends a POST request to associate a todo id "<todo_id>" with a project id "<project_id>"
    Then the status code 404 will be received from the todoAPI
    And the response body from the todoAPI should contain the error message "Could not find parent thing for relationship todos/<todo_id>/tasksof"
    Examples:
      | todo_id | project_id |
      | 13      | 1          |
