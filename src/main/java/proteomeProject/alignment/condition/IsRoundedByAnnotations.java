package proteomeProject.alignment.condition;

import proteomeProject.annotation.Annotation;
import proteomeProject.dataEntities.VariantsStandards;
import proteomeProject.report.html.HtmlAlignmentReport;
import proteomeProject.report.svg.AnnotationSVG;
import proteomeProject.report.txt.AnnotationPrinter;
import proteomeProject.utils.ProjectPaths;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.util.*;

import static proteomeProject.dataEntities.IonType.Type.B;

/**
 * Created by the7winds on 28.05.16.
 */
public class IsRoundedByAnnotations implements Condition {

    private final static String OUTPUT = "alignment(rounded by annotations)";
    private final PrintStream output;
    private final Set<Annotation> annotations = Collections.synchronizedSet(new HashSet<>());
    private final List<String> svgPaths = Collections.synchronizedList(new LinkedList<>());

    public IsRoundedByAnnotations() throws FileNotFoundException {
        output = new PrintStream(ProjectPaths.getOutput().resolve(OUTPUT).toFile());
    }

    @Override
    public void addIf(Annotation annotation) {
        if (check(annotation)) {
            annotations.add(annotation);
        }
    }

    private static boolean check(Annotation annotation) {
        Map<Integer, VariantsStandards.MapAmino> modifications =
                VariantsStandards.getInstance().getModifications(annotation.getPeptide());
        if (modifications != null) {
            return modifications.entrySet().stream()
                    .anyMatch(e -> {
                        if (e.getValue().isModification()) {
                            return annotation.getAnnotations().values().stream()
                                    .flatMap(Collection::stream)
                                    .anyMatch(x -> x.getType() == B
                                            ? x.getNum() < e.getKey()
                                            : x.getNum() > annotation.getPeptide().getPeptide().length() - e.getKey() + 1) &&
                                    annotation.getAnnotations().values().stream()
                                            .flatMap(Collection::stream)
                                            .anyMatch(x -> x.getType() == B
                                                    ? x.getNum() > e.getKey() + 1
                                                    : x.getNum() < annotation.getPeptide().getPeptide().length() - e.getKey());
                        }
                        return false;
                    });
        }

        return false;
    }

    @Override
    public void print(Annotation annotation) {
        if (annotations.contains(annotation)) {
            printTxt(annotation);
            printSvg(annotation);
        }
    }

    @Override
    public synchronized void printTxt(Annotation annotation) {
        AnnotationPrinter.print(output, annotation);
        output.println();
    }

    @Override
    public void printSvg(Annotation annotation) {
        svgPaths.add(AnnotationSVG.buildAnnotationSVG(annotation));
    }

    @Override
    public void makeReport() throws IOException {
        HtmlAlignmentReport.makeHtmlReport(OUTPUT, svgPaths);
    }
}
