package proteomeProject.dataEntities;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.SortedSet;
import java.util.TreeSet;

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

    public static Spectrum parse(File specFile, int scans) throws IOException {
        try (final BufferedReader reader = new BufferedReader(new FileReader(specFile))) {
            while (reader.ready()) {
                while (reader.ready() && !reader.readLine().equals("BEGIN IONS")) ;
                reader.readLine();  // skip id
                final int fileScans = Integer.valueOf(reader.readLine().split("=")[1]);   // scans

                if (scans == fileScans) {
                    reader.readLine();  // skip activation
                    reader.readLine();  // skip precursor_mz
                    final int precursorCharge = Integer.valueOf(reader.readLine().split("=")[1]);   // precursor_charge
                    final double precursorMass = Double.valueOf(reader.readLine().split("=")[1]);  // precursor_mass
                    final SortedSet<Double> peaks = new TreeSet<>();

                    // adds min and max spectrum bounds
                    peaks.add(0d);
                    peaks.add(precursorMass);

                    for (String line = reader.readLine(); !line.equals("END IONS"); line = reader.readLine()) {
                        peaks.add(Double.valueOf(line.split("\\s+")[0]));  // peak
                    }

                    Spectrum spectrum = new Spectrum(scans, precursorCharge, precursorMass, peaks);
                    spectrum.setSpecFile(specFile);
                    return spectrum;

                }
            }
        }

        return null;
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
