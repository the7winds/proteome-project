package proteomeProject.dataEntities;

import java.io.File;
import java.util.SortedSet;

/**
 * Created by the7winds on 30.03.16.
 */
public class Spectrum {

    private File specFile;
    private final int scans;
    private final int precursorCharge;
    private final double precursorMass;
    private final SortedSet<Double> peaks;


    public Spectrum(int scans, int precursorCharge, double precursorMass, SortedSet<Double> peaks) {
        this.scans = scans;
        this.precursorCharge = precursorCharge;
        this.precursorMass = precursorMass;
        this.peaks = peaks;
    }

    public void setSpecFile(File specFile) {
        this.specFile = specFile;
    }

    public File getSpecFile() {
        return specFile;
    }

    public int getScans() {
        return scans;
    }

    public int getPrecursorCharge() {
        return precursorCharge;
    }

    public double getPrecursorMass() {
        return precursorMass;
    }

    public SortedSet<Double> getPeaks() {
        return peaks;
    }
}
