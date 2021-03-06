package proteomeProject.searchVariantPeptide;

import org.apache.commons.lang3.StringUtils;
import proteomeProject.dataEntities.IonType;
import proteomeProject.dataEntities.Tag;
import proteomeProject.utils.Chemicals;
import proteomeProject.utils.Utils;

/**
 * Created by the7winds on 23.03.16.
 */

/**
 * Container for one result
 */

public class SearchVariantPeptideResult {

    private final DBResult dbResult;
    private final Tag tag;
    private final IonType.Type type;
    private final int first;
    private final int last;
    private final double delta;

    public SearchVariantPeptideResult(DBResult dbResult,
                                      Tag tag,
                                      IonType.Type type) {
        this.dbResult = dbResult;
        this.tag = tag;
        this.type = type;

        String preTag;

        if (type == IonType.Type.B) {
            first = getPeptide().indexOf(tag.getTag());
            last = first + tag.getTag().length();
            preTag = getPeptide().substring(0, getPeptide().indexOf(tag.getTag()));
        } else {
            String reverseTag = StringUtils.reverse(tag.getTag());
            last = getPeptide().length() - getPeptide().indexOf(reverseTag);
            first = last - tag.getTag().length();
            preTag = getPeptide().substring(getPeptide().lastIndexOf(reverseTag) + reverseTag.length());
        }

        delta = getOffset() - (Utils.evalTotalMass(preTag) + (type == IonType.Type.Y ? Chemicals.H2O.getMass() : 0));
    }

    public SearchVariantPeptideResult(DBResult dbResult) {
        this.dbResult = dbResult;
        tag = null;
        type = IonType.Type.B;
        delta = 0;
        first = 0;
        last = 0;
    }

    public String getFilename() {
        return dbResult.getSpecFile() + "_msdeconv.msalign";
    }

    public int getScanNum() {
        return dbResult.getScanNum();
    }

    public String getPeptide() {
        return dbResult.getPeptide();
    }

    public double getEValue() {
        return dbResult.getEValue();
    }

    public Tag getTag() {
        return tag;
    }

    public double getOffset() {
        return tag.getOffset();
    }

    public double getDelta() {
        return delta;
    }

    public IonType.Type getType() {
        return type;
    }

    public String getProtein() {
        return dbResult.getProtein();
    }

    public int getFirst() {
        return first;
    }

    public int getLast() {
        return last;
    }
}
