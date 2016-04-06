package proteomeProject.searchVariantPeptide;

import com.univocity.parsers.common.record.Record;
import com.univocity.parsers.tsv.TsvParser;
import com.univocity.parsers.tsv.TsvParserSettings;

import java.nio.file.Path;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Contains results of de-novo algorithm to simplify search.
 */

class ContributionContainer {

    private Map<String, Map<Integer, SpecResult>> container = new Hashtable<>();

    public ContributionContainer(Path contributionPath) {
        TsvParserSettings tsvParserSettings = new TsvParserSettings();
        tsvParserSettings.setHeaderExtractionEnabled(true);
        TsvParser tsvParser = new TsvParser(tsvParserSettings);

        tsvParser.beginParsing(contributionPath.toFile());

        for(Record record = tsvParser.parseNextRecord();
            record != null;
            record = tsvParser.parseNextRecord()) {
            SpecResult specResult = new SpecResult(record);
            if (!container.containsKey(specResult.getSpecFile())) {
                container.put(specResult.getSpecFile(), new Hashtable<>());
            }
            container.get(specResult.getSpecFile()).put(specResult.getScanId(), specResult);
        }
    }

    public SpecResult findByFileAndSpectrum(DBResult dbResult) {
        Map<Integer, SpecResult> spectrums = container.get(dbResult.getSpecFile());
        if (spectrums != null) {
            return spectrums.get(dbResult.getScanNum());
        }
        return null;
    }

    public static class SpecResult {

        private final String specFile;
        private final int scanId;
        private final String deNovoString;
        private final double offset;
        private final String tag;
        private final List<Double> peaks;

        public SpecResult(Record record) {
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

        public SpecResult(String specFile,
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

        public List<Double> getPeaks() {
            return peaks;
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
