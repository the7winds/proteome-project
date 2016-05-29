package proteomeProject.alignment.condition;

import proteomeProject.annotation.Annotation;
import proteomeProject.report.html.HtmlAlignmentReport;
import proteomeProject.report.svg.AnnotationSVG;
import proteomeProject.report.txt.AnnotationPrinter;
import proteomeProject.utils.ProjectPaths;
import proteomeProject.utils.Utils;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by the7winds on 29.05.16.
 */
public class Standard implements Condition {

    private static final String OUTPUT = "standards";
    private final PrintStream output;
    private final Map<Annotation, Annotation> annotations = new ConcurrentHashMap<>();
    private final List<String> svgPaths = Collections.synchronizedList(new LinkedList<>());

    public Standard() throws FileNotFoundException {
        output = new PrintStream(ProjectPaths.getOutput().resolve(OUTPUT).toFile());
    }

    @Override
    public void addIf(Annotation annotation) {
        Annotation stdAnnotation = Utils.getStandardAnnotation(annotation);
        if (stdAnnotation != null) {
            annotations.put(annotation, stdAnnotation);
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
        AnnotationPrinter.print(output, annotations.get(annotation));
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
