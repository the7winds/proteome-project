package proteomeProject.annotation;

import proteomeProject.dataEntities.IonType;

import java.io.PrintStream;
import java.util.List;
import java.util.Map;

/**
 * Created by the7winds on 03.04.16.
 */
public final class AnnotationPrinter {

    private static final String COLUMNS = "PEAK\tION TYPE";

    public static void print(PrintStream printStream, Annotation annotation) {
        printStream.printf("SCANS=%d\n", annotation.getSpectrum().getScans());
        printStream.printf("PEPTIDE=%s\n", annotation.getPeptide().getPeptide());
        printStream.printf("PRECURSOR MASS=%f\n", annotation.getSpectrum().getPrecursorMass());
        printStream.printf("THEORETICAL MASS=%f\n", annotation.getPeptide().getTheoreticMass());
        printStream.printf("PRECURSOR MASS-THEORETICAL MASS=%f\n", annotation.getSpectrum().getPrecursorMass() - annotation.getPeptide().getTheoreticMass());
        if (annotation.getTag() != null) {
            if (annotation.getType() != null) {
                printStream.printf("TAG=%s FIRST=%s%d LAST=%s%d\n"
                        , annotation.getTag().getTag()
                        , annotation.getType().name(), annotation.getFirst()
                        , annotation.getType().name(), annotation.getLast());
            } else {
                printStream.printf("TAG=%s\n", annotation.getTag().getTag());
            }
        }
        printStream.println(COLUMNS);

        for (Map.Entry<Double, List<IonType>> entry : annotation.getAnnotations().entrySet()) {
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
