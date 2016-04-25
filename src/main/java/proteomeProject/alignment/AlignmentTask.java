package proteomeProject.alignment;

import org.apache.commons.lang3.StringUtils;
import proteomeProject.annotation.Annotation;
import proteomeProject.dataEntities.*;
import proteomeProject.report.svg.AnnotationSVG;
import proteomeProject.report.svg.BoundsAlignedSVG;
import proteomeProject.report.txt.AlignmentPrinter;
import proteomeProject.report.txt.ModificationInTag;
import proteomeProject.utils.ProjectPaths;
import proteomeProject.utils.Utils;

import java.io.IOException;
import java.util.List;

import static proteomeProject.dataEntities.IonType.Type.B;
import static proteomeProject.utils.Chemicals.H2O;

/**
 * Created by the7winds on 20.04.16.
 */
class AlignmentTask implements Runnable {

    private final Peptide variant;
    private final Peptide standard;
    private final Tag tag;
    private final List<Annotation> variants;
    private final List<Annotation> standards;

    private final List<String> svgVar;
    private final List<String> svgStd;

    AlignmentTask(Peptide variant
            , Tag tag
            , List<Annotation> variants
            , List<Annotation> standards
            , List<String> svgVar
            , List<String> svgStd) {
        this.variant = variant;
        this.svgVar = svgVar;
        this.svgStd = svgStd;
        this.standard = new Peptide(VariantsStandards.getInstance().variantToStandard().get(variant));
        this.tag = tag;
        this.variants = variants;
        this.standards = standards;
    }

    private String tagString;
    private int idxOfTag;
    private IonType.Type type;
    private Spectrum spec;

    private Annotation varAnnotation;

    @Override
    public void run() {
        try {
            for (IonType.Type t : IonType.Type.values()) {
                type = t;

                tagString = type == B
                        ? tag.getTag()
                        : StringUtils.reverse(tag.getTag());

                idxOfTag = variant.getPeptide().indexOf(tagString);

                int first = type == B
                        ? idxOfTag
                        : variant.getPeptide().length() - tag.getTag().length() - idxOfTag;

                int last = type == B
                        ? idxOfTag + tag.getTag().length()
                        : variant.getPeptide().length() - idxOfTag;

                if (idxOfTag != -1) {
                    Peptide var = new Peptide(variant);
                    align(var, tag, type);
                    spec = SpectrumWrapper.getInstance()
                            .findSpectrumByScans(ProjectPaths.Sources.getSources()
                                    .resolve(tag.getSuffixedSpecFile())
                                    .toFile(), tag.getScanId());
                    varAnnotation = Annotation.annotate(spec
                            , var
                            , tag
                            , type
                            , first
                            , last);
                    variants.add(varAnnotation);

                    String file = Utils.getSvgName(Utils.newName());
                    svgVar.add(file);
                    AnnotationSVG.build(file, varAnnotation);

                    compareWithStandard();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // не рассматривается ситуация, когда несколько вхождений
    private static void align(Peptide peptide, Tag tag, IonType.Type type) throws IOException {
        double[] prefixes = type == B ? peptide.getbSpectrum() : peptide.getySpectrum();

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

    private void compareWithStandard() throws IOException {
        if (!VariantsStandards.containsModifications(tagString, variant)) {
            int stdIdx = standard.getPeptide().indexOf(tagString);
            int first = type == B
                    ? stdIdx
                    : standard.getPeptide().length() - tag.getTag().length() - stdIdx;
            int last = type == B
                    ? stdIdx + tag.getTag().length()
                    : standard.getPeptide().length() - stdIdx;

            align(standard, tag, type);
            Annotation stdAnnotation = Annotation.annotate(spec
                    , standard
                    , tag
                    , type
                    , first
                    , last);
            standards.add(stdAnnotation);

            String file = Utils.getSvgName(Utils.newName());
            svgStd.add(file);
            AnnotationSVG.build(file, stdAnnotation);

            if (stdBetter(stdAnnotation, varAnnotation)) {
                AlignmentPrinter.getInstance().printCompare(varAnnotation, stdAnnotation);
            }

            checkBounds(stdAnnotation);
        } else {
            ModificationInTag.getInstance().print(variant, standard, tag, type);
        }
    }

    private boolean stdBetter(Annotation stdAnnotation, Annotation varAnnotation) {
        int varCnt = (int) varAnnotation.getAnnotations()
                .entrySet()
                .stream()
                .filter(e -> !e.getValue().isEmpty())
                .count();

        int stdCnt = (int) stdAnnotation.getAnnotations()
                .entrySet()
                .stream()
                .filter(e -> !e.getValue().isEmpty())
                .count();

        return stdCnt > varCnt;
    }

    private void checkBounds(Annotation stdAnnotation) {
        double[] spec = stdAnnotation.getType() == B
                ? stdAnnotation.getPeptide().getbSpectrum()
                : stdAnnotation.getPeptide().getySpectrum();

        if ((!stdAnnotation.getAnnotations().get(0d).isEmpty()
                && !stdAnnotation.getAnnotations().get(stdAnnotation.getSpectrum().getPrecursorMass()).isEmpty())) {
            BoundsAlignedSVG.getInstance().build(stdAnnotation);
            AlignmentPrinter.getInstance().printBoundsAligned(stdAnnotation);
        } else if (!stdAnnotation.getAnnotations().get(0d).isEmpty()) {
            int last = stdAnnotation.getPeptide().getPeptide().length() - 1;
            double precursorDiff = stdAnnotation.getSpectrum().getPrecursorMass() -
                    (stdAnnotation.getType() == B
                            ? spec[last]
                            : (spec[last] - H2O.getMass()));

            if (precursorDiff < 0) {
                int idx;
                for (idx = spec.length - 1; idx >= 0 && spec[idx] > stdAnnotation.getSpectrum().getPrecursorMass(); --idx);
                BoundsAlignedSVG.getInstance().buildZeroAligned(stdAnnotation, precursorDiff, idx, spec[idx], spec[idx + 1]);
                AlignmentPrinter.getInstance().printZeroAligned(stdAnnotation, precursorDiff, idx, spec[idx], spec[idx + 1]);
            } else {
                BoundsAlignedSVG.getInstance().buildZeroAligned(stdAnnotation, precursorDiff);
                AlignmentPrinter.getInstance().printZeroAligned(stdAnnotation, precursorDiff);
            }
        } else if (!stdAnnotation.getAnnotations().get(stdAnnotation.getSpectrum().getPrecursorMass()).isEmpty()) {
            double zeroDiff = spec[0];

            if (zeroDiff < 0) {
                int idx;
                for (idx = 0; idx < spec.length && spec[idx] < 0; ++idx);
                BoundsAlignedSVG.getInstance().buildPrecursorAligned(stdAnnotation, zeroDiff, idx, spec[idx - 1], spec[idx]);
                AlignmentPrinter.getInstance().printPrecursorAligned(stdAnnotation, zeroDiff, idx, spec[idx - 1], spec[idx]);
            } else {
                BoundsAlignedSVG.getInstance().buildPrecursorAligned(stdAnnotation, zeroDiff);
                AlignmentPrinter.getInstance().printPrecursorAligned(stdAnnotation, zeroDiff);
            }
        }
    }
}