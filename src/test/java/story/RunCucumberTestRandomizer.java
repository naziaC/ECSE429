package story;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;

// Run FeatureRunner to run the tests in a random order
@RunWith(Cucumber.class)
@CucumberOptions(plugin = {"pretty"}, glue = {"story"})
public class RunCucumberTestRandomizer {

    @BeforeClass
    public static void validateFeaturePath() {
        String featurePath = System.getProperty("cucumber.features");
        if (featurePath == null || featurePath.isEmpty()) throw new IllegalArgumentException("System property 'cucumber.features' needs to be specified.");
    }
}
