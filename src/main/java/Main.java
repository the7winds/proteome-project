import org.apache.batik.transcoder.TranscoderException;
import org.kohsuke.args4j.CmdLineException;
import proteomeProject.alignment.TagAlignment;
import proteomeProject.annotation.SpectrumAnnotation;
import proteomeProject.dataEntities.ContributionWrapper;
import proteomeProject.dataEntities.Variants;
import proteomeProject.searchVariantPeptide.SearchVariantPeptide;
import proteomeProject.searchVariantPeptide.SearchVariantPeptideResults;
import proteomeProject.utils.Options;
import proteomeProject.utils.ProjectPaths;

import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Paths;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

/**
 * Created by the7winds on 05.04.16.
 */
public class Main {

    public static void main(String[] args) throws CmdLineException
            , IOException
            , InterruptedException
            , TranscoderException {

        long start = System.currentTimeMillis();

        Options.parse(args);

        try (PrintStream searchReportPrintStream = new PrintStream(ProjectPaths.Output.getSearchReport().toFile())) {
            ContributionWrapper.init(ProjectPaths.Sources.getContribution());
            Variants.init(ProjectPaths.Sources.getVariants());

            SearchVariantPeptideResults results = SearchVariantPeptide.main(ProjectPaths.Sources.getTsv(), searchReportPrintStream);

            SpectrumAnnotation.main(results, searchReportPrintStream);
        }


        try (PrintStream alignmentReportPrintStream = new PrintStream(ProjectPaths.Output.getAlignmentReport().toFile())) {
            TagAlignment.main(alignmentReportPrintStream);
        }


        Files.copy(Paths.get("src/main/resources/report/summary.html")
                , ProjectPaths.Output.getOutput().resolve("report").resolve("summary.html"), REPLACE_EXISTING);

        System.out.printf("time: %d", System.currentTimeMillis() - start);
    }
}
