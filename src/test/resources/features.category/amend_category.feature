Feature: Amend a Category
  As a user,
  I want to amend a category,
  so that I can update the details of a category.

  Background:
    Given the REST API todo list Manager is running
    And the following categories exist in the system:
      | category_id | title     | description  |
      | 3           | CategoryA | DescriptionA |
      | 4           | CategoryB | DescriptionB |
      | 5           | CategoryC | DescriptionC |

  Scenario Outline: Amend a category with POST (Normal flow)
    When a user sends a POST request with category id "<category_id>", title "<title>", and description "<description>"
    Then the status code 200 will be received from categoryAPI
    And the category is updated with title "<title>" and description "<description>"
    Examples:
      | category_id | title             | description          |
      | 3           | Amended CategoryA | Amended DescriptionA |

  Scenario Outline: Amend a category with PUT (Alternate flow)
    When a user sends a PUT request with category id "<category_id>", title "<title>", and description "<description>"
    Then the status code 200 will be received from categoryAPI
    And the category is updated with title "<title>" and description "<description>"
    Examples:
      | category_id | title             | description          |
      | 4           | Amended CategoryB | Amended DescriptionB |

  Scenario Outline: Amend a category with POST and duplicate title (Error flow)
    When a user sends a POST request with category id "<category_id>", title "<title>", and description "<description>"
    Then the status code 400 will be received from categoryAPI
    And the response body should contain the error message "Title <title> already exists for another category with ID <category_id>" from categoryAPI
    Examples:
      | category_id | title     | description          |
      | 3           | CategoryC | Amended DescriptionA |

  Scenario Outline: Amend a category that does not exist (Error flow)
    When a user sends a POST request for a nonexistent category with id "<category_id>"
    Then the status code 404 will be received from categoryAPI
    And the response body should contain the error message "No such category entity instance with GUID or ID <category_id> found" from categoryAPI
    Examples:
      | category_id |
      | 100         |
