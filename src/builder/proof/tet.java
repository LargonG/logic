//package builder.proof;
//
//import builder.descriptions.*;
//import grammar.Expression;
//import grammar.operators.Operator;
//import grammar.Scheme;
//
//import java.util.*;
//import java.util.stream.Collector;
//import java.util.stream.Collectors;
//import java.util.stream.Stream;
//
///**
// * Proof, that equals to: <br>
// * [id] {context} |- {expression} [description]
// */
//public class Proof {
//    // ... |- {expression}
//    public final Expression expression;
//    // ... |- ... [description]
//    public Description description;
//
//    private final List<Expression> contextList;
//    private final Map<Expression, Integer> context;
//
//    private Expression deductionExpression;
//    private Map<Expression, Integer> deductionContext;
//
//    private int id;
//    private String expressionString;
//
//    private final Map<Expression, Integer> pushContext = new HashMap<>();
//
//    private final static Collector<Expression, ?, Map<Expression, Integer>> toCounter = Collectors.toMap(
//                expr -> expr,
//                expr -> 1,
//                Integer::sum
//        );
//
//    /**
//     * Full Proof mode: expression, context, description (can be modified)
//     * deduction (expression && context)
//     * @param expression main part of the proof
//     * @param context list of hypotheses
//     * @param id id in proofs list
//     * @param description how it was created
//     */
//    public Proof(final Expression expression,
//                 final List<Expression> context,
//                 final int id,
//                 final Description description) {
//        this.expression = expression;
//        this.contextList = context;
//        this.id = id;
//        this.description = description;
//
//        this.context = context.stream().collect(toCounter);
//    }
//
//    public Proof(final Expression expression,
//                 final List<Expression> context,
//                 final int id) {
//        this(expression, context, id, null);
//    }
//
//    public Proof(final Expression expression,
//                 final List<Expression> context,
//                 final Description description) {
//        this(expression, context, -1, description);
//    }
//
//    public Proof(final Expression expression,
//                 final List<Expression> context,
//                 final Description description,
//                 final Map<Expression, Integer> pushContext) {
//        this(expression, context, -1, description);
//        this.pushContext.putAll(pushContext);
//    }
//
//    /**
//     * Short Proof mode: only expression & description (no context, no id, no deduction)
//     * @param expression expression
//     * @param description cannot be "<code>* instanceof Deduction</code>"
//     */
//    public Proof(final Expression expression,
//                 final Description description) {
//        this.expression = expression;
//        this.description = description;
//
//        this.context = null;
//        this.contextList = null;
//
//        this.deductionExpression = null;
//        this.deductionContext = null;
//
//        this.id = -1;
//    }
//
//
//
//    public Proof deductionLeft(Expression alpha) {
//        assert !(description instanceof Deduction);
//        List<Expression> impl = Expression.separate(expression, Operator.IMPL, 1);
//        return new Proof(
//                impl.get(1),
//                new ModusPonens(
//                        new Proof(alpha, new Hypothesis()),
//                        this
//                )
//        );
//    }
//
//    public Proof deductionRight(Map<Expression, Integer> context, Expression alpha) {
//        Map<String, Expression> mp = new HashMap<String, Expression>() {{
//            put("a", alpha);
//            put("b", expression);
//        }};
//        if (description instanceof AxiomScheme || context.getOrDefault(expression, 0) > 0) {
//            return new Proof(
//                    Scheme.create("a->b", mp),
//                    new ModusPonens(
//                            this,
//                            new Proof(
//                                    Scheme.create("b->a->b", mp),
//                                    new AxiomScheme(0)
//                            )
//                    )
//            );
//        } else if (alpha.equals(expression)) {
//            return new Proof(
//                    Scheme.create("a->a", mp),
//                    new ModusPonens(
//                            new Proof(
//                                    Scheme.create("a->(a->a)->a", mp),
//                                    new AxiomScheme(0)),
//                            new Proof(
//                                    Scheme.create("(a->(a->a)->a)->(a->a)", mp),
//                                    new ModusPonens(
//                                            new Proof(
//                                                    Scheme.create("a->a->a", mp),
//                                                    new AxiomScheme(0)),
//                                            new Proof(
//                                                    Scheme.create("(a->a->a)->(a->(a->a)->a)->(a->a)", mp),
//                                                    new AxiomScheme(1))
//                                    )
//                            )
//                    )
//            );
//        } else {
//            assert description instanceof ModusPonens;
//
//            ModusPonens modusPonens = (ModusPonens) description;
//
//            Proof updModus = modusPonens.alpha.deductionRight(context, alpha);
//            Proof updPonens = modusPonens.alphaImplBetta.deductionRight(context, alpha);
//
//            mp.put("d", updModus.expression);
//            mp.put("f", updPonens.expression);
//
//            return new Proof(
//                    Scheme.create("a->b", mp),
//                    new ModusPonens(
//                            updPonens,
//                            new Proof(
//                                    Scheme.create("f->(a->b)", mp),
//                                    new ModusPonens(
//                                            updModus,
//                                            new Proof(
//                                                    Scheme.create("d->f->(a->b)", mp),
//                                                    new AxiomScheme(1)
//                                            )
//                                    )
//                            )
//                    )
//            );
//        }
//    }
//
//    public void getExpressionList(final List<Expression> result,
//                                  final Set<Expression> used) {
//        List<Proof> links = description.getLinks();
//        for (Proof link: links) {
//            if (!used.contains(link.expression)) {
//                link.getExpressionList(result, used);
//            }
//        }
//
//        if (!used.contains(expression)) {
//            used.add(expression);
//            result.add(expression);
//        }
//    }
//
//
//    public int getId() {
//        return id;
//    }
//
//    public List<Expression> getContextList() {
//        return contextList;
//    }
//
//    public Map<Expression, Integer> getContext() {
//        return context;
//    }
//
//    public Expression getDeductionExpression() {
//        calculateDeduction();
//        return deductionExpression;
//    }
//
//    public Map<Expression, Integer> getDeductionContext() {
//        calculateDeduction();
//        return deductionContext;
//    }
//
//    public String getExpressionString() {
//        if (expressionString == null) {
//            expressionString = (contextList != null ?
//                    contextList.stream()
//                            .map(Expression::suffixString)
//                            .reduce((left, right) -> left + "," + right)
//                            .orElse("")
//                    : "") + "|-" + expression.suffixString();
//        }
//        return expressionString;
//    }
//
//    @Override
//    public String toString() {
//        return metaExpression(getExpressionString(), id + 1, description);
//    }
//
//    public static String metaExpression(String expression, int id, Description description) {
//        return "[" + id + "] " + expression + " [" + description + "]";
//    }
//
//    private void calculateDeduction() {
//        assert context != null;
//        assert contextList != null;
//
//        if (deductionExpression == null || deductionContext == null) {
//            List<Expression> ded = Expression.separate(expression, Operator.IMPL);
//            deductionExpression = ded.get(ded.size() - 1);
//            deductionContext = Stream.concat(
//                    ded.subList(0, ded.size() - 1).stream(),
//                    contextList.stream()).collect(toCounter);
//        }
//    }
//
//    public static List<Expression> createContextList(Map<Expression, Integer> context) {
//        return context.entrySet()
//                .stream()
//                .map(entry -> Collections.nCopies(entry.getValue(), entry.getKey()))
//                .reduce((left, right) -> { left.addAll(right); return left; })
//                .orElseThrow(() -> new RuntimeException("Cannot create n copies of proof in context"));
//    }
//}
