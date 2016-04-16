package proteomeProject.searchVariantPeptide;

import com.univocity.parsers.common.record.Record;
import com.univocity.parsers.tsv.TsvParser;
import com.univocity.parsers.tsv.TsvParserSettings;
import org.apache.commons.lang3.StringUtils;
import proteomeProject.dataEntities.ContributionWrapper;
import proteomeProject.dataEntities.IonType;
import proteomeProject.dataEntities.Tag;

import java.io.FileNotFoundException;
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

    private SearchVariantPeptide(Path tsvPath) {
        this.tsvPath = tsvPath;
    }

    public static SearchVariantPeptideResults main(Path tsvPath, PrintStream output) throws FileNotFoundException {
        SearchVariantPeptideResults results = new SearchVariantPeptide(tsvPath).search();
        SearchReportPrinter.print(output, results);
        return results;
    }

    private SearchVariantPeptideResults search() {
        Collection<DBResult> variantPeptidesDBResults = getVariantPeptidesDBResults();

        final Collection<SearchVariantPeptideResult> tagFoundResults = new LinkedList<>();
        final Collection<SearchVariantPeptideResult> tagNotFoundResults = new LinkedList<>();
        final Collection<SearchVariantPeptideResult> tagNotExistsResults = new LinkedList<>();

        for (DBResult dbResult : variantPeptidesDBResults) {
            Tag tag = ContributionWrapper.getInstance().findByFileAndSpec(dbResult);
            if (tag != null) {
                boolean foundResults = false;

                if (dbResult.getPeptide().contains(tag.getTag())) {
                    foundResults = true;
                    tagFoundResults.add(new SearchVariantPeptideResult(dbResult, tag, IonType.Type.B));
                }

                String reverseTag = StringUtils.reverse(tag.getTag());

                if (dbResult.getPeptide().contains(reverseTag)) {
                    foundResults = true;
                    tagFoundResults.add(new SearchVariantPeptideResult(dbResult, tag, IonType.Type.Y));
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
