package builder;

import builder.descriptions.AxiomScheme;
import builder.descriptions.Deduction;
import builder.descriptions.ModusPonens;
import parser.*;
import resolver.Axioms;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class RealBuilder implements Builder<Proof, Expression> {
    private static class DeductionSteps {
        private final List<Expression> from;
        private final List<Expression> to;
        private final Expression subExpression;

        public DeductionSteps(final List<Expression> from,
                              final List<Expression> to,
                              final Expression subExpression) {
            this.from = from;
            this.to = to;
            this.subExpression = subExpression;
        }
    }
    @Override
    public List<Expression> build(final List<Proof> proofs) {
        List<Expression> result = new ArrayList<>();
        Set<Expression> used = new HashSet<>();

        Set<Integer> useful = getUsefulProofsIds(proofs);

        for (int id: useful) {
            // hypotheses |- expression
            // гипотезы справа мы не должны добавлять в доказательство, они нужны нам только для того
            // чтобы построить мета-вывод
            // аксиомы, думаю, можно добавлять, они всё-равно ни на что не влияют

            // В modus ponens нам говорилось о том, что гипотезы слева должны быть те же самые =>
            // гипотеза, если она была справа, то она будет и слева

            // В дедукции ситуация другая: в ней нам известно, какие гипотезы были слева в прошлом
            // Но часть из них мы перенесли вправо, то есть они должны быть либо
            // 1) аксиомами (это получается первый случай (в плане что мы аксиомы добавляли))
            // 2) получаться m.p. из предыдущих (тоже до этого мы их обработали)
            // 3) гипотезами, которые были слева до этого (гипотезы дожны быть слева в предыдущей строке (из которой действует дедукция))

            Proof proof = proofs.get(id);
            Consumer<Expression> newExpression = (expression -> {
                if (!used.contains(expression)) {
                    result.add(expression);
                    used.add(expression);
                }
            });

            if (proof.description instanceof AxiomScheme) {
                newExpression.accept(proof.expression);
            } else if (proof.description instanceof ModusPonens) {
                ModusPonens modus = (ModusPonens) proof.description;
                newExpression.accept(proofs.get(modus.alpha).expression);
                newExpression.accept(proofs.get(modus.alphaImplBetta).expression);
                // вероятнее мы не добавили аксиому, которая слева
                newExpression.accept(proof.expression);
            } else if (proof.description instanceof Deduction) {
                Deduction ded = (Deduction) proof.description;
                Proof before = proofs.get(ded.id);

                DeductionSteps steps = getDeductionSteps(before.expression, proof.expression);

                // теперь нам нужно достать выражения из before, пока суффикс не станет равным нашему
                // G |- a -> b
                // G, a -> b, a, b
                // a -> b -> c -> d -> e => a -> (b -> (c -> (d -> e))) => [a, b, c, d, e], нужно именно в этом порядке
                Expression fromLeft = before.expression;
                for (Expression from: steps.from) {
                    newExpression.accept(from);
                    BinaryOperator bin = (BinaryOperator) fromLeft;
                    newExpression.accept(bin.right);
                    fromLeft = bin.right;
                }

                // а потом впихнуть нужное вправо
                Expression buildingExpression = steps.subExpression;
                Map<Expression, Integer> context =
                        Stream.concat(proof.contextList.stream(), steps.to.stream())
                        .collect(Collectors.toMap(
                                expr -> expr,
                                expr -> 1,
                                Integer::sum
                        ));
                for (Expression to: steps.to) {
                    // аксиома либо уже принадлежит нашему доказательству (used.contains)
                    // является гипотезой == buildingExpression
                    // получается по правилу m.p. (ну типа)
                    // мы с to никак не взаимодействуем! Тлько с buildingExpression
                    context.merge(to, -1, Integer::sum);
                    Expression finalBuildingExpression = buildingExpression;
                    Map<String, Expression> mp = new HashMap<String, Expression>() {{
                        put("a", to);
                        put("b", finalBuildingExpression);
                    }};

                    // System.out.println(context + " " + buildingExpression);
                    if (buildingExpression.equals(to)) {
                        newExpression.accept(Scheme.create("a->(a->a)->a", mp));
                        newExpression.accept(Scheme.create("a->a->a", mp));
                        newExpression.accept(Scheme.create("(a->a->a)->(a->(a->a)->a)->a->a", mp));
                        newExpression.accept(Scheme.create("(a->(a->a)->a)->a->a", mp));
                        newExpression.accept((buildingExpression = Scheme.create("a->a", mp)));

                    } else if (Axioms.isAxiom(buildingExpression) != -1
                            || used.contains(buildingExpression)
                            || context.getOrDefault(buildingExpression, 0) > 0) {
                        newExpression.accept(Scheme.create("b->a->b", mp));
                        newExpression.accept(Scheme.create("b", mp));
                        newExpression.accept((buildingExpression = Scheme.create("a->b", mp)));
                    } else {
                        // Modus Ponens
                        // но кажется, что это невозможно
                        for (Expression res: result) {
                            System.out.println(res.suffixString());
                        }
                        System.out.println(used);
                        throw new RuntimeException("Modus Ponens???\nto:"
                                + to.suffixString() + "\n"
                                + buildingExpression.suffixString());
                    }
                }
            }
        }

        if (result.isEmpty()) {
            result.add(proofs.get(proofs.size() - 1).expression);
        }

        return result;
    }

    private Set<Integer> getUsefulProofsIds(final List<Proof> proofs) {
        Set<Integer> useful = new HashSet<>();
        Queue<Integer> next = new ArrayDeque<>();

        next.add(proofs.size() - 1);

        while (!next.isEmpty()) {
            int cur = next.poll();

            if (useful.contains(cur)) {
                continue;
            }
            useful.add(cur);

            Proof proof = proofs.get(cur);
            if (proof.description instanceof ModusPonens) {
                ModusPonens mp = (ModusPonens) proof.description;
                next.add(mp.alpha);
                next.add(mp.alphaImplBetta);
            } else if (proof.description instanceof Deduction) {
                Deduction ded = (Deduction) proof.description;
                next.add(ded.id);
            }
        }

        return useful;
    }

    private DeductionSteps getDeductionSteps(Expression from, Expression to) {
        List<Expression> fromImpl = Expression.separate(from, Operator.IMPL);
        List<Expression> toImpl = Expression.separate(to, Operator.IMPL);

        Expression subExpression = toImpl.get(toImpl.size() - 1);

        Function<List<Expression>, Expression> getLast = (list -> list.get(list.size() - 1));
        Consumer<List<Expression>> removeLast = (list -> list.remove(list.size() - 1));

        removeLast.accept(fromImpl);
        removeLast.accept(toImpl);

        while (!fromImpl.isEmpty() && !toImpl.isEmpty()) {
            if (getLast.apply(fromImpl).equals(getLast.apply(toImpl))) {
                subExpression = Expression.create(Operator.IMPL, getLast.apply(fromImpl), subExpression);
                removeLast.accept(fromImpl);
                removeLast.accept(toImpl);
            } else {
                break;
            }
        }

        Collections.reverse(toImpl);

        return new DeductionSteps(fromImpl, toImpl, subExpression);
    }
}
