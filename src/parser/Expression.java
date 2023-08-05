package parser;

import java.util.Map;

public interface Expression {
    boolean calculate(Map<String, Boolean> values);
    String suffixString(Operator before);

    static Expression create(Operator operator, Expression left, Expression right) {
        if (operator == null) {
            assert left != null || right != null;
            assert left == null || right == null;
            return left != null ? left : right;
        }

        if (operator.unary) {
            assert left != null || right != null;
            assert left == null || right == null;
            return new UnaryOperator(operator, left != null ? left : right);
        }

        assert left != null && right != null;
        return new BinaryOperator(operator, left, right);
    }
}
