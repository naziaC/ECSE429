package story;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.runner.RunWith;

// Run RunCucumberTest to execute the tests
@RunWith(Cucumber.class)
@CucumberOptions(plugin = "pretty", features = "src/test/resources")
public class RunCucumberTest {
}
