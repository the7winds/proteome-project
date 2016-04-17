package proteomeProject.alignment;

import org.apache.batik.transcoder.TranscoderException;
import org.apache.commons.lang3.StringUtils;
import proteomeProject.annotation.Annotation;
import proteomeProject.annotation.AnnotationPrinter;
import proteomeProject.dataEntities.*;
import proteomeProject.report.html.HtmlReport;
import proteomeProject.utils.Options;
import proteomeProject.utils.ProjectPaths;

import java.io.IOException;
import java.io.PrintStream;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static proteomeProject.dataEntities.IonType.Type.B;
import static proteomeProject.dataEntities.IonType.Type.Y;

/**
 * Created by the7winds on 06.04.16.
 */

public final class TagAlignment {

    static public void main(PrintStream output) throws InterruptedException, IOException, TranscoderException {
        ExecutorService executorService = Executors.newFixedThreadPool(Options.getThreadsNum());
        List<Annotation> annotations = Collections.synchronizedList(new LinkedList<>());

        for (Tag tag : ContributionWrapper.getInstance().getAllTags()) {
            for (Peptide variant : Variants.getInstance().getVariants()) {
                executorService.execute(() -> {
                    try {
                        int idx;
                        if ((idx = variant.getPeptide().indexOf(tag.getTag())) != -1) {
                            Peptide peptide = new Peptide(variant);
                            align(peptide, tag, B, output);
                            Spectrum spec = Spectrum.parse(ProjectPaths.Sources.getSources()
                                    .resolve(tag.getSuffixedSpecFile())
                                    .toFile(), tag.getScanId());
                            annotations.add(Annotation.annotate(spec
                                    , peptide
                                    , tag
                                    , B
                                    , idx
                                    , idx + tag.getTag().length()));
                        }
                        if ((idx = variant.getPeptide().indexOf(StringUtils.reverse(tag.getTag()))) != -1) {
                            Peptide peptide = new Peptide(variant);
                            align(peptide, tag, Y, output);
                            Spectrum spec = Spectrum.parse(ProjectPaths.Sources.getSources()
                                    .resolve(tag.getSuffixedSpecFile())
                                    .toFile(), tag.getScanId());
                            annotations.add(Annotation.annotate(spec
                                    , peptide
                                    , tag
                                    , Y
                                    , peptide.getPeptide().length() - idx - tag.getTag().length()
                                    , peptide.getPeptide().length() - idx));
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
            }
        }
        executorService.shutdown();
        executorService.awaitTermination(1, TimeUnit.DAYS);

        for (Annotation annotation : annotations) {
            AnnotationPrinter.print(output, annotation);
        }

        HtmlReport.makeHtmlReport("alignment.html", annotations);
    }

    // не рассматривается ситуация, когда несколько вхождений
    private static void align(Peptide peptide, Tag tag, IonType.Type type, PrintStream output) throws IOException {
        double[] prefixes = (type == B ? peptide.getbSpectrum() : peptide.getySpectrum());

        double delta;
        int idx;
        if (type == B) {
            idx = peptide.getPeptide().indexOf(tag.getTag());
            delta = tag.getPeaks()[0] - prefixes[idx];
        } else {
            idx = peptide.getPeptide().indexOf(StringUtils.reverse(tag.getTag()));
            idx = peptide.getPeptide().length() - (idx + tag.getTag().length());
            delta = tag.getPeaks()[0] - prefixes[idx];
        }

        for (int i = 0; i < prefixes.length; ++i) {
            prefixes[i] += delta;
        }
    }
}
