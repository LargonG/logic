package grammar;

import grammar.descriptions.natural.NaturalDescription;
import grammar.operators.Operator;
import grammar.proof.Context;
import grammar.proof.NProof;

import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class Variable implements Expression {
    public final String name;

    public Variable(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public boolean calculate(Map<String, Boolean> values) {
        return values.get(name);
    }

    @Override
    public String suffixString(Operator before, boolean brackets) {
        return name;
    }

    @Override
    public void getVariablesNames(Set<String> result) {
        result.add(name);
    }

    @Override
    public Expression paste(Map<String, Expression> values) {
        return this;
    }

    @Override
    public Expression toNormalForm() {
        return this;
    }

    @Override
    public NProof createNProof(Context context) {
        Expression expr = this;
        if (!context.contains(expr)) {
            Expression newExpr = Expression.create(Operator.IMPL, this, Nil.getInstance());
            assert context.contains(newExpr);
            expr = newExpr;
        }
        return new NProof(expr, context, new NaturalDescription());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Variable variable = (Variable) o;
        return Objects.equals(name, variable.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    @Override
    public int compareTo(Expression o) {
        if (o instanceof Variable) {
            Variable value = (Variable) o;
            return name.compareTo(value.name);
        }
        return -1;
    }
}
