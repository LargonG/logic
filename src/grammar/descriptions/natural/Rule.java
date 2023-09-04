package grammar.descriptions.natural;

import grammar.descriptions.natural.checker.*;

public enum Rule {
    AXIOM("Ax", 0, new AxiomChecker()),
    MODUS_PONENS("E->", 2, new ModusPonensChecker()),
    DEDUCTION("I->", 1, new DeductionChecker()),
    AND_COMPOSITION("I&", 2, new AndCompositionChecker()),
    AND_DECOMPOSITION_LEFT("El&", 1, new AndDecompositionLeftChecker()),
    AND_DECOMPOSITION_RIGHT("Er&", 1, new AndDecompositionRightChecker()),
    OR_COMPOSITION_LEFT("Il|", 1, new OrCompositionLeftChecker()),
    OR_COMPOSITION_RIGHT("Ir|", 1, new OrCompositionRightChecker()),
    EXCLUDED_MIDDLE_RULE("E|", 3, new ExcludedMiddleChecker()),
    NOT("E!!", 1, new NotChecker()),
    COMMENT("HELP", 2, new ModusPonensChecker())
    ;

    private final String value;
    private final int count;

    private final RuleChecker checker;

    Rule(final String value,
         final int count,
         final RuleChecker checker) {
        this.value = value;
        this.count = count;
        this.checker = checker;
    }

    @Override
    public String toString() {
        return value;
    }

    public int getCount() {
        return count;
    }

    public RuleChecker getChecker() {
        return checker;
    }
}