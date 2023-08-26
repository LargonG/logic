//package builder;
//
//import builder.descriptions.gilbert.AxiomScheme;
//import builder.descriptions.gilbert.Deduction;
//import builder.descriptions.gilbert.Hypothesis;
//import builder.descriptions.gilbert.ModusPonens;
//import builder.proof.GProof;
//import builder.proof.IProof;
//import builder.proof.Proof;
//import grammar.Expression;
//import grammar.operators.Operator;
//
//import java.util.*;
//import java.util.stream.Collectors;
//
///**
// * Creates the real proof of expression ("unpacks" meta proof)
// */
//public class RealBuilder implements Builder<IProof, GProof> {
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
//    @Override
//    public List<GProof> build(final List<IProof> proofs) {
//        List<GProof> real = new ArrayList<>(proofs.size());
//        Set<Integer> useful = getUseful(proofs.get(proofs.size() - 1));
//        for (int i = 0; i < proofs.size(); i++) {
//            GProof realAfter = null;
//            if (useful.contains(i)) {
//                GProof metaAfter = (GProof) proofs.get(i).getProof();
//                if (metaAfter.getDescription() instanceof AxiomScheme
//                || metaAfter.getDescription() instanceof Hypothesis) {
//                    realAfter = metaAfter;
//                } else if (metaAfter.getDescription() instanceof ModusPonens) {
//                    ModusPonens modusPonens = (ModusPonens) metaAfter.getDescription();
//                    realAfter = new GProof(
//                            metaAfter.getExpression(),
//                            metaAfter.getContext(),
//                            new ModusPonens(
//                                    real.get(modusPonens.alpha.getId()),            // alpha
//                                    real.get(modusPonens.alphaImplBetta.getId())    // alpha -> betta
//                            )
//                    );
//                } else if (metaAfter.getDescription() instanceof Deduction) {
//                    GProof metaBefore = (((Deduction) metaAfter.getDescription()).proof);
//
//                    DeductionSteps steps = getDeductionSteps(metaBefore, metaAfter);
//
//                    realAfter = real.get(metaBefore.getId());
//                    for (Expression ignored : steps.left) {
//                        // G |- a -> b => G, a |- b
//                        realAfter = realAfter.deductionLeft();
//                    }
//
//                    for (Expression right: steps.right) {
//                        realAfter = realAfter.deductionRight(right);
//                    }
//                } else {
//                    throw new IllegalArgumentException("Description is incorrect!");
//                }
//            }
//            real.add(realAfter);
//        }
//
//        List<IProof> result = new ArrayList<>();
//        real.get(real.size() - 1).getProofsTree(result);
//        return result.stream().map(box -> (GProof) box.getProof()).collect(Collectors.toList());
//    }
//
//    private Set<Integer> getUseful(final GProof proof) {
//        Set<Integer> result = new HashSet<>();
//        Queue<Proof> queue = new ArrayDeque<>();
//
//        queue.add(proof);
//        result.add(proof.getId());
//
//        while (!queue.isEmpty()) {
//            Proof current = queue.poll();
//            List<GProof> links = current.getDescription().getLinks().stream()
//                    .map(pr -> (GProof) pr).collect(Collectors.toList());
//            for (GProof link: links) {
//                if (!result.contains(link.getId())) {
//                    result.add(link.getId());
//                    queue.add(link);
//                }
//            }
//        }
//
//        return result;
//    }
//
//    private DeductionSteps getDeductionSteps(Proof before, Proof after) {
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
//}
