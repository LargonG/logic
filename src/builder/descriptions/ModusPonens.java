package builder.descriptions;

import builder.Proof;

import java.util.Arrays;
import java.util.List;

public class ModusPonens implements Description {
    public final Proof alpha;
    public final Proof alphaImplBetta;

    public ModusPonens(final List<Proof> proofs,
                       final int alphaId,
                       final int alphaImplBettaId) {
        this.alpha = proofs.get(alphaId);
        this.alphaImplBetta = proofs.get(alphaImplBettaId);
    }

    public ModusPonens(final Proof alpha,
                       final Proof alphaImplBetta) {
        this.alpha = alpha;
        this.alphaImplBetta = alphaImplBetta;
    }

    @Override
    public String toString() {
        return "M.P. " + (alpha.getId() + 1) + ", " + (alphaImplBetta.getId() + 1);
    }

    @Override
    public List<Proof> getLinks() {
        return Arrays.asList(alpha, alphaImplBetta);
    }
}
