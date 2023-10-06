package proof;

import grammar.Expression;
import proof.descriptions.natural.NaturalDescription;
import proof.descriptions.natural.NaturalRule;
import proof.context.ImmutableContext;
import proof.context.MutableContext;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public class PreProof {
    private final Proof proof;
    private final NaturalRule naturalRule;
    private final List<Integer> ids;

    private MutableContext pushContext;

    private NProof nProof;

    private Function<NProof, NProof> create;

    private PreProof(
            final NProof nProof,
            final Proof proof,
            final MutableContext pushContext,
            final NaturalRule naturalRule,
            final Function<NProof, NProof> create,
            final int... ids) {
        this.nProof = nProof;
        this.proof = proof;
        this.pushContext = pushContext;
        this.naturalRule = naturalRule;
        this.create = create;
        this.ids = Arrays.stream(ids).boxed().collect(Collectors.toList());
    }

    public PreProof(final Proof proof,
                    final MutableContext pushContext,
                    final NaturalRule naturalRule,
                    final int... ids) {
        this(null, proof, pushContext, naturalRule, null, ids);
    }

    public PreProof(final Proof proof,
                    final NaturalRule naturalRule,
                    final int... ids) {
        this(null, proof, null, naturalRule, null, ids);
    }

    public PreProof(final Expression expression,
                    final ImmutableContext context,
                    final MutableContext pushContext,
                    final NaturalRule naturalRule,
                    final int... ids
    ) {
        this(null, new Proof(expression, context), pushContext, naturalRule, null, ids);
    }

    public PreProof(final Expression expression,
                    final ImmutableContext context,
                    final NaturalRule naturalRule,
                    final int... ids) {
        this(null, new Proof(expression, context), null, naturalRule, null, ids);
    }

    public PreProof(final NProof nProof,
                    final MutableContext pushContext) {
        this(nProof, null, pushContext, null, null);
    }

    public PreProof(final NProof nProof) {
        this(nProof, null, null, null, null);
    }

    public PreProof(final Function<NProof, NProof> create,
                    final int... ids) {
        this(null, null, null, null, create, ids);
    }

    public NProof createNProof(List<NProof> others) {
        if (nProof == null) {
            if (create != null) {
                nProof = create.apply(others.get(ids.get(0)));
            } else {
                nProof = new NProof(proof,
                        new NaturalDescription(
                                naturalRule,
                                ids.stream().map(others::get)
                                        .collect(Collectors.toList())
                        ), pushContext);
                pushContext = null;
            }
        }

        if (pushContext != null) {
            nProof.addToPush(pushContext);
            pushContext = null;
        }

        return nProof;
    }
}
