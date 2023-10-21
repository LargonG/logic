package grammar;

import grammar.operators.Operator;
import proof.NProof;
import proof.Proof;
import proof.context.ImmutableContext;

public class BinaryImplement extends BinaryOperator {

    public BinaryImplement(Expression left, Expression right) {
        super(Operator.IMPL, left, right);
    }

    @Override
    public NProof createNProof(ImmutableContext context) {
        NProof rightProof = right.createNProof(context);
        if (rightProof.getProof().getExpression().equals(right)) {
            return operator.creator.right(null, rightProof, new Proof(this, context), left, right);
        }
        return operator.createNProof(left.createNProof(context), rightProof, new Proof(this, context));
    }
}
