package parser;

import java.util.Map;

public class UnaryOperator implements Expression {
    private final Operator operator;
    private final Expression expr;

    public UnaryOperator(Operator operator, Expression expr) {
        this.operator = operator;
        this.expr = expr;
    }

    @Override
    public String toString() {
        return "(" + operator.toString() + expr.toString() + ")";
    }

    @Override
    public boolean calculate(Map<String, Boolean> values) {
        switch (operator) {
            case NOT:
                return !expr.calculate(values);
            default:
                throw new IllegalStateException("Unexpected unary operator");
        }
    }

    @Override
    public String suffixString(Operator before) {
        return operator.toString() + expr.suffixString(this.operator);
    }
}
