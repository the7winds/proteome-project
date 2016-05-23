package proteomeProject.alignment;

import org.apache.commons.lang3.StringUtils;
import proteomeProject.annotation.Annotation;
import proteomeProject.dataEntities.*;
import proteomeProject.report.svg.BoundsAlignedSVG;
import proteomeProject.report.txt.AlignmentPrinter;
import proteomeProject.utils.ProjectPaths;

import java.io.IOException;
import java.util.Collection;
import java.util.Map;

import static proteomeProject.dataEntities.IonType.Type.B;
import static proteomeProject.utils.Chemicals.H2O;

/**
 * Created by the7winds on 20.04.16.
 */
class AlignmentTask implements Runnable {

    private final Peptide variant;
    private final Peptide standard;
    private final Tag tag;

    private final TagAlignment.AlignmentContainer alignmentContainer;

    AlignmentTask(Peptide variant
            , Tag tag
            , TagAlignment.AlignmentContainer alignmentContainer) {
        this.variant = variant;
        this.standard = new Peptide(VariantsStandards.getInstance().getStandard(variant.getName()));
        this.tag = tag;
        this.alignmentContainer = alignmentContainer;
    }

    private String tagString;
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

                int idxOfTag = variant.getPeptide().indexOf(tagString);

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
                    alignmentContainer.addVariant(varAnnotation);

                    if (isNotOnlyTag(varAnnotation, first, last)) {
                        alignmentContainer.addNotOnlyTag(varAnnotation);
                    }

                    if (isRoundedByAnnotations(varAnnotation)) {
                        alignmentContainer.addRoundedByAnnotations(varAnnotation);
                    }

                    compareWithStandard();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean isRoundedByAnnotations(Annotation varAnnotation) {
        Map<Integer, VariantsStandards.MapAmino> modifications =
                VariantsStandards.getInstance().getModifications(varAnnotation.getPeptide());
        if (modifications != null) {
            return modifications.entrySet().stream()
                    .anyMatch(e -> {
                        if (e.getValue().isModification()) {
                            return varAnnotation.getAnnotations().values().stream()
                                    .flatMap(Collection::stream)
                                    .anyMatch(x -> x.getType() == B
                                            ? x.getNum() < e.getKey()
                                            : x.getNum() > varAnnotation.getPeptide().getPeptide().length() - e.getKey() + 1) &&
                                    varAnnotation.getAnnotations().values().stream()
                                            .flatMap(Collection::stream)
                                            .anyMatch(x -> x.getType() == B
                                                    ? x.getNum() > e.getKey() + 1
                                                    : x.getNum() < varAnnotation.getPeptide().getPeptide().length() - e.getKey());
                        }
                        return false;
                    });
        }

        return false;
    }

    private boolean isNotOnlyTag(Annotation varAnnotation, int first, int last) {
        return varAnnotation.getAnnotations().values().stream()
                .flatMap(Collection::stream)
                .mapToInt(IonType::getNum)
                .anyMatch(n -> n < first || n > last);
    }

    // не рассматривается ситуация, когда несколько вхождений
    private static void align(Peptide peptide, Tag tag, IonType.Type type) throws IOException {
        double delta;
        int idx;
        if (type == B) {
            idx = peptide.getPeptide().indexOf(tag.getTag());
            delta = tag.getPeaks()[0] - peptide.getbSpectrum()[idx];
            peptide.shiftBSpectrum(delta);
        } else {
            idx = peptide.getPeptide().indexOf(StringUtils.reverse(tag.getTag()));
            idx = peptide.getPeptide().length() - (idx + tag.getTag().length());
            delta = tag.getPeaks()[0] - peptide.getySpectrum()[idx];
            peptide.shiftYSpectrum(delta);
        }
    }

    private void compareWithStandard() throws IOException {
        if (!VariantsStandards.getInstance().containsModifications(tagString, variant)) {
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

            alignmentContainer.addStandard(stdAnnotation);

            if (better(stdAnnotation, varAnnotation, 1)) {
                alignmentContainer.addStdBetter(varAnnotation, stdAnnotation);
            } else if (better(varAnnotation, stdAnnotation, 1)) {
                alignmentContainer.addVarBetter(varAnnotation, stdAnnotation);
            }

            checkBounds(stdAnnotation);
        } else {
            alignmentContainer.addModificationsInTag(varAnnotation);
        }
    }

    private boolean better(Annotation stdAnnotation, Annotation varAnnotation, int degree) {
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

        return stdCnt >= varCnt + degree;
    }

    // this method has some output because I can't split it
    private void checkBounds(Annotation stdAnnotation) {
        double[] spec = stdAnnotation.getType() == B
                ? stdAnnotation.getPeptide().getShiftedBSpectrum()
                : stdAnnotation.getPeptide().getShiftedYSpectrum();

        if ((!stdAnnotation.getAnnotations().get(0d).isEmpty()
                && !stdAnnotation.getAnnotations().get(stdAnnotation.getSpectrum().getPrecursorMass()).isEmpty())) {
            BoundsAlignedSVG.getInstance().buildBoundsAligned(stdAnnotation);
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
                double l = Math.abs(spec[idx] - stdAnnotation.getSpectrum().getPrecursorMass());
                double r = Math.abs(spec[idx + 1] - stdAnnotation.getSpectrum().getPrecursorMass());
                BoundsAlignedSVG.getInstance().buildZeroAligned(stdAnnotation, precursorDiff, idx + 1, l, r);
                AlignmentPrinter.getInstance().printZeroAligned(stdAnnotation, precursorDiff, idx + 1, l, r);
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