package builder.descriptions;

import builder.Proof;

import java.util.Collections;
import java.util.List;

/**
 * Description for the proof
 */
public interface Description {
    default List<Proof> getLinks() {
        return Collections.emptyList();
    }
}
