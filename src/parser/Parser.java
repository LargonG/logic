package parser;

import grammar.Expression;
import grammar.Operator;
import grammar.Scheme;
import grammar.Variable;

public class Parser {
    public Expression parse(String expression) {
        return parseExpression(null, null, null, new StringIndexer(expression));
    }

    /**
     *
     * @param oldOp оператор выражения "сверху" (выражения, для которого данное выражение является его "правой" частью)
     * @param op оператор, который соединяет сформированное выражение и остаток
     * @param left сформированное выражение
     * @param tail остаток
     * @return expression
     */
    private Expression parseExpression(Operator oldOp, Operator op, Expression left, StringIndexer tail) {
        int code;
        Expression right = null;
        while ((code = tail.read()) != -1) {
            char symbol = (char) code;

            // symbol in "()[Letter]{&,|,->,!}{\r,\t," "}"

            if (Character.isWhitespace(symbol)) {
                continue;
            }

            Expression expr = null;
            if (Character.isLetter(symbol)) {
                expr = parseVariable(symbol, tail);
            }

            if (symbol == '(') {
                expr = parseExpression(null, null, null, tail);
            }

            if (expr != null) {
                // left == null || right == null (op ==/!= null)
                if (left == null) {
                    left = expr;
                } else {
                    right = expr;
                }
                continue;
            }

            Operator newOp = null;
            switch (symbol) {
                case '&': newOp = Operator.AND; break;
                case '|': newOp = Operator.OR; break;
                case '-': newOp = Operator.IMPL; break;
                case '!': newOp = Operator.NOT; break;
            }

            // oldOp.prior <= op.prior
            assert symbol == ')' || newOp != null;
            if (symbol == ')' || oldOp != null && (newOp.priority < oldOp.priority || (newOp == oldOp && newOp.leftAssoc))) {
                if (!(symbol == ')' && oldOp == null)) tail.back();
                return Expression.create(op, left, right);
            }

            // impl == "->"
            if (newOp == Operator.IMPL) {
                tail.read();
            }

            if (op == null) {
                op = newOp;
            } else if (op.priority < newOp.priority || (op == newOp && !op.leftAssoc)) {
                right = parseExpression(op, newOp, right, tail);
            } else {
                left = Expression.create(op, left, right);
                op = newOp;
                right = null;
            }
        }

        return Expression.create(op, left, right);
    }

    private Expression parseVariable(char start, StringIndexer tail) {
        StringBuilder builder = new StringBuilder().append(start);
        int code;
        while ((code = tail.read()) != -1) {
            char symbol = (char) code;
            if (Character.isLetter(symbol) || Character.isDigit(symbol) || symbol == (char) 39) {
                builder.append(symbol);
            } else {
                tail.back();
                break;
            }
        }

        if (Character.isUpperCase(start)) {
            return new Variable(builder.toString());
        }
        return new Scheme(builder.toString());
    }
}
