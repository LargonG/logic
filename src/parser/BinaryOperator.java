package parser;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
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
    public String suffixString(Operator before) {
        if (before != null && before.priority >= operator.priority) {
            return "(" + left.suffixString(operator) + operator + right.suffixString(operator) + ")";
        }
        return left.suffixString(operator) + operator + right.suffixString(operator);
    }

    @Override
    public Expression paste(Map<String, Expression> values) {
        return new BinaryOperator(operator, left.paste(values), right.paste(values));
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
