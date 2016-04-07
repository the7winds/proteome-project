package proteomeProject;

import com.univocity.parsers.common.record.Record;
import com.univocity.parsers.tsv.TsvParser;
import com.univocity.parsers.tsv.TsvParserSettings;
import proteomeProject.searchVariantPeptide.DBResult;

import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

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

    public static class Tag {

        private final String specFile;
        private final int scanId;
        private final String deNovoString;
        private final double offset;
        private final String tag;
        private final List<Double> peaks;

        public Tag(Record record) {
            specFile = getRealSpecFilename(record.getString(ContributionColumns.SPEC_FILE.ordinal()));
            scanId = record.getInt(ContributionColumns.SCAN_ID.ordinal());
            deNovoString = record.getString(ContributionColumns.DE_NOVO_STRING.ordinal());
            offset = record.getDouble(ContributionColumns.OFFSET.ordinal());
            tag = record.getString(ContributionColumns.TAG.ordinal());
            peaks = Arrays.stream(record.getString(ContributionColumns.PEAK.ordinal()).split(","))
                    .mapToDouble(Double::valueOf)
                    .boxed()
                    .collect(Collectors.toList());
        }

        public Tag(String specFile,
                   int scanId,
                   String deNovoString,
                   double offset,
                   String tag,
                   List<Double> peaks) {
            this.specFile = specFile;
            this.scanId = scanId;
            this.deNovoString = deNovoString;
            this.offset = offset;
            this.tag = tag;
            this.peaks = peaks;
        }

        public String getSpecFile() {
            return specFile;
        }

        // BAD STYLE !
        public String getSuffixedSpecFile() {
            return specFile + "_msdeconv.msalign";
        }

        public int getScanId() {
            return scanId;
        }

        public String getDeNovoString() {
            return deNovoString;
        }

        public double getOffset() {
            return offset;
        }

        public String getTag() {
            return tag;
        }

        public Double[] getPeaks() {
            return peaks.toArray(new Double[peaks.size()]);
        }

        private final static String TYPE = "_msdeconv.msalign";

        private String getRealSpecFilename(String filename) {
            int end = filename.lastIndexOf(TYPE);
            return filename.substring(0, end);
        }
    }

    private enum ContributionColumns {
        SPEC_FILE,
        SCAN_ID,
        DE_NOVO_STRING,
        OFFSET,
        TAG,
        PEAK
    }
}
