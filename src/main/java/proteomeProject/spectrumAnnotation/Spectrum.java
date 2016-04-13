package proteomeProject.spectrumAnnotation;

import java.io.*;
import java.util.*;

/**
 * Created by the7winds on 30.03.16.
 */
public class Spectrum {

    private final int scans;
    private final SortedMap<Double, List<IonType>> peaks = new TreeMap<>();
    private final double precursorMass;

    public Spectrum(File specFile, int scans) throws IOException {
        this.scans = scans;
        Scanner scanner = new Scanner(specFile);
        scanner.useDelimiter("\\r\\n");
        while (true) {
            while (!scanner.next().equals("BEGIN IONS"));
            scanner.next();  // skip id
            if (scanner.next().equals("SCANS=" + Integer.toString(scans))) {
                scanner.next();  // skip activation
                scanner.next();  // skip precursor_mz
                scanner.next();  // skip precursor_charge
                String pm = scanner.next();  // skip precursor_mass
                precursorMass = Double.valueOf(pm.split("=")[1]);

                String input = scanner.next();
                while (!input.equals("END IONS")) {
                    String[] peaksWords = input.split("\\t+");
                    peaks.put(Double.valueOf(peaksWords[0]), null);
                    input = scanner.next();
                }

                break;
            }
        }
    }

    public int getScans() {
        return scans;
    }

    public Double[] getPeaks() {
        return peaks.keySet().toArray(new Double[peaks.size()]);
    }

    public SortedMap<Double, List<IonType>> getFullInfoPeaks() { return peaks; }

    public void annotatePeak(Double peak, IonType ionType) {
        peaks.putIfAbsent(peak, new LinkedList<>());
        peaks.get(peak).add(ionType);
    }

    public double getPrecursorMass() {
        return precursorMass;
    }
}
