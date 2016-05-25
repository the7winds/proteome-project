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
    private static final String COMPARE_STD = "compare(std better than var)";
    private static final String COMPARE_VAR = "compare(var better than std)";
    private static final String STD_BOUNDS_ALIGNED = "standard(bounds aligned)";
    private static final String VAR_BOUNDS_ALIGNED = "alignment(bounds aligned)";
    private static final String MODIFICATION_IN_TAG = "modification in tag";
    private static final String NOT_ONLY_TAG = "not only tag";
    private static final String ROUNDED_BY_ANNOTATIONS = "rounded by annotations";

    private PrintStream alignment;
    private PrintStream standard;
    private PrintStream compareStd;
    private PrintStream compareVar;
    private PrintStream stdBoundsAligned;
    private PrintStream varBoundsAligned;
    private PrintStream modificationInTag;
    private PrintStream notOnlyTag;
    private PrintStream roundedByAnnotations;

    private AlignmentPrinter() {
        try {
            alignment = new PrintStream(ProjectPaths.getOutput().resolve(ALIGNMENT).toFile());
            standard = new PrintStream(ProjectPaths.getOutput().resolve(STANDARD).toFile());
            compareStd = new PrintStream(ProjectPaths.getOutput().resolve(COMPARE_STD).toFile());
            stdBoundsAligned = new PrintStream(ProjectPaths.getOutput().resolve(STD_BOUNDS_ALIGNED).toFile());
            varBoundsAligned = new PrintStream(ProjectPaths.getOutput().resolve(VAR_BOUNDS_ALIGNED).toFile());
            compareVar = new PrintStream(ProjectPaths.getOutput().resolve(COMPARE_VAR).toFile());
            modificationInTag = new PrintStream(ProjectPaths.getOutput().resolve(MODIFICATION_IN_TAG).toFile());
            notOnlyTag = new PrintStream(ProjectPaths.getOutput().resolve(NOT_ONLY_TAG).toFile());
            roundedByAnnotations = new PrintStream(ProjectPaths.getOutput().resolve(ROUNDED_BY_ANNOTATIONS).toFile());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static AlignmentPrinter getInstance() {
        return INSTANCE;
    }

    public synchronized void printAlignment(Annotation annotation) {
        AnnotationPrinter.print(alignment, annotation);
        alignment.println();
    }

    public synchronized void printStandard(Annotation annotation) {
        AnnotationPrinter.print(standard, annotation);
        standard.println();
    }

    public synchronized void printCompareStd(Annotation var, Annotation std) {
        AnnotationPrinter.print(compareStd, var);
        compareStd.println();
        AnnotationPrinter.print(compareStd, std);
        compareStd.printf("\n\n\n");
    }

    public synchronized void printBoundsAligned(Annotation annotation, boolean std) {
        PrintStream printStream = std ? stdBoundsAligned : varBoundsAligned;
        
        AnnotationPrinter.print(printStream, annotation);
        int begin = annotation.getAnnotations().get(0d).stream()
                .min(Comparator.comparingDouble(IonType::getNum))
                .get()
                .getNum();
        int end = annotation.getAnnotations().get(annotation.getSpectrum().getPrecursorMass()).stream()
                .max(Comparator.comparingDouble(IonType::getNum))
                .get()
                .getNum();
        String p = annotation.getPeptide().getPeptide();
        printStream.printf("BOTH=%s\n", annotation.getType() == B
                ? p.substring(begin, end)
                : p.substring(end, begin));
        printStream.println();
    }

    public synchronized void printPrecursorAligned(Annotation stdAnnotation, double zeroDiff, boolean std) {
        PrintStream printStream = std ? stdBoundsAligned : varBoundsAligned;
        
        AnnotationPrinter.print(printStream, stdAnnotation);
        printStream.println("BOUNDS ALIGNED=PRECURSOR");
        printStream.printf("ZERO DIFF=%f\n", zeroDiff);
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
        printStream.printf("CUT=%s\n", cut);
        printStream.println();
    }

    public synchronized void printPrecursorAligned(Annotation stdAnnotation, double zeroDiff, int splittedIdx, double l, double r, boolean std) {
        PrintStream printStream = std ? stdBoundsAligned : varBoundsAligned;
        
        AnnotationPrinter.print(printStream, stdAnnotation);
        printStream.println("BOUNDS ALIGNED=PRECURSOR");
        printStream.printf("ZERO DIFF=%f\n", zeroDiff);
        printStream.printf("SPLITTED=%s%d\t|---%f---0---%f---|\n"
                , stdAnnotation.getType().name()
                , splittedIdx
                , l
                , r);
        String cut = stdAnnotation.getType() == B
                ? stdAnnotation.getPeptide().getPeptide()
                : StringUtils.reverse(stdAnnotation.getPeptide().getPeptide());
        cut = cut.substring(splittedIdx, stdAnnotation.getAnnotations().get(stdAnnotation.getSpectrum().getPrecursorMass()).stream()
                .max(Comparator.comparingInt(IonType::getNum)).get().getNum());
        cut = stdAnnotation.getType() == B
                ? cut
                : StringUtils.reverse(cut);
        printStream.printf("CUT=%s\n", cut);
        printStream.println();
    }

    public synchronized void printZeroAligned(Annotation stdAnnotation, double precursorDiff, boolean std) {
        PrintStream printStream = std ? stdBoundsAligned : varBoundsAligned;
        
        AnnotationPrinter.print(printStream, stdAnnotation);
        printStream.println("BOUNDS ALIGNED=ZERO");
        printStream.printf("PRECURSOR DIFF=%f\n", precursorDiff);
        String cut = stdAnnotation.getType() == B
                ? stdAnnotation.getPeptide().getPeptide()
                : StringUtils.reverse(stdAnnotation.getPeptide().getPeptide());
        cut = cut.substring(stdAnnotation.getAnnotations().get(0d).stream()
                .min(Comparator.comparingInt(IonType::getNum)).get().getNum());
        cut = stdAnnotation.getType() == B
                ? cut
                : StringUtils.reverse(cut);
        printStream.printf("CUT=%s\n", cut);
        printStream.println();
    }

    public synchronized void printZeroAligned(Annotation stdAnnotation, double precursorDiff, int splittedIdx, double l, double r, boolean std) {
        PrintStream printStream = std ? stdBoundsAligned : varBoundsAligned;

        AnnotationPrinter.print(printStream, stdAnnotation);
        printStream.println("BOUNDS ALIGNED=ZERO");
        printStream.printf("PRECURSOR DIFF=%f\n", precursorDiff);
        printStream.printf("SPLITTED=%s%d\t|---%f---%f---%f---|\n"
                , stdAnnotation.getType().name()
                , splittedIdx
                , l
                , stdAnnotation.getSpectrum().getPrecursorMass()
                , r);
        String cut = stdAnnotation.getType() == B
                ? stdAnnotation.getPeptide().getPeptide()
                : StringUtils.reverse(stdAnnotation.getPeptide().getPeptide());
        cut = cut.substring(stdAnnotation.getAnnotations().get(0d).stream()
                .min(Comparator.comparingInt(IonType::getNum)).get().getNum(), splittedIdx - 1);
        cut = stdAnnotation.getType() == B
                ? cut
                : StringUtils.reverse(cut);
        printStream.printf("CUT=%s\n", cut);
        printStream.println();
    }

    public synchronized void printCompareVar(Annotation varAnnotation, Annotation stdAnnotation) {
        AnnotationPrinter.print(compareVar, varAnnotation);
        compareVar.println();
        AnnotationPrinter.print(compareVar, stdAnnotation);
        compareVar.printf("\n\n\n");
    }

    public void printModifications(Annotation varAnnotation) {
        AnnotationPrinter.print(modificationInTag, varAnnotation);
        modificationInTag.println();
    }

    public void printNotOnlyTag(Annotation annotation) {
        AnnotationPrinter.print(notOnlyTag, annotation);
        notOnlyTag.println();
    }

    public void printRoundedByAnnotations(Annotation annotation) {
        AnnotationPrinter.print(roundedByAnnotations, annotation);
        roundedByAnnotations.println();
    }
}
