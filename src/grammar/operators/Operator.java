package grammar.operators;

import grammar.proof.NProof;
import grammar.proof.Proof;

public enum Operator {
    IMPL(2, false, false, new Implication()),
    OR  (3, false, true, new Or()),
    AND (4, false, true, new And()),
    NOT (5, true, false, new Not());

    public final int priority;
    public final boolean unary;
    public final boolean leftAssoc;

    public final Bundle creator;

    Operator(int priority, boolean unary, boolean leftAssoc,
             final Bundle creator) {
        this.priority = priority;
        this.unary = unary;
        this.leftAssoc = leftAssoc;
        this.creator = creator;
    }


    public NProof createNProof(NProof left, NProof right, Proof what) {
        return creator.createNProof(left, right, what);
    }

    @Override
    public String toString() {
        switch (this) {
            case OR: return "|";
            case AND: return "&";
            case IMPL: return "->";
            case NOT: return "!";
            default: throw new RuntimeException("Unexpected Operator type" + this.name());
        }
    }
}
