package grammar.proof;

import grammar.Expression;
import grammar.descriptions.natural.NaturalDescription;
import grammar.descriptions.natural.Rule;
import grammar.proof.context.ImmutableContext;
import grammar.proof.context.MutableContext;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public class PreProof {
    private final Proof proof;
    private final Rule rule;
    private final List<Integer> ids;

    private MutableContext pushContext;

    private NProof nProof;

    private Function<NProof, NProof> create;

    private PreProof(
            final NProof nProof,
            final Proof proof,
            final MutableContext pushContext,
            final Rule rule,
            final Function<NProof, NProof> create,
            final int... ids) {
        this.nProof = nProof;
        this.proof = proof;
        this.pushContext = pushContext;
        this.rule = rule;
        this.create = create;
        this.ids = Arrays.stream(ids).boxed().collect(Collectors.toList());
    }

    public PreProof(final Proof proof,
                    final MutableContext pushContext,
                    final Rule rule,
                    final int... ids) {
        this(null, proof, pushContext, rule, null, ids);
    }

    public PreProof(final Proof proof,
                    final Rule rule,
                    final int... ids) {
        this(null, proof, null, rule, null, ids);
    }

    public PreProof(final Expression expression,
                    final ImmutableContext context,
                    final MutableContext pushContext,
                    final Rule rule,
                    final int... ids
                    ) {
        this(null, new Proof(expression, context), pushContext, rule, null, ids);
    }

    public PreProof(final Expression expression,
                    final ImmutableContext context,
                    final Rule rule,
                    final int... ids) {
        this(null, new Proof(expression, context), null, rule, null, ids);
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
                                rule,
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
