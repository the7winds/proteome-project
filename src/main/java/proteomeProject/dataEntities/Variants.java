package proteomeProject.dataEntities;

import java.io.*;
import java.nio.file.Path;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by the7winds on 06.04.16.
 */
public class Variants {

    private static Variants INSTANCE;
    private List<Variant> variants = new LinkedList<>();


    public Variants(Path variantPath) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(variantPath.toFile()));
        while (br.ready()) {
            String name = br.readLine().substring(1);
            String peptide = br.readLine();
            variants.add(new Variant(name, peptide));
        }
    }


    public static void init(Path variantPath) throws IOException {
        if (INSTANCE == null) {
            INSTANCE = new Variants(variantPath);
        } else {
            throw new UnsupportedOperationException();
        }
    }


    public static class Variant {
        private final String name;
        private final Peptide peptide;

        public Variant(String name, String peptide) {
            this.name = name;
            this.peptide = new Peptide(peptide);
        }

        public String getName() {
            return name;
        }

        public Peptide getPeptide() {
            return peptide;
        }
    }


    public static Variants getInstance() {
        return INSTANCE;
    }

    public List<Variant> getVariants() {
        return variants;
    }
}
