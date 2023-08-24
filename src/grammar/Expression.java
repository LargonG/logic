package grammar;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public interface Expression extends Comparable<Expression> {
    boolean calculate(Map<String, Boolean> values);
    Expression paste(Map<String, Expression> values);
    String suffixString(Operator before, boolean brackets);

    default String suffixString() {
        return suffixString(null, false);
    }



    static Expression create(Operator operator, Expression left, Expression right) {
        if (operator == null) {
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

    static List<Expression> separate(Expression expr, Operator by) {
        return separate(expr, by, 0, false);
    }

    static List<Expression> separate(Expression expr, Operator by, int maxDeep) {
        return separate(expr, by, maxDeep, true);
    }

    static List<Expression> separate(Expression expr, Operator by, int maxDeep, boolean useDeep) {
        List<Expression> result = new ArrayList<>();
        while ((!useDeep || maxDeep-- > 0) && expr instanceof BinaryOperator) {
            BinaryOperator operation = (BinaryOperator) expr;
            if (operation.operator != by) {
                break;
            }
            result.add(by.leftAssoc ? operation.right : operation.left);
            expr = by.leftAssoc ? operation.left : operation.right;
        }
        result.add(expr);

        return result;
    }


}
