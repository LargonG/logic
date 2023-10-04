package grammar.operators;

import grammar.Expression;
import grammar.Nil;
import grammar.descriptions.natural.Rule;
import grammar.proof.NProof;
import grammar.proof.PreProof;
import grammar.proof.Proof;
import grammar.proof.context.MutableContext;

public class And implements Bundle {
    protected And() {
    }

    private NProof and(NProof root, Proof what, Expression base, boolean left) {
        return NProof.zip(
                new PreProof(root), // 0
                new PreProof(what, Rule.AXIOM), // 1
                new PreProof(base, what.getContext(),
                        left ? Rule.AND_DECOMPOSITION_RIGHT : Rule.AND_DECOMPOSITION_LEFT, 1), // 2
                new PreProof(Nil.getInstance(), what.getContext(),
                        MutableContext.of(what.getExpression()),
                        Rule.MODUS_PONENS, 0, 2), // 3
                new PreProof(Expression.create( // 4
                        Operator.IMPL,
                        what.getExpression(),
                        Nil.getInstance()
                ), what.getContext(), Rule.DEDUCTION, 3)
        );
    }

    @Override
    public NProof all(NProof left, NProof right, Proof what, Expression baseLeft, Expression baseRight) {
        return NProof.zip(
                new PreProof(left),
                new PreProof(right),
                new PreProof(what, Rule.AND_COMPOSITION, 0, 1)
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
