package grammar.descriptions.gilbert;

import grammar.descriptions.Description;

/**
 * Proof is hypothesis
 */
public class Hypothesis extends GuilbertDescription {
    public final int id;

    public Hypothesis(int id) {
        super(GuilbertRule.HYPOTHESIS);
        this.id = id;
    }

    public Hypothesis() {
        super(GuilbertRule.HYPOTHESIS);
        this.id = -1;
    }

    @Override
    public String toString() {
        return "Hyp. " + (id + 1);
    }
}
