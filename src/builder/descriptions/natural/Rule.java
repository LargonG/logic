package builder.descriptions.natural;

public enum Rule {
    AXIOM("Ax", 0),
    MODUS_PONENS("E->", 2),
    DEDUCTION("I->", 1),
    AND_COMPOSITION("I&", 2),
    AND_DECOMPOSITION_LEFT("El&", 1),
    AND_DECOMPOSITION_RIGHT("Er&", 1),
    OR_COMPOSITION_LEFT("Il|", 1),
    OR_COMPOSITION_RIGHT("Ir|", 1),
    EXCLUDED_MIDDLE_RULE("E|", 3),
    NOT("E!!", 1)
    ;

    private final String value;
    private final int count;

    Rule(final String value,
         final int count) {
        this.value = value;
        this.count = count;
    }

    @Override
    public String toString() {
        return value;
    }

    public int getCount() {
        return count;
    }
}