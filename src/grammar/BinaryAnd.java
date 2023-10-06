package grammar;

import grammar.operators.Operator;
import proof.NProof;
import proof.Proof;
import proof.context.ImmutableContext;

public class BinaryAnd extends BinaryOperator {
    public BinaryAnd(Expression left, Expression right) {
        super(Operator.AND, left, right);
    }

    @Override
    public NProof createNProof(ImmutableContext context) {
        NProof leftProof = null;
        NProof rightProof = null;

        if (left.hashCode() < right.hashCode()) {
            leftProof = left.createNProof(context);
        } else {
            rightProof = right.createNProof(context);
        }

        if (leftProof != null && !leftProof.getProof().getExpression().equals(left)
                || rightProof != null && !rightProof.getProof().getExpression().equals(right)) {
            return operator.createNProof(leftProof, rightProof, new Proof(this, context));
        }

        if (leftProof == null) {
            leftProof = left.createNProof(context);
        } else {
            rightProof = right.createNProof(context);
        }

        return operator.createNProof(leftProof, rightProof, new Proof(this, context));
    }
}
