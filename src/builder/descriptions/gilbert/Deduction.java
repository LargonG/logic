package builder.descriptions.gilbert;

import builder.descriptions.Description;
import builder.proof.GProof;
import builder.proof.MetaProof;

import java.util.Collections;
import java.util.List;

/**
 * Proof is built by deduction
 */
public class Deduction implements Description {
    public final GProof link;

    public Deduction(final GProof link) {
        this.link = link;
    }

    public Deduction(final List<GProof> proofs,
                     final int id) {
        this.link = proofs.get(id);
    }

    @Override
    public String toString() {
        return "Ded. " + (link.getId() + 1);
    }

    @Override
    public List<GProof> getLinks() {
        return Collections.singletonList(link);
    }
}
