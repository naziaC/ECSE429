Feature: Create a Category
  As a user,
  I want to create a category,
  so that I can manage my tasks and organize my work.

  Background:
    Given the REST API todo list Manager is running

  Scenario Outline: Create a category with all fields (Normal flow)
    When a user sends a POST request with title "<title>" and description "<description>"
    Then the status code 201 will be received from categoryAPI
    And a new category is created with title "<title>" and description "<description>"
    Examples:
      | title     | description  |
      | CategoryD | DescriptionD |

  Scenario Outline: Create a category with no description (Alternate flow)
    When a user sends a POST request with title "<title>" and description "<description>"
    Then the status code 201 will be received from categoryAPI
    And a new category is created with title "<title>" and description "<description>"
    Examples:
      | title     | description  |
      | CategoryE |              |

  Scenario Outline: Create a category with no title (Error flow)
    When a user sends a POST request with title "<title>" and description "<description>"
    Then the status code 400 will be received from categoryAPI
    And the response body should contain the error message "Failed Validation: title : can not be empty" from categoryAPI
    Examples:
      | title     | description  |
      |           | DescriptionF |

  Scenario Outline: Create a category with invalid field (Error flow)
    When a user sends a POST request with category id "<category_id>"
    Then the status code 400 will be received from categoryAPI
    And the response body should contain the error message "Invalid Creation: Failed Validation: Not allowed to create with id" from categoryAPI
    Examples:
      | category_id |
      | 100         |
