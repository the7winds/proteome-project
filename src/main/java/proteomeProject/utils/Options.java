package proteomeProject.utils;

import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Created by the7winds on 13.04.16.
 */
public final class Options {

    private Options() {

    }

    public static void parse(String[] args) throws CmdLineException, IOException {
        CmdLineParser parser = new CmdLineParser(new Options());
        parser.parseArgument(args);
        ProjectPaths.resolveProjectPaths();
    }

    /**
     * path to sources directory
     */
    @Option(name = "--sources")
    static Path sources = Paths.get("/");

    /**
     * path to tsv file
     */
    @Option(name = "--tsv")
    static Path tsv;

    /**
     * path to contribution file
     */
    @Option(name = "--ctb")
    static Path contribution;

    /**
     * variants peptide file
     */
    @Option(name = "--var")
    static Path variants;

    /**
     * output directory
     */
    @Option(name = "--output")
    static Path output;

    @Option(name = "--outputSearchReport")
    static String searchReport = "searchReport";

    @Option(name = "--alignmentReport")
    static String alignmentReport = "alignmentReport";

    @Option(name = "--threads-num")
    static int threadsNum;

    public static int getThreadsNum() {
        return threadsNum;
    }
}
