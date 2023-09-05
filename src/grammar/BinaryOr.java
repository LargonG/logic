package grammar;

import grammar.operators.Operator;
import grammar.proof.Context;
import grammar.proof.NProof;
import grammar.proof.Proof;

public class BinaryOr extends BinaryOperator {
    public BinaryOr(Expression left, Expression right) {
        super(Operator.OR, left, right);
    }

    @Override
    public NProof createNProof(Context context) {
        NProof leftProof = null;
        NProof rightProof = null;

        if ((left.hashCode() < right.hashCode() ?
                (leftProof = left.createNProof(context)) : (rightProof = right.createNProof(context)))
                .getProof().getExpression().equals(left.hashCode() < right.hashCode() ? left : right)) {
            return operator.createNProof(leftProof, rightProof, new Proof(this, context));
        }
        NProof some = left.hashCode() < right.hashCode() ? (rightProof = right.createNProof(context)) :
                (leftProof = left.createNProof(context));
        return operator.createNProof(leftProof, rightProof, new Proof(this, context));
    }
}
