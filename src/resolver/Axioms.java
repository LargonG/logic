package resolver;

import grammar.Expression;
import grammar.operators.Operator;
import grammar.predicates.quantifiers.Quantifier;
import parser.ExpressionParser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class Axioms {
    private static final ExpressionParser EXPRESSION_PARSER;

    public static final List<Expression> values;

    private static final Resolver resolver;

    static {
        EXPRESSION_PARSER = new ExpressionParser();
        values = new ArrayList<Expression>() {{
            add(EXPRESSION_PARSER.parse("a->b->a"));                   // 0
            add(EXPRESSION_PARSER.parse("(a->b)->(a->b->c)->(a->c)")); // 1
            add(EXPRESSION_PARSER.parse("a->b->a&b"));                 // 2
            add(EXPRESSION_PARSER.parse("a&b->a"));                    // 3
            add(EXPRESSION_PARSER.parse("a&b->b"));                    // 4
            add(EXPRESSION_PARSER.parse("a->a|b"));                    // 5
            add(EXPRESSION_PARSER.parse("b->a|b"));                    // 6
            add(EXPRESSION_PARSER.parse("(a->y)->(b->y)->(a|b->y)"));  // 7
            add(EXPRESSION_PARSER.parse("(a->b)->(a->!b)->!a"));       // 8
            add(EXPRESSION_PARSER.parse("!!a->a"));                    // 9
        }};

        resolver = new Resolver();
    }

    private Axioms() {

    }

    public static int isAxiom(Expression expression) {
        int i = 0;
        for (Expression axiom: values) {
            Map<String, Expression> memes = new HashMap<>();
            if (resolver.resolve(expression, axiom, memes)) {
                return i;
            }
            i++;
        }

        return -1;
    }

    public static int isPredicateAxiom(Expression expression, String newName) {
        int i = isAxiom(expression);
        if (i == -1) {
            i = values.size();
            List<Expression> sep = Expression.separate(expression, Operator.IMPL, 1);
            if (sep.size() > 1) {
                Expression left = sep.get(0);
                Expression right = sep.get(1);

                if (left instanceof Quantifier) {
                    // (@x.a) -> a[x:=y]
                    Quantifier leftQ = (Quantifier) left;
                    if (leftQ.expression.canRenameLetter(leftQ.letter.getName(), newName)
                            && leftQ.expression.renameLetter(leftQ.letter.getName(), newName).equals(right)) {
                        i = values.size();
                    }
                }

                if (right instanceof Quantifier) {
                    // a[x:=y] -> ?x.a
                    Quantifier rightQ = (Quantifier) right;
                    if (rightQ.expression.canRenameLetter(rightQ.letter.getName(), newName)
                            && rightQ.expression.renameLetter(rightQ.letter.getName(), newName).equals(left)) {
                        i = values.size() + 1;
                    }
                }
            }
        }

        return i;
    }
}
