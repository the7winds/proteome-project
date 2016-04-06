import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import proteomeProject.searchVariantPeptide.SearchVariantPeptide;
import proteomeProject.searchVariantPeptide.SearchVariantPeptideResults;
import proteomeProject.spectrumAnnotation.SpectrumAnnotation;
import proteomeProject.utils.ProjectPaths;

import java.io.IOException;

/**
 * Created by the7winds on 05.04.16.
 */
public class Main {

    public static void main(String[] args) throws CmdLineException, IOException {
        CmdLineParser parser = new CmdLineParser(new ProjectPaths());
        parser.parseArgument(args);

        SearchVariantPeptideResults results = SearchVariantPeptide.main(ProjectPaths.getTsv(),
                ProjectPaths.getContribution(),
                ProjectPaths.getOutput());
    }
}
