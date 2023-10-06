package proof.builder.transformations;

import grammar.Expression;
import proof.descriptions.gilbert.GuilbertRule;
import grammar.operators.Operator;
import grammar.predicates.quantifiers.Exists;
import grammar.predicates.quantifiers.ForAll;
import grammar.predicates.quantifiers.Quantifier;
import proof.GProof;
import proof.builder.GProofBuilder;
import proof.context.ImmutableContext;

import java.util.ArrayList;
import java.util.List;

public class ExistsTransformations {
    public static GProof inLeft(final List<GProof> proofs) {
        Workspace w = new Workspace(proofs.get(0), false, true, true);
        Quantifier fa = new ForAll(w.quantifier.letter, w.left);

        w.convert.put("fa", fa);
        ImmutableContext newContext = w.context.merge(w.impl, fa);

        w.builder
                .append("fa->a", w.convert, newContext, GuilbertRule.AXIOM)
                .append(fa, newContext, GuilbertRule.HYPOTHESIS)
                .append(w.left, newContext, GuilbertRule.MODUS_PONENS, -1, -2)
                .append(w.impl, newContext, GuilbertRule.HYPOTHESIS)
                .append(w.right, newContext, GuilbertRule.MODUS_PONENS, -2, -1)
                .append("impl->fa->b", w.convert, w.context, GuilbertRule.DEDUCTION, -1)
                .append(w.builder.get().unpackDeduction())
                .append("self->fa->b", w.convert, w.context, GuilbertRule.EXISTS, -1)
                .append(w.proof)
                .append("fa->b", w.convert, w.context, GuilbertRule.MODUS_PONENS, -1, -2)
        ;

        return w.builder.get();
    }

    public static GProof inRight(final List<GProof> proofs) {
        assert proofs.size() == 1;

        Workspace w = new Workspace(proofs.get(0), false, true, false);
        Quantifier eb = new Exists(w.quantifier.letter, w.right);
        w.convert.put("eb", eb);

        ImmutableContext newContext = w.context.merge(w.impl, w.left);

        w.builder
                .append(w.impl, newContext, GuilbertRule.HYPOTHESIS)
                .append(w.left, newContext, GuilbertRule.HYPOTHESIS)
                .append(w.right, newContext, GuilbertRule.MODUS_PONENS, -1, -2)
                .append("b->eb", w.convert, newContext, GuilbertRule.AXIOM)
                .append(eb, newContext, GuilbertRule.MODUS_PONENS, -2, -1)
                .append("impl->a->eb", w.convert, w.context, GuilbertRule.DEDUCTION, -1)
                .append(w.builder.get().unpackDeduction())
                .append("self->a->eb", w.convert, w.context, GuilbertRule.EXISTS, -1)
                .append(w.proof)
                .append("a->eb", w.convert, w.context, GuilbertRule.MODUS_PONENS, -1, -2)
        ;
        return w.builder.get();
    }

    public static GProof outLeft(final List<GProof> proofs) {
        assert proofs.size() == 1;

        Workspace w = new Workspace(proofs.get(0), false, false, true);
        Quantifier eimpl = new Exists(w.quantifier.letter, w.impl);
        Expression na = Workspace.parse("!a", w.convert);
        Quantifier ena = new Exists(w.quantifier.letter, na);

        w.convert.put("eimpl", eimpl);
        w.convert.put("na", na);
        w.convert.put("ena", ena);

        ImmutableContext newContext1 = w.context.merge(w.right);
        ImmutableContext newContext2 = w.context.merge(na);


        w.builder
                .append("(!fa->eimpl)->(b->eimpl)->(!fa|b->eimpl)",
                        w.convert, w.context, GuilbertRule.AXIOM)
                .append("b", w.convert,
                        newContext1.merge(w.quantifier.expression),
                        GuilbertRule.HYPOTHESIS)
                .append("a->b", w.convert,
                        newContext1,
                        GuilbertRule.DEDUCTION, -1)
                .append(w.builder.get().unpackDeduction())
                .append("(a->b)->eimpl", w.convert, newContext1, GuilbertRule.AXIOM)
                .append("eimpl", w.convert, newContext1, GuilbertRule.MODUS_PONENS, -2, -1)
                .append("b->eimpl", w.convert, w.context, GuilbertRule.DEDUCTION, -1)
                .append(w.builder.get().unpackDeduction())
                .append("na->na|b", w.convert, w.context, GuilbertRule.AXIOM)
                .append("na|b", w.convert, newContext2, GuilbertRule.DEDUCTION, -1)
                .append(w.builder.get().unpackDeduction())
                .append(GProofBuilder::orToImpl, -1)
                .append("(a->b)->eimpl", w.convert, newContext2, GuilbertRule.AXIOM)
                .append(eimpl, newContext2, GuilbertRule.MODUS_PONENS, -2, -1)
                .append("na->eimpl", w.convert, w.context, GuilbertRule.DEDUCTION, -1)
                .append(w.builder.get().unpackDeduction())
                .append("ena->eimpl", w.convert, w.context, GuilbertRule.EXISTS, -1)
                .append(GProofBuilder::notInside,
                        new ArrayList<Object>() {{
                            add(Workspace.parse("!fa", w.convert));
                            add(w.context);
                        }})
                .append(GProofBuilder::transitive, -1, -2)
                .append("(b->eimpl)->(!fa|b->eimpl)", w.convert, w.context, GuilbertRule.MODUS_PONENS, -1, 0)
                .append("!fa|b->eimpl", w.convert, w.context, GuilbertRule.MODUS_PONENS, -13, -1)
                .append(w.proof)
                .append(GProofBuilder::implToOr, -1)
                .append(eimpl, w.context, GuilbertRule.MODUS_PONENS, -1, -3)
        ;

        return w.builder.get();
    }

    public static GProof outRight(final List<GProof> proofs) {
        // (a->?x.b) -> ?x.a->b
        assert proofs.size() == 1;

        Workspace w = new Workspace(proofs.get(0), false, false, false);
        Expression na = Expression.create(Operator.NOT, w.left, null);
        Expression eimpl = new Exists(w.quantifier.letter, w.impl);

        w.convert.put("na", na);
        w.convert.put("eimpl", eimpl);

        ImmutableContext newContext1 = w.context.merge(w.quantifier.expression);
        ImmutableContext newContext2 = w.context.merge(na);

        w.builder
                .append("b", w.convert, newContext1.merge(w.right), GuilbertRule.HYPOTHESIS)
                .append(w.impl, newContext1, GuilbertRule.DEDUCTION, -1)
                .append(w.builder.get().unpackDeduction())
                .append("impl->eimpl", w.convert, newContext1, GuilbertRule.AXIOM)
                .append(eimpl, newContext1, GuilbertRule.MODUS_PONENS, -2, -1)
                .append("b->eimpl", w.convert, w.context, GuilbertRule.DEDUCTION, -1)
                .append(w.builder.get().unpackDeduction())
                .append("eb->eimpl", w.convert, w.context, GuilbertRule.EXISTS, -1)
                .append("na->na|b", w.convert, w.context, GuilbertRule.AXIOM)
                .append("na|b", w.convert, newContext2, GuilbertRule.DEDUCTION, -1)
                .append(w.builder.get().unpackDeduction())
                .append(GProofBuilder::orToImpl, -1)
                .append("impl->eimpl", w.convert, newContext2, GuilbertRule.AXIOM)
                .append(eimpl, newContext2, GuilbertRule.MODUS_PONENS, -2, -1)
                .append("na->eimpl", w.convert, w.context, GuilbertRule.DEDUCTION, -1)
                .append(w.builder.get().unpackDeduction())
                .append("(na->eimpl)->(eb->eimpl)->(na|eb->eimpl)", w.convert, w.context, GuilbertRule.AXIOM)
                .append("(eb->eimpl)->(na|eb->eimpl)", w.convert, w.context, GuilbertRule.MODUS_PONENS, -2, -1)
                .append("na|eb->eimpl", w.convert, w.context, GuilbertRule.MODUS_PONENS, -11, -1)
                .append(w.proof)
                .append(GProofBuilder::implToOr, -1)
                .append(eimpl, w.context, GuilbertRule.MODUS_PONENS, -1, -3)
        ;

        return w.builder.get();
    }
}
