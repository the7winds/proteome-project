package proteomeProject.report.txt;

import proteomeProject.dataEntities.IonType;
import proteomeProject.dataEntities.Peptide;
import proteomeProject.dataEntities.Tag;
import proteomeProject.utils.ProjectPaths;

import java.io.FileNotFoundException;
import java.io.PrintStream;

/**
 * Created by the7winds on 20.04.16.
 */
public class ModificationInTag {

    private static final String Modification = "modification";
    private static final ModificationInTag INSTANCE = new ModificationInTag();

    private PrintStream output;

    private ModificationInTag() {
        try {
            output = new PrintStream(ProjectPaths.getOutput().resolve(Modification).toFile());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static ModificationInTag getInstance() {
        return INSTANCE;
    }

    public synchronized void print(Peptide variant, Peptide standard, Tag tag, IonType.Type type) {
        output.printf("%s=%s\n", variant.getName(), variant.getPeptide());
        output.printf("%s=%s\n", standard.getName(), standard.getPeptide());
        output.printf("TAG=%s\tTYPE=%s\n", tag.getTag(), type.name());
        output.println();
    }
}
