package grammar.operators;

import grammar.Expression;
import grammar.Nil;
import grammar.descriptions.natural.NaturalRule;
import grammar.proof.NProof;
import grammar.proof.PreProof;
import grammar.proof.Proof;
import grammar.proof.context.MutableContext;

public class Or implements Bundle {
    private NProof or(NProof root, Proof what, boolean left) {
        return NProof.zip(
                new PreProof(root),
                new PreProof(what, left ? NaturalRule.OR_COMPOSITION_LEFT : NaturalRule.OR_COMPOSITION_RIGHT, 0)
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
        MutableContext pushContext = MutableContext.of(what.getExpression());
        MutableContext phi = MutableContext.of(baseLeft);
        MutableContext pci = MutableContext.of(baseRight);
        return NProof.zip(
                new PreProof(left), // 0
                new PreProof(right), // 1
                new PreProof(baseLeft, what.getContext(), NaturalRule.AXIOM), // 2
                new PreProof(baseRight, what.getContext(), NaturalRule.AXIOM), // 3
                new PreProof(Nil.getInstance(), what.getContext(), phi, NaturalRule.MODUS_PONENS, 0, 2), // 4
                new PreProof(Nil.getInstance(), what.getContext(), pci, NaturalRule.MODUS_PONENS, 1, 3), // 5
                new PreProof(what.getExpression(), what.getContext(), NaturalRule.AXIOM), // 6
                new PreProof(Nil.getInstance(), what.getContext(), pushContext, NaturalRule.EXCLUDED_MIDDLE_RULE, 4, 5, 6), // 7
                new PreProof(Expression.create(
                        Operator.IMPL,
                        what.getExpression(),
                        Nil.getInstance()
                ), what.getContext(), NaturalRule.DEDUCTION, 7) // 8
        );
    }
}
