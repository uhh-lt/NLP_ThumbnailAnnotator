package nlp.floschne.thumbnailAnnotator.core.domain;

import java.util.ArrayList;
import java.util.Collections;

public class ThumbnailUrlList extends ArrayList<ThumbnailUrl> {

    @Override
    public boolean add(ThumbnailUrl thumbnailUrl) {
        boolean result = super.add(thumbnailUrl);
        if (result)
            Collections.sort(this);
        return result;
    }

    public void sort() {
        Collections.sort(this);
    }
}
