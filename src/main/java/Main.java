import org.apache.batik.transcoder.TranscoderException;
import org.kohsuke.args4j.CmdLineException;
import proteomeProject.alignment.TagAlignment;
import proteomeProject.annotation.SpectrumAnnotation;
import proteomeProject.dataEntities.ContributionWrapper;
import proteomeProject.dataEntities.SpectrumWrapper;
import proteomeProject.dataEntities.VariantsStandards;
import proteomeProject.searchVariantPeptide.SearchVariantPeptide;
import proteomeProject.searchVariantPeptide.SearchVariantPeptideResults;
import proteomeProject.utils.Options;
import proteomeProject.utils.ProjectPaths;

import java.io.IOException;
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

        // parse options
        Options.parse(args);

        // initialize data containers;
        ContributionWrapper.init(ProjectPaths.Sources.getContribution());
        VariantsStandards.init(ProjectPaths.Sources.getVariantStandard());
        SpectrumWrapper.init(ProjectPaths.Sources.getSpectrums());

        SearchVariantPeptideResults results = SearchVariantPeptide.main(ProjectPaths.Sources.getTsv());

        SpectrumAnnotation.main(results);

        TagAlignment.main();


        Files.copy(Paths.get("src/main/resources/report/summary.html")
                , ProjectPaths.getReport().resolve("summary.html"), REPLACE_EXISTING);

        System.out.printf("time: %d", System.currentTimeMillis() - start);
    }
}
