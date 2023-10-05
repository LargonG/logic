package grammar.predicates;

import grammar.Expression;
import grammar.proof.NProof;
import grammar.proof.context.ImmutableContext;
import util.Renamer;

import java.util.Map;
import java.util.Set;

public interface Predicate extends Expression {

    @Override
    default boolean calculate(Map<String, Boolean> values) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    default Expression paste(Map<String, Expression> values) {
        return this;
    }

    @Override
    default Expression toNormalForm() {
        return this;
    }

    @Override
    default NProof createNProof(ImmutableContext context) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    default void getVariablesNames(Set<String> result) {
        // do nothing
    }
}
