package resolver;

import parser.Expression;

import java.util.Set;

public class Hypothesis {
    public static boolean isHypothesis(Set<Exception> hypotheses, Expression expr) {
        return hypotheses.contains(expr);
    }
}
