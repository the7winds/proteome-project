package proteomeProject.searchVariantPeptide;

import com.univocity.parsers.common.record.Record;
import com.univocity.parsers.tsv.TsvParser;
import com.univocity.parsers.tsv.TsvParserSettings;
import org.apache.commons.lang3.StringUtils;

import java.io.PrintStream;
import java.nio.file.Path;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;

/**
 * Created by the7winds on 23.03.16.
 */

/**
 *  Looks up for each variant peptide a de-novo tag (if it exists).
 *  The tag can be reversed.
 */

public class SearchVariantPeptide {

    private final static String VARIANT_TAG = "variant";

    private final Path tsvPath;
    private final Path contributionPath;

    private SearchVariantPeptide(Path tsvPath, Path contributionPath) {
        this.tsvPath = tsvPath;
        this.contributionPath = contributionPath;
    }

    public static SearchVariantPeptideResults main(Path tsvPath, Path contributionPath, PrintStream output) {
        SearchVariantPeptideResults results = new SearchVariantPeptide(tsvPath, contributionPath).search();
        new SearchReportPrinter(output).print(results);
        return results;
    }

    private SearchVariantPeptideResults search() {
        Collection<DBResult> variantPeptidesDBResults = getVariantPeptidesDBResults();
        ContributionContainer contributionContainer =
                new ContributionContainer(contributionPath);

        final Collection<SearchVariantPeptideResult> tagFoundResults = new LinkedList<>();
        final Collection<SearchVariantPeptideResult> tagNotFoundResults = new LinkedList<>();
        final Collection<SearchVariantPeptideResult> tagNotExistsResults = new LinkedList<>();

        for (DBResult dbResult : variantPeptidesDBResults) {
            ContributionContainer.SpecResult specResult = contributionContainer.findByFileAndSpectrum(dbResult);
            if (specResult != null) {
                boolean foundResults = false;

                if (dbResult.getPeptide().contains(specResult.getTag())) {
                    foundResults = true;
                    tagFoundResults.add(new SearchVariantPeptideResult(dbResult, specResult, false));
                }

                String reverseTag = StringUtils.reverse(specResult.getTag());

                if (dbResult.getPeptide().contains(reverseTag)) {
                    foundResults = true;
                    tagFoundResults.add(new SearchVariantPeptideResult(dbResult, specResult, true));
                }

                if (!foundResults) {
                    tagNotFoundResults.add(new SearchVariantPeptideResult(dbResult));
                }
            } else {
                tagNotExistsResults.add(new SearchVariantPeptideResult(dbResult));
            }
        }

        return new SearchVariantPeptideResults(tagFoundResults, tagNotFoundResults, tagNotExistsResults);
    }

    private Collection<DBResult> getVariantPeptidesDBResults() {
        TsvParserSettings tsvParserSettings = new TsvParserSettings();

        // just to ignore first string which starts with '#'
        String[] headers = new String[DBResult.DBResultsColumns.values().length];
        tsvParserSettings.setHeaders(headers);

        TsvParser tsvParser = new TsvParser(tsvParserSettings);

        tsvParser.beginParsing(tsvPath.toFile());

        Collection<DBResult> variantResults = new HashSet<>();

        for(Record record = tsvParser.parseNextRecord();
            record != null;
            record = tsvParser.parseNextRecord()) {
            if (record.getString(DBResult.DBResultsColumns.PROTEIN.ordinal()).contains(VARIANT_TAG)) {
                variantResults.add(new DBResult(record));
            }
        }

        return variantResults;
    }
}
