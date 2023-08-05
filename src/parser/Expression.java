package parser;

import java.util.Map;

public interface Expression {
    boolean calculate(Map<String, Boolean> values);
    String suffixString(Operator before);

    static Expression create(Operator operator, Expression left, Expression right) {
        if (operator == null && right == null) {
            return left;
        }
        if (operator.unary && (left == null || right == null)) {
            return new UnaryOperator(operator, left != null ? left : right);
        }
        if (right != null && !operator.unary) {
            return new BinaryOperator(operator, left, right);
        }
        throw new IllegalArgumentException("Incorrect arguments combination: " + operator + " "
                + left + " " + right);
    }
}
