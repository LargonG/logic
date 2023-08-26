package grammar.operators;

import grammar.proof.NProof;
import grammar.proof.Proof;
import grammar.Expression;

public class Not implements Bundle {
    private final static RuntimeException exception =
            new UnsupportedOperationException("Not does not support creating operation");

    @Override
    public NProof all(NProof left, NProof right, Proof what, Expression baseLeft, Expression baseRight) {
        throw exception;
    }

    @Override
    public NProof left(NProof left, NProof right, Proof what, Expression baseLeft, Expression baseRight) {
        throw exception;
    }

    @Override
    public NProof right(NProof left, NProof right, Proof what, Expression baseLeft, Expression baseRight) {
        throw exception;
    }

    @Override
    public NProof none(NProof left, NProof right, Proof what, Expression baseLeft, Expression baseRight) {
        throw exception;
    }
}
