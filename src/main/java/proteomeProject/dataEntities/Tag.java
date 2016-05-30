package proteomeProject.dataEntities;

import com.univocity.parsers.common.record.Record;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by the7winds on 14.04.16.
 */
public class Tag {

    private final String specFile;
    private final int scanId;
    private final String deNovoString;
    private final double offset;
    private final String tag;
    private final List<Double> peaks;

    public Tag(Record record) {
        specFile = getRealSpecFilename(record.getString(ContributionWrapper.ContributionColumns.SPEC_FILE.ordinal()));
        scanId = record.getInt(ContributionWrapper.ContributionColumns.SCAN_ID.ordinal());
        deNovoString = record.getString(ContributionWrapper.ContributionColumns.DE_NOVO_STRING.ordinal());
        offset = record.getDouble(ContributionWrapper.ContributionColumns.OFFSET.ordinal());
        tag = record.getString(ContributionWrapper.ContributionColumns.TAG.ordinal());
        peaks = Arrays.stream(record.getString(ContributionWrapper.ContributionColumns.PEAK.ordinal()).split(","))
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
    
    public String getSuffixedSpecFile() {
        return specFile + TYPE;
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
