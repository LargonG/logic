package grammar;

import builder.proof.Context;
import builder.proof.NProof;
import grammar.operators.Operator;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class UnaryOperator implements Expression {
    public final Operator operator;
    public final Expression expr;

    public UnaryOperator(Operator operator, Expression expr) {
        this.operator = operator;
        this.expr = expr;
    }

    @Override
    public String toString() {
        return "(" + operator.toString() + expr.toString() + ")";
    }

    @Override
    public boolean calculate(Map<String, Boolean> values) {
        switch (operator) {
            case NOT:
                return !expr.calculate(values);
            default:
                throw new IllegalStateException("Unexpected unary operator");
        }
    }

    @Override
    public String suffixString(Operator before, boolean brackets) {
        return operator.toString() + expr.suffixString(this.operator, true);
    }

    @Override
    public void getVariablesNames(Set<String> result) {
        expr.getVariablesNames(result);
    }

    @Override
    public Expression paste(Map<String, Expression> values) {
        return new UnaryOperator(operator, expr.paste(values));
    }

    @Override
    public NProof createNProof(Context context) {
        throw new UnsupportedOperationException("Unary operators are not supported");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UnaryOperator that = (UnaryOperator) o;
        return operator == that.operator && Objects.equals(expr, that.expr);
    }

    @Override
    public int hashCode() {
        return Objects.hash(operator, expr);
    }

    @Override
    public int compareTo(Expression o) {
        if (o instanceof Scheme || o instanceof BinaryOperator) {
            return -1;
        } else if (o instanceof UnaryOperator) {
            UnaryOperator unary = (UnaryOperator) o;
            int result = operator.compareTo(unary.operator);
            if (result == 0) {
                result = expr.compareTo(unary.expr);
            }
            return result;
        }
        return 1;
    }
}
