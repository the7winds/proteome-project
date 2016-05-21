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
    private final Map<String, Map<Integer, Integer>> modification = new HashMap<>();

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
            modification.put(variant.getName(), getModificationsMap(varPeptide, stdPeptide));
        }
    }

    private static Map<Integer, Integer> getModificationsMap(String varPeptide, String stdPeptide) {
        int[][] maxMatch = new int[stdPeptide.length()][varPeptide.length()];
        boolean[][] back = new boolean[stdPeptide.length()][varPeptide.length()]; // true = diagonal, false = vertical

        for (int matched = 0, i = 1; i < stdPeptide.length(); ++i) {
            if (stdPeptide.charAt(i) == varPeptide.charAt(0)) {
                matched = 1;
                back[i][0] = true;
            }
            maxMatch[i][0] = matched;
        }

        for (int j, matched, i = 1; i < stdPeptide.length(); ++i) {
            for (j = 1; j < i && j < varPeptide.length(); ++j) {
                matched = stdPeptide.charAt(i) == varPeptide.charAt(j) ? 1 : 0;
                if (maxMatch[i - 1][j - 1] + matched > maxMatch[i - 1][j]) {
                    maxMatch[i][j] = maxMatch[i - 1][j - 1] + matched;
                    back[i][j] = true;
                } else {
                    maxMatch[i][j] = maxMatch[i - 1][j];
                    back[i][j] = false;
                }
            }
            if (i < varPeptide.length()) {
                matched = stdPeptide.charAt(i) == varPeptide.charAt(j) ? 0 : 1;
                maxMatch[i][j] = maxMatch[i - 1][j - 1] + matched;
                back[i][j] = true;
            }
        }

        Map<Integer, Integer> modificationsMap = new HashMap<>();
        for (int i = stdPeptide.length() - 1, j = varPeptide.length() - 1; i >= 0 && j >= 0;) {
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
        Map<Integer, Integer> map = INSTANCE.modification.get(variant.getName());

        for (int i = 0; i < tagString.length(); ++i) {
            if (map.get(idx + i) == null) {
                return true;
            }
        }

        return false;
    }

    public Map<Integer, Integer> getModifications(Peptide variant) {
        return modification.get(variant.getName());
    }
}
