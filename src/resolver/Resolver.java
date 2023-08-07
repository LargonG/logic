package resolver;

import parser.*;

import java.util.Map;

public class Resolver {
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
