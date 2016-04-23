package proteomeProject.dataEntities;

import proteomeProject.utils.Chemicals;

/**
 * Created by the7winds on 30.03.16.
 */
public class IonType {

    public enum Type {
        B,
        Y
    }

    private final Chemicals[] defect;
    private final Type type;
    private final int num;

    public IonType(Chemicals[] defect, Type type, int num) {
        this.defect = defect;
        this.type = type;
        this.num = num;
    }

    public Chemicals[] getDefect() {
        return defect;
    }

    public Type getType() {
        return type;
    }

    @Override
    public String toString() {
        String ionStr = type.name() + num;
        if (defect != null) {
            for (Chemicals chemical : defect) {
                ionStr += " - " + chemical.name();
            }
        }
        return ionStr;
    }

    public int getNum() {
        return num;
    }
}
