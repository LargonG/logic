package grammar;

import builder.descriptions.natural.NaturalDescription;
import builder.descriptions.natural.Rule;
import builder.proof.Context;
import builder.proof.NProof;
import builder.proof.PreProof;
import builder.proof.Proof;
import grammar.operators.Operator;

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
    public NProof createNProof(Context context) {
        Context newContext = context.add(this);
        return NProof.zip(
                new PreProof(new Proof(this, newContext), Rule.AXIOM),
                new PreProof(Expression.create(Operator.IMPL, this, this), context,
                        Rule.DEDUCTION, 0)
        );
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
