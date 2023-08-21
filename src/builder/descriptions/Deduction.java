package builder.descriptions;

import builder.Proof;

import java.util.Collections;
import java.util.List;

/**
 * Proof is built by deduction
 */
public class Deduction implements Description {
    public final Proof proof;

    public Deduction(final Proof proof) {
        this.proof = proof;
    }

    public Deduction(final List<Proof> proofs,
                     final int id) {
        this.proof = proofs.get(id);
    }

    @Override
    public String toString() {
        return "Ded. " + (proof.getId() + 1);
    }

    @Override
    public List<Proof> getLinks() {
        return Collections.singletonList(proof);
    }
}
