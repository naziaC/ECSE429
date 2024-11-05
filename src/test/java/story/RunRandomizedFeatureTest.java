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

// This class is used to shuffle the feature files in a random order and uses the RunRandomizedCucumberTest class to run the tests
// Run RunRandomizedFeatureTest to run the tests in a random order
@RunWith(Parameterized.class)
public record RunRandomizedFeatureTest(String featurePath, String featureName) {

    static String[] featuresDirectories = {
            "src/test/resources/features/todos",
            "src/test/resources/features/project",
            "src/test/resources/features/category"
    };

    @Parameters(name = "{index}: {1}")
    public static Iterable<Object[]> getFeatureFiles() {
        List<Object[]> features = new ArrayList<>();
        for (String dir : featuresDirectories) {
            File baseDir = new File(dir);
            if (baseDir.exists() && baseDir.isDirectory()) {
                getFeatureFilesFromDirectory(baseDir, features);
            }
        }
        Collections.shuffle(features);
        return features;
    }

    private static void getFeatureFilesFromDirectory(File directory, List<Object[]> features) {
        for (File file : Objects.requireNonNull(directory.listFiles())) {
            if (file.isDirectory()) {
                getFeatureFilesFromDirectory(file, features);
            } else if (file.isFile() && file.getName().endsWith(".feature")) {
                features.add(new Object[]{file.getAbsolutePath().replace("\\", "/"), file.getName()});
            }
        }
    }

    @Test
    public void runRandomizedFeatureTest() {
        System.setProperty("cucumber.features", featurePath);
        JUnitCore.runClasses(RunRandomizedCucumberTest.class);
    }
}
