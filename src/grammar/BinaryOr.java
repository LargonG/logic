package grammar;

import grammar.operators.Operator;
import proof.NProof;
import proof.Proof;
import proof.context.ImmutableContext;

public class BinaryOr extends BinaryOperator {
    public BinaryOr(Expression left, Expression right) {
        super(Operator.OR, left, right);
    }

    @Override
    public NProof createNProof(ImmutableContext context) {
        NProof leftProof = null;
        NProof rightProof = null;

        if (left.size() < right.size()) {
            leftProof = left.createNProof(context);
            if (leftProof.getProof().getExpression().equals(left)) {
                return operator.creator.left(leftProof, rightProof, new Proof(this, context), left, right);
            }
            rightProof = right.createNProof(context);
            if (rightProof.getProof().getExpression().equals(right)) {
                return operator.creator.right(leftProof, rightProof, new Proof(this, context), left, right);
            }
        } else {
            rightProof = right.createNProof(context);
            if (rightProof.getProof().getExpression().equals(right)) {
                return operator.creator.right(leftProof, rightProof, new Proof(this, context), left, right);
            }
            leftProof = left.createNProof(context);
            if (leftProof.getProof().getExpression().equals(left)) {
                return operator.creator.left(leftProof, rightProof, new Proof(this, context), left, right);
            }
        }
        return operator.creator.none(leftProof, rightProof, new Proof(this, context), left, right);
    }
}
