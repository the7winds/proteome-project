package proteomeProject.alignment;

import org.apache.batik.transcoder.TranscoderException;
import proteomeProject.annotation.Annotation;
import proteomeProject.dataEntities.ContributionWrapper;
import proteomeProject.dataEntities.Peptide;
import proteomeProject.dataEntities.Tag;
import proteomeProject.dataEntities.VariantsStandards;
import proteomeProject.report.html.HtmlAlignmentReport;
import proteomeProject.report.svg.BoundsAlignedSVG;
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

        List<Annotation> annotations = Collections.synchronizedList(new LinkedList<>());
        List<Annotation> standards = Collections.synchronizedList(new LinkedList<>());

        List<String> svgVar = Collections.synchronizedList(new LinkedList<>());
        List<String> svgStd = Collections.synchronizedList(new LinkedList<>());

        List<String> svgCmpVarBetter = Collections.synchronizedList(new LinkedList<>());
        List<String> svgCmpStdBetter = Collections.synchronizedList(new LinkedList<>());

        for (Tag tag : ContributionWrapper.getInstance().getAllTags()) {
            for (Peptide variant : VariantsStandards.getInstance().getVariants()) {
                executorService.execute(new AlignmentTask(variant
                        , tag
                        , annotations
                        , standards
                        , svgVar
                        , svgStd
                        , svgCmpVarBetter
                        , svgCmpStdBetter));
            }
        }
        executorService.shutdown();
        executorService.awaitTermination(1, TimeUnit.DAYS);

        for (Annotation annotation : annotations) {
            AlignmentPrinter.getInstance().printAlignment(annotation);
        }

        for (Annotation annotation : standards) {
            AlignmentPrinter.getInstance().printStandard(annotation);
        }

        HtmlAlignmentReport.makeHtmlReport("alignment", svgVar);
        HtmlAlignmentReport.makeHtmlReport("standards", svgStd);
        HtmlAlignmentReport.makeHtmlReport("bounds aligned", BoundsAlignedSVG.getInstance().getElements());
        HtmlAlignmentReport.makeHtmlReport("compare(std better than var)", svgCmpStdBetter);
        HtmlAlignmentReport.makeHtmlReport("compare(var better than std)", svgCmpVarBetter);

        ConcurrentMap<Annotation, Annotation> reverseMap = new ConcurrentHashMap<>();
        executorService = Executors.newCachedThreadPool();

        for (Annotation annotation : annotations) {
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
}
