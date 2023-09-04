package grammar.proof;

import grammar.descriptions.natural.NaturalDescription;
import grammar.descriptions.natural.Rule;
import grammar.Expression;
import grammar.Nil;
import grammar.operators.Operator;

import java.util.*;

public class NProof extends MetaProof {
    private Context pushContext;
    public NProof(final Proof proof,
                  final NaturalDescription description) {
        super(proof, description, -1);
        pushContext = Context.empty();
    }

    public NProof(final Proof proof,
                  final NaturalDescription description,
                  final Context pushContext) {
        super(proof, description, -1);
        this.pushContext = pushContext;
    }

    public NProof(final Expression expression,
                  final Context context,
                  final NaturalDescription description) {
        super(expression, context, description, -1);
        pushContext = Context.empty();
    }

    @Override
    protected void getProofsTree(List<MetaProof> proofs) {
        pushTree(Context.empty());
        getProofsTree(proofs, 0);
    }

    private void getProofsTree(List<MetaProof> proofs, int depth) {
        List<NProof> links = description.getLinks();
        for (NProof link: links) {
            link.getProofsTree(proofs, depth + 1);
        }

        proofs.add(this);
        setId(depth - 1);
    }

    public void pushTree(Context context) {
        Context newContext = push(context);
        List<NProof> links = description.getLinks();
        for (NProof link: links) {
            link.pushTree(newContext);
        }
    }

    private Context push(Context context) {
        Context newContext = Context.merge(this.pushContext, context);
        this.proof = new Proof(this.proof.getExpression(),
                Context.merge(this.proof.getContext(), newContext));
        this.pushContext = Context.empty();
        return newContext;
    }

    public boolean check() {
        return getDescription().getRule().getChecker().check(this, description.getLinks());
    }

    void addToPush(Context context) {
        this.pushContext = Context.merge(this.pushContext, context);
    }

    @Override
    public NaturalDescription getDescription() {
        return (NaturalDescription) description;
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
                                          final int N) {
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
                    Expression elem = Context.diff(
                            left.getProof().getContext(),
                            right.getProof().getContext())
                            .getList().get(0);
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
        Context leftContext = left.getProof().getContext();
        Context rightContext = right.getProof().getContext();
        Context context = left.getProof().getContext().remove(A);

        Expression notA = Expression.create(Operator.IMPL, A, Nil.getInstance());
        Expression aOrNotA = Expression.create(Operator.OR, A, notA);

        return NProof.zip(
                new PreProof(A, leftContext, Rule.AXIOM), // 0
                new PreProof(aOrNotA, leftContext, Rule.OR_COMPOSITION_LEFT, 0), // 1
                new PreProof(Expression.create(Operator.IMPL, A, aOrNotA), // 2
                        context, Rule.DEDUCTION, 1),
                new PreProof(notA, rightContext, Rule.AXIOM), // 3
                new PreProof(aOrNotA, rightContext, Rule.OR_COMPOSITION_RIGHT, 3), // 4
                new PreProof(Expression.create(Operator.IMPL, notA, aOrNotA), context, Rule.DEDUCTION, 4), // 5
                new PreProof(NProof::contraPosition, 2), // 6
                new PreProof(NProof::contraPosition, 5), // 7
                new PreProof(NProof::deductionLeft, 6), // 8
                new PreProof(NProof::deductionLeft, 7), // 9
                new PreProof(Nil.getInstance(), context.add( // 10
                        Expression.create(Operator.IMPL, aOrNotA, Nil.getInstance())),
                        Rule.MODUS_PONENS, 9, 8
                        ),
                new PreProof(aOrNotA, context, Rule.NOT, 10), // 11
                new PreProof(left), // 12
                new PreProof(right), // 13
                new PreProof(left.getProof().getExpression(), context, Rule.EXCLUDED_MIDDLE_RULE,
                        12, 13, 11) // 14
        );
    }

    public static NProof contraPosition(NProof proof) {
        Expression expr = proof.getProof().getExpression();
        List<Expression> sep = Expression.separate(
                proof.getProof().getExpression(),
                Operator.IMPL, 1);

        Expression left = sep.get(0);
        Expression right = sep.get(1);

        Expression notLeft = Expression.create(Operator.IMPL, left, Nil.getInstance());
        Expression notRight = Expression.create(Operator.IMPL, right, Nil.getInstance());
        Expression contraProof = Expression.create(Operator.IMPL, notRight, notLeft);

        Context context = Context.merge(
                proof.getProof().getContext(),
                Context.of(expr, notRight, left)
        );

        return NProof.zip(
                new PreProof(proof), // 0
                new PreProof(expr, context, Rule.AXIOM), // 1
                new PreProof(left, context, Rule.AXIOM), // 2
                new PreProof(right, context, Rule.MODUS_PONENS, 1, 2), // 3
                new PreProof(notRight, context, Rule.AXIOM), // 4
                new PreProof(Nil.getInstance(), context, Rule.MODUS_PONENS, 4, 3), // 5
                new PreProof(notLeft, context.remove(left), Rule.DEDUCTION, 5), // 6
                new PreProof( // 7
                        contraProof, context.remove(left, notRight), Rule.DEDUCTION, 6),
                new PreProof( // 8
                        Expression.create(Operator.IMPL, expr, contraProof),
                        proof.getProof().getContext(),
                        Rule.DEDUCTION, 7
                ),
                new PreProof(contraProof, proof.getProof().getContext(), Rule.MODUS_PONENS, 8, 0) // 9
        );
    }

    public static NProof deductionLeft(NProof proof) {
        List<Expression> sep = Expression.separate(proof.getProof().getExpression(), Operator.IMPL, 1);

        Expression left = sep.get(0);
        Expression right = sep.get(1);

        Context context = proof.getProof().getContext().add(left);

        return NProof.zip(
                new PreProof(proof, Context.of(left)),
                new PreProof(left, context, Rule.AXIOM),
                new PreProof(right, context, Rule.MODUS_PONENS, 0, 1)
        );
    }

    public static NProof comment(PreProof proof) {
        NProof nProof = proof.createNProof(Collections.emptyList());
        return NProof.zip(
                new PreProof(nProof),
                new PreProof(nProof.getProof(), Rule.COMMENT, 0)
        );
    }
}
