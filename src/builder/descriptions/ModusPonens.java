package builder.descriptions;

import builder.descriptions.Description;

public class ModusPonens implements Description {
    public final int alpha;
    public final int alphaImplBetta;

    public ModusPonens(int alpha, int alphaImplBetta) {
        this.alpha = alpha;
        this.alphaImplBetta = alphaImplBetta;
    }

    @Override
    public String toString() {
        return "M.P. " + (alpha + 1) + ", " + (alphaImplBetta + 1);
    }
}
