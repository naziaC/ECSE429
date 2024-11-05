package story;

import org.junit.Test;
import org.junit.runner.JUnitCore;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

// This class is used to shuffle the feature files in a random order and uses the RunCucumberTestRandomizer class to run the tests
// Run FeatureRunner to run the tests in a random order
@RunWith(Parameterized.class)
public class FeatureRunner {

    private final String featurePath;
    private final String featureName; // Used to display the feature name in the test results

    static String[] featuresDirectories = {
            "src/test/resources/features/todos",
            "src/test/resources/features/project",
            "src/test/resources/features/category"
    };

    public FeatureRunner(String featurePath, String featureName) {
        this.featurePath = featurePath;
        this.featureName = featureName;
    }

    @Parameters(name = "{index}: Feature({1})")
    public static Iterable<Object[]> featureFiles() {
        List<Object[]> features = new ArrayList<>();
        for (String dir : featuresDirectories) {
            File baseDir = new File(dir);
            if (baseDir.exists() && baseDir.isDirectory()) {
                collectFeatureFiles(baseDir, features);
            }
        }
        Collections.shuffle(features);
        return features;
    }


    private static void collectFeatureFiles(File directory, List<Object[]> features) {
        for (File file : Objects.requireNonNull(directory.listFiles())) {
            if (file.isDirectory()) {
                collectFeatureFiles(file, features); // Recursively search for feature files since we have it separated by directories
            } else if (file.isFile() && file.getName().endsWith(".feature")) {
                features.add(new Object[]{file.getAbsolutePath().replace("\\", "/"), file.getName()});
            }
        }
    }

    @Test
    public void runFeature() {
        System.setProperty("cucumber.features", featurePath);
        JUnitCore.runClasses(RunCucumberTestRandomizer.class);
    }
}
