package proteomeProject.annotation;

import org.apache.batik.transcoder.TranscoderException;
import proteomeProject.dataEntities.Peptide;
import proteomeProject.dataEntities.Spectrum;
import proteomeProject.dataEntities.SpectrumWrapper;
import proteomeProject.report.html.HtmlAlignmentReport;
import proteomeProject.report.svg.AnnotationSVG;
import proteomeProject.report.txt.SearchPrinter;
import proteomeProject.searchVariantPeptide.SearchVariantPeptideResult;
import proteomeProject.searchVariantPeptide.SearchVariantPeptideResults;
import proteomeProject.utils.ProjectPaths;
import proteomeProject.utils.Utils;

import java.io.IOException;
import java.util.Collection;
import java.util.LinkedList;

/**
 * Created by the7winds on 30.03.16.
 */
public class SpectrumAnnotation {

    public static void main(SearchVariantPeptideResults variantPeptideResults)
            throws IOException, InterruptedException, TranscoderException {

        Collection<String> annotations = new LinkedList<>();
        for (SearchVariantPeptideResult variantPeptideResult : variantPeptideResults.getTagFoundResults()) {
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

            annotations.add(AnnotationSVG.buildAnnotationSVG(annotation));

            SearchPrinter.getInstance().print(SearchPrinter.Type.TAG_FOUND, annotation);
        }

        HtmlAlignmentReport.makeHtmlReport("searchFound", annotations);
        annotations.clear();

        for (SearchVariantPeptideResult variantPeptideResult: variantPeptideResults.getTagNotFoundResults()) {
            Spectrum spectrum = SpectrumWrapper.getInstance()
                    .findSpectrumByScans(ProjectPaths.Sources.getSources()
                            .resolve(variantPeptideResult.getFilename())
                            .toFile(), variantPeptideResult.getScanNum());
            Annotation annotation = Annotation.annotate(spectrum
                    , new Peptide(variantPeptideResult.getProtein(), variantPeptideResult.getPeptide())
                    , variantPeptideResult.getTag());

            annotations.add(AnnotationSVG.buildAnnotationSVG(annotation));

            SearchPrinter.getInstance().print(SearchPrinter.Type.TAG_NOT_FOUND, annotation);
        }

        HtmlAlignmentReport.makeHtmlReport("searchNotFound", annotations);
        annotations.clear();

        for (SearchVariantPeptideResult variantPeptideResult: variantPeptideResults.getTagNotExistsResults()) {
            Spectrum spectrum = SpectrumWrapper.getInstance()
                    .findSpectrumByScans(ProjectPaths.Sources.getSources()
                            .resolve(variantPeptideResult.getFilename())
                            .toFile(), variantPeptideResult.getScanNum());
            Annotation annotation = Annotation.annotate(spectrum
                    , new Peptide(variantPeptideResult.getProtein(), variantPeptideResult.getPeptide())
                    , variantPeptideResult.getTag());

            annotations.add(AnnotationSVG.buildAnnotationSVG(annotation));

            SearchPrinter.getInstance().print(SearchPrinter.Type.TAG_NOT_EXISTS, annotation);
        }

        HtmlAlignmentReport.makeHtmlReport("searchNotExist", annotations);
    }
}
