package grammar.operators;

import grammar.Expression;
import grammar.Nil;
import proof.descriptions.natural.NaturalRule;
import proof.NProof;
import proof.PreProof;
import proof.Proof;
import proof.context.MutableContext;

public class Implication implements Bundle {
    @Override
    public NProof all(NProof left, NProof right, Proof what, Expression baseLeft, Expression baseRight) {
        return NProof.zip(
                new PreProof(right, MutableContext.of(baseLeft)),
                new PreProof(what, NaturalRule.DEDUCTION, 0)
        );
    }

    @Override
    public NProof left(NProof left, NProof right, Proof what, Expression baseLeft, Expression baseRight) {
        MutableContext pushImmutableContext = MutableContext.of(what.getExpression());
        return NProof.zip(
                new PreProof(left), // 0
                new PreProof(right), // 1
                new PreProof(what, NaturalRule.AXIOM), // 2
                new PreProof(baseRight, what.getContext(), NaturalRule.MODUS_PONENS, 2, 0), // 3
                new PreProof(Nil.getInstance(), what.getContext(), pushImmutableContext, NaturalRule.MODUS_PONENS, 1, 3), // 4
                new PreProof( // 5
                        Expression.create(
                                Operator.IMPL,
                                what.getExpression(),
                                Nil.getInstance()),
                        what.getContext(), NaturalRule.DEDUCTION, 4)
        );
    }

    @Override
    public NProof right(NProof left, NProof right, Proof what, Expression baseLeft, Expression baseRight) {
        return all(left, right, what, baseLeft, baseRight);
    }

    @Override
    public NProof none(NProof left, NProof right, Proof what, Expression baseLeft, Expression baseRight) {
        return NProof.zip(
                new PreProof(left), // 0
                new PreProof(right), // 1
                new PreProof(baseLeft, what.getContext(), NaturalRule.AXIOM), // 2
                new PreProof(Nil.getInstance(), what.getContext(),
                        MutableContext.of(right.getProof().getExpression()),
                        NaturalRule.MODUS_PONENS, 0, 2), // 3
                new PreProof(baseRight, what.getContext(),
                        MutableContext.of(baseLeft),
                        NaturalRule.NOT, 3), // 4
                new PreProof(what, NaturalRule.DEDUCTION, 4)
        );
    }
}
