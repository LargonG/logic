package grammar.operators;

import grammar.proof.Context;
import grammar.proof.NProof;
import grammar.proof.PreProof;
import grammar.proof.Proof;
import grammar.descriptions.natural.Rule;
import grammar.Expression;
import grammar.Nil;

public class And implements Bundle {
    protected And() {
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
        return NProof.zip(
                new PreProof(left), // 0
                new PreProof(right), // 1
                new PreProof(what, Rule.AXIOM), // 2
                new PreProof(baseRight, what.getContext(), Rule.AND_DECOMPOSITION_RIGHT, 2), // 3
                new PreProof(Nil.getInstance(), what.getContext(), Context.of(what.getExpression()),
                        Rule.MODUS_PONENS, 1, 3), // 4
                new PreProof(Expression.create( // 5
                        Operator.IMPL,
                        what.getExpression(),
                        Nil.getInstance()
                ), what.getContext(), Rule.DEDUCTION, 4)
                // add push context
        );
    }

    @Override
    public NProof right(NProof left, NProof right, Proof what, Expression baseLeft, Expression baseRight) {
        return NProof.zip(
                new PreProof(left), // 0
                new PreProof(right), // 1
                new PreProof(what, Rule.AXIOM), // 2
                new PreProof(baseLeft, what.getContext(), Rule.AND_DECOMPOSITION_LEFT, 2), // 3
                new PreProof(Nil.getInstance(), what.getContext(), Context.of(what.getExpression()),
                        Rule.MODUS_PONENS, 0, 3), // 4
                new PreProof(Expression.create( // 5
                        Operator.IMPL,
                        what.getExpression(),
                        Nil.getInstance()
                ), what.getContext(), Rule.DEDUCTION, 4)
        );
    }

    @Override
    public NProof none(NProof left, NProof right, Proof what, Expression baseLeft, Expression baseRight) {
        return right(left, right, what, baseLeft, baseRight);
    }
}
