package proteomeProject.utils;

import javafx.util.Pair;
import proteomeProject.spectrumAnnotation.IonType;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import static proteomeProject.spectrumAnnotation.IonType.Type.B;
import static proteomeProject.spectrumAnnotation.IonType.Type.Y;
import static proteomeProject.utils.Chemicals.H2O;

/**
 * Created by the7winds on 06.04.16.
 */
public class Utils {
    public static List<Double> getPrefixes(String peptide, IonType.Type type) {
        List<Pair<Double, Boolean>> masses = new LinkedList<>();
        for (int i = 0; i < peptide.length();) {
            if (peptide.charAt(i) == '+') {
                int t;
                for (t = 0; !Character.isAlphabetic(peptide.charAt(i + t)); ++t);
                masses.add(new Pair<>(Double.valueOf(peptide.substring(i + 1, i + t)), false));
                i += t;
            } else {
                masses.add(new Pair<>(Chemicals.AminoAcid.getMass(peptide.charAt(i)), true));
                i++;
            }
        }

        LinkedList<Double> prefix = new LinkedList<>();
        prefix.add(0d);
        double d = 0;
        for (Pair<Double, Boolean> pair : masses) {
            if (pair.getValue()) {
                prefix.addLast(prefix.getLast() + pair.getKey() + d);
                d = 0;
            } else {
                d = pair.getKey();
            }
        }

        if (type == B) {
            return prefix;
        } else {
            LinkedList<Double> suffix = new LinkedList<>();
            double sum = prefix.getLast();
            suffix.addAll(prefix.stream().map(p -> sum - p).collect(Collectors.toList()));
            return suffix.stream()
                    .mapToDouble(a -> a + H2O.getMass())
                    .sorted()
                    .boxed()
                    .collect(Collectors.toList());
        }
    }

    public static double evalTotalMass(String peptide) {
        List<Double> pref = getPrefixes(peptide, Y);
        return pref.get(pref.size() - 1);
    }
}
