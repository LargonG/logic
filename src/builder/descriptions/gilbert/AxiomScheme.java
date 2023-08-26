package builder.descriptions.gilbert;

import builder.descriptions.Description;

/**
 * Proof is axiom (and what scheme is used (index of scheme))
 */
public class AxiomScheme implements Description {
    public final int id;

    public AxiomScheme(int id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "Ax. sch. " + (id + 1);
    }
}
