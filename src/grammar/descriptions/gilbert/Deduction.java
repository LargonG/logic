package grammar.descriptions.gilbert;

import grammar.proof.GProof;

import java.util.Collections;
import java.util.List;

/**
 * Proof is built by deduction
 */
public class Deduction extends GuilbertDescription {
    public final GProof link;
    private final int id;

    public Deduction(final List<GProof> proofs,
                     final int id) {
        super(GuilbertRule.DEDUCTION, proofs.get(id));
        this.link = proofs.get(id);
        this.id = id;
    }

    @Override
    public String toString() {
        return "Ded. " + (id + 1);
    }

    @Override
    public List<GProof> getLinks() {
        return Collections.singletonList(link);
    }
}
