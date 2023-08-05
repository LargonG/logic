package parser;

public class Parser {
    public Expression parse(String expression) {
        return parseExpression(null, null, null, new StringIndexer(expression));
    }

    private Expression parseExpression(Operator oldOp, Operator op, Expression left, StringIndexer cursor) {
        int code;
        Expression right = null;
        while ((code = cursor.read()) != -1) {
            char symbol = (char) code;
            if (Character.isWhitespace(symbol)) {
                continue;
            }

            if (symbol == ')') {
                if (oldOp != null) cursor.back();
                return Expression.create(op, left, right);
            }

            if (symbol == '(') {
                Expression expr = parseExpression(null, null, null, cursor);
                if (left == null) {
                    left = expr;
                } else {
                    right = expr;
                }
                continue;
            }

            if (Character.isLetter(symbol)) {
                cursor.back();
                Expression expr = parseVariable(cursor);
                if (left == null) {
                    left = expr;
                } else {
                    right = expr;
                }
                continue;
            }

            Operator newOp;
            switch (symbol) {
                case '&':
                    newOp = Operator.AND;
                    break;
                case '|':
                    newOp = Operator.OR;
                    break;
                case '-':
                    newOp = Operator.IMPL;
                    break;
                case '!':
                    newOp = Operator.NOT;
                    break;
                default:
                    throw new RuntimeException("Parse exception: " + symbol + " " + cursor.position());
            }

            if (newOp == Operator.IMPL) {
                cursor.read();
            }

            if (newOp.unary) {
                if (left == null) {
                    left = Expression.create(newOp, parseUnary(cursor), null);
                } else {
                    right = Expression.create(newOp, right, parseUnary(cursor));
                }
                continue;
            }

            if (oldOp != null && (newOp.priority < oldOp.priority || newOp == oldOp && newOp.leftAssoc)) {
                cursor.back();
                if (newOp == Operator.IMPL) cursor.back();
                return Expression.create(op, left, right);
            }

            if (op == null) {
                op = newOp;
            } else if (newOp.priority > op.priority || newOp == op && !op.leftAssoc) {
                right = parseExpression(op, newOp, right, cursor);
            } else {
                left = Expression.create(op, left, right);
                op = newOp;
                right = null;
            }
        }

        return Expression.create(op, left, right);
    }

    private Expression parseUnary(StringIndexer cursor) {
        int code;
        while ((code = cursor.read()) != -1) {
            char symbol = (char) code;
            if (Character.isWhitespace(symbol)) {
                continue;
            }

            if (symbol == '(') {
                return parseExpression(null, null, null, cursor);
            }
            if (symbol == '!') {
                return Expression.create(Operator.NOT, parseUnary(cursor),null);
            }
            if (Character.isLetter(symbol)) {
                cursor.back();
                return parseVariable(cursor);
            }

            throw new IllegalStateException("Unexpected value: " + symbol + " " + cursor.position());
        }
        throw new RuntimeException("Parse error");
    }

    private Expression parseVariable(StringIndexer cursor) {
        int start = cursor.position();
        int code;
        while ((code = cursor.read()) != -1) {
            char symbol = (char) code;
            if (!(cursor.position() - start > 1 && (Character.isLetter(symbol) || Character.isDigit(symbol) ||
                    symbol == (char) 39)
            || Character.isLetter(symbol))) {
                cursor.back();
                break;
            }
        }
        String name = cursor.substring(start, cursor.position());
        return new Variable(name);
    }
}
