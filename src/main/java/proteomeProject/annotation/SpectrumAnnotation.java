package proteomeProject.annotation;

import org.apache.batik.transcoder.TranscoderException;
import proteomeProject.dataEntities.Peptide;
import proteomeProject.dataEntities.Spectrum;
import proteomeProject.dataEntities.SpectrumWrapper;
import proteomeProject.report.html.HtmlReport;
import proteomeProject.searchVariantPeptide.SearchVariantPeptideResult;
import proteomeProject.searchVariantPeptide.SearchVariantPeptideResults;
import proteomeProject.utils.ProjectPaths;

import java.io.IOException;
import java.io.PrintStream;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by the7winds on 30.03.16.
 */
public class SpectrumAnnotation {

    private static final String TAG_FOUND = "TAG FOUND";
    private static final String TAG_NOT_FOUND = "TAG NOT FOUND";
    private static final String TAG_NOT_EXISTS = "TAG NOT EXISTS";

    public static void main(SearchVariantPeptideResults variantPeptideResults,
                            PrintStream output) throws IOException, InterruptedException, TranscoderException {

        output.println(TAG_FOUND);
        List<Annotation> annotations = new LinkedList<>();
        for (SearchVariantPeptideResult variantPeptideResult : variantPeptideResults.getTagFoundResults()) {
            // if (variantPeptideResult.getDelta() )
            Spectrum spectrum = SpectrumWrapper.getInstance()
                    .findSpectrumByScans(ProjectPaths.Sources.getSources()
                            .resolve(variantPeptideResult.getFilename())
                            .toFile(), variantPeptideResult.getScanNum());
            Annotation annotation = Annotation.annotate(spectrum
                    , new Peptide(variantPeptideResult.getProtein()
                            , variantPeptideResult.getPeptide())
                    , variantPeptideResult.getTag()
                    , variantPeptideResult.getType()
                    , variantPeptideResult.getFirst()
                    , variantPeptideResult.getLast());
            annotations.add(annotation);
            AnnotationPrinter.print(output, annotation);
        }

        HtmlReport.makeHtmlReport("searchFound.html", annotations);
        annotations.clear();

        output.println(TAG_NOT_FOUND);
        for (SearchVariantPeptideResult variantPeptideResult: variantPeptideResults.getTagNotFoundResults()) {
            Spectrum spectrum = SpectrumWrapper.getInstance()
                    .findSpectrumByScans(ProjectPaths.Sources.getSources()
                            .resolve(variantPeptideResult.getFilename())
                            .toFile(), variantPeptideResult.getScanNum());
            Annotation annotation = Annotation.annotate(spectrum
                    , new Peptide(variantPeptideResult.getProtein(), variantPeptideResult.getPeptide())
                    , variantPeptideResult.getTag());
            annotations.add(annotation);
            AnnotationPrinter.print(output, annotation);
        }

        HtmlReport.makeHtmlReport("searchNotFound.html", annotations);
        annotations.clear();

        output.println(TAG_NOT_EXISTS);
        for (SearchVariantPeptideResult variantPeptideResult: variantPeptideResults.getTagNotExistsResults()) {
            Spectrum spectrum = SpectrumWrapper.getInstance()
                    .findSpectrumByScans(ProjectPaths.Sources.getSources()
                            .resolve(variantPeptideResult.getFilename())
                            .toFile(), variantPeptideResult.getScanNum());
            Annotation annotation = Annotation.annotate(spectrum
                    , new Peptide(variantPeptideResult.getProtein(), variantPeptideResult.getPeptide())
                    , variantPeptideResult.getTag());
            annotations.add(annotation);
            AnnotationPrinter.print(output, annotation);
        }

        HtmlReport.makeHtmlReport("searchNotExist.html", annotations);
    }
}
