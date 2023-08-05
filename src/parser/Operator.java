package parser;

public enum Operator {
    IMPL(2, false, false),
    OR  (3, false, true),
    AND (4, false, true),
    NOT (5, true, false);

    public final int priority;
    public final boolean unary;
    public final boolean leftAssoc;

    Operator(int priority, boolean unary, boolean leftAssoc) {
        this.priority = priority;
        this.unary = unary;
        this.leftAssoc = leftAssoc;
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
