package proteomeProject.report.txt;

import org.apache.commons.lang3.StringUtils;
import proteomeProject.annotation.Annotation;
import proteomeProject.dataEntities.IonType;
import proteomeProject.utils.ProjectPaths;

import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.Comparator;

import static proteomeProject.dataEntities.IonType.Type.B;

/**
 * Created by the7winds on 20.04.16.
 */
public class AlignmentPrinter {

    private static final AlignmentPrinter INSTANCE = new AlignmentPrinter();

    private static final String ALIGNMENT = "alignment";
    private static final String STANDARD = "standard";
    private static final String COMPARE = "compare(std better var)";
    private static final String BOUNDS_ALIGNED = "bounds aligned";

    private PrintStream alignment;
    private PrintStream standard;
    private PrintStream compare;
    private PrintStream boundsAligned;

    private AlignmentPrinter() {
        try {
            alignment = new PrintStream(ProjectPaths.getOutput().resolve(ALIGNMENT).toFile());
            standard = new PrintStream(ProjectPaths.getOutput().resolve(STANDARD).toFile());
            compare = new PrintStream(ProjectPaths.getOutput().resolve(COMPARE).toFile());
            boundsAligned = new PrintStream(ProjectPaths.getOutput().resolve(BOUNDS_ALIGNED).toFile());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static AlignmentPrinter getInstance() {
        return INSTANCE;
    }

    public void printAlignment(Annotation annotation) {
        AnnotationPrinter.print(alignment, annotation);
        alignment.println();
    }

    public void printStandard(Annotation annotation) {
        AnnotationPrinter.print(standard, annotation);
        standard.println();
    }

    public synchronized void printCompare(Annotation var, Annotation std) {
        AnnotationPrinter.print(compare, var);
        AnnotationPrinter.print(compare, std);
        compare.println("------------");
        compare.println();
    }

    public synchronized void printBoundsAligned(Annotation annotation) {
        AnnotationPrinter.print(boundsAligned, annotation);
        int begin = annotation.getAnnotations().get(0d).stream()
                .min(Comparator.comparingDouble(IonType::getNum))
                .get()
                .getNum();
        int end = annotation.getAnnotations().get(annotation.getSpectrum().getPrecursorMass()).stream()
                .max(Comparator.comparingDouble(IonType::getNum))
                .get()
                .getNum();
        String p = annotation.getPeptide().getPeptide();
        boundsAligned.printf("BOTH=%s\n", annotation.getType() == B
                ? p.substring(begin, end)
                : p.substring(end, begin));
        boundsAligned.println();
    }

    public synchronized void printPrecursorAligned(Annotation stdAnnotation, double precursorDiff) {
        AnnotationPrinter.print(boundsAligned, stdAnnotation);
        boundsAligned.printf("ZERO DIFF=%f\n", precursorDiff);
        String cut = stdAnnotation.getType() == B
                ? stdAnnotation.getPeptide()
                .getPeptide()
                : StringUtils.reverse(stdAnnotation.getPeptide()
                .getPeptide());
        cut = cut.substring(0, stdAnnotation.getAnnotations().get(stdAnnotation.getSpectrum().getPrecursorMass()).stream()
                .max(Comparator.comparingInt(IonType::getNum)).get().getNum());
        cut = stdAnnotation.getType() == B
                ? cut
                : StringUtils.reverse(cut);
        boundsAligned.printf("CUT=%s\n", cut);
        boundsAligned.println();
    }

    public synchronized void printPrecursorAligned(Annotation stdAnnotation, double precursorDiff, int idx, double l, double r) {
        AnnotationPrinter.print(boundsAligned, stdAnnotation);
        boundsAligned.printf("ZERO DIFF=%f\n", precursorDiff);
        boundsAligned.printf("SPLITTED=%s%d\t|---%f---0---%f---|\n"
                , stdAnnotation.getType().name()
                , idx
                , l
                , r);
        String cut = stdAnnotation.getType() == B
                ? stdAnnotation.getPeptide()
                .getPeptide()
                : StringUtils.reverse(stdAnnotation.getPeptide()
                .getPeptide());
        cut = cut.substring(idx, stdAnnotation.getAnnotations().get(stdAnnotation.getSpectrum().getPrecursorMass()).stream()
                .max(Comparator.comparingInt(IonType::getNum)).get().getNum());
        cut = stdAnnotation.getType() == B
                ? cut
                : StringUtils.reverse(cut);
        boundsAligned.printf("CUT=%s\n", cut);
        boundsAligned.println();
    }

    public synchronized void printZeroAligned(Annotation stdAnnotation, double precursorDiff) {
        AnnotationPrinter.print(boundsAligned, stdAnnotation);
        boundsAligned.printf("PRECURSOR DIFF=%f\n", precursorDiff);
        String cut = stdAnnotation.getType() == B
                ? stdAnnotation.getPeptide()
                .getPeptide()
                : StringUtils.reverse(stdAnnotation.getPeptide()
                .getPeptide());
        cut = cut.substring(stdAnnotation.getAnnotations().get(0d).stream()
                .min(Comparator.comparingInt(IonType::getNum)).get().getNum());
        cut = stdAnnotation.getType() == B
                ? cut
                : StringUtils.reverse(cut);
        boundsAligned.printf("CUT=%s\n", cut);
        boundsAligned.println();
    }

    public synchronized void printZeroAligned(Annotation stdAnnotation, double precursorDiff, int idx, double l, double r) {
        AnnotationPrinter.print(boundsAligned, stdAnnotation);
        boundsAligned.printf("PRECURSOR DIFF=%f\n", precursorDiff);
        boundsAligned.printf("SPLITTED=%s%d\t|---%f---%f---%f---|\n"
                , stdAnnotation.getType().name()
                , idx + 1
                , l
                , stdAnnotation.getSpectrum().getPrecursorMass()
                , r);
        String cut = stdAnnotation.getType() == B
                ? stdAnnotation.getPeptide()
                .getPeptide()
                : StringUtils.reverse(stdAnnotation.getPeptide()
                .getPeptide());
        cut = cut.substring(stdAnnotation.getAnnotations().get(0d).stream()
                .min(Comparator.comparingInt(IonType::getNum)).get().getNum(), idx);
        cut = stdAnnotation.getType() == B
                ? cut
                : StringUtils.reverse(cut);
        boundsAligned.printf("CUT=%s\n", cut);
        boundsAligned.println();
    }
}
