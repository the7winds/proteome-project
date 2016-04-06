package proteomeProject.spectrumAnnotation;

import javafx.util.Pair;
import proteomeProject.searchVariantPeptide.SearchVariantPeptideResult;
import proteomeProject.searchVariantPeptide.SearchVariantPeptideResults;
import proteomeProject.utils.Chemicals;
import proteomeProject.utils.Chemicals.AminoAcid;
import proteomeProject.utils.Printer;
import proteomeProject.utils.ProjectPaths;

import java.io.IOException;
import java.io.PrintStream;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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

    private static void annotateIons(Spectrum spectrum, String peptide, IonType.Type type) {
        Set<Double> peaks = spectrum.getPeaks();

        Double[] prefixMass = getPrefixes(peptide, type);
        int pLen = prefixMass.length;

        double eps = 0.6;
        int i = 0;

        for (double peak : peaks) {
            for (; i < pLen; ++i) {
                if (Math.abs(prefixMass[i] - peak) < eps) {
                    spectrum.annotatePeak(peak, new IonType(null, type, i + 1));
                    ++i;
                    break;
                } else if (Math.abs(prefixMass[i] - (peak - H2O.getMass())) < eps) {
                    spectrum.annotatePeak(peak, new IonType(new Chemicals[] { H2O }, type, i + 1));
                    ++i;
                    break;
                } else if (Math.abs(prefixMass[i] - (peak - NH3.getMass())) < eps) {
                    spectrum.annotatePeak(peak, new IonType(new Chemicals[] { NH3 }, type, i + 1));
                    ++i;
                    break;
                } else if (Math.abs(prefixMass[i] - (peak - NH3.getMass() - H2O.getMass())) < eps) {
                    spectrum.annotatePeak(peak, new IonType(new Chemicals[] { H2O, NH3 }, type, i + 1));
                    ++i;
                    break;
                } else if (Math.abs(prefixMass[i] - (peak - 2 * H2O.getMass() - peak)) < eps) {
                    spectrum.annotatePeak(peak, new IonType(new Chemicals[] { H2O, H2O }, type, i + 1));
                    ++i;
                    break;
                } else if (prefixMass[i] - peak > eps) {
                    break;
                } else if (peak - prefixMass[i] > eps) {
                    continue;
                }
            }
        }
    }

    private static Double[] getPrefixes(String peptide, IonType.Type type) {
        List<Pair<Double, Boolean>> masses = new LinkedList<>();
        for (int i = 0; i < peptide.length();) {
            if (peptide.charAt(i) == '+') {
                int t;
                for (t = 0; !Character.isAlphabetic(peptide.charAt(i + t)); ++t);
                masses.add(new Pair<>(Double.valueOf(peptide.substring(i + 1, i + t)), false));
                i += t;
            } else {
                masses.add(new Pair<>(AminoAcid.valueOf(Character.toString(peptide.charAt(i))).getAverageMass(), true));
                i++;
            }
        }

        double sum = masses.stream()
                .mapToDouble(Pair::getKey)
                .sum();

        LinkedList<Double> prefix = new LinkedList<>();
        double d = 0;
        for (Pair<Double, Boolean> pair : masses) {
            if (pair.getValue()) {
                prefix.addLast((prefix.isEmpty() ? 0 : prefix.getLast()) + pair.getKey() + d);
                d = 0;
            } else {
                d = pair.getKey();
            }
        }

        if (type == B) {
            return prefix.toArray(new Double[prefix.size()]);
        } else {
            LinkedList<Double> suffix = new LinkedList<>();
            suffix.add(sum);
            suffix.addAll(prefix.stream().map(p -> sum - p).collect(Collectors.toList()));
            suffix.removeLast();

            return suffix.stream()
                    .mapToDouble(a -> a + H2O.getMass())
                    .sorted()
                    .boxed()
                    .collect(Collectors.toList())
                    .toArray(new Double[suffix.size()]);
        }
    }
}
