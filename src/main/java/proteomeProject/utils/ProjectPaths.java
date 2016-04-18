package proteomeProject.utils;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Collection;
import java.util.LinkedList;

import static proteomeProject.utils.Options.*;

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
        private static Path variantStandard;
        private static Collection<Path> spectrums = new LinkedList<>();

        private Sources() {
        }

        private static void resolveSourcesPaths() {
            tsv = sources.resolve(Options.tsv);
            contribution = sources.resolve(Options.contribution);
            variantStandard = sources.resolve(Options.variantStandard);
            for (String path : Options.specs) {
                spectrums.add(sources.resolve(path));
            }
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

        public static Path getVariantStandard() {
            return variantStandard;
        }

        public static Collection<Path> getSpectrums() {
            return spectrums;
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
            searchReport = output.resolve(Options.searchReport);
            alignmentReport = output.resolve(Options.alignmentReport);

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
