package proteomeProject.annotation;

import proteomeProject.dataEntities.IonType;
import proteomeProject.dataEntities.Peptide;
import proteomeProject.dataEntities.Spectrum;
import proteomeProject.dataEntities.Tag;
import proteomeProject.utils.Chemicals;

import java.util.*;

import static proteomeProject.dataEntities.IonType.Type.B;
import static proteomeProject.dataEntities.IonType.Type.Y;
import static proteomeProject.utils.Chemicals.H2O;
import static proteomeProject.utils.Chemicals.NH3;

/**
 * Created by the7winds on 13.04.16.
 */
public class Annotation {

    private static final Double EPS = 0.02;

    private Tag tag;
    private Peptide peptide;
    private Spectrum spectrum;
    private IonType.Type type;
    private int first;
    private int last;
    private SortedMap<Double, List<IonType>> annotations = new TreeMap<>();

    private Annotation() {

    }

    public void annotatePeak(double peak, IonType ionType) {
        annotations.putIfAbsent(peak, new LinkedList<>());
        if (ionType != null) {
            annotations.get(peak).add(ionType);
        }
    }

    public Tag getTag() {
        return tag;
    }

    public Spectrum getSpectrum() {
        return spectrum;
    }

    public IonType.Type getType() {
        return type;
    }

    public int getFirst() {
        return first;
    }

    public int getLast() {
        return last;
    }

    public SortedMap<Double, List<IonType>> getAnnotations() {
        return annotations;
    }

    public Peptide getPeptide() {
        return peptide;
    }

    public static Annotation annotate(Spectrum spectrum, Peptide peptide) {
        Annotation annotation = new Annotation();
        annotation.peptide = peptide;
        annotation.spectrum = spectrum;
        annotation.makeAnnotation(B);
        annotation.makeAnnotation(Y);
        return annotation;
    }

    public static Annotation annotate(Spectrum spectrum, Peptide peptide, IonType.Type type) {
        Annotation annotation = new Annotation();
        annotation.peptide = peptide;
        annotation.spectrum = spectrum;
        annotation.makeAnnotation(type);
        return annotation;
    }

    public static Annotation annotate(Spectrum spectrum, Peptide peptide, Tag tag) {
        Annotation annotation = new Annotation();
        annotation.peptide = peptide;
        annotation.spectrum = spectrum;
        annotation.tag = tag;
        annotation.makeAnnotation(B);
        annotation.makeAnnotation(Y);
        return annotation;
    }

    public static Annotation annotate(Spectrum spectrum, Peptide peptide, Tag tag, IonType.Type type, int first, int last) {
        Annotation annotation = new Annotation();
        annotation.peptide = peptide;
        annotation.spectrum = spectrum;
        annotation.tag = tag;
        annotation.type = type;
        annotation.first = first;
        annotation.last = last;
        annotation.makeAnnotation(type);
        return annotation;
    }

    private void makeAnnotation(IonType.Type type) {
        SortedSet<Double> peaks = spectrum.getPeaks();
        double[] theorSpec = (type == B ? peptide.getShiftedBSpectrum() : peptide.getShiftedYSpectrum());

        for (double peak : peaks) {
            for (int i = 0; i < theorSpec.length; ++i) {
                if (Math.abs(theorSpec[i] - peak) < EPS) {
                    annotatePeak(peak, new IonType(null, type, i));
                } else if (i > 0) {
                    if (Math.abs(peak - (theorSpec[i] - H2O.getMass())) < EPS) {
                        annotatePeak(peak, new IonType(new Chemicals[] { H2O }, type, i));
                    } else if (Math.abs(peak - (theorSpec[i] - NH3.getMass())) < EPS) {
                        annotatePeak(peak, new IonType(new Chemicals[] { NH3 }, type, i));
                    } else if (Math.abs(peak - (theorSpec[i] - NH3.getMass() - H2O.getMass())) < EPS) {
                        annotatePeak(peak, new IonType(new Chemicals[] { H2O, NH3 }, type, i));
                    } else if (Math.abs(peak - (theorSpec[i] - 2 * H2O.getMass())) < EPS) {
                        annotatePeak(peak, new IonType(new Chemicals[] { H2O, H2O }, type, i));
                    }
                } else {
                    annotatePeak(peak, null);
                }
            }
        }
    }
}
