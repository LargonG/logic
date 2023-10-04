package grammar.predicates.arithmetic;

public enum ArithmeticOperator {
    PLUS("+", 0),
    MUL("*", 1);

    public final String value;
    public final int priority;


    ArithmeticOperator(final String value,
                       final int priority) {
        this.value = value;
        this.priority = priority;
    }

    @Override
    public String toString() {
        return value;
    }
}
