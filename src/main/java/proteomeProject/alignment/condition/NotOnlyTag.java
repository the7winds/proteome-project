package proteomeProject.alignment.condition;

import proteomeProject.annotation.Annotation;
import proteomeProject.dataEntities.IonType;
import proteomeProject.report.html.HtmlAlignmentReport;
import proteomeProject.report.svg.AnnotationSVG;
import proteomeProject.report.txt.AnnotationPrinter;
import proteomeProject.utils.ProjectPaths;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.util.*;

/**
 * Created by the7winds on 28.05.16.
 */
public class NotOnlyTag implements Condition {

    private final static String OUTPUT = "alignment(not only tag)";
    private final PrintStream output;
    private final Set<Annotation> annotations = Collections.synchronizedSet(new HashSet<>());
    private final List<String> svgPaths = Collections.synchronizedList(new LinkedList<>());

    public NotOnlyTag() throws FileNotFoundException {
        output = new PrintStream(ProjectPaths.getOutput().resolve(OUTPUT).toFile());
    }

    @Override
    public void addIf(Annotation annotation) {
        if (notOnlyTag(annotation)) {
            annotations.add(annotation);
        }
    }

    private static boolean notOnlyTag(Annotation annotation) {
        int first = annotation.getFirst();
        int last = annotation.getLast();
        return annotation.getAnnotations().values().stream()
                .flatMap(Collection::stream)
                .mapToInt(IonType::getNum)
                .anyMatch(n -> n < first || n > last);
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
