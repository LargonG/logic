package grammar.proof;

import grammar.Expression;
import grammar.operators.Operator;

import java.util.List;
import java.util.Objects;

public class Proof {
    protected final Expression expression;
    protected final Context context;

    protected Proof fullContextForm = null;

    /**
     * Создаёт конструкцию Proof, которая на бумаге выглядит вот так: <br>
     * {context} |- {expression} [{description}]
     * @param expression -- выражение, что доказывается с этим контекстом
     * @param context -- контекст, список выражений, которые мы считаем за истину
     */
    public Proof(final Expression expression,
                    final Context context) {
        this.expression = expression;
        this.context = context;
    }

    public Proof getFullContextForm() {
        if (fullContextForm == null) {
            List<Expression> sep = Expression.separate(expression, Operator.IMPL);
            fullContextForm = new Proof(
                    sep.get(sep.size() - 1),
                    new Context(context, sep.subList(0, sep.size() - 1)));
        }
        return fullContextForm;
    }

    /**
     * Выражение, что выводится доказательством
     * @return выражение
     */
    public final Expression getExpression() {
        return expression;
    }

    /**
     * Список выражений, что приняли за истину,
     * называется контекстом
     * @return контекст (список гипотез)
     */
    public final Context getContext() {
        return context;
    }

    /**
     * Сравнивает два Proof по их выражению справа и
     * по, с точностью до перестановки, контекстам слева <br>
     * Т.е. сравнивает по {context} |- {expression} <br>
     * Если <code>o.getClass() != this.getClass()</code>, то возвращает false
     * @param o the reference object with which to compare.
     * @return true, если равны их expression & context
     */
    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Proof proof = (Proof) o;
        return Objects.equals(expression, proof.expression) && Objects.equals(context, proof.context);
    }

    @Override
    public final int hashCode() {
        return Objects.hash(expression, context);
    }

    @Override
    public String toString() {
        return context + "|-" + expression.suffixString();
    }
}
