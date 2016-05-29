package proteomeProject.alignment.condition;

import org.apache.commons.lang3.StringUtils;
import proteomeProject.annotation.Annotation;
import proteomeProject.dataEntities.IonType;
import proteomeProject.report.html.HtmlAlignmentReport;
import proteomeProject.report.svg.BoundsAlignedSVG;
import proteomeProject.report.txt.AnnotationPrinter;
import proteomeProject.utils.ProjectPaths;
import proteomeProject.utils.Utils;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiConsumer;
import java.util.function.Function;

import static proteomeProject.dataEntities.IonType.Type.B;
import static proteomeProject.utils.Chemicals.H2O;

/**
 * Created by the7winds on 28.05.16.
 */
public class Bounds {

    private static class BoundsAligned implements Condition {

        private final String OUTPUT;
        private final PrintStream output;
        private final Map<Annotation, BoundsAlignedContainer> boundsAligned = new ConcurrentHashMap<>();
        private final Map<Annotation, BoundsAlignedContainer> zeroAligned = new ConcurrentHashMap<>();
        private final Map<Annotation, BoundsAlignedContainer> precursorAligned = new ConcurrentHashMap<>();
        private final List<String> svgPaths = Collections.synchronizedList(new LinkedList<>());

        BoundsAligned(String output) throws FileNotFoundException {
            OUTPUT = output;
            this.output = new PrintStream(ProjectPaths.getOutput().resolve(OUTPUT).toFile());
        }

        @Override
        public void addIf(Annotation annotation) {
            double[] spec = annotation.getType() == B
                    ? annotation.getPeptide().getShiftedBSpectrum()
                    : annotation.getPeptide().getShiftedYSpectrum();

            if ((!annotation.getAnnotations().get(0d).isEmpty()
                    && !annotation.getAnnotations().get(annotation.getSpectrum().getPrecursorMass()).isEmpty())) {
                boundsAligned.put(annotation, BoundsAlignedContainer.getBoundsAligned(annotation));
            } else if (!annotation.getAnnotations().get(0d).isEmpty()) {
                int last = annotation.getPeptide().getPeptide().length() - 1;
                double precursorDiff = annotation.getSpectrum().getPrecursorMass() -
                        (annotation.getType() == B
                                ? spec[last]
                                : (spec[last] - H2O.getMass()));

                if (precursorDiff < 0) {
                    int idx;
                    for (idx = spec.length - 1; idx >= 0 && spec[idx] > annotation.getSpectrum().getPrecursorMass(); --idx)
                        ;
                    double l = Math.abs(spec[idx] - annotation.getSpectrum().getPrecursorMass());
                    double r = Math.abs(spec[idx + 1] - annotation.getSpectrum().getPrecursorMass());
                    zeroAligned.put(annotation, BoundsAlignedContainer.getZeroAligned(annotation, precursorDiff, idx + 1, l, r));
                } else {
                    zeroAligned.put(annotation, BoundsAlignedContainer.getZeroAligned(annotation, precursorDiff));
                }
            } else if (!annotation.getAnnotations().get(annotation.getSpectrum().getPrecursorMass()).isEmpty()) {
                double zeroDiff = spec[0];

                if (zeroDiff < 0) {
                    int idx;
                    for (idx = 0; idx < spec.length && spec[idx] < 0; ++idx) ;
                    precursorAligned.put(annotation, BoundsAlignedContainer.getPrecursorAligned(annotation, zeroDiff, idx, spec[idx - 1], spec[idx]));
                } else {
                    precursorAligned.put(annotation, BoundsAlignedContainer.getPrecursorAligned(annotation, zeroDiff));
                }
            }
        }

        @Override
        public void print(Annotation annotation) {
            if (boundsAligned.containsKey(annotation)
                    || zeroAligned.containsKey(annotation)
                    || precursorAligned.containsKey(annotation)) {
                printTxt(annotation);
                printSvg(annotation);
            }
        }

        @Override
        public synchronized void printTxt(Annotation annotation) {
            BoundsAlignedContainer container;
            container = boundsAligned.get(annotation);
            if (container == null) {
                container = zeroAligned.get(annotation);
                if (container == null) {
                    container = precursorAligned.get(annotation);
                }
            }
            container.type.printTxt(container, output);
        }

        @Override
        public void printSvg(Annotation annotation) {
            BoundsAlignedContainer container;
            container = boundsAligned.get(annotation);
            if (container == null) {
                container = zeroAligned.get(annotation);
                if (container == null) {
                    container = precursorAligned.get(annotation);
                }
            }
            svgPaths.add(container.type.printSvg(container));
        }

        @Override
        public void makeReport() throws IOException {
            HtmlAlignmentReport.makeHtmlReport(OUTPUT, svgPaths);
        }
    }

    public static class VarBoundsAligned implements Condition {

        static final String OUTPUT = "alignment(bounds aligned)";
        final BoundsAligned boundsAligned;

        public VarBoundsAligned() throws FileNotFoundException {
            boundsAligned = new BoundsAligned(OUTPUT);
        }

        @Override
        public void addIf(Annotation annotation) {
            boundsAligned.addIf(annotation);
        }

        @Override
        public void print(Annotation annotation) {
            boundsAligned.print(annotation);
        }

        @Override
        public synchronized void printTxt(Annotation annotation) {
            // never used
            boundsAligned.printTxt(annotation);
        }

        @Override
        public void printSvg(Annotation annotation) {
            // never used
            boundsAligned.printTxt(annotation);
        }

        @Override
        public void makeReport() throws IOException {
            boundsAligned.makeReport();
        }
    }

    public static class StdBoundsAligned implements Condition {

        final static String OUTPUT = "standard(bounds aligned)";
        final BoundsAligned boundsAligned;
        final Map<Annotation, Annotation> annotations = new ConcurrentHashMap<>();

        public StdBoundsAligned() throws FileNotFoundException {
            boundsAligned = new BoundsAligned(OUTPUT);
        }

        @Override
        public void addIf(Annotation annotation) {
            Annotation stdAnnotation = Utils.getStandardAnnotation(annotation);
            if (stdAnnotation != null) {
                annotations.put(annotation, stdAnnotation);
                boundsAligned.addIf(stdAnnotation);
            }
        }

        @Override
        public void print(Annotation annotation) {
            if (annotations.containsKey(annotation)) {
                boundsAligned.print(annotations.get(annotation));
            }
        }

        @Override
        public synchronized void printTxt(Annotation annotation) {
            // never used
            boundsAligned.printTxt(annotations.get(annotation));
        }

        @Override
        public void printSvg(Annotation annotation) {
            // never used
            boundsAligned.printSvg(annotation);
        }

        @Override
        public void makeReport() throws IOException {
            boundsAligned.makeReport();
        }
    }

    private static BoundsAlignedContainer checkBounds(Annotation annotation) {
        double[] spec = annotation.getType() == B
                ? annotation.getPeptide().getShiftedBSpectrum()
                : annotation.getPeptide().getShiftedYSpectrum();

        if ((!annotation.getAnnotations().get(0d).isEmpty()
                && !annotation.getAnnotations().get(annotation.getSpectrum().getPrecursorMass()).isEmpty())) {
            return BoundsAlignedContainer.getBoundsAligned(annotation);
        } else if (!annotation.getAnnotations().get(0d).isEmpty()) {
            int last = annotation.getPeptide().getPeptide().length() - 1;
            double precursorDiff = annotation.getSpectrum().getPrecursorMass() -
                    (annotation.getType() == B
                            ? spec[last]
                            : (spec[last] - H2O.getMass()));

            if (precursorDiff < 0) {
                int idx;
                for (idx = spec.length - 1; idx >= 0 && spec[idx] > annotation.getSpectrum().getPrecursorMass(); --idx)
                    ;
                double l = Math.abs(spec[idx] - annotation.getSpectrum().getPrecursorMass());
                double r = Math.abs(spec[idx + 1] - annotation.getSpectrum().getPrecursorMass());
                return BoundsAlignedContainer.getZeroAligned(annotation, precursorDiff, idx + 1, l, r);
            } else {
                return BoundsAlignedContainer.getZeroAligned(annotation, precursorDiff);
            }
        } else if (!annotation.getAnnotations().get(annotation.getSpectrum().getPrecursorMass()).isEmpty()) {
            double zeroDiff = spec[0];

            if (zeroDiff < 0) {
                int idx;
                for (idx = 0; idx < spec.length && spec[idx] < 0; ++idx) ;
                return BoundsAlignedContainer.getPrecursorAligned(annotation, zeroDiff, idx, spec[idx - 1], spec[idx]);
            } else {
                return BoundsAlignedContainer.getPrecursorAligned(annotation, zeroDiff);
            }
        }
        return null;
    }

    private static class BoundsAlignedContainer {

        private enum Type {
            BOTH((boundsAlignedContainer, printStream) -> {
                Annotation annotation = boundsAlignedContainer.annotation;

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
            }, boundsAlignedContainer -> BoundsAlignedSVG.buildBoundsAligned(boundsAlignedContainer.annotation)),

            ZERO((boundsAlignedContainer, printStream) -> {
                Annotation annotation = boundsAlignedContainer.annotation;

                AnnotationPrinter.print(printStream, annotation);
                printStream.println("BOUNDS ALIGNED=ZERO");
                printStream.printf("PRECURSOR DIFF=%f\n", boundsAlignedContainer.diff);
                String cut = annotation.getType() == B
                        ? annotation.getPeptide().getPeptide()
                        : StringUtils.reverse(annotation.getPeptide().getPeptide());
                cut = cut.substring(annotation.getAnnotations().get(0d).stream()
                        .min(Comparator.comparingInt(IonType::getNum)).get().getNum());
                cut = annotation.getType() == B
                        ? cut
                        : StringUtils.reverse(cut);
                printStream.printf("CUT=%s\n", cut);
                printStream.println();
            }, boundsAlignedContainer -> BoundsAlignedSVG.buildZeroAligned(boundsAlignedContainer.annotation, boundsAlignedContainer.diff)),

            ZERO_SPLITED((boundsAlignedContainer, printStream) -> {
                Annotation annotation = boundsAlignedContainer.annotation;

                AnnotationPrinter.print(printStream, annotation);
                printStream.println("BOUNDS ALIGNED=ZERO");
                printStream.printf("PRECURSOR DIFF=%f\n", boundsAlignedContainer.diff);
                printStream.printf("SPLITTED=%s%d\t|---%f---%f---%f---|\n"
                        , annotation.getType().name()
                        , boundsAlignedContainer.idx
                        , boundsAlignedContainer.l
                        , annotation.getSpectrum().getPrecursorMass()
                        , boundsAlignedContainer.r);
                String cut = annotation.getType() == B
                        ? annotation.getPeptide().getPeptide()
                        : StringUtils.reverse(annotation.getPeptide().getPeptide());
                cut = cut.substring(annotation.getAnnotations().get(0d).stream()
                        .min(Comparator.comparingInt(IonType::getNum)).get().getNum(), boundsAlignedContainer.idx - 1);
                cut = annotation.getType() == B
                        ? cut
                        : StringUtils.reverse(cut);
                printStream.printf("CUT=%s\n", cut);
                printStream.println();
            }, boundsAlignedContainer -> BoundsAlignedSVG.buildZeroAligned(boundsAlignedContainer.annotation
                    , boundsAlignedContainer.diff
                    , boundsAlignedContainer.idx
                    , boundsAlignedContainer.l
                    , boundsAlignedContainer.r)),

            PRECURSOR((boundsAlignedContainer, printStream) -> {
                Annotation annotation = boundsAlignedContainer.annotation;

                AnnotationPrinter.print(printStream, annotation);
                printStream.println("BOUNDS ALIGNED=PRECURSOR");
                printStream.printf("ZERO DIFF=%f\n", boundsAlignedContainer.diff);
                String cut = annotation.getType() == B
                        ? annotation.getPeptide()
                        .getPeptide()
                        : StringUtils.reverse(annotation.getPeptide()
                        .getPeptide());
                cut = cut.substring(0, annotation.getAnnotations().get(annotation.getSpectrum().getPrecursorMass()).stream()
                        .max(Comparator.comparingInt(IonType::getNum)).get().getNum());
                cut = annotation.getType() == B
                        ? cut
                        : StringUtils.reverse(cut);
                printStream.printf("CUT=%s\n", cut);
                printStream.println();
            }, boundsAlignedContainer -> BoundsAlignedSVG.buildPrecursorAligned(boundsAlignedContainer.annotation, boundsAlignedContainer.diff)),

            PRECURSOR_SPLITTED((boundsAlignedContainer, printStream) -> {
                Annotation annotation = boundsAlignedContainer.annotation;

                AnnotationPrinter.print(printStream, annotation);
                printStream.println("BOUNDS ALIGNED=PRECURSOR");
                printStream.printf("ZERO DIFF=%f\n", boundsAlignedContainer.diff);
                printStream.printf("SPLITTED=%s%d\t|---%f---0---%f---|\n"
                        , annotation.getType().name()
                        , boundsAlignedContainer.idx
                        , boundsAlignedContainer.l
                        , boundsAlignedContainer.r);
                String cut = annotation.getType() == B
                        ? annotation.getPeptide().getPeptide()
                        : StringUtils.reverse(annotation.getPeptide().getPeptide());
                cut = cut.substring(boundsAlignedContainer.idx, annotation.getAnnotations().get(annotation.getSpectrum().getPrecursorMass()).stream()
                        .max(Comparator.comparingInt(IonType::getNum)).get().getNum());
                cut = annotation.getType() == B
                        ? cut
                        : StringUtils.reverse(cut);
                printStream.printf("CUT=%s\n", cut);
                printStream.println();
            }, boundsAlignedContainer -> BoundsAlignedSVG.buildPrecursorAligned(boundsAlignedContainer.annotation
                    , boundsAlignedContainer.diff
                    , boundsAlignedContainer.idx
                    , boundsAlignedContainer.l
                    , boundsAlignedContainer.r));

            private final BiConsumer<BoundsAlignedContainer, PrintStream> txtPrinter;
            private final Function<BoundsAlignedContainer, String> svgPrinter;

            Type(BiConsumer<BoundsAlignedContainer, PrintStream> txtPrinter, Function<BoundsAlignedContainer, String> svgPrinter) {
                this.txtPrinter = txtPrinter;
                this.svgPrinter = svgPrinter;
            }

            public void printTxt(BoundsAlignedContainer boundsAlignedContainer, PrintStream printStream) {
                txtPrinter.accept(boundsAlignedContainer, printStream);
            }

            public String printSvg(BoundsAlignedContainer boundsAlignedContainer) {
               return svgPrinter.apply(boundsAlignedContainer);
            }
        }

        Type type;
        Annotation annotation;
        int idx;
        double l;
        double r;
        double diff;

        private BoundsAlignedContainer() {}

        static BoundsAlignedContainer getBoundsAligned(Annotation annotation) {
            BoundsAlignedContainer boundsAlignedContainer = new BoundsAlignedContainer();
            boundsAlignedContainer.type = Type.BOTH;
            boundsAlignedContainer.annotation = annotation;
            return boundsAlignedContainer;
        }

        static BoundsAlignedContainer getZeroAligned(Annotation annotation, double precursorDiff, int idx, double l, double r) {
            BoundsAlignedContainer boundsAlignedContainer = new BoundsAlignedContainer();
            boundsAlignedContainer.annotation = annotation;
            boundsAlignedContainer.type = Type.ZERO_SPLITED;
            boundsAlignedContainer.diff = precursorDiff;
            boundsAlignedContainer.idx = idx;
            boundsAlignedContainer.l = l;
            boundsAlignedContainer.r = r;
            return boundsAlignedContainer;
        }

        static BoundsAlignedContainer getZeroAligned(Annotation annotation, double precursorDiff) {
            BoundsAlignedContainer boundsAlignedContainer = new BoundsAlignedContainer();
            boundsAlignedContainer.annotation = annotation;
            boundsAlignedContainer.type = Type.ZERO;
            boundsAlignedContainer.diff = precursorDiff;
            return boundsAlignedContainer;
        }

        static BoundsAlignedContainer getPrecursorAligned(Annotation annotation, double zeroDiff, int idx, double l, double r) {
            BoundsAlignedContainer boundsAlignedContainer = new BoundsAlignedContainer();
            boundsAlignedContainer.annotation = annotation;
            boundsAlignedContainer.type = Type.PRECURSOR_SPLITTED;
            boundsAlignedContainer.diff = zeroDiff;
            boundsAlignedContainer.idx = idx;
            boundsAlignedContainer.l = l;
            boundsAlignedContainer.r = r;
            return boundsAlignedContainer;
        }

        static BoundsAlignedContainer getPrecursorAligned(Annotation annotation, double zeroDiff) {
            BoundsAlignedContainer boundsAlignedContainer = new BoundsAlignedContainer();
            boundsAlignedContainer.annotation = annotation;
            boundsAlignedContainer.type = Type.PRECURSOR;
            boundsAlignedContainer.diff = zeroDiff;
            return boundsAlignedContainer;
        }
    }
}
