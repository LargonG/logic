package resolver;

import parser.Expression;
import parser.Parser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class Axioms {
    private static final Parser parser;

    public static final List<Expression> values;

    private static final Resolver resolver;

    static {
        parser = new Parser();
        values = new ArrayList<Expression>() {{
            add(parser.parse("a->b->a"));                   // 0
            add(parser.parse("(a->b)->(a->b->c)->(a->c)")); // 1
            add(parser.parse("a->b->a&b"));                 // 2
            add(parser.parse("a&b->a"));                    // 3
            add(parser.parse("a&b->b"));                    // 4
            add(parser.parse("a->a|b"));                    // 5
            add(parser.parse("b->a|b"));                    // 6
            add(parser.parse("(a->y)->(b->y)->(a|b->y)"));  // 7
            add(parser.parse("(a->b)->(a->!b)->!a"));       // 8
            add(parser.parse("!!a->a"));                    // 9
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

    public static Expression createByAxiom(int axiomId, Map<String, Expression> substitution) {
        return values.get(axiomId).paste(substitution);
    }
}
