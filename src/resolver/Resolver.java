package resolver;

import grammar.*;

import java.util.Map;

public class Resolver {
    /**
     * Сравнивает структуру выражений:
     * если оно одинаковое, то возвращает <code>true</code>,
     * иначе -- <code>false</code>
     * <br>
     * Пример: <br>
     * BinOp == BinaryOperator <br>
     * UnOp == UnaryOperator <br>
     * <br>
     * expression = BinOp(AND, Var(A), Var(B)) <br>
     * scheme = BinOp(AND, Scheme(a), Scheme(b)) <br>
     * returns true <br>
     * <br>
     * expression = BinOp(AND, Var(A), Var(B)) <br>
     * scheme = Scheme(a) <br>
     * returns true <br>
     * <br>
     * expression = BinOp(OR, Var(A), Var(B)) <br>
     * scheme = BinOp(AND, Var(A), Var(B)) <br>
     * returns false <br>
     *
     *
     * @param expression -- выражение, которое должно соответствовать определённой структуре
     * @param scheme -- выражение с эталонной структурой
     * @param memes -- scheme -> expression
     * @return true, if structure of <code>expression</code> is equals to <code>scheme</code> structure
     */
    public boolean resolve(Expression expression, Expression scheme, Map<String, Expression> memes) {
        if (expression instanceof BinaryOperator && scheme instanceof BinaryOperator) {
            BinaryOperator expr = (BinaryOperator) expression;
            BinaryOperator sh = (BinaryOperator) scheme;
            return expr.operator == sh.operator
                    && resolve(expr.left, sh.left, memes)
                    && resolve(expr.right, sh.right, memes);
        } else if (expression instanceof UnaryOperator && scheme instanceof UnaryOperator) {
            UnaryOperator expr = (UnaryOperator) expression;
            UnaryOperator sh = (UnaryOperator) scheme;
            return expr.operator == sh.operator
                    && resolve(expr.expr, sh.expr, memes);
        } else if (expression instanceof Variable && scheme instanceof Variable) {
            Variable expr = (Variable) expression;
            Variable sh = (Variable) scheme;
            return expr.name.equals(sh.name);
        } else if (expression instanceof Scheme || scheme instanceof Scheme) {
            if (expression instanceof Scheme) {
                Expression buffer = expression;
                expression = scheme;
                scheme = buffer;
            }
            Scheme sh = (Scheme) scheme;
            if (memes.containsKey(sh.name)) {
                return memes.get(sh.name).equals(expression);
            }
            memes.put(sh.name, expression);
            return true;
        }
        return false;
    }
}
