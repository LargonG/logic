package builder;

import builder.descriptions.Description;
import parser.Expression;

import java.util.List;
import java.util.Map;

public class Proof {
    public final Expression expression;
    public final List<Expression> contextList;
    public final Map<Expression, Integer> context;

    public final Expression deductionExpression;
    public final Map<Expression, Integer> deductionContext;

    public final int id;

    public Description description;

    private String expressionString;

    public Proof(Expression expr,
                 List<Expression> contextList,
                 Map<Expression, Integer> context,
                 Expression dedExpr,
                 Map<Expression, Integer> dedContext,
                 int id,
                 Description description,
                 String expressionString) {
        this.expression = expr;
        this.contextList = contextList;
        this.context = context;
        this.deductionExpression = dedExpr;
        this.deductionContext = dedContext;
        this.id = id;
        this.description = description;
        this.expressionString = expressionString;
    }

    private String getExpressionString() {
        if (expressionString == null) {
            StringBuilder builder = new StringBuilder();
            for (int i = 0; i < contextList.size(); i++) {
                builder.append(contextList.get(i).suffixString(null));
                if (i + 1 < contextList.size()) {
                    builder.append(",");
                }
            }
            builder.append("|-").append(expression.suffixString(null));
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
