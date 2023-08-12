package builder;

import builder.descriptions.*;
import parser.Expression;
import parser.Operator;
import parser.Scheme;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Proof {
    public final Expression expression;
    private final List<Expression> contextList;
    private final Map<Expression, Integer> context;

    private Expression deductionExpression;
    private Map<Expression, Integer> deductionContext;

    private final int id;

    public Description description;

    private String expressionString;

    private final static Collector<Expression, ?, Map<Expression, Integer>> toCounter = Collectors.toMap(
                expr -> expr,
                expr -> 1,
                Integer::sum
        );

    /**
     * Full Proof mode: expression, context, description (can be modified)
     * deduction (expression && context)
     * @param expression main part of the proof
     * @param context list of hypotheses
     * @param id id in proofs list
     */
    public Proof(final Expression expression,
                 final List<Expression> context,
                 final int id) {
        this.expression = expression;
        this.contextList = context;
        this.id = id;

        this.context = context.stream().collect(toCounter);
    }

    /**
     * Short Proof mode: only expression & description (no context, no id, no deduction)
     * @param expression expression
     * @param description cannot be "<code>* instanceof Deduction</code>"
     */
    public Proof(final Expression expression,
                 final Description description) {
        this.expression = expression;
        this.description = description;

        this.context = null;
        this.contextList = null;

        this.deductionExpression = null;
        this.deductionContext = null;

        this.id = -1;
    }

    public Proof deductionLeft(Expression alpha) {
        assert !(description instanceof Deduction);
        List<Expression> impl = Expression.separate(expression, Operator.IMPL, 1);
        return new Proof(
                impl.get(1),
                new ModusPonens(
                        new Proof(alpha, new Hypothesis()),
                        this
                )
        );
    }

    public Proof deductionRight(Map<Expression, Integer> context, Expression alpha) {
        Map<String, Expression> mp = new HashMap<String, Expression>() {{
            put("a", alpha);
            put("b", expression);
        }};

        if (description instanceof AxiomScheme || context.getOrDefault(expression, 0) > 0) {
            //
            // d -> a -> d
            // d
            // a -> d
            assert description instanceof AxiomScheme || description instanceof Hypothesis;
            return new Proof(
                    Scheme.create("a->b", mp), // b
                    new ModusPonens(
                            this, // a
                            new Proof(Scheme.create("b->a->b", mp), new AxiomScheme(0)) // a -> b
                    )
            );
        } else if (alpha.equals(expression)) {
            return new Proof(
                    Scheme.create("a->a", mp),
                    new ModusPonens(
                            new Proof(
                                    Scheme.create("a->(a->a)->a", mp),
                                    new AxiomScheme(0)),
                            new Proof(
                                    Scheme.create("(a->(a->a)->a)->a->a", mp),
                                    new ModusPonens(
                                            new Proof(
                                                    Scheme.create("a->a->a", mp),
                                                    new AxiomScheme(0)),
                                            new Proof(
                                                    Scheme.create("(a->a->a)->(a->(a->a)->a)->a->a", mp),
                                                    new AxiomScheme(1))
                                    ))
                    )
            );
        } else {
            // Modus Ponens
            assert description instanceof ModusPonens;
            ModusPonens modusPonens = (ModusPonens) description;
            Proof updModusAlpha = modusPonens.alpha.deductionRight(context, alpha);
            Proof updModusAlphaImplBetta = modusPonens.alphaImplBetta.deductionRight(context, alpha);

            // (a -> d) -> (a -> d -> b) -> (a -> b)
            // a -> (d)
            // a -> (d -> b)
            mp.put("d", updModusAlpha.expression);
            mp.put("f", updModusAlphaImplBetta.expression);
            return new Proof(
                    Scheme.create("a->b", mp),
                    new ModusPonens(
                            updModusAlphaImplBetta,
                            new Proof(
                                    Scheme.create("f->(a->b)", mp),
                                    new ModusPonens(
                                            updModusAlpha,
                                            new Proof(
                                                    Scheme.create("d->f->(a->b)", mp),
                                                    new AxiomScheme(1)
                                            )
                                    )
                            )
                    )
            );
        }
    }

    public void getExpressionList(final List<Expression> result) {
        List<Proof> links = description.getLinks();
        for (Proof link: links) {
            link.getExpressionList(result);
        }
        result.add(expression);
    }


    public int getId() {
        return id;
    }

    public List<Expression> getContextList() {
        return contextList;
    }

    public Map<Expression, Integer> getContext() {
        return context;
    }

    public Expression getDeductionExpression() {
        calculateDeduction();
        return deductionExpression;
    }

    public Map<Expression, Integer> getDeductionContext() {
        calculateDeduction();
        return deductionContext;
    }

    public String getExpressionString() {
        if (expressionString == null) {
            expressionString = (contextList != null ?
                    contextList.stream()
                            .map(Expression::suffixString)
                            .reduce((left, right) -> left + "," + right)
                            .orElse("")
                    : "") + "|-" + expression.suffixString();
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

    private void calculateDeduction() {
        assert context != null;
        assert contextList != null;

        if (deductionExpression == null || deductionContext == null) {
            List<Expression> ded = Expression.separate(expression, Operator.IMPL);
            deductionExpression = ded.get(ded.size() - 1);
            deductionContext = Stream.concat(
                    ded.subList(0, ded.size() - 1).stream(),
                    contextList.stream()).collect(toCounter);
        }
    }
}
