package proteomeProject.dataEntities;

import com.univocity.parsers.common.record.Record;
import com.univocity.parsers.tsv.TsvParser;
import com.univocity.parsers.tsv.TsvParserSettings;
import proteomeProject.searchVariantPeptide.DBResult;

import java.nio.file.Path;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Contains results of de-novo algorithm to simplify search.
 */

public class ContributionWrapper {

    private List<Tag> allTags = new LinkedList<>();
    private Map<String, Map<Integer, Tag>> container = new Hashtable<>();
    private static ContributionWrapper INSTANCE;


    public static void init(Path contributionPath) {
        if (INSTANCE == null) {
            INSTANCE = new ContributionWrapper(contributionPath);
        } else {
            throw new UnsupportedOperationException();
        }
    }

    public static ContributionWrapper getInstance() {
        return INSTANCE;
    }

    private ContributionWrapper(Path contributionPath) {
        TsvParserSettings tsvParserSettings = new TsvParserSettings();
        tsvParserSettings.setHeaderExtractionEnabled(true);
        TsvParser tsvParser = new TsvParser(tsvParserSettings);

        tsvParser.beginParsing(contributionPath.toFile());

        for(Record record = tsvParser.parseNextRecord();
            record != null;
            record = tsvParser.parseNextRecord()) {
            Tag tag = new Tag(record);
            allTags.add(tag);
            if (!container.containsKey(tag.getSpecFile())) {
                container.put(tag.getSpecFile(), new Hashtable<>());
            }
            container.get(tag.getSpecFile()).put(tag.getScanId(), tag);
        }
    }

    public Tag findByFileAndSpec(DBResult dbResult) {
        Map<Integer, Tag> specs = container.get(dbResult.getSpecFile());
        if (specs != null) {
            return specs.get(dbResult.getScanNum());
        }
        return null;
    }

    public List<Tag> getAllTags() {
        return allTags;
    }

    enum ContributionColumns {
        SPEC_FILE,
        SCAN_ID,
        DE_NOVO_STRING,
        OFFSET,
        TAG,
        PEAK
    }
}
