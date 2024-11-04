Feature: Get a Specific Category
  As a user,
  I want to get a specific category,
  so that I can view its details.

  Background:
    Given the REST API todo list Manager is running
    And the following categories exist in the system:
      | category_id | title     | description  |
      | 3           | CategoryA | DescriptionA |
      | 4           | CategoryB | DescriptionB |
      | 5           | CategoryC | DescriptionC |

  Scenario Outline: Get category by ID (Normal flow)
    When a user sends a GET request for an existing category with id "<category_id>"
    Then the status code 200 will be received from categoryAPI
    And the response body should contain category details with id "<category_id>", title "<title>", and description "<description>"
    Examples:
      | category_id | title     | description  |
      | 3           | CategoryA | DescriptionA |

  Scenario Outline: Get category by title (Alternate flow)
    When a user sends a GET request for an existing category with title "<title>"
    Then the status code 200 will be received from categoryAPI
    And the response body should contain category details with id "<category_id>", title "<title>", and description "<description>"
    Examples:
      | category_id | title     | description  |
      | 3           | CategoryA | DescriptionA |

  Scenario Outline: Get a category with invalid ID (Error flow)
    When a user sends a GET request for a nonexistent category with id "<category_id>"
    Then the status code 404 will be received from categoryAPI
    And the response body should contain the error message "Could not find an instance with categories/<category_id>" from categoryAPI
    Examples:
      | category_id |
      | 100         |
