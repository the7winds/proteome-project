package proteomeProject.spectrumAnnotation;

import proteomeProject.searchVariantPeptide.SearchVariantPeptideResult;
import proteomeProject.searchVariantPeptide.SearchVariantPeptideResults;
import proteomeProject.utils.Chemicals;
import proteomeProject.utils.Printer;
import proteomeProject.utils.ProjectPaths;
import proteomeProject.utils.Utils;

import java.io.IOException;
import java.io.PrintStream;
import java.util.List;

import static proteomeProject.spectrumAnnotation.IonType.Type.B;
import static proteomeProject.spectrumAnnotation.IonType.Type.Y;
import static proteomeProject.utils.Chemicals.H2O;
import static proteomeProject.utils.Chemicals.NH3;

/**
 * Created by the7winds on 30.03.16.
 */
public class SpectrumAnnotation {

    private static final String TAG_FOUND = "tag found:";
    private static final String TAG_NOT_EXISTS = "tag not exists:";

    public static void main(SearchVariantPeptideResults variantPeptideResults,
                            PrintStream output) throws IOException {

        output.println(TAG_FOUND);
        Printer printer = new AnnotationPrinter(output);

        for (SearchVariantPeptideResult variantPeptideResult : variantPeptideResults.getTagFoundResults()) {
            // if (variantPeptideResult.getDelta() )
            Spectrum spectrum = new Spectrum(ProjectPaths.getData()
                    .resolve(variantPeptideResult.getFilename())
                    .toFile(), variantPeptideResult.getScanNum());
            annotate(spectrum, variantPeptideResult.getPeptide());
            printer.print(spectrum);
        }

        output.println(TAG_NOT_EXISTS);
        for (SearchVariantPeptideResult variantPeptideResult: variantPeptideResults.getTagNotExistsResults()) {
            Spectrum spectrum = new Spectrum(ProjectPaths.getData()
                    .resolve(variantPeptideResult.getFilename())
                    .toFile(), variantPeptideResult.getScanNum());
            annotate(spectrum, variantPeptideResult.getPeptide());
            printer.print(spectrum);
        }
    }

    private static void annotate(Spectrum spectrum, String peptide) {
        annotateIons(spectrum, peptide, B);
        annotateIons(spectrum, peptide, Y);
    }

    public static void annotateIons(Spectrum spectrum, String peptide, IonType.Type type) {
        List<Double> tmp = Utils.getPrefixes(peptide, type);
        Double[] prefixMass = Utils.getPrefixes(peptide, type).toArray(new Double[tmp.size()]);
        annotateIons(spectrum, prefixMass, type);
    }

    public static void annotateIons(Spectrum spectrum, Double[] prefixMass, IonType.Type type) {
        Double[] peaks = spectrum.getPeaks();
        int pLen = prefixMass.length;
        double eps = 0.02;

        for (double peak : peaks) {
            for (int i = 0; i < pLen; ++i) {
                if (Math.abs(prefixMass[i] - peak) < eps) {
                    spectrum.annotatePeak(peak, new IonType(null, type, i));
                } else if (Math.abs(peak - (prefixMass[i] - H2O.getMass())) < eps) {
                    spectrum.annotatePeak(peak, new IonType(new Chemicals[] { H2O }, type, i));
                } else if (Math.abs(peak - (prefixMass[i] - NH3.getMass())) < eps) {
                    spectrum.annotatePeak(peak, new IonType(new Chemicals[] { NH3 }, type, i));
                } else if (Math.abs(peak - (prefixMass[i] - NH3.getMass() - H2O.getMass())) < eps) {
                    spectrum.annotatePeak(peak, new IonType(new Chemicals[] { H2O, NH3 }, type, i));
                } else if (Math.abs(peak - (prefixMass[i] - 2 * H2O.getMass())) < eps) {
                    spectrum.annotatePeak(peak, new IonType(new Chemicals[] { H2O, H2O }, type, i));
                }
            }
        }
    }
}
