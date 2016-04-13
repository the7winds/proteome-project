package proteomeProject.spectrumAnnotation;

import proteomeProject.utils.Utils;

import java.io.PrintStream;
import java.util.List;
import java.util.Map;

/**
 * Created by the7winds on 03.04.16.
 */
public class AnnotationPrinter {

    private static final String COLUMNS = "PEAK\tION TYPE";

    private PrintStream printStream = System.out;

    public AnnotationPrinter(PrintStream printStream) {
        this.printStream = printStream;
    }

    public void print(Spectrum spectrum, String peptide) {
        printStream.printf("SCANS=%d\n", spectrum.getScans());
        printStream.printf("PEPTIDE=%s\n", peptide);
        printStream.printf("PRECURSOR MASS=%f\n", spectrum.getPrecursorMass());
        double theoreticalMass = Utils.evalTotalMass(peptide);
        printStream.printf("THEORETICAL MASS=%f\n", theoreticalMass);
        printStream.printf("PRECURSOR MASS-THEORETICAL MASS=%f\n", spectrum.getPrecursorMass() - theoreticalMass);
        printStream.println(COLUMNS);

        for (Map.Entry<Double, List<IonType>> entry : spectrum.getFullInfoPeaks().entrySet()) {
            String out = entry.getKey().toString();
            if (entry.getValue() != null) {
                for (IonType ionType : entry.getValue()) {
                    out += "\t" + ionType.toString();
                }
            }
            printStream.println(out);
        }
        printStream.println();
    }
}
