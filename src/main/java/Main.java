import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import proteomeProject.ContributionWrapper;
import proteomeProject.Variants;
import proteomeProject.searchVariantPeptide.SearchVariantPeptide;
import proteomeProject.searchVariantPeptide.SearchVariantPeptideResults;
import proteomeProject.spectrumAnnotation.SpectrumAnnotation;
import proteomeProject.tagAllignment.TagAlignment;
import proteomeProject.utils.ProjectPaths;

import java.io.IOException;

/**
 * Created by the7winds on 05.04.16.
 */
public class Main {

    public static void main(String[] args) throws CmdLineException, IOException {
        CmdLineParser parser = new CmdLineParser(new ProjectPaths());
        parser.parseArgument(args);

        ContributionWrapper.init(ProjectPaths.getContribution());
        Variants.init(ProjectPaths.getVariants());

        SearchVariantPeptideResults results = SearchVariantPeptide.main(ProjectPaths.getTsv(),
                ProjectPaths.getOutput());

        SpectrumAnnotation.main(results, ProjectPaths.getOutput());

        ProjectPaths.getOutput().println("ALIGNMENT");
        TagAlignment.main(ProjectPaths.getOutput());
    }
}
