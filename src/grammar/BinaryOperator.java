package grammar;

import grammar.proof.Context;
import grammar.proof.NProof;
import grammar.proof.Proof;
import grammar.operators.Operator;

import java.util.*;
import java.util.function.BiFunction;

public class BinaryOperator implements Expression {
    public final Operator operator;
    public final Expression left;
    public final Expression right;

    public BinaryOperator(Operator operator, Expression left, Expression right) {
        this.operator = operator;
        this.left = left;
        this.right = right;
    }

    @Override
    public String toString() {
        return "(" + operator.toString() + "," + left.toString() + "," + right.toString() + ")";
    }

    @Override
    public boolean calculate(Map<String, Boolean> values) {
        Map<Operator, BiFunction<Boolean, Boolean, Boolean>> mapping = new HashMap<Operator, BiFunction<Boolean, Boolean, Boolean>>()
        {{
            put(Operator.OR, (left, right) -> left || right);
            put(Operator.AND, (left, right) -> left && right);
            put(Operator.IMPL, (left, right) -> !left || right);
        }};
        return mapping.get(operator).apply(left.calculate(values), right.calculate(values));
    }

    @Override
    public String suffixString(Operator before, boolean brackets) {
        if (before != null && (before.priority > operator.priority || before == operator && brackets)) {
            return "(" + left.suffixString(operator, !operator.leftAssoc)
                    + operator
                    + right.suffixString(operator, operator.leftAssoc) + ")";
        }
        return left.suffixString(operator, !operator.leftAssoc)
                + operator
                + right.suffixString(operator, operator.leftAssoc);
    }

    @Override
    public void getVariablesNames(Set<String> result) {
        left.getVariablesNames(result);
        right.getVariablesNames(result);
    }

    @Override
    public Expression paste(Map<String, Expression> values) {
        return new BinaryOperator(operator, left.paste(values), right.paste(values));
    }

    @Override
    public NProof createNProof(Context context) {
        NProof left = this.left.createNProof(context);
        NProof right = this.right.createNProof(context);
        return operator.createNProof(left, right, new Proof(this, context));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BinaryOperator that = (BinaryOperator) o;
        return operator == that.operator && Objects.equals(left, that.left) && Objects.equals(right, that.right);
    }

    @Override
    public int hashCode() {
        return Objects.hash(operator, left, right);
    }

    @Override
    public int compareTo(Expression o) {
        if (o instanceof Scheme)
            return -1;
        if (o instanceof BinaryOperator) {
            BinaryOperator bin = (BinaryOperator) o;
            int result = operator.compareTo(bin.operator);

            if (result == 0) {
                result = left.compareTo(bin.left);
            }

            if (result == 0) {
                result = right.compareTo(bin.right);
            }

            return result;
        }
        return 1;
    }
}
