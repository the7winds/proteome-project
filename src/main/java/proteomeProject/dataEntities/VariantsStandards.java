package proteomeProject.dataEntities;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by the7winds on 06.04.16.
 */
public class VariantsStandards {

    private static VariantsStandards INSTANCE;
    private final Map<Peptide, Peptide> varToStd = new HashMap<>();
    private final Map<Peptide, Map<Integer, Integer>> modification = new HashMap<>();

    private VariantsStandards(Path variantPath) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(variantPath.toFile()));
        while (br.ready()) {
            String varName = br.readLine();
            String varPeptide = br.readLine();
            String stdName = br.readLine();
            String stdPeptide = br.readLine();

            Peptide variant = new Peptide(varName, varPeptide);
            Peptide standard = new Peptide(stdName, stdPeptide);

            varToStd.put(variant, standard);
            modification.put(variant, getModificationsMap(varPeptide, stdPeptide));
        }
    }

    private static Map<Integer, Integer> getModificationsMap(String varPeptide, String stdPeptide) {
        int[][] minEdits = new int[stdPeptide.length()][varPeptide.length()];
        int[][] dist = new int[stdPeptide.length()][varPeptide.length()];
        boolean[][] back = new boolean[stdPeptide.length()][varPeptide.length()]; // true = diagonal, false = vertical

        {
            int m = -1;
            minEdits[0][0] = stdPeptide.charAt(0) == varPeptide.charAt(0) ? 0 : 1;
            if (minEdits[0][0] == 0) {
                m = 0;
                back[0][0] = true;
            }

            for (int i = 1; i < stdPeptide.length(); ++i) {
                if (m == -1) {
                    if (stdPeptide.charAt(i) == varPeptide.charAt(0)) {
                        m = i;
                        minEdits[i][0] = m;
                        back[i][0] = true;
                    } else {
                        minEdits[i][0] = 1;
                        dist[i][0] = i;
                    }
                } else {
                    minEdits[i][0] = m;
                    dist[i][0] = i - m;
                    back[i][0] = true;
                }
            }
        }

        for (int j, delta, i = 1; i < stdPeptide.length(); ++i) {
            for (j = 1; j < i && j < varPeptide.length() - (i < varPeptide.length() ? 1 : 0); ++j) {
                delta = stdPeptide.charAt(i) == varPeptide.charAt(j) ? 0 : 1;
                if (minEdits[i - 1][j - 1] + dist[i - 1][j - 1] + delta <= minEdits[i - 1][j]) {
                    minEdits[i][j] = minEdits[i - 1][j - 1] + dist[i - 1][j - 1] + delta;
                    back[i][j] = true;
                } else {
                    minEdits[i][j] = minEdits[i - 1][j];
                    dist[i][j] = dist[i - 1][j] + 1;
                    back[i][j] = false;
                }
            }
            if (i < varPeptide.length()) {
                delta = stdPeptide.charAt(i) == varPeptide.charAt(j) ? 0 : 1;
                minEdits[i][j] = minEdits[i - 1][j - 1] + delta;
                back[i][j] = true;
            }
        }

        Map<Integer, Integer> modificationsMap = new HashMap<>();
        for (int i = stdPeptide.length() - 1,
                j = varPeptide.length() - 1; i >= 0 && j >= 0;) {
            if (stdPeptide.charAt(i) == varPeptide.charAt(j)) {
                modificationsMap.put(j, i);
            }
            if (back[i][j]) {
                --i;
                --j;
            } else {
                --i;
            }
        }

        return modificationsMap;
    }

    public static void init(Path variantPath) throws IOException {
        if (INSTANCE == null) {
            INSTANCE = new VariantsStandards(variantPath);
        } else {
            throw new UnsupportedOperationException();
        }
    }

    public static VariantsStandards getInstance() {
        return INSTANCE;
    }

    public List<Peptide> getVariants() {
        return varToStd.keySet().stream().collect(Collectors.toList());
    }

    public Map<Peptide, Peptide> variantToStandard() {
        return varToStd;
    }

    public static boolean containsModifications(String tagString, Peptide variant) {
        int idx = variant.getPeptide().indexOf(tagString);
        Map<Integer, Integer> map = INSTANCE.modification.get(variant);

        for (int i = 0; i < tagString.length(); ++i) {
            if (map.get(idx + i) == null) {
                return true;
            }
        }

        return false;
    }
}
