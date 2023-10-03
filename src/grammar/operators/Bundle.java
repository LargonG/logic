package grammar.operators;

import grammar.Expression;
import grammar.proof.NProof;
import grammar.proof.Proof;

import java.util.List;

public interface Bundle {
    NProof all(NProof left, NProof right, Proof what, Expression baseLeft, Expression baseRight);
    NProof left(NProof left, NProof right, Proof what, Expression baseLeft, Expression baseRight);
    NProof right(NProof left, NProof right, Proof what, Expression baseLeft, Expression baseRight);
    NProof none(NProof left, NProof right, Proof what, Expression baseLeft, Expression baseRight);

    default NProof createNProof(NProof left, NProof right, Proof what) {
        List<Expression> lst = Expression.decomposition(what.getExpression());

        Expression baseLeft = lst.get(0);
        Expression baseRight = lst.get(1);

        if (left != null && left.getProof().getExpression().equals(baseLeft)
                && right != null
                && right.getProof().getExpression().equals(baseRight)) {
            return all(left, right, what, baseLeft, baseRight);
        } else if (left != null
                && left.getProof().getExpression().equals(baseLeft)) {
            return left(left, right, what, baseLeft, baseRight);
        } else if (right != null
                && right.getProof().getExpression().equals(baseRight)) {
            return right(left, right, what, baseLeft, baseRight);
        } else {
            return none(left, right, what, baseLeft, baseRight);
        }
    }
}
