package parser;

import grammar.Expression;
import grammar.Nil;
import grammar.Scheme;
import grammar.Variable;
import grammar.operators.Operator;
import grammar.predicates.BinaryPredicate;
import grammar.predicates.PredicateOperator;
import grammar.predicates.arithmetic.Arithmetic;
import grammar.predicates.arithmetic.ArithmeticOperator;
import grammar.predicates.arithmetic.Letter;
import grammar.predicates.quantifiers.Exists;
import grammar.predicates.quantifiers.ForAll;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ExpressionParser implements Parser<Expression> {
    private final static Set<Character> arithmeticSymbol = new HashSet<Character>() {{
        add('+');
        add('*');
        add('=');
    }};

    private final static Map<Character, ArithmeticOperator> toArithmeticOperator =
            new HashMap<Character, ArithmeticOperator>() {{
                put('+', ArithmeticOperator.PLUS);
                put('*', ArithmeticOperator.MUL);
            }};

    private final static Map<Character, Operator> toLogicOperator =
            new HashMap<Character, Operator>() {{
                put('-', Operator.IMPL);
                put('!', Operator.NOT);
                put('&', Operator.AND);
                put('|', Operator.OR);
            }};

    @Override
    public Expression parse(String expression) {
        return parseExpression(null, null, null, new StringIndexer(expression), false);
    }

    /**
     * @param oldOp оператор выражения "сверху" (выражения, для которого данное выражение является его "правой" частью)
     * @param op    оператор, который соединяет сформированное выражение и остаток
     * @param left  сформированное выражение
     * @param tail  остаток
     * @return expression
     */
    private Expression parseExpression(Operator oldOp, Operator op, Expression left,
                                       StringIndexer tail, boolean brackets) {
        int code;
        Expression right = null;
        while ((code = tail.read()) != -1) {
            char symbol = (char) code;

            // symbol in "()[Letter]{&,|,->,!}{\r,\t," "}"

            if (symbol == ')' && !brackets) {
                tail.back();
                break;
            }

            if (Character.isWhitespace(symbol)) {
                continue;
            }

            Expression expr = null;
            if (Character.isLetter(symbol) || Character.isDigit(symbol)) {
                expr = parseUnary(symbol, tail);
            } else if (symbol == '_') {
                expr = parseNil(symbol, tail);
            } else if (symbol == '?' || symbol == '@') {
                Letter letter = parseLetter(parseWord((char) tail.readSymbol(), tail));

                char dot = (char) tail.readSymbol();
                assert dot == '.';

                expr = parseExpression(null, null, null, tail, false);

                if (symbol == '?') {
                    expr = new Exists(letter, expr);
                } else {
                    expr = new ForAll(letter, expr);
                }
            }

            if (symbol == '(') {
                expr = parseExpression(null, null, null, tail, true);
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
            }

            // oldOp.prior <= op.prior
            assert symbol == ')' || newOp != null;
            if (symbol == ')' || oldOp != null
                    && (newOp.priority < oldOp.priority
                    || (newOp == oldOp && newOp.leftAssoc))) {
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
                right = parseExpression(op, newOp, right, tail, brackets);
            } else {
                left = Expression.create(op, left, right);
                op = newOp;
                right = null;
            }
        }

        return Expression.create(op, left, right);
    }

    private Arithmetic parseArithmetic(ArithmeticOperator oldOp,
                                       Arithmetic left, ArithmeticOperator op,
                                       StringIndexer tail, int brackets) {
        int code;
        Arithmetic right = null;
        while ((code = tail.readSymbol()) != -1) {
            char symbol = (char) code;

            // symbol in "()[Letter]{+,*}"

            if (toLogicOperator.containsKey(symbol) || symbol == ')' && brackets == 0
                    || symbol == '=') {
                tail.back();
                break;
            }

            Arithmetic expr = null;
            if (Character.isLetter(symbol) || Character.isDigit(symbol)) {
                expr = new Letter(parseWord(symbol, tail));
            } else if (symbol == '(') {
                expr = parseArithmetic(null, null, null, tail, brackets + 1);
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

            ArithmeticOperator newOp = null;
            switch (symbol) {
                case '+':
                    newOp = ArithmeticOperator.PLUS;
                    break;
                case '*':
                    newOp = ArithmeticOperator.MUL;
                    break;
            }

            // oldOp.prior <= op.prior
            assert symbol == ')' || newOp != null;
            if (symbol == ')' || oldOp != null &&
                    (newOp.priority < oldOp.priority || newOp == oldOp)) {
                if (!(symbol == ')' && oldOp == null)) tail.back();
                return Arithmetic.create(op, left, right);
            }

            if (op == null) {
                op = newOp;
            } else if (op.priority < newOp.priority) {
                right = parseArithmetic(op, right, newOp, tail, brackets);
            } else {
                left = Arithmetic.create(op, left, right);
                op = newOp;
                right = null;
            }
        }

        return Arithmetic.create(op, left, right);
    }

    private Expression parseUnary(char start, StringIndexer tail) {
        String word = parseWord(start, tail);
        int next = tail.readSymbol();
        if (next == -1) {
            return parseVariable(word);
        }

        if (arithmeticSymbol.contains((char) next)) {
            Arithmetic left = parseLetter(word);
            if (toArithmeticOperator.containsKey((char) next)) {
                left = parseArithmetic(null, left, toArithmeticOperator.get((char) next), tail, 0);
                next = tail.readSymbol();
            }
            Arithmetic right = parseArithmetic(null, null, null, tail, 0);
            return new BinaryPredicate(PredicateOperator.EQUALS, left, right);
        } else {
            tail.back();
            return parseVariable(word);
        }
    }

    private Expression parseVariable(String word) {
        if (Character.isUpperCase(word.charAt(0))) {
            return new Variable(word);
        }
        return new Scheme(word);
    }

    private Letter parseLetter(String word) {
        return new Letter(word);
    }

    private String parseWord(char start, StringIndexer tail) {
        StringBuilder builder = new StringBuilder().append(start);
        int code;
        while ((code = tail.read()) != -1) {
            if (Character.isLetter(code) || Character.isDigit(code) || code == 39) {
                builder.append((char) code);
            } else {
                tail.back();
                break;
            }
        }

        return builder.toString();
    }

    private Expression parseNil(char start, StringIndexer tail) {
        char middle = (char) tail.read();
        char end = (char) tail.read();
        assert start == '_' && middle == '|' && end == '_';
        return Nil.getInstance();
    }
}
