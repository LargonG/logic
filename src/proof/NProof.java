package proof;

import grammar.Expression;
import grammar.Nil;
import grammar.Variable;
import proof.descriptions.natural.NaturalDescription;
import proof.descriptions.natural.NaturalRule;
import grammar.operators.Operator;
import proof.context.Context;
import proof.context.ImmutableContext;
import proof.context.MutableContext;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class NProof extends MetaProof {
    MutableContext pushContext = null;

    public NProof(final Proof proof,
                  final NaturalDescription description) {
        super(proof, description);
    }

    public NProof(final Proof proof,
                  final NaturalDescription description,
                  final MutableContext pushContext) {
        super(proof, description);
        this.pushContext = pushContext;
    }

    public NProof(final Expression expression,
                  final ImmutableContext immutableContext,
                  final NaturalDescription description) {
        super(expression, immutableContext, description);
    }

    public static NProof zip(final List<PreProof> proofs) {
        List<NProof> result = new ArrayList<>(proofs.size());
        for (PreProof pre : proofs) {
            result.add(pre.createNProof(result));
        }

        return result.get(result.size() - 1);
    }

    public static NProof zip(final PreProof... proofs) {
        return zip(Arrays.asList(proofs));
    }

    public static NProof zipContext(List<NProof> proofs,
                                    List<String> vars) {
        final int N = vars.size();
        List<NProof> container = Collections.emptyList();
        for (int i = 0; i < N; i++) {
            int varsLeft = N - i;
            container = new ArrayList<>((1 << (varsLeft - 1)));
            for (int j = 0; j < proofs.size() / 2; j++) {
                int rj = j + (1 << (varsLeft - 1));
                if (proofs.get(j).getProof().getExpression()
                        .equals(proofs.get(rj).getProof().getExpression())) {
                    NProof left = proofs.get(j);
                    NProof right = proofs.get(rj);
                    Expression elem = new Variable(vars.get(N - i - 1));
                    container.add(zipContext(left, right, elem));
                } else {
                    throw new IllegalArgumentException("Proof is not true");
                }
            }

            proofs = container;
        }

        if (N == 0 && proofs.size() == 1) {
            container = proofs;
        }

        return container.get(0);
    }

    private static NProof zipContext(NProof left, NProof right, Expression A) {

        Expression expression = left.getProof().getExpression();
        ImmutableContext context = left.getProof().getContext().diff(A);

        Expression notA = Expression.create(Operator.IMPL, A, Nil.getInstance());
        Expression aOrNotA = Expression.create(Operator.OR, A, notA);

        Expression notAorNotA = Expression.create(Operator.IMPL, aOrNotA, Nil.getInstance());

        ImmutableContext contextA = context.merge(notAorNotA);
        ImmutableContext contextBase = contextA.merge(A);
        return NProof.zip(
                new PreProof(A, contextBase, NaturalRule.AXIOM), // 0
                new PreProof(aOrNotA, contextBase, NaturalRule.OR_COMPOSITION_LEFT, 0), // 1
                new PreProof(notAorNotA, contextBase, NaturalRule.AXIOM), // 2
                new PreProof(Nil.getInstance(), contextBase, NaturalRule.MODUS_PONENS, 2, 1), // 3
                new PreProof(notA, contextA, NaturalRule.DEDUCTION, 3), // 4
                new PreProof(aOrNotA, contextA, NaturalRule.OR_COMPOSITION_RIGHT, 4), // 5
                new PreProof(notAorNotA, contextA, NaturalRule.AXIOM), // 6
                new PreProof(Nil.getInstance(), contextA, NaturalRule.MODUS_PONENS, 6, 5), // 7
                new PreProof(aOrNotA, context, NaturalRule.NOT, 7), // 8
                new PreProof(left), // 9
                new PreProof(right), // 10
                new PreProof(expression, context, NaturalRule.EXCLUDED_MIDDLE_RULE, 9, 10, 8) // 11
        );
    }

    @Override
    protected void getProofTree(List<MetaProof> proofs) {
        pushTree(MutableContext.empty());
        getProofsTree(proofs, 0);
    }

    private void getProofsTree(final List<MetaProof> proofs, int depth) {
        List<NProof> links = description.getLinks();
        for (NProof link : links) {
            link.getProofsTree(proofs, depth + 1);
        }

        proofs.add(this);
    }

    @Override
    public void printProofsTree(PrintWriter out) {
        printProofsTree(out, MutableContext.empty(), 0);
    }

    private void printProofsTree(PrintWriter out, Context context, int depth) {
        if (pushContext != null) {
            context.add(pushContext);
        }
        List<NProof> links = description.getLinks();
        for (NProof link : links) {
            link.printProofsTree(out, context, depth + 1);
        }

        String old = proof.getContext().toString();
        String nes = context.toString();
        String cnt = old.isEmpty() ? nes : nes.isEmpty() ? old : (old + "," + nes);
        out.println("[" + depth + "] " +
                metaExpression(
                        cnt + "|-" + proof.getExpression().suffixString(), this.description));
        if (pushContext != null) {
            context.remove(pushContext);
        }
    }

    public void pushTree(Context context) {
        Context pushed = push(context);

        List<NProof> links = description.getLinks();
        for (NProof link : links) {
            link.pushTree(context);
        }

        if (pushed != null) {
            context.remove(pushed);
        }
    }

    private Context push(Context context) {
        if (pushContext != null) {
            context.add(this.pushContext);
        }
        Context pushed = this.pushContext;
        this.proof = new Proof(proof.getExpression(), proof.getContext().merge(context));
        this.pushContext = null;
        return pushed;
    }

    public boolean check() {
        return getDescription().getRule().getChecker().check(this, description.getLinks());
    }

    void addToPush(Context context) {
        if (this.pushContext == null) {
            this.pushContext = MutableContext.empty();
        }
        this.pushContext.add(context);
    }

    @Override
    public NaturalDescription getDescription() {
        return (NaturalDescription) description;
    }
}
