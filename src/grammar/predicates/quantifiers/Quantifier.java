package grammar.predicates.quantifiers;

import grammar.Expression;
import grammar.operators.Operator;
import grammar.predicates.arithmetic.Letter;
import grammar.proof.NProof;
import grammar.proof.context.ImmutableContext;

import java.util.Map;
import java.util.Set;

public abstract class Quantifier implements Expression {
    protected final String sign;
    protected final Letter letter;
    protected final Expression expression;

    protected Quantifier(final Letter letter,
                         final Expression expression,
                         final String sign) {
        this.letter = letter;
        this.expression = expression;
        this.sign = sign;
    }

    @Override
    public boolean calculate(Map<String, Boolean> values) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public NProof createNProof(ImmutableContext context) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public String suffixString(Operator before, boolean brackets) {
        if (before != null) {
            return "(" + sign + letter + "." + expression.suffixString() + ")";
        }
        return sign + letter + "." + expression.suffixString();
    }

    @Override
    public String toString() {
        return "(" + sign + letter + "," + expression.toString() + ")";
    }

    @Override
    public void getVariablesNames(Set<String> result) {
        expression.getVariablesNames(result);
    }

    @Override
    public void getLettersNames(Set<String> result) {
        expression.getLettersNames(result);
        letter.getLettersNames(result);
    }

    @Override
    public void getLetters(Set<Letter> letters) {
        expression.getLetters(letters);
        letters.add(letter);
    }
}
