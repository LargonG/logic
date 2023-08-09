package builder;

import builder.descriptions.Description;
import parser.Expression;
import parser.Operator;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Proof {
    public final Expression expression;
    public final List<Expression> contextList;
    public final Map<Expression, Integer> context;

    public final Expression deductionExpression;
    public final Map<Expression, Integer> deductionContext;

    public final int id;

    public Description description;

    private String expressionString;

    public Proof(final Expression expr,
                 final List<Expression> context,
                 final int id) {
        this.expression = expr;
        this.contextList = context;
        this.id = id;

        this.context = context.stream().collect(
                Collectors.toMap(
                        expression -> expression,
                        expression -> 1,
                        Integer::sum));

        List<Expression> ded = Expression.separate(expr, Operator.IMPL);
        this.deductionExpression = ded.get(ded.size() - 1);
        this.deductionContext = Stream.concat(
                ded.subList(0, ded.size() - 1).stream(),
                context.stream()).collect(
                        Collectors.toMap(
                            expression -> expression,
                            expression -> 1,
                            Integer::sum));
    }

    public String getExpressionString() {
        if (expressionString == null) {
            StringBuilder builder = new StringBuilder();
            for (int i = 0; i < contextList.size(); i++) {
                builder.append(contextList.get(i).suffixString());
                if (i + 1 < contextList.size()) {
                    builder.append(",");
                }
            }
            builder.append("|-").append(expression.suffixString());
            expressionString = builder.toString();
        }
        return expressionString;
    }

    @Override
    public String toString() {
        return metaExpression(getExpressionString(), id + 1, description);
    }

    public static String metaExpression(String expression, int id, Description description) {
        return "[" + id + "] " + expression + " [" + description + "]";
    }
}
