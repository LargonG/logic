package grammar.descriptions.natural.checker;

import grammar.Expression;
import grammar.descriptions.RuleChecker;
import grammar.descriptions.natural.NaturalRule;
import grammar.operators.Operator;
import grammar.proof.MetaProof;

import java.util.List;

public class OrCompositionRightChecker implements RuleChecker {
    @Override
    public boolean check(final MetaProof root,
                         final List<MetaProof> children) {
        MetaProof from = children.get(0);
        List<Expression> decomp = Expression.decomposition(
                root.getProof().getExpression(),
                Operator.OR);

        Expression right = decomp.get(1);

        return from.getProof().getExpression().equals(right)
                && root.getProof().getContext().equals(from.getProof().getContext())
                && children.size() == 1;
    }

    @Override
    public NaturalRule getRule() {
        return NaturalRule.OR_COMPOSITION_RIGHT;
    }
}
