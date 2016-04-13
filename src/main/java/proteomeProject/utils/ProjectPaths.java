package proteomeProject.utils;

import java.io.IOException;
import java.nio.file.Path;

import static proteomeProject.utils.ParsedArgs.*;

/**
 * Created by the7winds on 28.03.16.
 */

/**
 * contains arguments
 */

public class ProjectPaths {

    static void resolveProjectPaths() throws IOException {
        Sources.resolveSourcesPaths();
        Output.resolveOutputPaths();
    }

    /**
     * contains paths to source files
     */

    public static final class Sources {

        private static Path tsv;
        private static Path contribution;
        private static Path variants;

        private Sources() {
        }

        private static void resolveSourcesPaths() {
            tsv = sources.resolve(ParsedArgs.tsv);
            contribution = sources.resolve(ParsedArgs.contribution);
            variants = sources.resolve(ParsedArgs.variants);
        }

        public static Path getSources() {
            return sources;
        }

        public static Path getTsv() {
            return tsv;
        }

        public static Path getContribution() {
            return contribution;
        }

        public static Path getVariants() {
            return variants;
        }
    }

    /**
     * contains paths to the program's output
     */

    public static final class Output {

        private static Path searchReport;
        private static Path alignmentReport;

        private Output() {
        }

        private static void resolveOutputPaths() throws IOException {
            searchReport = output.resolve(SEARCH_REPORT);
            alignmentReport = output.resolve(ALIGNMENT_REPORT);

            output.toFile().mkdir();
            searchReport.toFile().createNewFile();
            alignmentReport.toFile().createNewFile();
        }

        public static Path getOutput() {
            return output;
        }

        public static Path getSearchReport() {
            return searchReport;
        }

        public static Path getAlignmentReport() {
            return alignmentReport;
        }
    }
}
