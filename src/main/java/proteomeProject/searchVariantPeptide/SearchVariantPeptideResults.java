package proteomeProject.searchVariantPeptide;

import java.util.Collection;

/**
 * Created by the7winds on 23.03.16.
 */

/**
 *  Class stores results of the searching.
 */

public class SearchVariantPeptideResults {

    private final Collection<SearchVariantPeptideResult> tagFoundResults;
    private final Collection<SearchVariantPeptideResult> tagNotFoundResults;
    private final Collection<SearchVariantPeptideResult> TagNotExistsResults;

    public SearchVariantPeptideResults(Collection<SearchVariantPeptideResult> tagFoundResults,
                                       Collection<SearchVariantPeptideResult> tagNotFoundResults,
                                       Collection<SearchVariantPeptideResult> tagNotExistsResults) {
        this.tagFoundResults = tagFoundResults;
        this.tagNotFoundResults = tagNotFoundResults;
        this.TagNotExistsResults = tagNotExistsResults;
    }

    public Collection<SearchVariantPeptideResult> getTagFoundResults() {
        return tagFoundResults;
    }

    public Collection<SearchVariantPeptideResult> getTagNotFoundResults() {
        return tagNotFoundResults;
    }

    public Collection<SearchVariantPeptideResult> getTagNotExistsResults() {
        return TagNotExistsResults;
    }
}
