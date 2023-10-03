package grammar;

import grammar.descriptions.natural.Rule;
import grammar.operators.Operator;
import grammar.proof.NProof;
import grammar.proof.PreProof;
import grammar.proof.Proof;
import grammar.proof.context.ImmutableContext;

import java.util.Map;
import java.util.Set;

public class Nil implements Expression {
    private final static Nil instance;

    public static Nil getInstance() {
        return instance;
    }

    static {
        instance = new Nil();
    }

    private Nil() {

    }
    @Override
    public boolean calculate(Map<String, Boolean> values) {
        return false;
    }

    @Override
    public Expression paste(Map<String, Expression> values) {
        return this;
    }

    @Override
    public Expression toNormalForm() {
        return this;
    }

    @Override
    public NProof createNProof(ImmutableContext context) {
        ImmutableContext newContext = context.merge(this);
        return NProof.zip(
                new PreProof(new Proof(this, newContext), Rule.AXIOM),
                new PreProof(Expression.create(Operator.IMPL, this, this), context,
                        Rule.DEDUCTION, 0)
        );
    }

    @Override
    public String toString() {
        return "_|_";
    }

    @Override
    public String suffixString(Operator before, boolean brackets) {
        return "_|_";
    }

    @Override
    public void getVariablesNames(Set<String> result) {
        // do nothing
    }

    @Override
    public int compareTo(Expression o) {
        if (o instanceof Variable) {
            return 1;
        } else if (o instanceof Nil) {
            return 0;
        }
        return -1;
    }
}
