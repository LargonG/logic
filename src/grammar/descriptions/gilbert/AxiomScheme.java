package grammar.descriptions.gilbert;

/**
 * Proof is axiom (and what scheme is used (index of scheme))
 */
public class AxiomScheme extends GuilbertDescription {
    public final int id;

    public AxiomScheme(int id) {
        super(GuilbertRule.AXIOM);
        this.id = id;
    }

    @Override
    public String toString() {
        return "Ax. sch. " + (id + 1);
    }
}
