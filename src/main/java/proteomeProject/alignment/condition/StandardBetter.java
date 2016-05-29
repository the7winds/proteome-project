package proteomeProject.alignment.condition;

import proteomeProject.annotation.Annotation;
import proteomeProject.report.html.HtmlAlignmentReport;
import proteomeProject.report.svg.CompareSVG;
import proteomeProject.report.txt.AnnotationPrinter;
import proteomeProject.utils.ProjectPaths;
import proteomeProject.utils.Utils;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Created by the7winds on 28.05.16.
 */
public class StandardBetter implements Condition {

    private final static String OUTPUT = "compare(std better than var)";
    private final PrintStream output;
    private final ConcurrentMap<Annotation, Annotation> annotations = new ConcurrentHashMap<>();
    private final List<String> svgPaths = Collections.synchronizedList(new LinkedList<>());

    public StandardBetter() throws FileNotFoundException {
        output = new PrintStream(ProjectPaths.getOutput().resolve(OUTPUT).toFile());
    }

    @Override
    public void addIf(Annotation annotation) {
        Annotation stdAnnotation = Utils.getStandardAnnotation(annotation);
        if (stdAnnotation != null) {
            if (Utils.greater(stdAnnotation, annotation, 1)) {
                annotations.put(annotation, stdAnnotation);
            }
        }
    }

    @Override
    public void print(Annotation annotation) {
        if (annotations.containsKey(annotation)) {
            printTxt(annotation);
            printSvg(annotation);
        }
    }

    @Override
    public synchronized void printTxt(Annotation annotation) {
        AnnotationPrinter.print(output, annotation);
        output.println();
        AnnotationPrinter.print(output, annotations.get(annotation));
        output.printf("\n\n\n");
    }

    @Override
    public void printSvg(Annotation annotation) {
        svgPaths.add(CompareSVG.build(annotations.get(annotation), annotation));
    }

    @Override
    public void makeReport() throws IOException {
        HtmlAlignmentReport.makeHtmlReport(OUTPUT, svgPaths);
    }
}
