package gherkin;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.cucumber.java.en.Then;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class ExampleStepDefinition {

    private boolean testPassed;

    @Given("this is a test")
    public void this_is_a_test() {
        System.out.println("Setting up the test");
        testPassed = false;  // Initial state
    }

    @When("I run the test")
    public void i_run_the_test() {
        testPassed = true;  // Simulate a passing test
    }

    @Then("it should pass")
    public void it_should_pass() {
        assertTrue(testPassed, "The test should pass");
    }
}
