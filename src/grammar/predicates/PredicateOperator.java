package grammar.predicates;

public enum PredicateOperator {
    EQUALS("=");

    private final String value;

    PredicateOperator(final String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return value;
    }
}
