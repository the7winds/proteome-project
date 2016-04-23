package proteomeProject.report.txt;

import proteomeProject.annotation.Annotation;
import proteomeProject.searchVariantPeptide.SearchVariantPeptideResults;
import proteomeProject.utils.ProjectPaths;

import java.io.FileNotFoundException;
import java.io.PrintStream;

import static proteomeProject.report.txt.SearchPrinter.Type.*;

/**
 * Created by the7winds on 20.04.16.
 */
public class SearchPrinter {

    private static final String SEARCH = "search";

    public enum Type {
        TAG_FOUND,
        TAG_NOT_FOUND,
        TAG_NOT_EXISTS;

        boolean printed = false;
    }

    private PrintStream output;

    private static final SearchPrinter INSTANCE = new SearchPrinter();

    private SearchPrinter() {
        try {
            output = new PrintStream(ProjectPaths.getOutput().resolve(SEARCH).toFile());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static SearchPrinter getInstance() {
        return INSTANCE;
    }

    public void print(Type type, Annotation annotation) {
        if (!type.printed) {
            output.println();
            output.println(type.name());
            type.printed = true;
        }

        AnnotationPrinter.print(output, annotation);
    }

    private static final String COLUMNS1 = "filename\tscan\tpeptide\ttag\treverse\toffset\tprotein\tdelta(theor_offset-offset)\te-value";
    private static final String COLUMNS2 = "filename\tscan\tpeptide\tprotein\te-value";

    public void print(SearchVariantPeptideResults results) {
        output.println(TAG_FOUND.name());
        output.println(COLUMNS1);
        results.getTagFoundResults().forEach(result -> {
            output.println(result.getFilename()
                    + '\t' + result.getScanNum()
                    + '\t' + result.getPeptide()
                    + '\t' + result.getTag()
                    + '\t' + result.getType().name()
                    + '\t' + result.getOffset()
                    + '\t' + result.getProtein()
                    + '\t' + result.getDelta()
                    + '\t' + result.getEValue());
            output.flush();
        });

        output.println(TAG_NOT_FOUND.name());
        output.println(COLUMNS2);
        results.getTagNotFoundResults().forEach(result ->
                output.println(result.getFilename()
                        + '\t' + result.getScanNum()
                        + '\t' + result.getPeptide()
                        + '\t' + result.getProtein()
                        + '\t' + result.getEValue()));

        output.println(TAG_NOT_EXISTS.name());
        output.println(COLUMNS2);
        results.getTagNotExistsResults().forEach(result ->
                output.println(result.getFilename()
                        + '\t' + result.getScanNum()
                        + '\t' + result.getPeptide()
                        + '\t' + result.getProtein()
                        + '\t' + result.getEValue()));
    }
}
