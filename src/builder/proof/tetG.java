//package builder.proof;
//
//import builder.descriptions.*;
//import grammar.Expression;
//import grammar.Scheme;
//import grammar.operators.Operator;
//
//import java.util.*;
//import java.util.stream.Collectors;
//
//public class GProof extends Proof {
//    private static class DeductionSteps {
//        private final List<Expression> left;
//        private final List<Expression> right;
//        private final Expression subExpression;
//
//        private DeductionSteps(final List<Expression> left,
//                               final List<Expression> right,
//                               final Expression subExpression) {
//            this.left = left;
//            this.right = right;
//            this.subExpression = subExpression;
//        }
//    }
//
//    private GProof fullContextForm;
//
//    public GProof(final Expression expression,
//                  final Context context,
//                  final Description description) {
//        super(expression, context, description);
//    }
//
//    public GProof(final Expression expression,
//                  final Context context) {
//        super(expression, context);
//    }
//
//    @Override
//    protected void getProofsTree(final List<IProof> result) {
//        getProofsTree(result, new HashSet<>());
//    }
//
//
//    private void getProofsTree(final List<IProof> result,
//                              final Set<Proof> used) {
//        final List<GProof> links =
//                description.getLinks().stream()
//                .map(proof -> (GProof) proof)
//                .collect(Collectors.toList());
//        for (final GProof link: links) {
//            if (!used.contains(link)) {
//                link.getProofsTree(result, used);
//            }
//        }
//
//        if (!used.contains(this)) {
//            used.add(this);
//            result.add(new IProof(this, result.size()));
//        }
//    }
//
//    public GProof getFullContextForm() {
//        if (fullContextForm == null) {
//            List<Expression> sep = Expression.separate(expression, Operator.IMPL);
//            fullContextForm = new GProof(
//                    sep.get(sep.size() - 1),
//                    new Context(context, sep.subList(0, sep.size() - 1)),
//                    new Deduction(this));
//        }
//        return fullContextForm;
//    }
//
//    public GProof deductionLeft() {
//        List<Expression> impl = Expression.separate(expression, Operator.IMPL, 1);
//
//        Expression alpha = impl.get(0);
//        Expression betta = impl.get(1);
//
//        Context newContext = context.add(alpha);
//
//        return new GProof(
//                betta,
//                newContext,
//                new ModusPonens(
//                        new GProof(alpha, newContext, new Hypothesis()),
//                        this
//                )
//        );
//    }
//
//    public GProof deductionRight(Expression alpha) {
//        Context newContext = context.remove(alpha);
//        Map<String, Expression> mp = new HashMap<String, Expression>() {{
//            put("a", alpha);
//            put("b", expression);
//        }};
//        if (description instanceof AxiomScheme || newContext.getMap().getOrDefault(expression, 0) > 0) {
//            return new GProof(
//                    Scheme.create("a->b", mp),
//                    newContext,
//                    new ModusPonens(
//                            this,
//                            new GProof(
//                                    Scheme.create("b->a->b", mp),
//                                    newContext,
//                                    new AxiomScheme(0)
//                            )
//                    )
//            );
//        } else if (alpha.equals(expression)) {
//            return new GProof(
//                    Scheme.create("a->a", mp),
//                    newContext,
//                    new ModusPonens(
//                            new GProof(
//                                    Scheme.create("a->(a->a)->a", mp),
//                                    newContext,
//                                    new AxiomScheme(0)),
//                            new GProof(
//                                    Scheme.create("(a->(a->a)->a)->(a->a)", mp),
//                                    newContext,
//                                    new ModusPonens(
//                                            new GProof(
//                                                    Scheme.create("a->a->a", mp),
//                                                    newContext,
//                                                    new AxiomScheme(0)),
//                                            new GProof(
//                                                    Scheme.create("(a->a->a)->(a->(a->a)->a)->(a->a)", mp),
//                                                    newContext,
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
//            GProof updModus = modusPonens.alpha.deductionRight(alpha);
//            GProof updPonens = modusPonens.alphaImplBetta.deductionRight(alpha);
//
//            mp.put("d", updModus.expression);
//            mp.put("f", updPonens.expression);
//
//            return new GProof(
//                    Scheme.create("a->b", mp),
//                    newContext,
//                    new ModusPonens(
//                            updPonens,
//                            new GProof(
//                                    Scheme.create("f->(a->b)", mp),
//                                    newContext,
//                                    new ModusPonens(
//                                            updModus,
//                                            new GProof(
//                                                    Scheme.create("d->f->(a->b)", mp),
//                                                    newContext,
//                                                    new AxiomScheme(1)
//                                            )
//                                    )
//                            )
//                    )
//            );
//        }
//    }
//
//    public GProof unpackDeduction() {
//        List<GProof> links = description.getLinks().stream().map(link -> (GProof) link).collect(Collectors.toList());
//        links.replaceAll(GProof::unpackDeduction);
//        GProof root = this;
//        if (description instanceof ModusPonens) {
//            root = new GProof(expression, context, new ModusPonens(links.get(0), links.get(1)));
//        } else if (description instanceof Deduction) {
//            GProof before = links.get(0);
//            DeductionSteps steps = getDeductionSteps(before, this);
//
//            root = before;
//            for (Expression left: steps.left) {
//                root = root.deductionLeft();
//            }
//
//            for (Expression right: steps.right) {
//                root = root.deductionRight(right);
//            }
//        }
//
//        return root;
//    }
//
//    private DeductionSteps getDeductionSteps(GProof before, GProof after) {
//        List<Expression> from = Expression.separate(before.getExpression(), Operator.IMPL);
//        List<Expression> to = Expression.separate(after.getExpression(), Operator.IMPL);
//
//        Expression subExpression = from.get(from.size() - 1);
//        from.remove(from.size() - 1);
//        to.remove(to.size() - 1);
//
//        while (!from.isEmpty() && !to.isEmpty()
//                && from.get(from.size() - 1).equals(to.get(to.size() - 1))) {
//            subExpression = Expression.create(Operator.IMPL, from.get(from.size() - 1), subExpression);
//            from.remove(from.size() - 1);
//            to.remove(to.size() - 1);
//        }
//        Collections.reverse(to);
//
//        return new DeductionSteps(from, to, subExpression);
//    }
//
//    @Override
//    public String toString() {
//        return context + "|-" + expression.suffixString() + " [" + description + "]";
//    }
//}
