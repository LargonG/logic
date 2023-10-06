package grammar.operators;

import grammar.Expression;
import grammar.Nil;
import proof.descriptions.natural.NaturalRule;
import proof.NProof;
import proof.PreProof;
import proof.Proof;
import proof.context.MutableContext;

public class And implements Bundle {
    protected And() {
    }

    private NProof and(NProof root, Proof what, Expression base, boolean left) {
        return NProof.zip(
                new PreProof(root), // 0
                new PreProof(what, NaturalRule.AXIOM), // 1
                new PreProof(base, what.getContext(),
                        left ? NaturalRule.AND_DECOMPOSITION_RIGHT : NaturalRule.AND_DECOMPOSITION_LEFT, 1), // 2
                new PreProof(Nil.getInstance(), what.getContext(),
                        MutableContext.of(what.getExpression()),
                        NaturalRule.MODUS_PONENS, 0, 2), // 3
                new PreProof(Expression.create( // 4
                        Operator.IMPL,
                        what.getExpression(),
                        Nil.getInstance()
                ), what.getContext(), NaturalRule.DEDUCTION, 3)
        );
    }

    @Override
    public NProof all(NProof left, NProof right, Proof what, Expression baseLeft, Expression baseRight) {
        return NProof.zip(
                new PreProof(left),
                new PreProof(right),
                new PreProof(what, NaturalRule.AND_COMPOSITION, 0, 1)
        );
    }

    @Override
    public NProof left(NProof left, NProof right, Proof what, Expression baseLeft, Expression baseRight) {
        return and(right, what, baseRight, true);
    }

    @Override
    public NProof right(NProof left, NProof right, Proof what, Expression baseLeft, Expression baseRight) {
        return and(left, what, baseLeft, false);
    }

    @Override
    public NProof none(NProof left, NProof right, Proof what, Expression baseLeft, Expression baseRight) {
        return and(left != null ? left : right, what, left != null ? baseLeft : baseRight, left == null);
    }
}
