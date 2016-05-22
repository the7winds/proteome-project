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
import proteomeProject.report.svg.ReverseSVG;
import proteomeProject.report.txt.AlignmentPrinter;
import proteomeProject.utils.Options;

import java.io.IOException;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

/**
 * Created by the7winds on 06.04.16.
 */

public final class TagAlignment {

    static public void main() throws InterruptedException, IOException, TranscoderException {
        ExecutorService executorService = Executors.newFixedThreadPool(Options.getThreadsNum());

        AlignmentContainer alignmentContainer = new AlignmentContainer();

        for (Tag tag : ContributionWrapper.getInstance().getAllTags()) {
            for (Peptide variant : VariantsStandards.getInstance().getVariants()) {
                executorService.execute(new AlignmentTask(variant, tag, alignmentContainer));
            }
        }

        executorService.shutdown();
        executorService.awaitTermination(1, TimeUnit.DAYS);

        alignmentContainer.buildReports();

        // for reverse

        executorService = Executors.newCachedThreadPool();
        ConcurrentMap<Annotation, Annotation> reverseMap = new ConcurrentHashMap<>();

        for (Annotation annotation : alignmentContainer.getVariants()) {
            executorService.execute(new SearchReverseAnnotationsTask(annotation, reverseMap));
        }

        executorService.shutdown();
        executorService.awaitTermination(1, TimeUnit.DAYS);

        List<String> svgReverse = new LinkedList<>();

        for (Map.Entry<Annotation, Annotation> entry : reverseMap.entrySet()) {
            svgReverse.add(ReverseSVG.build(entry.getKey(), entry.getValue()));
            AlignmentPrinter.getInstance().printReverseAnnotations(entry.getKey(), entry.getValue());
        }

        HtmlAlignmentReport.makeHtmlReport("alignment reverse", svgReverse);
    }

    static class AlignmentContainer {

        private final List<Annotation> variants = Collections.synchronizedList(new LinkedList<>());
        private final List<Annotation> standards = Collections.synchronizedList(new LinkedList<>());
        private final List<Annotation> notOnlyTag = Collections.synchronizedList(new LinkedList<>());
        private final List<Annotation> modificationsInTag = Collections.synchronizedList(new LinkedList<>());
        private final Map<Annotation, Annotation> cmpVarBetter = new ConcurrentHashMap<>();
        private final Map<Annotation, Annotation> cmpStdBetter = new ConcurrentHashMap<>();

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

            try {
                HtmlAlignmentReport.makeHtmlReport("bounds aligned", BoundsAlignedSVG.getInstance().getElements());
            } catch (IOException e) {
                e.printStackTrace();
            }

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

            executorService.shutdown();
            executorService.awaitTermination(1, TimeUnit.DAYS);
        }
    }
}
