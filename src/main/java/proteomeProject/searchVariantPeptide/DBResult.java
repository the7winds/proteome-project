package proteomeProject.searchVariantPeptide;

import com.univocity.parsers.common.record.Record;

/**
 *  Contains data base results.
 */

class DBResult {

    private final String specFile;
    private final String specId;
    private final int scanNum;
    private final String protein;
    private final String peptide;
    private final double eValue;

    public DBResult(Record record) {
        specFile = getRealSpecFilename(record.getString(DBResultsColumns.SPEC_FILE.ordinal()));
        specId = record.getString(DBResultsColumns.SPEC_ID.ordinal());
        scanNum = record.getInt(DBResultsColumns.SCAN_NUM.ordinal());
        protein = record.getString(DBResultsColumns.PROTEIN.ordinal());
        peptide = record.getString(DBResultsColumns.PEPTIDE.ordinal());
        eValue = record.getDouble(DBResultsColumns.E_VALUE.ordinal());
    }

    public DBResult(String specFile,
                    String specId,
                    int scanNum,
                    String protein,
                    String peptide,
                    double eValue) {
        this.specFile = specFile;
        this.specId = specId;
        this.scanNum = scanNum;
        this.protein = protein;
        this.peptide = peptide;
        this.eValue = eValue;
    }

    public String getSpecFile() {
        return specFile;
    }

    public String getSpecId() {
        return specId;
    }

    public int getScanNum() {
        return scanNum;
    }

    public String getProtein() {
        return protein;
    }

    public String getPeptide() {
        return peptide;
    }

    public double getEValue() {
        return eValue;
    }

    private String getRealSpecFilename(String specFilename) {
        int endIndex = specFilename.lastIndexOf(".");
        return specFilename.substring(0, endIndex);
    }

    enum DBResultsColumns {
        SPEC_FILE,
        SPEC_ID,
        SCAN_NUM,
        FRAG_METHOD,
        PRECURSOR,
        ISOTOPE_ERROR,
        PRECURSOR_ERROR,
        CHARGE,
        PEPTIDE,
        PROTEIN,
        DE_NOVO_SCORE,
        MSGF_SCORE,
        SPEC_E_VALUE,
        E_VALUE
    }
}
