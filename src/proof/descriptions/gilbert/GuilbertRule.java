package proof.descriptions.gilbert;

import proof.descriptions.Rule;
import proof.descriptions.RuleChecker;

public enum GuilbertRule implements Rule {
    AXIOM("Sch. Ax.", 0),
    HYPOTHESIS("Hyp.", 0),
    MODUS_PONENS("M.P.", 2),
    FORALL("F.", 1),
    EXISTS("E.", 1),
    INCORRECT("Incorrect", 0),
    DEDUCTION("Ded.", 1);

    public final String value;
    public final int count;

    GuilbertRule(final String value,
                 final int count) {
        this.value = value;
        this.count = count;
    }

    @Override
    public int getCount() {
        return count;
    }

    @Override
    public RuleChecker getChecker() {
        return null;
    }

    @Override
    public String toString() {
        return value;
    }
}
