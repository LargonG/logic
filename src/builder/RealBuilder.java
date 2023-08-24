package builder;

import builder.descriptions.AxiomScheme;
import builder.descriptions.Deduction;
import builder.descriptions.Hypothesis;
import builder.descriptions.ModusPonens;
import grammar.Expression;
import grammar.Operator;

import java.util.*;

/**
 * Creates the real proof of expression ("unpacks" meta proof)
 */
public class RealBuilder implements Builder<Proof, Expression> {
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

    @Override
    public List<Expression> build(final List<Proof> proofs) {
        List<Proof> real = new ArrayList<>(proofs.size());
        Set<Integer> useful = getUseful(proofs.get(proofs.size() - 1));
        for (int i = 0; i < proofs.size(); i++) {
            Proof realAfter = null;
            if (useful.contains(i)) {
                Proof metaAfter = proofs.get(i);
                if (metaAfter.description instanceof AxiomScheme
                || metaAfter.description instanceof Hypothesis) {
                    realAfter = metaAfter;
                } else if (metaAfter.description instanceof ModusPonens) {
                    ModusPonens modusPonens = (ModusPonens) metaAfter.description;
                    realAfter = new Proof(
                            metaAfter.expression,
                            new ModusPonens(
                                    real.get(modusPonens.alpha.getId()),            // alpha
                                    real.get(modusPonens.alphaImplBetta.getId())    // alpha -> betta
                            )
                    );
                } else if (metaAfter.description instanceof Deduction) {
                    Proof metaBefore = (((Deduction) metaAfter.description).proof);

                    DeductionSteps steps = getDeductionSteps(metaBefore, metaAfter);

                    Map<Expression, Integer> context = new HashMap<>(metaBefore.getContext());
                    realAfter = real.get(metaBefore.getId());
                    for (Expression left: steps.left) {
                        // G |- a -> b => G, a |- b
                        context.merge(left, 1, Integer::sum);
                        realAfter = realAfter.deductionLeft(left);
                    }

                    for (Expression right: steps.right) {
                        context.merge(right, -1, Integer::sum);
                        realAfter = realAfter.deductionRight(context, right);
                    }
                } else {
                    throw new IllegalArgumentException("Description is incorrect!");
                }
            }
            real.add(realAfter);
        }

        List<Expression> result = new ArrayList<>();
        Set<Expression> used = new HashSet<>();
        real.get(real.size() - 1).getExpressionList(result, used);
        return result;
    }

    private Set<Integer> getUseful(final Proof proof) {
        Set<Integer> result = new HashSet<>();
        Queue<Proof> queue = new ArrayDeque<>();

        queue.add(proof);
        result.add(proof.getId());

        while (!queue.isEmpty()) {
            Proof current = queue.poll();
            List<Proof> links = current.description.getLinks();
            for (Proof link: links) {
                if (!result.contains(link.getId())) {
                    result.add(link.getId());
                    queue.add(link);
                }
            }
        }

        return result;
    }

    private DeductionSteps getDeductionSteps(Proof before, Proof after) {
        List<Expression> from = Expression.separate(before.expression, Operator.IMPL);
        List<Expression> to = Expression.separate(after.expression, Operator.IMPL);

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
}
