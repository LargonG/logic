package grammar;

import grammar.operators.Operator;
import grammar.proof.NProof;
import grammar.proof.context.ImmutableContext;
import parser.ExpressionParser;

import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class Scheme implements Expression {
    private static final ExpressionParser EXPRESSION_PARSER = new ExpressionParser();

    public final String name;

    public Scheme(String name) {
        this.name = name;
    }

    @Override
    public boolean calculate(Map<String, Boolean> values) {
        throw new UnsupportedOperationException("Cannot calculate scheme");
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public String suffixString(Operator before, boolean brackets) {
        return name;
    }

    @Override
    public void getVariablesNames(Set<String> result) {
        // do nothing
    }

    @Override
    public Expression paste(Map<String, Expression> values) {
        return values.get(name);
    }

    @Override
    public Expression toNormalForm() {
        return this;
    }

    @Override
    public NProof createNProof(ImmutableContext immutableContext) {
        throw new UnsupportedOperationException("Scheme cannot create natural proof");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Scheme scheme = (Scheme) o;
        return Objects.equals(name, scheme.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    @Override
    public int compareTo(Expression o) {
        if (o instanceof Scheme) {
            Scheme sh = (Scheme) o;
            return name.compareTo(sh.name);
        }
        return 1;
    }

    public static Expression create(String expression, Map<String, Expression> values) {
        return EXPRESSION_PARSER.parse(expression).paste(values);
    }
}
