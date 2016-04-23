package proteomeProject.alignment;

import org.apache.batik.transcoder.TranscoderException;
import proteomeProject.annotation.Annotation;
import proteomeProject.dataEntities.ContributionWrapper;
import proteomeProject.dataEntities.Peptide;
import proteomeProject.dataEntities.Tag;
import proteomeProject.dataEntities.VariantsStandards;
import proteomeProject.report.html.HtmlReport;
import proteomeProject.report.txt.AlignmentPrinter;
import proteomeProject.utils.Options;

import java.io.IOException;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Created by the7winds on 06.04.16.
 */

public final class TagAlignment {

    static public void main() throws InterruptedException, IOException, TranscoderException {
        ExecutorService executorService = Executors.newFixedThreadPool(Options.getThreadsNum());
        List<Annotation> annotations = Collections.synchronizedList(new LinkedList<>());
        List<Annotation> standards = Collections.synchronizedList(new LinkedList<>());

        for (Tag tag : ContributionWrapper.getInstance().getAllTags()) {
            for (Peptide variant : VariantsStandards.getInstance().getVariants()) {
                executorService.execute(new AlignmentTask(variant, tag, annotations, standards));
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

        HtmlReport.makeHtmlReport("alignment.html", annotations);
    }
}
