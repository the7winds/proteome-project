package proteomeProject.utils;

import org.kohsuke.args4j.Option;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Created by the7winds on 28.03.16.
 */

/**
 * Singleton class contains arguments
 */

public class ProjectPaths {

    /**
     * path to data directory
     */
    @Option(name = "--data")
    private static Path data;

    /**
     * path to tsv file
     */
    @Option(name = "--tsv")
    private static Path tsv;

    /**
     * path to contribution file
     */
    @Option(name = "--ctb")
    private static Path contribution;

    /**
     * output output
     */
    private static PrintStream output = System.out;

    @Option(name = "-f")
    private static void setOutput(String path) throws IOException {
        File file = new File(path);
        file.createNewFile();
        output = new PrintStream(file);
    }

    public static Path getData() {
        return data;
    }

    public static Path getTsv() {
        return data.resolve(tsv);
    }

    public static Path getContribution() {
        return data.resolve(contribution);
    }

    public static PrintStream getOutput() {
        return output;
    }
}
