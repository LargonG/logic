package builder.descriptions;

import builder.Proof;

import java.util.Collections;
import java.util.List;

public interface Description {
    default List<Proof> getLinks() {
        return Collections.emptyList();
    }
}
