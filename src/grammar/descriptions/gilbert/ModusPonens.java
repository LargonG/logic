package grammar.descriptions.gilbert;

import grammar.descriptions.Description;
import grammar.proof.GProof;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Proof is built by the rule "modus ponens"
 */
public class ModusPonens extends GuilbertDescription {
    public final GProof alpha;
    public final GProof alphaImplBetta;


    private final int alphaId;
    private final int alphaImplBettaId;

    public ModusPonens(final List<GProof> proofs,
                       final int alphaId,
                       final int alphaImplBettaId) {
        super(GuilbertRule.MODUS_PONENS, proofs.get(alphaId), proofs.get(alphaImplBettaId));
        this.alpha = proofs.get(alphaId);
        this.alphaImplBetta = proofs.get(alphaImplBettaId);
        this.alphaId = alphaId;
        this.alphaImplBettaId = alphaImplBettaId;
    }

    public ModusPonens(final GProof alpha,
                       final GProof alphaImplBetta) {
        super(GuilbertRule.MODUS_PONENS, alpha, alphaImplBetta);
        this.alpha = alpha;
        this.alphaImplBetta = alphaImplBetta;
        this.alphaId = -1;
        this.alphaImplBettaId = -1;
    }

    public ModusPonens(final List<GProof> proofs) {
        this(proofs.get(0), proofs.get(1));
        assert proofs.size() == 2;
    }

    @Override
    public String toString() {
        return "M.P. " + (alphaId + 1) + ", " + (alphaImplBettaId + 1);
    }
}
