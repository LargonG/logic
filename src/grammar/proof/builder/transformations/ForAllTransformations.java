package grammar.proof.builder.transformations;

import grammar.descriptions.gilbert.GuilbertRule;
import grammar.predicates.quantifiers.Exists;
import grammar.predicates.quantifiers.ForAll;
import grammar.predicates.quantifiers.Quantifier;
import grammar.proof.GProof;
import grammar.proof.builder.GProofBuilder;
import grammar.proof.context.ImmutableContext;

import java.util.List;

public class ForAllTransformations {
    public static GProof inLeft(final List<GProof> proofs) {
        assert proofs.size() == 1;

        Workspace w = new Workspace(proofs.get(0), true, true, true);
        w.convert.put("fb", new ForAll(w.quantifier.letter, w.right));

        w.builder
                .append(w.proof)
                .append("self->impl", w.convert, w.context, GuilbertRule.AXIOM)
                .append(w.impl, w.context, GuilbertRule.MODUS_PONENS, -2, -1)
                .append("a->fb", w.convert, w.context, GuilbertRule.FORALL, -1)
        ;
        return w.builder.get();
    }

    public static GProof inRight(final List<GProof> proofs) {
        assert proofs.size() == 1;

        Workspace w = new Workspace(proofs.get(0), true, true, false);
        w.convert.put("ea", new Exists(w.quantifier.letter, w.left));

        w.builder
                .append(w.proof)
                .append("self->impl", w.convert, w.context, GuilbertRule.AXIOM)
                .append(w.impl, w.context, GuilbertRule.MODUS_PONENS, -2, -1)
                .append("ea->b", w.convert, w.context, GuilbertRule.EXISTS, -1)
        ;
        return w.builder.get();
    }

    public static GProof outLeft(final List<GProof> proofs) {
        assert proofs.size() == 1;


        Workspace w = new Workspace(proofs.get(0), true, false, true);
        Quantifier fimpl = new ForAll(w.quantifier.letter, w.impl);
        w.convert.put("fimpl", fimpl);

        ImmutableContext newContext = w.context.merge(w.self);
        w.builder
                .append(w.self, newContext, GuilbertRule.HYPOTHESIS)
                .append("a->ea", w.convert, newContext, GuilbertRule.AXIOM)
                .append(GProofBuilder::transitive, -1, -2)
                .append("self->impl", w.convert, w.context, GuilbertRule.DEDUCTION, -1)
                .append(w.builder.get().unpackDeduction())
                .append("self->fimpl", w.convert, w.context, GuilbertRule.FORALL, -1)
                .append(w.proof)
                .append(fimpl, w.context, GuilbertRule.MODUS_PONENS, -1, -2)
        ;

        return w.builder.get();
    }

    public static GProof outRight(final List<GProof> proofs) {
        assert proofs.size() == 1;

        Workspace w = new Workspace(proofs.get(0), true, false, false);
        w.convert.put("fimpl", new ForAll(w.quantifier.letter, w.impl));
        ImmutableContext newContext = w.context.merge(w.self, w.left);
        w.builder
                .append(w.self, newContext, GuilbertRule.HYPOTHESIS)
                .append(w.left, newContext, GuilbertRule.HYPOTHESIS)
                .append(w.right, newContext, GuilbertRule.MODUS_PONENS, -1, -2)
                .append("fb->b", w.convert, newContext, GuilbertRule.AXIOM)
                .append("b", w.convert, newContext, GuilbertRule.MODUS_PONENS, -2, -1)
                .append("self->impl", w.convert, w.context, GuilbertRule.DEDUCTION, -1)
                .append(w.builder.get().unpackDeduction())
                .append("self->fimpl", w.convert, w.context, GuilbertRule.FORALL, -1)
                .append(w.proof)
                .append("fimpl", w.convert, w.context, GuilbertRule.MODUS_PONENS, -1, -2)
        ;
        return w.builder.get();
    }
}
