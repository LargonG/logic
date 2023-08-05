package parser;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;

public class BinaryOperator implements Expression {
    private final Operator operator;
    private final Expression left;
    private final Expression right;

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
        if (before.priority >= operator.priority) {
            return "(" + left.suffixString(operator) + operator + right.suffixString(operator) + ")";
        }
        return left.suffixString(operator) + operator + right.suffixString(operator);
    }
}
