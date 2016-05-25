package proteomeProject.alignment;

import org.apache.batik.transcoder.TranscoderException;
import proteomeProject.annotation.Annotation;
import proteomeProject.dataEntities.ContributionWrapper;
import proteomeProject.dataEntities.Peptide;
import proteomeProject.dataEntities.Tag;
import proteomeProject.dataEntities.VariantsStandards;
import proteomeProject.report.html.HtmlAlignmentReport;
import proteomeProject.report.svg.AnnotationSVG;
import proteomeProject.report.svg.BoundsAlignedSVG;
import proteomeProject.report.svg.CompareSVG;
import proteomeProject.report.txt.AlignmentPrinter;

import java.io.IOException;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Created by the7winds on 06.04.16.
 */

public final class TagAlignment {

    static public void main() throws InterruptedException, IOException, TranscoderException {
        ExecutorService executorService = Executors.newCachedThreadPool();

        AlignmentContainer alignmentContainer = new AlignmentContainer();

        for (Tag tag : ContributionWrapper.getInstance().getAllTags()) {
            for (Peptide variant : VariantsStandards.getInstance().getVariants()) {
                executorService.execute(new AlignmentTask(variant, tag, alignmentContainer));
            }
        }

        executorService.shutdown();
        executorService.awaitTermination(1, TimeUnit.DAYS);

        alignmentContainer.buildReports();
    }

    static class AlignmentContainer {

        private final List<Annotation> variants = Collections.synchronizedList(new LinkedList<>());
        private final List<Annotation> standards = Collections.synchronizedList(new LinkedList<>());
        private final List<Annotation> notOnlyTag = Collections.synchronizedList(new LinkedList<>());
        private final List<Annotation> modificationsInTag = Collections.synchronizedList(new LinkedList<>());
        private final Map<Annotation, Annotation> cmpVarBetter = new ConcurrentHashMap<>();
        private final Map<Annotation, Annotation> cmpStdBetter = new ConcurrentHashMap<>();
        private final List<Annotation> roundedByAnnotations = Collections.synchronizedList(new LinkedList<>());
        private final List<BoundsAlignedContainer> stdBoundsAligned = Collections.synchronizedList(new LinkedList<>());
        private final List<BoundsAlignedContainer> varBoundsAligned = Collections.synchronizedList(new LinkedList<>());

        void addVariant(Annotation annotation) {
            variants.add(annotation);
        }

        void addStandard(Annotation standard) {
            standards.add(standard);
        }

        void addNotOnlyTag(Annotation annotation) {
            notOnlyTag.add(annotation);
        }

        void addModificationsInTag(Annotation annotation) {
            modificationsInTag.add(annotation);
        }

        void addVarBetter(Annotation variant, Annotation standard) {
            cmpVarBetter.put(variant, standard);
        }

        void addStdBetter(Annotation variant, Annotation standard) {
            cmpStdBetter.put(variant, standard);
        }

        List<Annotation> getVariants() {
            return variants;
        }

        void buildReports() throws InterruptedException {

            ExecutorService executorService = Executors.newCachedThreadPool();

            executorService.submit(() -> {
                LinkedList<String> paths = new LinkedList<>();
                for (Annotation annotation : variants) {
                    AlignmentPrinter.getInstance().printAlignment(annotation);
                    paths.add(AnnotationSVG.buildAnnotationSVG(annotation));
                }
                try {
                    HtmlAlignmentReport.makeHtmlReport("alignment", paths);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });

            executorService.submit(() -> {
                LinkedList<String> paths = new LinkedList<>();
                for (Annotation annotation : standards) {
                    AlignmentPrinter.getInstance().printStandard(annotation);
                    paths.add(AnnotationSVG.buildAnnotationSVG(annotation));
                }
                try {
                    HtmlAlignmentReport.makeHtmlReport("standards", paths);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });

            executorService.submit(() -> {
                LinkedList<String> paths = new LinkedList<>();
                for (Map.Entry<Annotation, Annotation> e : cmpStdBetter.entrySet()) {
                    AlignmentPrinter.getInstance().printCompareStd(e.getKey(), e.getValue());
                    paths.add(CompareSVG.build(e.getValue(), e.getKey()));
                }
                try {
                    HtmlAlignmentReport.makeHtmlReport("compare(std better than var)", paths);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });

            executorService.submit(() -> {
                LinkedList<String> paths = new LinkedList<>();
                for (Map.Entry<Annotation, Annotation> e : cmpVarBetter.entrySet()) {
                    AlignmentPrinter.getInstance().printCompareVar(e.getKey(), e.getValue());
                    paths.add(CompareSVG.build(e.getValue(), e.getKey()));
                }
                try {
                    HtmlAlignmentReport.makeHtmlReport("compare(var better than std)", paths);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });

            executorService.submit(() -> {
                LinkedList<String> paths = new LinkedList<>();
                for (Annotation annotation : modificationsInTag) {
                    AlignmentPrinter.getInstance().printModifications(annotation);
                    paths.add(AnnotationSVG.buildAnnotationSVG(annotation));
                }
                try {
                    HtmlAlignmentReport.makeHtmlReport("alignment(modifications in tag)", paths);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });

            executorService.submit(() -> {
                LinkedList<String> paths = new LinkedList<>();
                for (Annotation annotation : notOnlyTag) {
                    AlignmentPrinter.getInstance().printNotOnlyTag(annotation);
                    paths.add(AnnotationSVG.buildAnnotationSVG(annotation));
                }
                try {
                    HtmlAlignmentReport.makeHtmlReport("alignment(not only tag)", paths);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });

            executorService.submit(() -> {
                LinkedList<String> paths = new LinkedList<>();
                for (Annotation annotation : roundedByAnnotations) {
                    AlignmentPrinter.getInstance().printRoundedByAnnotations(annotation);
                    paths.add(AnnotationSVG.buildAnnotationSVG(annotation));
                }
                try {
                    HtmlAlignmentReport.makeHtmlReport("alignment(rounded by annotations)", paths);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });

            executorService.submit(() -> {
                LinkedList<String> paths = new LinkedList<>();
                for (BoundsAlignedContainer boundsAlignedContainer : stdBoundsAligned) {
                    switch (boundsAlignedContainer.type) {
                        case BOTH:
                            AlignmentPrinter.getInstance().printBoundsAligned(boundsAlignedContainer.annotation, true);
                            paths.add(BoundsAlignedSVG.buildBoundsAligned(boundsAlignedContainer.annotation));
                            break;
                        case ZERO:
                            AlignmentPrinter.getInstance().printZeroAligned(boundsAlignedContainer.annotation
                                    , boundsAlignedContainer.diff
                                    , true);
                            paths.add(BoundsAlignedSVG.buildZeroAligned(boundsAlignedContainer.annotation
                                    , boundsAlignedContainer.diff));
                            break;
                        case ZERO_SPLITED:
                            AlignmentPrinter.getInstance().printZeroAligned(boundsAlignedContainer.annotation
                                    , boundsAlignedContainer.diff
                                    , boundsAlignedContainer.idx
                                    , boundsAlignedContainer.l
                                    , boundsAlignedContainer.r
                                    , true);
                            paths.add(BoundsAlignedSVG.buildZeroAligned(boundsAlignedContainer.annotation
                                    , boundsAlignedContainer.diff
                                    , boundsAlignedContainer.idx
                                    , boundsAlignedContainer.l
                                    , boundsAlignedContainer.r));
                            break;
                        case PRECURSOR:
                            AlignmentPrinter.getInstance().printPrecursorAligned(boundsAlignedContainer.annotation
                                    , boundsAlignedContainer.diff
                                    , true);
                            paths.add(BoundsAlignedSVG.buildPrecursorAligned(boundsAlignedContainer.annotation
                                    , boundsAlignedContainer.diff));
                            break;
                        case PRECURSOR_SPLITTED:
                            AlignmentPrinter.getInstance().printPrecursorAligned(boundsAlignedContainer.annotation
                                    , boundsAlignedContainer.diff
                                    , boundsAlignedContainer.idx
                                    , boundsAlignedContainer.l
                                    , boundsAlignedContainer.r
                                    , true);
                            paths.add(BoundsAlignedSVG.buildPrecursorAligned(boundsAlignedContainer.annotation
                                    , boundsAlignedContainer.diff
                                    , boundsAlignedContainer.idx
                                    , boundsAlignedContainer.l
                                    , boundsAlignedContainer.r));
                            break;
                    }
                }
                try {
                    HtmlAlignmentReport.makeHtmlReport("standard(bounds aligned)", paths);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });

            executorService.submit(() -> {
                LinkedList<String> paths = new LinkedList<>();
                for (BoundsAlignedContainer boundsAlignedContainer : varBoundsAligned) {
                    switch (boundsAlignedContainer.type) {
                        case BOTH:
                            AlignmentPrinter.getInstance().printBoundsAligned(boundsAlignedContainer.annotation, false);
                            paths.add(BoundsAlignedSVG.buildBoundsAligned(boundsAlignedContainer.annotation));
                            break;
                        case ZERO:
                            AlignmentPrinter.getInstance().printZeroAligned(boundsAlignedContainer.annotation
                                    , boundsAlignedContainer.diff
                                    , false);
                            paths.add(BoundsAlignedSVG.buildZeroAligned(boundsAlignedContainer.annotation
                                    , boundsAlignedContainer.diff));
                            break;
                        case ZERO_SPLITED:
                            AlignmentPrinter.getInstance().printZeroAligned(boundsAlignedContainer.annotation
                                    , boundsAlignedContainer.diff
                                    , boundsAlignedContainer.idx
                                    , boundsAlignedContainer.l
                                    , boundsAlignedContainer.r
                                    , false);
                            paths.add(BoundsAlignedSVG.buildZeroAligned(boundsAlignedContainer.annotation
                                    , boundsAlignedContainer.diff
                                    , boundsAlignedContainer.idx
                                    , boundsAlignedContainer.l
                                    , boundsAlignedContainer.r));
                            break;
                        case PRECURSOR:
                            AlignmentPrinter.getInstance().printPrecursorAligned(boundsAlignedContainer.annotation
                                    , boundsAlignedContainer.diff
                                    , false);
                            paths.add(BoundsAlignedSVG.buildPrecursorAligned(boundsAlignedContainer.annotation
                                    , boundsAlignedContainer.diff));
                            break;
                        case PRECURSOR_SPLITTED:
                            AlignmentPrinter.getInstance().printPrecursorAligned(boundsAlignedContainer.annotation
                                    , boundsAlignedContainer.diff
                                    , boundsAlignedContainer.idx
                                    , boundsAlignedContainer.l
                                    , boundsAlignedContainer.r
                                    , false);
                            paths.add(BoundsAlignedSVG.buildPrecursorAligned(boundsAlignedContainer.annotation
                                    , boundsAlignedContainer.diff
                                    , boundsAlignedContainer.idx
                                    , boundsAlignedContainer.l
                                    , boundsAlignedContainer.r));
                            break;
                    }
                }
                try {
                    HtmlAlignmentReport.makeHtmlReport("alignment(bounds aligned)", paths);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            
            executorService.shutdown();
            executorService.awaitTermination(1, TimeUnit.DAYS);
        }

        void addRoundedByAnnotations(Annotation varAnnotation) {
            roundedByAnnotations.add(varAnnotation);
        }

        void addStdBoundsAligned(BoundsAlignedContainer boundsAlignedContainer) {
            if (boundsAlignedContainer != null) {
                stdBoundsAligned.add(boundsAlignedContainer);
            }
        }

        void addVarBoundsAligned(BoundsAlignedContainer boundsAlignedContainer) {
            if (boundsAlignedContainer != null) {
                varBoundsAligned.add(boundsAlignedContainer);
            }
        }
    }

    static class BoundsAlignedContainer {

        private enum Type {
            BOTH,
            ZERO,
            ZERO_SPLITED,
            PRECURSOR,
            PRECURSOR_SPLITTED
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
