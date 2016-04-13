package proteomeProject.searchVariantPeptide;

import org.apache.commons.lang3.StringUtils;
import proteomeProject.ContributionWrapper.Tag;
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
    private final boolean reverse;
    private final double delta;

    public SearchVariantPeptideResult(DBResult dbResult,
                                      Tag tag,
                                      boolean reverse) {
        this.dbResult = dbResult;
        this.tag = tag;
        this.reverse = reverse;

        String preTag;

        if (!reverse) {
            preTag = getPeptide().substring(0, getPeptide().indexOf(getTag()));
        } else {
            String reverseTag = StringUtils.reverse(getTag());
            preTag = getPeptide().substring(getPeptide().lastIndexOf(reverseTag) + reverseTag.length());
        }

        delta = getOffset() - (Utils.evalTotalMass(preTag) + (reverse ? Chemicals.H2O.getMass() : 0));
    }

    public SearchVariantPeptideResult(DBResult dbResult) {
        this.dbResult = dbResult;
        tag = null;
        reverse = false;
        delta = 0;
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

    public String getTag() {
        return tag.getTag();
    }

    public double getOffset() {
        return tag.getOffset();
    }

    public double getDelta() {
        return delta;
    }

    public boolean isReverse() {
        return reverse;
    }

    public String getProtein() {
        return dbResult.getProtein();
    }
}
