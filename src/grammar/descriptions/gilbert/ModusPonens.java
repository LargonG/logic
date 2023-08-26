package grammar.descriptions.gilbert;

import grammar.descriptions.Description;
import grammar.proof.GProof;

import java.util.Arrays;
import java.util.List;

/**
 * Proof is built by the rule "modus ponens"
 */
public class ModusPonens implements Description {
    public final GProof alpha;
    public final GProof alphaImplBetta;

    public ModusPonens(final List<GProof> proofs,
                       final int alphaId,
                       final int alphaImplBettaId) {
        this.alpha = proofs.get(alphaId);
        this.alphaImplBetta = proofs.get(alphaImplBettaId);
    }

    public ModusPonens(final GProof alpha,
                       final GProof alphaImplBetta) {
        this.alpha = alpha;
        this.alphaImplBetta = alphaImplBetta;
    }

    public ModusPonens(final List<GProof> proofs) {
        this(proofs.get(0), proofs.get(1));
        assert proofs.size() == 2;
    }

    @Override
    public String toString() {
        return "M.P. " + (alpha.getId() + 1) + ", " + (alphaImplBetta.getId() + 1);
    }

    @Override
    public List<GProof> getLinks() {
        return Arrays.asList(alpha, alphaImplBetta);
    }
}
