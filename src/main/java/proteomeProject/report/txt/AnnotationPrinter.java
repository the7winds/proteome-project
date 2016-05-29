package proteomeProject.report.txt;

import proteomeProject.annotation.Annotation;
import proteomeProject.dataEntities.IonType;
import proteomeProject.dataEntities.Peptide;
import proteomeProject.dataEntities.VariantsStandards;

import java.io.PrintStream;
import java.util.List;
import java.util.Map;

import static proteomeProject.dataEntities.IonType.Type.B;

/**
 * Created by the7winds on 03.04.16.
 */
public final class AnnotationPrinter {

    private static final String COLUMNS = "PEAK\tION TYPE";

    public static void print(PrintStream printStream, Annotation annotation) {
        printStream.printf("SCANS=%d\n", annotation.getSpectrum().getScans());
        printStream.printf("PEPTIDE=%s\n", annotation.getPeptide().getPeptide());
        printStream.printf("NAME=%s\n", annotation.getPeptide().getName());
        printStream.printf("PRECURSOR MASS=%f\n", annotation.getSpectrum().getPrecursorMass());
        printStream.printf("THEORETICAL MASS=%f\n", annotation.getPeptide().getTheoreticMass());
        printStream.printf("PRECURSOR MASS-THEORETICAL MASS=%f\n", annotation.getSpectrum().getPrecursorMass() - annotation.getPeptide().getTheoreticMass());
        printModificationMap(printStream, annotation);
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
                    out += String.format("\t(%s : %f)"
                            , ionType.toString()
                            , ionType.getType() == B
                                    ? annotation.getPeptide().getbSpectrum()[ionType.getNum()]
                                    : annotation.getPeptide().getySpectrum()[ionType.getNum()]);
                }
            }
            printStream.println(out);
        }
    }

    private static void printModificationMap(PrintStream printStream, Annotation annotation) {
        Peptide standard = VariantsStandards.getInstance().getStandard(annotation.getPeptide().getName());
        if (standard != null) {
            printStream.println("MODIFICATIONS MAP");
            printStream.printf("STD: %s\n", standard.getPeptide());
            printStream.printf("VAR: ");
            Map<Integer, VariantsStandards.MapAmino> modificationsMap = VariantsStandards.getInstance().getModifications(annotation.getPeptide());
            for (int j = 0, i = 0; i < annotation.getPeptide().getPeptide().length(); ++i, ++j) {
                if (modificationsMap.containsKey(j)) {
                    int p = modificationsMap.get(i).getIdx();
                    for (; j < p; ++j) {
                        printStream.print(" ");
                    }
                }
                printStream.print(annotation.getPeptide().getPeptide().charAt(i));
            }
            printStream.println();
        }
    }
}
