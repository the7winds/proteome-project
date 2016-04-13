import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import proteomeProject.ContributionWrapper;
import proteomeProject.Variants;
import proteomeProject.searchVariantPeptide.SearchVariantPeptide;
import proteomeProject.searchVariantPeptide.SearchVariantPeptideResults;
import proteomeProject.spectrumAnnotation.SpectrumAnnotation;
import proteomeProject.tagAllignment.TagAlignment;
import proteomeProject.utils.ParsedArgs;
import proteomeProject.utils.ProjectPaths;

import java.io.IOException;

/**
 * Created by the7winds on 05.04.16.
 */
public class Main {

    public static void main(String[] args) throws CmdLineException, IOException {

        ParsedArgs.parse(args);

        ContributionWrapper.init(ProjectPaths.Sources.getContribution());
        Variants.init(ProjectPaths.Sources.getVariants());

        SearchVariantPeptideResults results = SearchVariantPeptide.main(ProjectPaths.Sources.getTsv(),
                ProjectPaths.Output.getSearchReport());

        SpectrumAnnotation.main(results, ProjectPaths.Output.getSearchReport());

        TagAlignment.main(ProjectPaths.Output.getAlignmentReport());
    }
}
