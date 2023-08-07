package resolver;

import parser.*;

import java.util.*;

public final class Axioms {
    private static final Parser parser;

    public static final Set<Expression> values;

    private static final Resolver resolver;

    static {
        parser = new Parser();
        values = new LinkedHashSet<Expression>() {{
            add(parser.parse("a->b->a"));
            add(parser.parse("(a->b)->(a->b->c)->(a->c)"));
            add(parser.parse("a->b->a&b"));
            add(parser.parse("a&b->a"));
            add(parser.parse("a&b->b"));
            add(parser.parse("a->a|b"));
            add(parser.parse("b->a|b"));
            add(parser.parse("(a->y)->(b->y)->(a|b->y)"));
            add(parser.parse("(a->b)->(a->!b)->!a"));
            add(parser.parse("!!a->a"));
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
}
