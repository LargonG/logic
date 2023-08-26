package builder.descriptions.gilbert;

import builder.descriptions.Description;

/**
 * Proof is hypothesis
 */
public class Hypothesis implements Description {
    public final int id;

    public Hypothesis(int id) {
        this.id = id;
    }

    public Hypothesis() {
        this.id = -1;
    }

    @Override
    public String toString() {
        return "Hyp. " + (id + 1);
    }
}
