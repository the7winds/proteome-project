package proteomeProject.alignment.condition;

import proteomeProject.annotation.Annotation;
import proteomeProject.dataEntities.IonType;
import proteomeProject.report.html.HtmlAlignmentReport;
import proteomeProject.report.svg.AnnotationSVG;
import proteomeProject.report.txt.AnnotationPrinter;
import proteomeProject.utils.ProjectPaths;
import proteomeProject.utils.Utils;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static proteomeProject.dataEntities.IonType.Type.B;

/**
 * Created by the7winds on 29.05.16.
 */
public class EqualPeaks {

    private static class EqualsPeaks implements Condition {

        private final String OUTPUT;
        private final PrintStream output;
        private final Set<Annotation> annotations = Collections.synchronizedSet(new HashSet<>());
        private final List<String> svgPaths = Collections.synchronizedList(new LinkedList<>());

        public EqualsPeaks(String OUTPUT) throws FileNotFoundException {
            this.OUTPUT = OUTPUT;
            output = new PrintStream(ProjectPaths.getOutput().resolve(OUTPUT).toFile());
        }

        @Override
        public void addIf(Annotation annotation) {
            if (EqualPeaks.equalsPeaks(annotation)) {
                annotations.add(annotation);
            }
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

    private static class ForJoin implements Condition {

        private final String OUTPUT;
        private final PrintStream output;
        private final Map<Integer, Map<String, Set<Annotation>>> annotations = new ConcurrentHashMap<>();
        private final List<String> svgPaths = Collections.synchronizedList(new LinkedList<>());

        ForJoin(String OUTPUT) throws FileNotFoundException {
            this.OUTPUT = OUTPUT;
            output = new PrintStream(ProjectPaths.getOutput().resolve(OUTPUT).toFile());
        }

        @Override
        public void addIf(Annotation annotation) {
            if (EqualPeaks.equalsPeaks(annotation)) {
                annotations.putIfAbsent(annotation.getSpectrum().getScans(), new ConcurrentHashMap<>());
                annotations.get(annotation.getSpectrum().getScans()).putIfAbsent(annotation.getPeptide().getName(), Collections.synchronizedSet(new HashSet<>()));
                annotations.get(annotation.getSpectrum().getScans())
                        .get(annotation.getPeptide().getName())
                        .add(annotation);
            }
        }

        @Override
        public void print(Annotation annotation) {
            if (annotations.getOrDefault(annotation.getSpectrum().getScans(), Collections.emptyMap())
                    .getOrDefault(annotation.getPeptide().getName(), Collections.emptySet()).size() >= 2) {
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

    public static class VarForJoin implements Condition {

        private final static String OUTPUT = "alignment(join)";
        private final ForJoin forJoin;

        public VarForJoin() throws FileNotFoundException {
            forJoin = new ForJoin(OUTPUT);
        }

        @Override
        public void addIf(Annotation annotation) {
            forJoin.addIf(annotation);
        }

        @Override
        public void print(Annotation annotation) {
            forJoin.print(annotation);
        }

        @Override
        public void printTxt(Annotation annotation) {
            // never calls
        }

        @Override
        public void printSvg(Annotation annotation) {
            // never calls
        }

        @Override
        public void makeReport() throws IOException {
            forJoin.makeReport();
        }
    }

    public static class StdForJoin implements Condition {

        private final static String OUTPUT = "standard(join)";
        private final ForJoin forJoin;
        private final Map<Annotation, Annotation> annotations = new ConcurrentHashMap<>();

        public StdForJoin() throws FileNotFoundException {
            forJoin = new ForJoin(OUTPUT);
        }

        @Override
        public void addIf(Annotation annotation) {
            Annotation stdAnnotation = Utils.getStandardAnnotation(annotation);
            if (stdAnnotation != null) {
                annotations.put(annotation, stdAnnotation);
                forJoin.addIf(stdAnnotation);
            }
        }

        @Override
        public void print(Annotation annotation) {
            if (annotations.containsKey(annotation)) {
                forJoin.print(annotations.get(annotation));
            }
        }

        @Override
        public void printTxt(Annotation annotation) {
            // never calls
        }

        @Override
        public void printSvg(Annotation annotation) {
            // never calls
        }

        @Override
        public void makeReport() throws IOException {
            forJoin.makeReport();
        }
    }

    public static class VarEqualPeaks implements Condition {

        private final static String OUTPUT = "alignment(equal peaks)";
        private EqualsPeaks equalPeaks;

        public VarEqualPeaks() throws FileNotFoundException {
            equalPeaks = new EqualsPeaks(OUTPUT);
        }

        @Override
        public void addIf(Annotation annotation) {
            equalPeaks.addIf(annotation);
        }

        @Override
        public void print(Annotation annotation) {
            equalPeaks.print(annotation);
        }

        @Override
        public void printTxt(Annotation annotation) {
            // never calls
        }

        @Override
        public void printSvg(Annotation annotation) {
            // never calls
        }

        @Override
        public void makeReport() throws IOException {
            equalPeaks.makeReport();
        }
    }

    public static class StdEqualPeaks implements Condition {

        private final static String OUTPUT = "standard(equal peaks)";
        private EqualsPeaks equalPeaks;
        private Map<Annotation, Annotation> annotations = new ConcurrentHashMap<>();

        public StdEqualPeaks() throws FileNotFoundException {
            equalPeaks = new EqualsPeaks(OUTPUT);
        }

        @Override
        public void addIf(Annotation annotation) {
            Annotation stdAnnotation = Utils.getStandardAnnotation(annotation);
            if (stdAnnotation != null) {
                annotations.put(annotation, stdAnnotation);
                equalPeaks.addIf(stdAnnotation);
            }
        }

        @Override
        public void print(Annotation annotation) {
            if (annotations.containsKey(annotation)) {
                equalPeaks.print(annotations.get(annotation));
            }
        }

        @Override
        public void printTxt(Annotation annotation) {
            // never calls
        }

        @Override
        public void printSvg(Annotation annotation) {
            // never calls
        }

        @Override
        public void makeReport() throws IOException {
            equalPeaks.makeReport();
        }
    }

    private static boolean equalsPeaks(Annotation annotation) {
        Map.Entry<Double, List<IonType>> entry = annotation.getAnnotations().entrySet().stream()
                .filter(x -> x.getValue().stream().anyMatch(o -> o.getDefect() == null))
                .findFirst()
                .get();

        double s1 = annotation.getType() == B
                ? annotation.getPeptide().getbSpectrum()[entry.getValue().get(0).getNum()]
                : annotation.getPeptide().getySpectrum()[entry.getValue().get(0).getNum()];
        double s2 = entry.getKey();

        return Math.abs(s1 - s2) < 0.01;
    }
}
