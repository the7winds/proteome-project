package proteomeProject.searchVariantPeptide;

import java.io.PrintStream;

/**
 * Created by the7winds on 03.04.16.
 */
final class SearchReportPrinter {

    private static final String TAG_FOUND = "tag found results:";
    private static final String COLUMNS1 = "filename\tscan\tpeptide\ttag\treverse\toffset\tprotein\tdelta(theor_offset-offset)\te-value";

    private static final String TAG_NOT_FOUND = "tag not found results:";
    private static final String TAG_NOT_EXISTS = "tag not exists:";
    private static final String COLUMNS2 = "filename\tscan\tpeptide\tprotein\te-value";

    static void print(PrintStream printStream, SearchVariantPeptideResults results) {
        printStream.println(TAG_FOUND);
        printStream.println(COLUMNS1);
        results.getTagFoundResults().forEach(result -> {
            printStream.println(result.getFilename()
                    + '\t' + result.getScanNum()
                    + '\t' + result.getPeptide()
                    + '\t' + result.getTag()
                    + '\t' + result.getType().name()
                    + '\t' + result.getOffset()
                    + '\t' + result.getProtein()
                    + '\t' + result.getDelta()
                    + '\t' + result.getEValue());
            printStream.flush();
        });

        printStream.println(TAG_NOT_FOUND);
        printStream.println(COLUMNS2);
        results.getTagNotFoundResults().forEach(result ->
                printStream.println(result.getFilename()
                        + '\t' + result.getScanNum()
                        + '\t' + result.getPeptide()
                        + '\t' + result.getProtein()
                        + '\t' + result.getEValue()));

        printStream.println(TAG_NOT_EXISTS);
        printStream.println(COLUMNS2);
        results.getTagNotExistsResults().forEach(result ->
                printStream.println(result.getFilename()
                        + '\t' + result.getScanNum()
                        + '\t' + result.getPeptide()
                        + '\t' + result.getProtein()
                        + '\t' + result.getEValue()));
    }
}