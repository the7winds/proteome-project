package proteomeProject.spectrumAnnotation;

import proteomeProject.utils.Printer;

import java.io.PrintStream;
import java.util.List;
import java.util.Map;

/**
 * Created by the7winds on 03.04.16.
 */
public class AnnotationPrinter implements Printer {

    private static final String COLUMNS = "peak\tion type";

    private PrintStream printStream = System.out;

    public AnnotationPrinter() {
    }

    public AnnotationPrinter(PrintStream printStream) {
        this.printStream = printStream;
    }

    @Override
    public void setUpOutput(PrintStream printStream) {
        printStream = printStream;
    }

    @Override
    public void print(Object object) {
        Spectrum spectrum = (Spectrum) object;

        String title = "scan = " + spectrum.getScans();
        printStream.println(title);
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
    }
}
