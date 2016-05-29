package proteomeProject.alignment;

import org.apache.batik.transcoder.TranscoderException;
import proteomeProject.alignment.condition.*;
import proteomeProject.annotation.Annotation;
import proteomeProject.dataEntities.ContributionWrapper;
import proteomeProject.dataEntities.Peptide;
import proteomeProject.dataEntities.Tag;
import proteomeProject.dataEntities.VariantsStandards;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Created by the7winds on 06.04.16.
 */

public final class TagAlignment {

    static public void main() throws InterruptedException, IOException, TranscoderException {
        ExecutorService executorService = Executors.newCachedThreadPool();

        Collection<ConditionTask> conditionTasks = new LinkedList<>();
        conditionTasks.add(new ConditionTask(new Alignment()));
        conditionTasks.add(new ConditionTask(new NotOnlyTag()));
        conditionTasks.add(new ConditionTask(new ModificationInTag()));
        conditionTasks.add(new ConditionTask(new IsRoundedByAnnotations()));
        conditionTasks.add(new ConditionTask(new Standard()));
        conditionTasks.add(new ConditionTask(new VariantBetter()));
        conditionTasks.add(new ConditionTask(new StandardBetter()));
        conditionTasks.add(new ConditionTask(new Bounds.VarBoundsAligned()));
        conditionTasks.add(new ConditionTask(new Bounds.StdBoundsAligned()));
        conditionTasks.add(new ConditionTask(new EqualPeaks.VarEqualPeaks()));
        conditionTasks.add(new ConditionTask(new EqualPeaks.StdEqualPeaks()));

        conditionTasks.add(new ConditionTask(new EqualPeaks.VarForJoin()) {

            final Collection<Annotation> annotations = Collections.synchronizedList(new LinkedList<>());

            @Override
            public void eval(Annotation annotation) {
                annotations.add(annotation);
                condition.addIf(annotation);
            }

            @Override
            public void makeReport() throws IOException {
                annotations.forEach(condition::print);
                condition.makeReport();
            }
        });
        conditionTasks.add(new ConditionTask(new EqualPeaks.StdForJoin()) {

            final Collection<Annotation> annotations = Collections.synchronizedList(new LinkedList<>());

            @Override
            public void eval(Annotation annotation) {
                annotations.add(annotation);
                condition.addIf(annotation);
            }

            @Override
            public void makeReport() throws IOException {
                annotations.forEach(condition::print);
                condition.makeReport();
            }
        });

        for (Tag tag : ContributionWrapper.getInstance().getAllTags()) {
            for (Peptide variant : VariantsStandards.getInstance().getVariants()) {
                executorService.execute(new AlignmentTask(variant, tag, conditionTasks));
            }
        }

        executorService.shutdown();
        executorService.awaitTermination(1, TimeUnit.DAYS);

        for (ConditionTask task : conditionTasks) {
            task.makeReport();
        }
    }
}
