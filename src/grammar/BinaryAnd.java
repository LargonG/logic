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
        NProof leftProof;
        NProof rightProof;
        Proof proof = new Proof(this, context);

        if (left.size() < right.size()) {
            leftProof = left.createNProof(context);
            if (!leftProof.getProof().getExpression().equals(left)) {
                return operator.creator.right(leftProof, null, proof, left, right);
            }
            rightProof = right.createNProof(context);
            if (!rightProof.getProof().getExpression().equals(right)) {
                return operator.creator.left(leftProof, rightProof, proof, left, right);
            }
        } else {
            rightProof = right.createNProof(context);
            if (!rightProof.getProof().getExpression().equals(right)) {
                return operator.creator.left(null, rightProof, proof, left, right);
            }
            leftProof = left.createNProof(context);
            if (!leftProof.getProof().getExpression().equals(left)) {
                return operator.creator.right(leftProof, rightProof, proof, left, right);
            }
        }
        return operator.creator.all(leftProof, rightProof, proof, left, right);
    }
}
