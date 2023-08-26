package grammar.operators;

import grammar.descriptions.natural.Rule;
import grammar.proof.Context;
import grammar.proof.NProof;
import grammar.proof.PreProof;
import grammar.proof.Proof;
import grammar.Expression;
import grammar.Nil;

public class Or implements Bundle {
    private NProof or(NProof root, Proof what, boolean left) {
        return NProof.zip(
                new PreProof(root),
                new PreProof(what, left ? Rule.OR_COMPOSITION_LEFT : Rule.OR_COMPOSITION_RIGHT, 0)
        );
    }
    @Override
    public NProof all(NProof left, NProof right, Proof what, Expression baseLeft, Expression baseRight) {
        return or(left, what, true);
    }

    @Override
    public NProof left(NProof left, NProof right, Proof what, Expression baseLeft, Expression baseRight) {
        return or(left, what, true);
    }

    @Override
    public NProof right(NProof left, NProof right, Proof what, Expression baseLeft, Expression baseRight) {
        return or(right, what, false);
    }

    @Override
    public NProof none(NProof left, NProof right, Proof what, Expression baseLeft, Expression baseRight) {
        Context pushContext = Context.of(what.getExpression());
        Context phi = Context.of(baseLeft);
        Context pci = Context.of(baseRight);
        return NProof.zip(
                new PreProof(left), // 0
                new PreProof(right), // 1
                new PreProof(baseLeft, what.getContext(), Rule.AXIOM), // 2
                new PreProof(baseRight, what.getContext(), Rule.AXIOM), // 3
                new PreProof(Nil.getInstance(), what.getContext(), phi, Rule.MODUS_PONENS, 0, 2), // 4
                new PreProof(Nil.getInstance(), what.getContext(), pci, Rule.MODUS_PONENS, 1, 3), // 5
                new PreProof(what.getExpression(), what.getContext(), Rule.AXIOM), // 6
                new PreProof(Nil.getInstance(), what.getContext(), pushContext, Rule.EXCLUDED_MIDDLE_RULE, 4, 5, 6), // 7
                new PreProof(Expression.create(
                        Operator.IMPL,
                        what.getExpression(),
                        Nil.getInstance()
                ), what.getContext(), Rule.DEDUCTION, 7) // 8
        );
    }
}
