package proof;

import grammar.BinaryOperator;
import grammar.Expression;
import grammar.Scheme;
import proof.descriptions.gilbert.*;
import grammar.operators.Operator;
import grammar.predicates.quantifiers.Quantifier;
import proof.builder.GProofBuilder;
import proof.context.ImmutableContext;
import proof.context.LinkedContext;
import resolver.Axioms;

import java.io.PrintWriter;
import java.util.*;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

public class GProof extends MetaProof {
    public GProof(final Proof proof,
                  final GuilbertDescription description) {
        super(proof, description);
    }

    public GProof(final Expression expression,
                  final ImmutableContext context,
                  final GuilbertDescription description) {
        super(expression, context, description);
    }

    public static List<GProof> addMeta(final List<Proof> proofs) {
        List<GProof> metaProofs = new ArrayList<>(proofs.size());
        Map<Expression, List<Integer>> rightPartOfImplication = new HashMap<>();
        Map<Expression, List<Integer>> expressions = new HashMap<>();

        for (int i = 0; i < proofs.size(); i++) {
            Proof current = proofs.get(i);
            GuilbertDescription comment = new Incorrect();

            // Axiom
            int axiomId;
            if ((axiomId = Axioms.isAxiom(current.getExpression())) != -1) {
                comment = new AxiomScheme(axiomId);
            }

            // Hypothesis
            int hypothesisId = -1;
            if (axiomId == -1
                    && (hypothesisId = ((LinkedContext) current
                    .getContext())
                    .indexOf(current.getExpression())) != -1) {
                comment = new Hypothesis(hypothesisId);
            }

            // Modus Ponens
            int modus = -1, ponens = -1;
            if (axiomId == -1 && hypothesisId == -1) {
                for (int implI : rightPartOfImplication.getOrDefault(current.getExpression(), Collections.emptyList())) {
                    Proof implicationProof = metaProofs.get(implI).getProof();
                    BinaryOperator impl = (BinaryOperator) implicationProof.getExpression();
                    if (implicationProof.getContext().equals(current.getContext())) {
                        for (int ai : expressions.getOrDefault(impl.left, Collections.emptyList())) {
                            Proof alphaProof = metaProofs.get(ai).getProof();
                            if (alphaProof.getContext().equals(current.getContext())) {
                                modus = ai;
                                ponens = implI;
                                comment = new ModusPonens(metaProofs, modus, ponens);
                            }
                        }
                    }
                }
            }

            // Deduction
            int dedId = -1;
            if (axiomId == -1 && hypothesisId == -1 && modus == -1 && ponens == -1) {
                for (int proofId = 0; proofId < metaProofs.size(); proofId++) {
                    Proof proof = metaProofs.get(proofId).getProof();
                    if (proof.getFullContextForm().equals(current.getFullContextForm())) {
                        dedId = proofId;
                        comment = new Deduction(metaProofs, dedId);
                        break;
                    }
                }
            }

            // adding new params

            // proof
            int id = i;
            BiFunction<List<Integer>, List<Integer>, List<Integer>>
                    mergeLists = (oldL, newL) -> {
                oldL.addAll(newL);
                return oldL;
            };

            metaProofs.add(new GProof(current, comment));
            List<Expression> impl = Expression.separate(current.getExpression(), Operator.IMPL, 1);
            if (impl.size() > 1) {
                rightPartOfImplication.merge(impl.get(1), new ArrayList<>(Collections.singletonList(id)), mergeLists);
            }
            expressions.merge(current.getExpression(), new ArrayList<>(Collections.singletonList(id)), mergeLists);
        }

        return metaProofs;
    }

    @Override
    protected void getProofTree(List<MetaProof> proofs) {
        List<GProof> links = description.getLinks();
        for (GProof link : links) {
            link.getProofTree(proofs);
        }

        proofs.add(this);
    }

    @Override
    public void printProofsTree(PrintWriter out) {
        List<MetaProof> proofs = getProofTree();
        for (MetaProof proof : proofs) {
            out.println(proof);
        }
    }

    public GProof deductionLeft() {
        List<Expression> impl = Expression.decomposition(proof.getExpression(), Operator.IMPL);

        Expression alpha = impl.get(0);
        Expression betta = impl.get(1);

        ImmutableContext newContext = proof.getContext().merge(alpha);

        return new GProof(
                betta,
                newContext,
                new ModusPonens(
                        new GProof(alpha, newContext, new Hypothesis()),
                        this
                )
        );
    }

    public GProof deductionRight(Expression alpha) {
        ImmutableContext newContext = proof.getContext().diff(alpha);
        Expression expression = proof.getExpression();
        Map<String, Expression> mp = new HashMap<String, Expression>() {{
            put("a", alpha);
            put("b", expression);
        }};
        if (description instanceof AxiomScheme
                || description.getRule() == GuilbertRule.AXIOM
                || newContext.getMap().getOrDefault(expression, 0) > 0) {
            return new GProof(
                    Scheme.create("a->b", mp),
                    newContext,
                    new ModusPonens(
                            this,
                            new GProof(
                                    Scheme.create("b->a->b", mp),
                                    newContext,
                                    new AxiomScheme(0)
                            )
                    )
            );
        } else if (alpha.equals(expression)) {
            return new GProof(
                    Scheme.create("a->a", mp),
                    newContext,
                    new ModusPonens(
                            new GProof(
                                    Scheme.create("a->(a->a)->a", mp),
                                    newContext,
                                    new AxiomScheme(0)),
                            new GProof(
                                    Scheme.create("(a->(a->a)->a)->(a->a)", mp),
                                    newContext,
                                    new ModusPonens(
                                            new GProof(
                                                    Scheme.create("a->a->a", mp),
                                                    newContext,
                                                    new AxiomScheme(0)),
                                            new GProof(
                                                    Scheme.create("(a->a->a)->(a->(a->a)->a)->(a->a)", mp),
                                                    newContext,
                                                    new AxiomScheme(1))
                                    )
                            )
                    )
            );
        } else if (description instanceof ModusPonens || description.getRule() == GuilbertRule.MODUS_PONENS) {
            GProof updModus = description.<GProof>getLinks().get(0).deductionRight(alpha);
            GProof updPonens = description.<GProof>getLinks().get(1).deductionRight(alpha);

            mp.put("d", updModus.getProof().getExpression());
            mp.put("f", updPonens.getProof().getExpression());

            return new GProof(
                    Scheme.create("a->b", mp),
                    newContext,
                    new ModusPonens(
                            updPonens,
                            new GProof(
                                    Scheme.create("f->(a->b)", mp),
                                    newContext,
                                    new ModusPonens(
                                            updModus,
                                            new GProof(
                                                    Scheme.create("d->f->(a->b)", mp),
                                                    newContext,
                                                    new AxiomScheme(1)
                                            )
                                    )
                            )
                    )
            );
        } else if (description.getRule() == GuilbertRule.FORALL) {
            GProof upd = description.<GProof>getLinks().get(0).deductionRight(alpha);

            List<Expression> d = Expression.decomposition(expression, Operator.IMPL);

            Quantifier q = (Quantifier) d.get(1);
            mp.put("b", d.get(0));
            mp.put("c", q.expression);
            mp.put("fc", q);

            List<Object> argsFirst = new ArrayList<Object>() {{
                add(alpha);
                add(d.get(0));
                add(q.expression);
                add(newContext);
            }};

            List<Object> argsSecond = new ArrayList<Object>() {{
                add(alpha);
                add(d.get(0));
                add(q);
                add(newContext);
            }};

            GProofBuilder builder = new GProofBuilder();
            builder
                    .append(upd)
                    .append(GProofBuilder::deductionForAllHelperFirst, argsFirst)
                    .append("(a&b)->c", mp, newContext, GuilbertRule.MODUS_PONENS, -2, -1)
                    .append("(a&b)->fc", mp, newContext, GuilbertRule.FORALL, -1)
                    .append(GProofBuilder::deductionForAllHelperSecond, argsSecond)
                    .append("a->b->fc", mp, newContext, GuilbertRule.MODUS_PONENS, -2, -1)
            ;
            return builder.get();
        } else if (description.getRule() == GuilbertRule.EXISTS) {
            GProof upd = description.<GProof>getLinks().get(0).deductionRight(alpha);

            List<Expression> d = Expression.decomposition(expression, Operator.IMPL);

            Quantifier q = (Quantifier) d.get(0);
            mp.put("b", q.expression);
            mp.put("c", d.get(1));
            mp.put("eb", q);

            List<Object> argsFirst = new ArrayList<Object>() {{
                add(alpha);
                add(q.expression);
                add(d.get(1));
                add(newContext);
            }};

            List<Object> argsSecond = new ArrayList<Object>() {{
                add(alpha);
                add(d.get(0));
                add(d.get(1));
                add(newContext);
            }};

            GProofBuilder builder = new GProofBuilder();
            builder
                    .append(upd)
                    .append(GProofBuilder::deductionExistHelperFirst, argsFirst)
                    .append("b->a->c", mp, newContext, GuilbertRule.MODUS_PONENS, -2, -1)
                    .append("eb->a->c", mp, newContext, GuilbertRule.EXISTS, -1)
                    .append(GProofBuilder::deductionExistHelperSecond, argsSecond)
                    .append("a->eb->c", mp, newContext, GuilbertRule.MODUS_PONENS, -2, -1)
            ;
            return builder.get();
        } else {
            throw new IllegalStateException("Something went wrong..." +
                    "\nDescription: " + description.getRule());
        }
    }

    public GProof unpackDeduction() {

        List<GProof> links = description.getLinks()
                .stream()
                .map(link -> ((GProof) link).unpackDeduction()).collect(Collectors.toList());
        GProof root = this;

        Expression expression = proof.getExpression();
        ImmutableContext context = proof.getContext();
        if (description instanceof ModusPonens || description.getRule() == GuilbertRule.MODUS_PONENS) {
            root = new GProof(expression, context, new ModusPonens(links.get(0), links.get(1)));
        } else if (description instanceof Deduction || description.getRule() == GuilbertRule.DEDUCTION) {
            GProof before = links.get(0);
            DeductionSteps steps = getDeductionSteps(before, this);

            root = before;
            for (Expression left : steps.left) {
                root = root.deductionLeft();
            }

            for (Expression right : steps.right) {
                root = root.deductionRight(right);
            }
        }

        return root;
    }

    private DeductionSteps getDeductionSteps(GProof before, GProof after) {
        List<Expression> from = Expression.separate(before.getProof().getExpression(), Operator.IMPL);
        List<Expression> to = Expression.separate(after.getProof().getExpression(), Operator.IMPL);

        Expression subExpression = from.get(from.size() - 1);
        from.remove(from.size() - 1);
        to.remove(to.size() - 1);

        while (!from.isEmpty() && !to.isEmpty()
                && from.get(from.size() - 1).equals(to.get(to.size() - 1))) {
            subExpression = Expression.create(Operator.IMPL, from.get(from.size() - 1), subExpression);
            from.remove(from.size() - 1);
            to.remove(to.size() - 1);
        }
        Collections.reverse(to);

        return new DeductionSteps(from, to, subExpression);
    }

    private static class DeductionSteps {
        private final List<Expression> left;
        private final List<Expression> right;
        private final Expression subExpression;

        private DeductionSteps(final List<Expression> left,
                               final List<Expression> right,
                               final Expression subExpression) {
            this.left = left;
            this.right = right;
            this.subExpression = subExpression;
        }
    }
}
