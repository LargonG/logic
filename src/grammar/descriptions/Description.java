package grammar.descriptions;

import grammar.proof.MetaProof;

import java.util.Collections;
import java.util.List;

/**
 * Description for the proof
 */
public interface Description extends Cloneable {
    default <T extends MetaProof> List<T> getLinks() {
        return Collections.emptyList();
    }
}
