package proteomeProject.annotation;

import proteomeProject.dataEntities.Peptide;
import proteomeProject.dataEntities.Spectrum;
import proteomeProject.searchVariantPeptide.SearchVariantPeptideResult;
import proteomeProject.searchVariantPeptide.SearchVariantPeptideResults;
import proteomeProject.utils.ProjectPaths;

import java.io.IOException;
import java.io.PrintStream;

/**
 * Created by the7winds on 30.03.16.
 */
public class SpectrumAnnotation {

    private static final String TAG_FOUND = "TAG FOUND";
    private static final String TAG_NOT_FOUND = "TAG NOT FOUND";
    private static final String TAG_NOT_EXISTS = "TAG NOT EXISTS";

    public static void main(SearchVariantPeptideResults variantPeptideResults,
                            PrintStream output) throws IOException, InterruptedException {

        output.println(TAG_FOUND);
        for (SearchVariantPeptideResult variantPeptideResult : variantPeptideResults.getTagFoundResults()) {
            // if (variantPeptideResult.getDelta() )
            Spectrum spectrum = Spectrum.parse(ProjectPaths.Sources.getSources()
                    .resolve(variantPeptideResult.getFilename())
                    .toFile(), variantPeptideResult.getScanNum());
            Annotation annotation = Annotation.annotate(spectrum,
                    new Peptide(variantPeptideResult.getProtein(), variantPeptideResult.getPeptide()), variantPeptideResult.getTag());
            AnnotationPrinter.print(output, annotation);
        }

        output.println(TAG_NOT_FOUND);
        for (SearchVariantPeptideResult variantPeptideResult: variantPeptideResults.getTagNotFoundResults()) {
            Spectrum spectrum = Spectrum.parse(ProjectPaths.Sources.getSources()
                    .resolve(variantPeptideResult.getFilename())
                    .toFile(), variantPeptideResult.getScanNum());
            Annotation annotation = Annotation.annotate(spectrum
                    , new Peptide(variantPeptideResult.getProtein(), variantPeptideResult.getPeptide())
                    , variantPeptideResult.getTag());
            AnnotationPrinter.print(output, annotation);
        }

        output.println(TAG_NOT_EXISTS);
        for (SearchVariantPeptideResult variantPeptideResult: variantPeptideResults.getTagNotExistsResults()) {
            Spectrum spectrum = Spectrum.parse(ProjectPaths.Sources.getSources()
                    .resolve(variantPeptideResult.getFilename())
                    .toFile(), variantPeptideResult.getScanNum());
            Annotation annotation = Annotation.annotate(spectrum
                    , new Peptide(variantPeptideResult.getProtein(), variantPeptideResult.getPeptide())
                    , variantPeptideResult.getTag());
            AnnotationPrinter.print(output, annotation);
        }
    }
}
