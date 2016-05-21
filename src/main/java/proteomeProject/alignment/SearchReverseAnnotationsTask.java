package proteomeProject.alignment;

import proteomeProject.annotation.Annotation;
import proteomeProject.dataEntities.IonType;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;

import static proteomeProject.dataEntities.IonType.Type.B;
import static proteomeProject.dataEntities.IonType.Type.Y;

/**
 * Created by the7winds on 21.05.16.
 */
public class SearchReverseAnnotationsTask implements Runnable {

    private final Annotation annotation;
    private final ConcurrentMap<Annotation, Annotation> reverseMap;

    private Set<Double> notAnnotated;

    public SearchReverseAnnotationsTask(Annotation annotation, ConcurrentMap<Annotation, Annotation> reverseMap) {
        this.annotation = annotation;
        this.reverseMap = reverseMap;
    }

    @Override
    public void run() {
        notAnnotated = annotation.getAnnotations().entrySet().stream()
                .filter(e -> e.getValue().isEmpty())
                .map(Map.Entry::getKey)
                .collect(Collectors.toSet());

        Annotation optimalShifted = null;

        if (annotation.getType() == B) {
            optimalShifted = Annotation.annotate(annotation.getSpectrum(), annotation.getPeptide(), Y);
            for (double peak : annotation.getSpectrum().getPeaks()) {
                for (int i = 0; i < annotation.getPeptide().getySpectrum().length; ++i) {
                    double delta = peak - annotation.getPeptide().getySpectrum()[i];
                    annotation.getPeptide().shiftYSpectrum(delta);
                    Annotation shifted = Annotation.annotate(annotation.getSpectrum(), annotation.getPeptide(), Y);
                    optimalShifted = compareAnnotations(shifted, optimalShifted);
                }
            }
        } else if (annotation.getType() == Y) {
            optimalShifted = Annotation.annotate(annotation.getSpectrum(), annotation.getPeptide(), B);
            for (double peak : annotation.getSpectrum().getPeaks()) {
                for (int i = 0; i < annotation.getPeptide().getbSpectrum().length; ++i) {
                    double delta = peak - annotation.getPeptide().getbSpectrum()[i];
                    annotation.getPeptide().shiftBSpectrum(delta);
                    Annotation shifted = Annotation.annotate(annotation.getSpectrum(), annotation.getPeptide(), B);
                    optimalShifted = compareAnnotations(shifted, optimalShifted);
                }
            }
        }

        reverseMap.put(annotation, optimalShifted);
    }

    private Annotation compareAnnotations(Annotation newAnnotation, Annotation oldAnnotation) {
        int newIdx = 0;

        for (Map.Entry<Double, List<IonType>> entry : newAnnotation.getAnnotations().entrySet()) {
            if (notAnnotated.contains(entry.getKey()) && !entry.getValue().isEmpty()) {
                ++newIdx;
            }
        }

        int oldIdx = 0;

        for (Map.Entry<Double, List<IonType>> entry : oldAnnotation.getAnnotations().entrySet()) {
            if (notAnnotated.contains(entry.getKey()) && !entry.getValue().isEmpty()) {
                ++oldIdx;
            }
        }

        return newIdx >= oldIdx ? newAnnotation : oldAnnotation;
    }
}
