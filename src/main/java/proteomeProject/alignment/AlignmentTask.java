package proteomeProject.alignment;

import org.apache.commons.lang3.StringUtils;
import proteomeProject.annotation.Annotation;
import proteomeProject.dataEntities.*;
import proteomeProject.utils.ProjectPaths;

import java.util.Collection;

import static proteomeProject.dataEntities.IonType.Type.B;

/**
 * Created by the7winds on 20.04.16.
 */
public class AlignmentTask implements Runnable {

    private final Peptide variant;
    private final Tag tag;
    private final Collection<ConditionTask> conditionTasks;

    AlignmentTask(Peptide variant
            , Tag tag
            , Collection<ConditionTask> conditionTasks) {
        this.variant = variant;
        this.tag = tag;
        this.conditionTasks = conditionTasks;
    }

    @Override
    public void run() {
        for (IonType.Type type : IonType.Type.values()) {

            String tagString = type == B
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
                Spectrum spec = SpectrumWrapper.getInstance()
                        .findSpectrumByScans(ProjectPaths.Sources.getSources()
                                .resolve(tag.getSuffixedSpecFile())
                                .toFile(), tag.getScanId());
                Annotation varAnnotation = Annotation.annotate(spec
                        , var
                        , tag
                        , type
                        , first
                        , last);

                for (ConditionTask task : conditionTasks) {
                    task.eval(varAnnotation);
                }
            }
        }
    }

    // не рассматривается ситуация, когда несколько вхождений
    public static void align(Peptide peptide, Tag tag, IonType.Type type) {
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
}