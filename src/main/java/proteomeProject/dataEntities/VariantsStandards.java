package proteomeProject.dataEntities;

import java.io.*;
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
    private Map<Peptide, Peptide> varToStd = new HashMap<>();

    private VariantsStandards(Path variantPath) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(variantPath.toFile()));
        while (br.ready()) {
            String varName = br.readLine();
            String varPeptide = br.readLine();
            String stdName = br.readLine();
            String stdPeptide = br.readLine();

            varToStd.put(new Peptide(varName, varPeptide), new Peptide(stdName, stdPeptide));
        }
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

    public Map<Peptide, Peptide> getVarToStd() {
        return varToStd;
    }
}
