package grammar;

import grammar.operators.Operator;
import grammar.proof.NProof;
import grammar.proof.Proof;
import grammar.proof.context.ImmutableContext;

public class BinaryImplement extends BinaryOperator {

    public BinaryImplement(Expression left, Expression right) {
        super(Operator.IMPL, left, right);
    }

    @Override
    public NProof createNProof(ImmutableContext context) {
        NProof right = this.right.createNProof(context);
        if (right.getProof().getExpression().equals(this.right)) {
            return operator.createNProof(null, right, new Proof(this, context));
        }
        return operator.createNProof(this.left.createNProof(context), right, new Proof(this, context));
    }
}
