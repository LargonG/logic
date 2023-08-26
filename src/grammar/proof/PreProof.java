package grammar.proof;

import grammar.descriptions.natural.NaturalDescription;
import grammar.descriptions.natural.Rule;
import grammar.Expression;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public class PreProof {
    private final Proof proof;
    private final Rule rule;
    private final List<Integer> ids;

    private final Context pushContext;

    private NProof nProof;

    private Function<NProof, NProof> create;

    private PreProof(
            final NProof nProof,
            final Proof proof,
            final Context pushContext,
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
                    final Context pushContext,
                    final Rule rule,
                    final int... ids) {
        this(null, proof, pushContext, rule, null, ids);
    }

    public PreProof(final Proof proof,
                    final Rule rule,
                    final int... ids) {
        this(null, proof, Context.empty(), rule, null, ids);
    }

    public PreProof(final Expression expression,
                    final Context context,
                    final Context pushContext,
                    final Rule rule,
                    final int... ids
                    ) {
        this(null, new Proof(expression, context), pushContext, rule, null, ids);
    }

    public PreProof(final Expression expression,
                    final Context context,
                    final Rule rule,
                    final int... ids) {
        this(null, new Proof(expression, context), Context.empty(), rule, null, ids);
    }

    public PreProof(final NProof nProof,
                    final Context pushContext) {
        this(new NProof(nProof.getProof(),
                nProof.getDescription(),
                pushContext), null, Context.empty(), null, null);
    }

    public PreProof(final NProof nProof) {
        this(nProof, null, Context.empty(), null, null);
    }

    public PreProof(final Function<NProof, NProof> create,
                    final int... ids) {
        this(null, null, Context.empty(), null, create, ids);
    }

    public NProof createNProof(List<NProof> others) {
        if (nProof == null) {
            if (create != null) {
                nProof = create.apply(others.get(ids.get(0)));
            } else {
                nProof = new NProof(proof,
                        new NaturalDescription(rule,
                                ids.stream()
                                        .map(others::get)
                                        .collect(Collectors.toList())),
                        pushContext);
            }
        } else if (pushContext != null) {
            nProof.addToPush(pushContext); // WARNING: при вызове фукнции 2 раза, nProof->context увеличится
        }

        return nProof;
    }
}
