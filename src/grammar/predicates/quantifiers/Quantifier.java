package grammar.predicates.quantifiers;

import grammar.Expression;
import grammar.operators.Operator;
import grammar.predicates.arithmetic.Letter;
import proof.NProof;
import proof.context.ImmutableContext;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public abstract class Quantifier implements Expression {
    public final String sign;
    public final Letter letter;
    public final Expression expression;

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

    @Override
    public boolean canRenameLetter(String oldName, String newName) {
        Set<String> inside = new HashSet<>();
        expression.getLettersNames(inside);
        return !inside.contains(oldName) || !letter.getName().equals(newName);
    }
}
