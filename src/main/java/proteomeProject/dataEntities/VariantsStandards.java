package proteomeProject.dataEntities;

import proteomeProject.utils.Chemicals;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by the7winds on 06.04.16.
 */
public class VariantsStandards {

    private static VariantsStandards INSTANCE;

    private final List<Peptide> variants = new LinkedList<>();
    private final Map<String, Peptide> varToStd = new HashMap<>();
    private final Map<String, Map<Integer, MapAmino>> varToModifications = new HashMap<>();

    private VariantsStandards(Path variantPath) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(variantPath.toFile()));
        while (br.ready()) {
            String varName = br.readLine();
            String varPeptide = br.readLine();
            String stdName = br.readLine();
            String stdPeptide = br.readLine();

            Peptide variant = new Peptide(varName, varPeptide);
            Peptide standard = new Peptide(stdName, stdPeptide);

            variants.add(variant);
            varToStd.put(variant.getName(), standard);
            varToModifications.put(variant.getName(), getModificationsMap(varPeptide, stdPeptide));
        }
    }

    private static Map<Integer, MapAmino> getModificationsMap(String varPeptide, String stdPeptide) {
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

        Map<Integer, MapAmino> modificationsMap = new HashMap<>();
        for (int i = stdPeptide.length() - 1, j = varPeptide.length() - 1; i >= 0 && j >= 0;) {
            modificationsMap.put(j, new MapAmino(stdPeptide.charAt(i) != varPeptide.charAt(j), Chemicals.AminoAcid.valueOf(stdPeptide.charAt(i)), i));

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
        return variants;
    }

    public Peptide getStandard(String varName) {
        return varToStd.get(varName);
    }

    public static boolean containsModifications(String tagString, Peptide variant) {
        int idx = variant.getPeptide().indexOf(tagString);
        Map<Integer, MapAmino> map = INSTANCE.varToModifications.get(variant.getName());

        for (int i = 0; i < tagString.length(); ++i) {
            if (map.get(idx + i).isModification()) {
                return true;
            }
        }

        return false;
    }

    public Map<Integer, MapAmino> getModifications(Peptide variant) {
        return varToModifications.get(variant.getName());
    }

    public static class MapAmino {

        private final boolean modification;
        private final Chemicals.AminoAcid aminoAcid;
        private final int idx;

        public MapAmino(boolean modification, Chemicals.AminoAcid aminoAcid, int idx) {
            this.modification = modification;
            this.aminoAcid = aminoAcid;
            this.idx = idx;
        }

        public boolean isModification() {
            return modification;
        }

        public Chemicals.AminoAcid getAminoAcid() {
            return aminoAcid;
        }

        public int getIdx() {
            return idx;
        }
    }
}
