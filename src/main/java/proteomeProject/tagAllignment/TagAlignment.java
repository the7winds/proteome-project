package proteomeProject.tagAllignment;

import org.apache.commons.lang3.StringUtils;
import proteomeProject.ContributionWrapper;
import proteomeProject.Variants;
import proteomeProject.spectrumAnnotation.AnnotationPrinter;
import proteomeProject.spectrumAnnotation.IonType;
import proteomeProject.spectrumAnnotation.Spectrum;
import proteomeProject.spectrumAnnotation.SpectrumAnnotation;
import proteomeProject.utils.ProjectPaths;
import proteomeProject.utils.Utils;

import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Path;
import java.util.List;

/**
 * Created by the7winds on 06.04.16.
 */

public class TagAlignment {

    static public void main(Path output) throws IOException {
        new TagAlignment().alignAll(new PrintStream(output.toFile()));
    }

    void alignAll(PrintStream output) throws IOException {
        for (ContributionWrapper.Tag tag : ContributionWrapper.getInstance().getAllTags()) {
            for (Variants.Variant var : Variants.getInstance().getVariants()) {
                if (var.getPeptide().contains(tag.getTag())) {
                    alignAndAnnotateAndPrint(var, tag, IonType.Type.B, output);
                }
                if (var.getPeptide().contains(StringUtils.reverse(tag.getTag()))) {
                    alignAndAnnotateAndPrint(var, tag, IonType.Type.Y, output);
                }
            }
        }
    }

    // не рассматривается ситуация, когда несколько вхождений
    private void alignAndAnnotateAndPrint(Variants.Variant var, ContributionWrapper.Tag tag, IonType.Type type, PrintStream output) throws IOException {
        List<Double> tmp = Utils.getPrefixes(var.getPeptide(), type);
        Double[] prefixes = tmp.toArray(new Double[tmp.size()]);

        Spectrum spec = new Spectrum(ProjectPaths.Sources.getSources()
                .resolve(tag.getSuffixedSpecFile())
                .toFile(), tag.getScanId());

        int first;
        double delta;
        int idx;
        if (type == IonType.Type.B) {
            idx = first = var.getPeptide().indexOf(tag.getTag());
            delta = tag.getPeaks()[0] - prefixes[first];
        } else {
            first = var.getPeptide().indexOf(StringUtils.reverse(tag.getTag()));
            idx = var.getPeptide().length() - (first + tag.getTag().length());
            delta = tag.getPeaks()[0] - prefixes[idx];
        }

        for (int i = 0; i < prefixes.length; ++i) {
            prefixes[i] += delta;
        }

        output.printf("SPEC=%s\n", tag.getSpecFile());
        output.printf("TAG=%s FIRST=%s%d LAST=%s%d\n", tag.getTag(), type.toString(), idx, type.toString(), (idx + tag.getTag().length()));

        SpectrumAnnotation.annotateIons(spec, prefixes, type);
        new AnnotationPrinter(output).print(spec, var.getPeptide());
    }
}
