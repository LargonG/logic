package grammar.operators;

import grammar.proof.*;
import grammar.BinaryOperator;
import grammar.Expression;

public interface Bundle {
    NProof all(NProof left, NProof right, Proof what, Expression baseLeft, Expression baseRight);
    NProof left(NProof left, NProof right, Proof what, Expression baseLeft, Expression baseRight);
    NProof right(NProof left, NProof right, Proof what, Expression baseLeft, Expression baseRight);
    NProof none(NProof left, NProof right, Proof what, Expression baseLeft, Expression baseRight);

    default NProof createNProof(NProof left, NProof right, Proof what) {
        BinaryOperator op = ((BinaryOperator) what.getExpression());

        Expression baseLeft = op.left;
        Expression baseRight = op.right;

        if (left.getProof().getExpression().equals(baseLeft) && right.getProof().getExpression().equals(baseRight)) {
            return all(left, right, what, baseLeft, baseRight);
        } else if (left.getProof().getExpression().equals(baseLeft)) {
            return left(left, right, what, baseLeft, baseRight);
        } else if (right.getProof().getExpression().equals(baseRight)) {
            return right(left, right, what, baseLeft, baseRight);
        } else {
            return none(left, right, what, baseLeft, baseRight);
        }
    }
}
