package grammar.operators;

import grammar.descriptions.natural.Rule;
import grammar.proof.Context;
import grammar.proof.NProof;
import grammar.proof.PreProof;
import grammar.proof.Proof;
import grammar.Expression;
import grammar.Nil;

public class Implication implements Bundle {
    @Override
    public NProof all(NProof left, NProof right, Proof what, Expression baseLeft, Expression baseRight) {
        return NProof.zip(
                new PreProof(right, Context.of(baseLeft)),
                new PreProof(what, Rule.DEDUCTION, 0)
        );
    }

    @Override
    public NProof left(NProof left, NProof right, Proof what, Expression baseLeft, Expression baseRight) {
        Context pushContext = Context.of(what.getExpression());
        return NProof.zip(
                new PreProof(left), // 0
                new PreProof(right), // 1
                new PreProof(what, Rule.AXIOM), // 2
                new PreProof(baseRight, what.getContext(), Rule.MODUS_PONENS, 2, 0), // 3
                new PreProof(Nil.getInstance(), what.getContext(), pushContext, Rule.MODUS_PONENS, 1, 3), // 4
                new PreProof( // 5
                        Expression.create(
                                Operator.IMPL,
                                what.getExpression(),
                                Nil.getInstance()),
                        what.getContext(), Rule.DEDUCTION, 4)
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
                new PreProof(baseLeft, what.getContext(), Rule.AXIOM), // 2
                new PreProof(Nil.getInstance(), what.getContext(), Context.of(right.getProof().getExpression()),
                        Rule.MODUS_PONENS, 0, 2), // 3
                new PreProof(baseRight, what.getContext(),
                        Context.of(baseLeft),
                        Rule.NOT, 3), // 4
                new PreProof(what, Rule.DEDUCTION, 4)
        );
    }
}
